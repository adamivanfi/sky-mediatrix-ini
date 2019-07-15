#Variables
$job="325_InputReport"
$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
cd $scriptdir
$mediatrix_home="${scriptdir}\..\.."
$TS=(Get-Date -format "yyyyMM")
$Logfile = "${mediatrix_home}\logs\nttam_${job}.log"
. ./900_config.ps1

#Programm
LogWrite "Execution of ${job} by $user on $machine started"
& ${groovy_home}\bin\groovy.bat ${scriptdir}\325_InputReport.groovy  >> $Logfile 2>&1

& ${groovy_home}\bin\groovy.bat ${scriptdir}\325_CancellationReport.groovy  >> $Logfile 2>&1

#& ${groovy_home}\bin\groovy.bat ${scriptdir}\325_CleanReport.groovy  >> $Logfile 2>&1
LogWrite "Execution of ${job} by $user on $machine finished"


