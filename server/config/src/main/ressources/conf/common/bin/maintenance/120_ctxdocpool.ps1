#Variables

#Imports
$job="120_ctx_consistency"
. ./900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

#Programm
LogWrite "Execute ${job} by $user on $machine started"

CtxDbExecute "${scriptdir}\120_ctx_consistency.sql"

CtxDbExecute "${scriptdir}\313_ctxdocpool.sql"




LogWrite "Execute ${job} by $user on $machine finished"


