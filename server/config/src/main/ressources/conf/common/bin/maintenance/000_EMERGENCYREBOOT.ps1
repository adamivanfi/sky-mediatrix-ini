#Variables
$REASON="not specified"

$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir

#Imports
$job000d="EmergencyReboot"
$job=$job000d
. ${scriptdir}/900_config.ps1

$DEPLOYMENT_NAME="${job000d}_${TS}_${REASON}"


#Programm
LogWriteOk "${job000d}: Started by $user on $machine, Reason: $REASON"
LogWriteOk "${job000d}: $DEPLOYMENT_NAME"

LogWriteOk "${job000d}: Stop Processes"
. ${scriptdir}/020_stopprocesses.ps1

LogWriteOk "${job000d}: Check DBSessions"
. ${scriptdir}/030_checkDBsessions.ps1

LogWriteOk "${job000d}: Consistencycheck"
. ${scriptdir}/040_consistencycheck.ps1

if (($machine -eq "S-NG-TDMS1") -or ($machine -eq "S-NG-DDMS1")) {
  LogWriteOk "${job000d}: Removelogfiles"
. ${scriptdir}/051_removelogfiles.ps1
}else{
  LogWriteOk "${job000d}: Movelogfiles"
. ${scriptdir}/050_movelogfiles.ps1
}

#LogWrite "Deploy libraries"
#. ${scriptdir}/060_deployment.ps1

LogWriteOk "${job000d}: Start Processes"
$somename = Read-Host "Alles vorbereitet für den Start, sobald die anderen Maschinen runtergefahren sind, drücken Sie Enter um die Applikation hochzufahren"
. ${scriptdir}/070_startprocesses.ps1


LogWriteOk "${job000d}: finished by $user on $machine"
