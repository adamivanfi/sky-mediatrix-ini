#Variables

#Imports
$job="610_AfterDeployment"
. ./900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

#Programm
LogWrite "Execution of ${job} by $user on $machine started"

CtxDbExecute "${scriptdir}\611_AfterDeployment_Privileges_CX.sql"
MtxDbExecute "${scriptdir}\611_AfterDeployment_Privileges_MX.sql"


LogWrite "Execution of ${job} by $user on $machine finished"




