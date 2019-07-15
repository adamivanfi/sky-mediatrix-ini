
Function CtxDbExecute{
  Param ([string]$script)
 	 LogWrite "DBScriptExecution CX $script by $user on ${machine}"
	 #if (-not ($job -match "241_fuzzydbupdate") ) {
		LogWriteWarn "Sollte das Script hier mehr als 1 Minute stehen bleiben -> Verdacht auf Contex-DB-Verbindungs-Fehler."
	 #}
	 & $oracle_sqlplus  -S "${ctxdb_user}/${ctxdb_pass}`@${ctxdb_tns}" `@${script}  >> $Logfile 2>&1
	 LogWrite "DB:CTX $script execution complete"
 }

Function MtxDbExecute{
   Param ([string]$script)
	 LogWrite "DBScriptExecution MX $script by $user on ${machine}"
	 
	# if (-not ($job -match "241_fuzzydbupdate") ) {
		 LogWriteWarn "Sollte das Script hier mehr als 1 Minute stehen bleiben -> Verdacht auf Mediatrix-DB-Verbindungs-Fehler."
	 #}	
	 & $oracle_sqlplus  -S "${mtxdb_user}/${mtxdb_pass}`@${mtxdb_tns}" `@${script} >> $Logfile 2>&1
	 LogWrite "DB:MTX $script execution complete"
}

Function EtlDbExecute{
   Param ([string]$script)
	LogWrite "DBScriptExecution MX/ETL $script by $user on ${machine}"
	LogWriteWarn "Sollte das Script hier mehr als 1 Minute stehen bleiben -> Verdacht auf MediatrixETL-DB-Verbindungs-Fehler."
	& $oracle_sqlplus  -S "${etldb_user}/${etldb_pass}`@${etldb_tns}" `@${script}  >> $Logfile 2>&1
   	LogWrite "DB:ETL $script execution complete"
}