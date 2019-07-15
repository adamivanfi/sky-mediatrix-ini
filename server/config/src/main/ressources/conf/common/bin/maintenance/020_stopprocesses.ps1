$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$scriptdir="c:\mediatrix\bin\maintenance\"
$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir

#Imports
$job="020_stopprocesses"
. ${scriptdir}/900_config.ps1

$DEPLOYMENT_NAME="${job000d}_${TS}_${REASON}"

LogWrite "Stopping processes by $user on $machine"

if ($machine -match "S-NG-MTRIX1-B") {
  LogWrite "Firewall wird automatisch aktiviert damit backend-prozesse angestoßen werden. In 2 Minuten werden die Prozesse gestoppt"
  netsh advfirewall set currentprofile state on
  sleep -seconds (60)

}

#if (($machine -match "S-NG-DDMS1")) {
#  net stop ityxidigmaster_TEST
#  net stop ityxcontexserver_test
#}else{  


if (($machine -match "S-NG-MTRIX3-B") -or ($machine -match "S-NG-TDMS1")) {
  #$out= & ${mediatrix_home}\bin\contexclient_auto.cmd "`!" "de.ityx.contex.impl.designer.ContexDesignerServer" "STOP" "ALL"  
  #LogWrite "Wfl Prozesse werden gestoppt, in 30 Sekunden gehts weiter"
  #sleep -seconds (30)
}

LogWrite "WindowsProzesse werden angehalten"

net stop ityxoutbounddemon >> $Logfile 2>&1
net stop ityxescalationdemon >> $Logfile 2>&1
net stop ityxemaildemon >> $Logfile 2>&1
net stop ityxteammanagementmonitoring >> $Logfile 2>&1
net stop ityxteammanagementrouting >> $Logfile 2>&1
net stop ityxreport >> $Logfile 2>&1
net stop ityxappserver >> $Logfile 2>&1
net stop ityxdbmonitoringdemon >> $Logfile 2>&1
net stop ityxptmedemon >> $Logfile 2>&1
net stop ityxscheduler >> $Logfile 2>&1

net stop ityxcontexserver >> $Logfile 2>&1
net stop ityxidigindexserver >> $Logfile 2>&1

net stop ityxcontexicatassembler >> $Logfile 2>&1
net stop ityxcontexidigassembler >> $Logfile 2>&1

net stop ityxlicensetracker >> $Logfile 2>&1
net stop elasticsearch-service-x64 >> $Logfile 2>&1
net stop ActiveMQ >> $Logfile 2>&1

#}


if ($machine -match "S-NG-MTRIX1-B") {
  LogWrite "Firewall wird deaktiviert. "
  netsh advfirewall set currentprofile state off
} 

LogWrite "Stopping all other java processes"
Stop-Process -processname java  >> $Logfile 2>&1
Stop-Process -processname jp2launcher >> $Logfile 2>&1


checkWarn
LogWriteOk "Stopping processes by $user on $machine finished"