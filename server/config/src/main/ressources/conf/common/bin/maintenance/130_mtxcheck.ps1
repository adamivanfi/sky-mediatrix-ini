#Variables

#Imports
$job="120_docpoolcheck"
. ./900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

#Programm
LogWrite "Execute ${job} by $user on $machine started"

MtxDbExecute "${scriptdir}\131_mtxcheck.sql"



LogWriteOK "Execute ${job} by $user on $machine finished"


