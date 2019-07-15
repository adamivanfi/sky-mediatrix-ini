#Imports
$job="010"
. ./900_config.ps1

Function DistributeNewLib{
  Param ([string]$server, [string]$folder)
	 LogWrite "Distribute new libs to server $server by $user on $machine"
	 LogWrite "Mount Networkpath: \\${server}\c$"
	 #net use \\${server}\c$  >> $Logfile 2>&1
         $out=net use \\${server}\c$
	 Check
	 LogWrite $out

         LogWrite "Remove old MaintenanceScrips \\${server}\c$\${folder}\bin\maintenance"
         Remove-Item -Force -Recurse \\${server}\c$\${folder}\bin\maintenance  >> $Logfile 2>&1
	 CheckWarn
	 LogWrite $out

	 LogWrite "Distribute new library \\${server}\c$\${folder}\bin\maintenance "
	 $out=robocopy /mir "${mediatrix_home}\bin\maintenance" \\${server}\c$\${folder}\bin\maintenance   
	 CheckOutput $out

 	 LogWriteOk "Distribute new libs to server $server by $user on $machine complete"
}

if ($machine -match "S-NG-CTEX1-B") {
 DistributeNewLib "s-ng-ocr1-b" "mediatrix"
 DistributeNewLib "s-ng-ocr2-b" "mediatrix"
 DistributeNewLib "s-ng-mtrix1-b" "mediatrix"
 DistributeNewLib "s-ng-mtrix2-b" "mediatrix"
 DistributeNewLib "s-ng-ctex2-b" "mediatrix"
}elseif ($machine -match "S-NG-TDMS1") {
 DistributeNewLib "s-ng-ddms1" "mediatrix_TEST"
 DistributeNewLib "s-ng-ddms2" "mediatrix_int"
 DistributeNewLib "s-ng-ctex1-b.pfad.biz" "mediatrix"
}else{
 LogWrite "Execution of DB-Jobs is only possible from S-NG-CTEX1-B"  
}


