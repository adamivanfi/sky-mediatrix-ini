#Variables
$job="320_1h"
. $((Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) + "\900_config.ps1")
$Logfile = "${mediatrix_home}\logs\nttam_${job}.log"

#Programm
LogWrite "Execution of ${job} by $user on $machine"

LogWrite "Execute 550check by $user on $machine"
& ${groovy_home}\bin\groovy.bat ${scriptdir}\322_500check.groovy  >> $Logfile 2>&1

LogWrite "Execute CXCheck by $user on $machine"
CtxDbExecute "${scriptdir}\321_contex.sql"

LogWrite "Execute MXCheck by $user on $machine"
MtxDbExecute "${scriptdir}\321_mediatrix.sql"

LogWrite "Execution of ${job} by $user on $machine finished"