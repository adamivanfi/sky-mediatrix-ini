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
         #LogWrite "Remove old library.new \\${server}\c$\${folder}\libs.new "
         #Remove-Item -Force -Recurse \\${server}\c$\${folder}\libs.new  >> $Logfile 2>&1
	 CheckWarn
	 LogWrite $out

	 LogWrite "Distribute new library \\${server}\c$\${folder}\libs.new "
	 robocopy /mir "${mediatrix_home}\libs.new" "\\${server}\c$\${folder}\libs.new"  
	 CheckOutput $out

	 LogWrite $out
	 robocopy /mir "${mediatrix_home}\license-service\lib.new" "\\${server}\c$\${folder}\license-service\lib.new"
	 CheckOutput $out 
	 LogWrite $out
	 robocopy /mir "${mediatrix_home}\license-service\nativelib.new" "\\${server}\c$\${folder}\license-service\nativelib.new"
	 CheckOutput $out 

	 LogWrite $out 
	 robocopy /mir "${mediatrix_home}\activemq\lib.new" "\\${server}\c$\${folder}\activemq\lib.new"
	 CheckOutput $out

	 LogWrite $out 
	 robocopy /mir "${mediatrix_home}\es\lib.new" "\\${server}\c$\${folder}\es\lib.new"
	 CheckOutput $out 

	 LogWrite $out 
	 robocopy /mir "${mediatrix_home}\tomcat\webapps\contex-ws.new"  "\\${server}\c$\${folder}\tomcat\webapps\contex-ws.new"
	 CheckOutput $out 
	

	 LogWrite $out
 	 LogWriteOk "Distribute new libs to server $server by $user on $machine complete"
}



if ($machine -match "S-NG-MTRIX3-B") {
 DistributeNewLib "s-ng-ctex1-b" "mediatrix"
 DistributeNewLib "s-ng-ctex2-b" "mediatrix"
 DistributeNewLib "s-ng-ocr1-b" "mediatrix"
 DistributeNewLib "s-ng-ocr2-b" "mediatrix"
 DistributeNewLib "s-ng-mtrix1-b" "mediatrix"
 DistributeNewLib "s-ng-mtrix2-b" "mediatrix"
 #DistributeNewLib "s-ng-mtrix3-b" "mediatrix"
 DistributeNewLib "s-ng-mtrix4-b" "mediatrix"
}elseif ($machine -match "S-NG-TDMS1") {
  DistributeNewLib "s-ng-mtrix3-b" "mediatrix"
 DistributeNewLib "s-ng-mtrix4-b" "mediatrix"
# DistributeNewLib "s-ng-ctex1-b" "mediatrix"
 DistributeNewLib "s-ng-ddms1" "mediatrix_TEST"
 DistributeNewLib "s-ng-ddms2" "mediatrix_int"
 DistributeNewLib "s-ng-ddms2" "mediatrix_access_business\mediatrix_int"

}else{
 LogWrite "Execution of DB-Jobs is only possible from S-NG-CTEX1-B"  
}


