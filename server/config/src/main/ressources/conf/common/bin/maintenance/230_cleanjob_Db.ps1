#Variables
$job="230_cleanjob_db"

#Imports
$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir
. ${scriptdir}/900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

#Programm
LogWrite "Execution of ${job} by $user on $machine started"

MtxDbExecute "${scriptdir}\232_cleanjob_dbmtx.sql"
EtlDbExecute "${scriptdir}\233_cleanjob_etl.sql"
CtxDbExecute "${scriptdir}\231_cleanjob_dbcontex1.sql"
CtxDbExecute "${scriptdir}\231_cleanjob_dbcontex2.sql"
CtxDbExecute "${scriptdir}\231_cleanjob_dbcontex3.sql"
CtxDbExecute "${scriptdir}\231_cleanjob_dbcontex4.sql"
CtxDbExecute "${scriptdir}\231_cleanjob_dbcontex5.sql"
CtxDbExecute "${scriptdir}\231_cleanjob_dbcontex6.sql"

LogWrite "Execution of ${job} by $user on $machine finished"


