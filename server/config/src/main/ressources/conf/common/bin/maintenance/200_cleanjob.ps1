#Imports
$job="Cleanjob"

#Imports
$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."
cd $scriptdir
. ${scriptdir}/900_config.ps1

#Programm
LogWrite "Execution of ${job} by $user on $machine started"

LogWrite "CleanjobFiles"
. ${scriptdir}/220_cleanjob_files.ps1


LogWrite "CleanjobDB"
. ${scriptdir}/230_cleanjob_Db.ps1


LogWrite "Execution of ${job} by $user on $machine finished"