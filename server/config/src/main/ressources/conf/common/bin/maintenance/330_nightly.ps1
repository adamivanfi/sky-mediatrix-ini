#Variables

#Imporrt
$job="330_nightly"
. ./900_config.ps1
. ${scriptdir}/922_dbconnections.ps1

#Programm
LogWrite "Execution of ${job} by $user on $machine started"

LogWrite "Starting Cleanjob"
. ${scriptdir}/200_cleanjob.ps1
. ${scriptdir}/240_newdbupdate_SEPA.ps1

LogWrite "Execution of ${job} by $user on $machine finished"


