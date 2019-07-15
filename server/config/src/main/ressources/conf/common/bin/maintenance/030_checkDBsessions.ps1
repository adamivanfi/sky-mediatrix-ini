#Variables

#Imports
$job="002_checkDBsessions"
. ./900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

LogWrite "Check SQLSessions by $user on $machine started"

if (($machine -match "S-NG-CTEX1-B") -or ($machine -match "S-NG-TDMS1")) {
	CtxDbExecute "${scriptdir}\030_checkDBsessions.sql"
	MtxDbExecute "${scriptdir}\030_checkDBsessions.sql"
}

LogWrite "Check SQLSessions by $user on $machine finished"
