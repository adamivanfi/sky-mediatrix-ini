$job241 ="241_fuzzydbupdate"
. ./900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

LogWriteOk "Execution of $job241 by $user on $machine started"

#CtxDbExecute "${scriptdir}\241_newdbupdate.sql"
$out=&  "${mediatrix_home}\bin\contexclient_auto.cmd" "-F" "-N" "newdb_sky" "-m" "1"
Check
CheckOutput $out


LogWriteOk "Execution of $job241 by $user on $machine finished"

