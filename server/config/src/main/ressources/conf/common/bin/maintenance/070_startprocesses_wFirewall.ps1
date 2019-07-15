#Variables

#Imports
$job="060_startprocesses"
. ./900_config.ps1

#Programm
LogWrite "Execute ${job} by $user on $machine started"

if ($machine -match "S-NG-CTEX1-B") {
 LogWrite "S-NG-CTEX1-B"
 net start ityxasposermi
 net start ityxidigmaster
 net start ityxcontexserver
 #net start ityxcontexworkflowservice
 
 #net start ityxcontexidigassembler
 #net start ityxcontexicatassembler

 LogWrite "ReportingDeamon wird nach 20 Minuten gestartet. Bitte Fenster nicht schlieﬂen"
 sleep -seconds (60*20)
 #net start ityxreport 		#ityxreport should run on mtrix1-b

 LogWrite "ReportingDeamon gestartet"
 #net start ityxappserver
 #net start ityxemaildemon
 #net start ityxescalationdemon
 #net start ityxmonitoring
 #net start ityxyard

}elseif ($machine -match "S-NG-MTRIX1-B") {
  LogWrite "Firewall wird automatisch aktiviert. BETA"
  netsh advfirewall set currentprofile state on
  net start ityxappserver
  net start ityxasposermi
  #net start ityxcontexserver
  #net start ityxcontexworkflowservice

  #LogWrite "Firewall wird nach 3 Minuten automatisch deaktiviert. "
  #sleep -seconds (60*3)
  #netsh advfirewall set currentprofile state off
  #LogWrite "Firewall wurde automatisch deaktiviert. "

LogWrite "Firewall bleibt eingeschaltet! Denke dran den zu deaktivieren. "
  
  LogWrite "EmailDaemon wird nach 10 Minuten automatisch gestartet. Bitte Fenster nicht schlieﬂen"
  sleep -seconds (60*10)
  net start ityxemaildemon
  net start ityxescalationdemon
  net start ityxreport

}elseif ($machine -match "S-NG-MTRIX2-B") {
 net start ityxasposermi
 net start ityxcontexserver
 net start ityxcontexworkflowservice

}elseif ($machine -match "S-NG-CTEX2-B") {
 net start ityxasposermi
 net start ityxcontexserver
 net start ityxcontexworkflowservice


}elseif ($machine -match "S-NG-OCR1-B") {
  net start ityxcontexserver

}elseif ($machine -match "S-NG-OCR2-B") {
  net start ityxcontexserver
  net start ityxmonitoring
  net start ityxyard
  sleep -seconds (60*20)
  LogWrite "AssemblerDienste werden nach 20 Minuten automatisch gestartet. Bitte Fenster nicht schlieﬂen"
  net start ityxcontexidigassembler
  net start ityxcontexicatassembler

}elseif (($machine -match "S-NG-TDMS1") -or ($machine -match "S-NG-DDMS1")) {
  LogWrite "Starting INT"
  net start ityxasposermi
  net start ityxidigmaster
  net start ityxcontexserver
  net start ityxcontexworkflowservice
  net start ityxappserver

  LogWrite "Agentursteuerung wird nach 5 Minuten automatisch gestartet. Bitte Fenster nicht schlieﬂen"
  sleep -seconds (60*5)
  net start ityxyard
  net start ityxmonitoring

  LogWrite "EmailDaemon wird nach 20 Minuten automatisch gestartet. Bitte Fenster nicht schlieﬂen"
  sleep -seconds (60*20)
  net start ityxemaildemon
  net start ityxescalationdemon

  LogWrite "AssemblerDienste werden nach 5 Minuten automatisch gestartet. Bitte Fenster nicht schlieﬂen"
  sleep -seconds (60*5)
  net start ityxcontexidigassembler
  net start ityxcontexicatassembler

#}elseif (($machine -match "S-NG-DDMS1")) {
#  LogWrite "Starting DEV_INT"
#  net start ityxidigmaster_TEST
#  net start ityxcontexserver_test
  
}else{
   LogWrite "Unknown Machine: $machine"
}

LogWriteOK "Execute ${job} by $user on $machine done"