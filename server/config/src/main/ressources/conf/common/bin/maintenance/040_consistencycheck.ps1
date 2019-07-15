#Variables

#Imports
$job="002_checkDBsessions"
. ./900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

LogWrite "Check SQLSessions by $user on $machine started"

if (($machine -match "S-NG-MTRIX3-B") -or ($machine -match "S-NG-TDMS1")) {
	CtxDbExecute "${scriptdir}\041_CTXConsistencycheck.sql"
	MtxDbExecute "${scriptdir}\042_MTXConsistencycheck.sql"
}

LogWrite "Check SQLSessions by $user on $machine finished"

