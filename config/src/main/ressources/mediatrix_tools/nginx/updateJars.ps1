#rm -ErrorAction:Continue -recurse -Force C:\mediatrix\nginx\html\mediatrix
pushd C:\mediatrix\nginx\html
mkdir mediatrix
mkdir mediatrix\nativelibs
mkdir mediatrix\jars
mkdir mediatrix\libs
mkdir mediatrix\jnlp

& java -jar ..\jnlpdownloader-0.0.3.jar mediatrix http://127.0.0.1:8081/mediatrix/jnlp/mediatrix.jnlp
robocopy /mir  mediatrix\libs mediatrix\jars
$WC=New-Object -TypeName System.Net.WebClient
$j=$WC.DownloadString("http://127.0.0.1:8081/mediatrix/jnlp/mediatrix.jnlp")

$j -replace "127.0.0.1:8081", "mediatrix.sky.de:8080" > C:\mediatrix\nginx\html\mediatrix\jnlp\mediatrix.jnlp
popd
