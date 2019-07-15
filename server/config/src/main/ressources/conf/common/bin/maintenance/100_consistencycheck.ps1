#Imports
$job="Consistency Check"
. ./900_config.ps1

#Programm
LogWrite "Execution of ${job} by $user on $machine started"
. ${scriptdir}/120_ctxdocpool.ps1
. ${scriptdir}/130_mtxcheck.ps1


LogWrite "Execution of ${job} by $user on $machine finished"