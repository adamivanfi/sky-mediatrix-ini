$job060="remotedeployment"
$job=$job060

#Imports
. ./900_config.ps1

$job000d="EmergencyReboot"

Function RemoteExecute{
  Param ([string]$rmachine, [string]$script)
  
  $psh="C:\Windows\system32\WindowsPowerShell\v1.0\powershell.exe"
  $psexec="${scriptdir}\tools\PsExec.exe"
  LogWrite ("${psexec} \\${rmachine} ${psh} ${script}" )
  & "${psexec}" "\\${rmachine}" "${psh}" "${script}" 

}

LogWriteOk "${job060}: Started by $user on $machine"

$scriptname="000_echo.ps1"
if ($job000d -and ($job000d -match "EmergencyReboot")){
  $scriptname="000_EMERGENCYREBOOT.ps1"
}elseif ($job000d -and ($job000d -match "Deployment")){
  $scriptname="009_deployment.ps1"
}

 if ($machine -match "S-NG-TDMS1") {
	RemoteExecute "s-ng-ddms2" "c:\mediatrix_int\bin\maintenance\${scriptname}"
	RemoteExecute "s-ng-ddms1" "c:\mediatrix_TEST\bin\maintenance\${scriptname}"

 }elseif ($machine -match "S-NG-CTEX1-B") {
	RemoteExecute "s-ng-ocr1-b" "c:\mediatrix_int\bin\maintenance\${scriptname}"
	RemoteExecute "s-ng-ocr2-b" "c:\mediatrix_int\bin\maintenance\${scriptname}"
	RemoteExecute "s-ng-mtrix1-b" "c:\mediatrix_int\bin\maintenance\${scriptname}"
 }



LogWriteOk "${job060}: Finished by $user on $machine"




