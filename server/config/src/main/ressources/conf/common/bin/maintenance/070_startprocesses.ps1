#Variables

#Imports
$job="060_startprocesses"
. $((Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) + "\900_config.ps1")

#Programm
LogWrite "Execute ${job} by $user on $machine started"

if (($machine -match "S-NG-CTEX1-B") -or ($machine -match "S-NG-CTEX2-B")) {
 If (isColdStandby){
	coldStandbyProtection
 }else{	
	#net start ityxidigmaster
	net start ityxcontexserver
	#net start ityxasposermi
	#net start ityxcontexidigassembler
 }
}elseif (($machine -match "S-NG-MTRIX1-B") -or ($machine -match "S-NG-MTRIX2-B")) {
  If (isColdStandby){
	#coldStandbyProtection
    net start ityxcontexserver
 }else{	
  LogWrite "Firewall wird aktiviert."
  netsh advfirewall set currentprofile state on
  net start ityxcontexserver 
  sleep 5 
  net start ityxappserver

  LogWrite "Firewall wird nach 4 Minuten automatisch deaktiviert."
  sleep -seconds (60*4)
  netsh advfirewall set currentprofile state off
  LogWrite "Firewall wurde automatisch deaktiviert."
  LogWriteOK "Bitte prüfen ob man sich anmelden kann. Sobald geschehen, kann DMS über OpsCon für die Agenturen freigegeben werden."
  LogWrite "EmailDaemon wird nach 10 Minuten automatisch gestartet. Bitte Fenster nicht schließen"

  net start ityxoutbounddemon
  net start ityxescalationdemon
  sleep -seconds (60*10)
  net start ityxemaildemon
  #20130220 - Reporting Daemon sollte laut ITyX Empfehlung auf der selben Maschine wie der MX AppServer laufen
  net start ityxreport
 }
}elseif (($machine -match "S-NG-MTRIX3-B") -or ($machine -match "S-NG-MTRIX4-B")) {
 If (isColdStandby){
    net start ityxcontexserver
 }else{
	net start ActiveMQ
  sleep 10
  net start ityxlicensetracker
  sleep 40  
  net start ityxidigindexserver
  sleep 45  
  net start ityxcontexserver 
  sleep 25 
  net start ityxscheduler
  net start ityxcontexidigassembler
  net start ityxcontexicatassembler
  net start ityxcontexiformassembler
  }
}elseif (($machine -match "S-NG-OCR1-B") -or ($machine -match "S-NG-OCR2-B")){
	If (isColdStandby){ 
		net start ityxcontexserver
	}else {
		net start ityxcontexserver
	#	net start ityxmonitoring
	#	net start ityxyard
	#	sleep -seconds (60*20)
	#	LogWrite "AssemblerDienste werden nach 20 Minuten automatisch gestartet. Bitte Fenster nicht schließen"
	#	net start ityxcontexidigassembler
	#	net start ityxcontexicatassembler
	}

}elseif (($machine -match "S-NG-TDMS1") -or ($machine -match "S-NG-DDMS1")) {
  LogWrite "Starting INT"
  
  net start ActiveMQ
  sleep 10
  net start ityxlicensetracker
  sleep 25  
  net start ityxidigindexserver
  sleep 25  
  net start ityxcontexserver 
  sleep 25 
  net start ityxscheduler
  sleep 10
  net start ityxappserver
  
sleep 30
  net start ityxdbmonitoringdemon
  net start ityxreport
 # net start ityxptmedemon

  LogWrite "Agentursteuerung wird nach 5 Minuten automatisch gestartet. Bitte Fenster nicht schließen"
  sleep -seconds (60*3)

#  net start ityxteammanagementrouting
#  net start ityxteammanagementmonitoring

  LogWrite "EmailDaemon wird nach 20 Minuten automatisch gestartet. Bitte Fenster nicht schließen"
  sleep -seconds (60*3)
  net start ityxemaildemon
 net start ityxescalationdemon
  net start ityxoutbounddemon

  LogWrite "AssemblerDienste werden nach 5 Minuten automatisch gestartet. Bitte Fenster nicht schließen"
  sleep -seconds (60*10)
  net start ityxcontexicatassembler 
  net start ityxcontexidigassembler
  

#}elseif (($machine -match "S-NG-DDMS1")) {
#  LogWrite "Starting DEV_INT"
#  net start ityxidigmaster_TEST
#  net start ityxcontexserver_test
  
}else{
   LogWrite "Unknown Machine: $machine"
}

LogWriteOK "Execute ${job} by $user on $machine done"