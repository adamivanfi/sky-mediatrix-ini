$job ="241_fuzzydbupdate"
$scriptdir=(Split-Path ((Get-Variable MyInvocation -Scope 0).Value).MyCommand.Path) 
$mediatrix_home="${scriptdir}\..\.."

. ${scriptdir}/900_config.ps1

LogWriteOk "Configuration of $job241 by $user on $machine done"

. ${scriptdir}/922_dbconnections.ps1

LogWriteOk "DB-Setup finished. Execution of $job241 by $user on $machine started"

if (($machine -match "S-NG-MTRIX4-B") -or ($machine -match "S-NG-MTRIX3-B") -or ($machine -match "S-NG-TDMS1")  ) {
	CtxDbExecute "${scriptdir}\241_newdbupdate_Vertragsdatum.sql"

#cd "${mediatrix_home}\bin"
#	& "${mediatrix_home}\bin\contexclient_auto.cmd" "-F" "-N" "newdb_sky" "-m" "1"
#	& "${mediatrix_home}\bin\contexclient_auto.cmd" "-F" "-N" "fuzzy_newdb" "-m" "1"
#	Check
#	CheckOutput $out
}
#cd $scriptdir

LogWriteOk "Execution of $job241 by $user on $machine finished"

