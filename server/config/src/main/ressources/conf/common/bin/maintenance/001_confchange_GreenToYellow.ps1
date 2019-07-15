#Variables
$REASON="unknown"

$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir

#Imports
$job000d="ConfChange"
$job=$job000d
. ${scriptdir}/900_config.ps1

. ${scriptdir}/003_confchange_configuration.ps1

ChangeConfigurationTo "GreenToYellow"

LogWriteOk "${job000d}: finished by $user on $machine"
