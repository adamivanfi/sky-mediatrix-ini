#Imports
$job="010"
. ./900_config.ps1

Function DistributeFuzzyIndex{
  Param ([string]$server, [string]$folder)
	 LogWrite "Distribute new libs to server $server by $user on $machine"
	 LogWrite "Mount Networkpath: \\${server}\d$"
	 #net use \\${server}\d$  >> $Logfile 2>&1
         $out=net use \\${server}\d$
	 Check
	 LogWrite $out
          

         LogWrite "Remove old Index \\${server}\d$\${folder}\"
         Remove-Item -Force -Recurse \\${server}\d$\${folder}\  >> $Logfile 2>&1
	 CheckWarn
	 LogWrite $out

	 LogWrite "Distribute new library \\${server}\c$\${folder} "
	 $out=robocopy /mir "d:\${folder}" \\${server}\d$\${folder}   
	 CheckOutput $out

 	 LogWriteOk "Distribute new libs to server $server by $user on $machine complete"
}

if ($machine -match "S-NG-OCR1-B") {
 DistributeFuzzyIndex "s-ng-mtrix2-b" "mediatrix_data\ContexData"
#}elseif ($machine -match "S-NG-TDMS1") {
 
}else{
 LogWrite "Execution of DB-Jobs is only possible from S-NG-CTEX1-B"  
}


