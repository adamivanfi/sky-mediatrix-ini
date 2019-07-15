# Build prerequisites all
## Cygwin
 https://cygwin.com/setup-x86_64.exe
 mit paketen: git, grep, curl, zip, unzip, tar, ncftp, openssl, openssh
## Java JDK 7
 http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html#jdk-7u80-oth-JPR
## ApacheMaven
 https://maven.apache.org/download.cgi
## WinMerge
 http://winmerge.org/downloads/?lang=de
## IDE: Eclipse or IntelliJ CommunityEdition
## WindowsPowerShell4
 Bei Windows7 PowershellUpgrade notwendig:
 http://www.microsoft.com/de-DE/download/details.aspx?id=40855

# Needed Accounts
## SSH rsa-schlüssel generieren mit PuttyKeygen oder openssl - z.B.:
C:\...\> ssh-keygen -t rsa -b 4096 -C "<e-mail address>"
## ...aktualisiert auch c:\user\<name>\.ssh - wenn nicht:
private und public key kopieren unter Windows ins Benutzerverzeichnis: %userprofile%/.ssh/id_rsa und %userprofile%/.ssh/id_rsa.pub
unter cygwin unter ~/.ssh/id_rsa und ~/.ssh/id_rsa.pub
## Irrelevant:
##   in der verwendeten Console:
##   C:\...\> eval $(ssh-agent -s)
##   C:\...\> ssh-add ~/.ssh/id_rsa
## VERALTET:
##   ITyX Account for ITyX GIT: https://cugit.ityx.de
##   über ITyX Support unter Angabe von namen und Email-Adresse einrichten lassen
Public-Schlüssel hinterlegen:
  - https://cugit.ityx.de/profile/keys
      URL öffnen, mit dem Benutzer "ntt_<name>" einloggen, public key hochladen.
  - NTT GITOLITE
      public key an Gregor oder Marcel zuschicken damit NTT Gitolite-Access eingerichtet wird

## SKY-Account
Sky-Benutzer und Sky-Admin-Benutzer über servcedesk@sky.de anlegen lassen, mit Angabe eines Accounts dessen Rechte als Vorlage genommen werden sollen


#Configuration
## Windows, Winmerge
unter Windows-Systemvariablen Pfad erweitern um Maven-Directory und JavaSDK/bin, Variable JAVA_HOME auf SDK setzen

in Powershell-Console, gestartet als Admin
 Set-ExecutionPolicy RemoteSigned
 cmd /c mklink "c:\Program Files (x86)\WinMerge\Filters\WinMergeJavaFilter.flt" "V:\Development\Sky\sky_dms\binaries\src\main\ressources\build_mac\WinMergeJavaFilter.flt"

## Cygwin
- Datei ~/.netrc erstellen mit dem Content
	machine cugit.ityx.de
	login ntt_meinusch
	password einVonItyxZugeschicktesPassword


## Git inital auschecken
mkdir -p ~/Development/Sky
cd ~/Development/Sky
git clone ssh://gitolite@cgn-durin-01:22/sky  sky_dms
git clone https://cugit.ityx.de/ntt-data/customer-sky.git  git_ityx_mxclient

#wenn bereits ausgescheckt, dann
mkdir -p ~/Development/Sky
cd ~/Development/Sky
ln -s /cygdrive/c/Dev/Sky/sky_dms ~/Development/Sky/sky_dms
ln -s /cygdrive/c/Dev/Sky/git_ityx_mxclient ~/Development/Sky/git_ityx_mxclient

## Pfad für Benutzer spezifizieren:
##   Erweitere mac6_cygwinToSkySFTP.ps1 durch die Zeilen:
##   if ($USERNAME -match ...
##   ...
##   elseif ($USERNAME -match "<myname>") {
##       $WORKSPACE="C:\Users\<myname>\Development\SKY\sky_dms"
##       #  $NTTUSERNAME="<myname>"
##   }


## initial Build
ins sky_dms wechseln und einmalig:
git config core.autocrlf false
./skybuild.sh -i
. ./skybuild.sh
mvn install

in IDE den Verzeichnis /home/USERNAME/Development/Sky/sky_dms importieren




#Wiederholtes Build & Deplyoment
1) Lokal bauen mit einem von den unten stehenden scripte
./skybuild.sh -c # für Client und Server Libraries
./skybuild.sh -s # für Server Libraries
./skybuild.sh -f # für alle Libraries (nur nach einem Minor/major releaseupdate notwendig)

=> dabei wird am Ende das Release-Zip-packet zur Sky-FTP übertagen

2) über Sky-VDI-Citrix anmelden und den Powershell Script ausführen:
\\wd008958.pfad.biz\c$\mediatrix_maintenance\desktop_env\Util\Tools\DevDeployment\1_FtpToInt.ps1
Dabei wird das Zip-Packet von Sky-FTP auf die Integration übertragen

3) auf der SKY-Integration (uk-int-cx wd008958) den "PSH AM M Cluster"-Powershell öffnen und das befehl:
 c000-ClusterReboot.ps1 -d
ausführen. Dabei wird das Release-ZipPacket entpackt, auf die Clusternodes kopiert und cluster neugestartet

