$TS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
Write-Host "${TS} 310_startet"

#Variables
$job="310_15min"

$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 

$TS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
Write-Host "${TS} path ermittelt"

$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir

$Logfile = "${mediatrix_home}\logs\nttam_${job}.log"

$TS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
Write-Host "${TS} verzeihnis gewechselt, log gesetzt"

#Imporrt
. ./900_config.ps1
$TS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
Write-Host "${TS} Config eingelesen"

. ${scriptdir}/922_dbconnections.ps1

$TS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
Write-Host "${TS} DB-Connections gesetzt eingelesen"

#Programm
LogWrite "Execution of ${job} by $user on $machine started"

MtxDbExecute "${scriptdir}\312_mtrix_kpi.sql"
MtxDbExecute "${scriptdir}\314_mtrix_jn_mon.sql"
CtxDbExecute "${scriptdir}\311_contex_kpi.sql"
CtxDbExecute "${scriptdir}\313_ctxdocpool.sql"


MtxDbExecute "${scriptdir}\321_mediatrix.sql"
LogWrite "Execute MXCheck by $user on $machine finished"


LogWrite "Execution of ${job} by $user on $machine finished"


