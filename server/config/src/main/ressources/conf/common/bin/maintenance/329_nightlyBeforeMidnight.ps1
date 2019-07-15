#Variables
$job="329_nightlyBeforeMidnight"

$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir

$Logfile = "${mediatrix_home}\logs\nttam_${job}.log"

#Imporrt
. ./900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

#Programm
LogWrite "Execution of ${job} by $user on $machine started"

MtxDbExecute "${scriptdir}\250_malogWorkaround.sql"
MtxDbExecute "${scriptdir}\251_mx_canceledindividualcorrespondence.sql"
MtxDbExecute "${scriptdir}\323_mtrix.sql"


LogWrite "Execute 550check by $user on $machine started"
& ${groovy_home}\bin\groovy.bat ${scriptdir}\322_500check.groovy  >> $Logfile 2>&1
LogWrite "Execute 550check by $user on $m

LogWrite "Execution of ${job} by $user on $machine finished"


