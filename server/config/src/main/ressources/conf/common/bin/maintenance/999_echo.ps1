#Variables
$REASON="not specified"

$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir

#Imports
$job000d="EmergencyReboot"
$job=$job000d
. ${scriptdir}\900_config.ps1

$DEPLOYMENT_NAME="${job000d}_${TS}_${REASON}"


#Programm
LogWriteOk "${job000d}: ECHO Started by $user on $machine, Reason: $REASON"
LogWriteOk "${job000d}: $DEPLOYMENT_NAME"



LogWriteOk "${job000d}: finished by $user on $machine"
