$TS=(Get-Date -format "yyyyMMdd_HHmm")
if (-not $Logfile){
 $Logfile = "${mediatrix_home}\logs\nttam_${job}_${TS}.log"
}

Function LogWrite{
   Param ([string]$logstring)
   if ($logstring ){
	$TSS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
	Add-content $Logfile -value "$TSS  $logstring"
   	Write-Host "$TSS  $logstring"
   }
}
Function LogWriteOk{
   Param ([string]$logstring)
   if ($logstring ){
	$TSS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
	Add-content $Logfile -value "$TSS  $logstring"
   	Write-Host -foregroundcolor green "$TSS  $logstring"
   }
}

Function LogWriteWarn{
   Param ([string]$logstring)
   if ($logstring ){
	$TSS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
	Add-content $Logfile -value "$TSS  $logstring"
   	Write-Host -foregroundcolor yellow "$TSS  $logstring"
   }
}

Function Check{
   if ($error){
    $TSS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
    Add-content $Logfile -value "$TSS ERROR ${error}"
    Write-Host -foregroundcolor red "$TSS ERROR ${error}"
    if ($error.count -gt 0){  
	  $error.clear()
    }
    throw("Break because of previous Error")
   }
   
}

Function CheckWarn{
   if ($error){
    $TSS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
    Add-content $Logfile -value "$TSS WARN ${error}"
    Write-Host -foregroundcolor yellow "$TSS WARN ${error}"
    if ($error.count -gt 0){  
	  $error.clear()
    }
   }
}

Function CheckOutput{
    Param ([string]$logstring)
   if ($logstring -and ($logstring -match "ERROR")){
    $TSS=(Get-Date -format "yyyy-MM-dd_HH:mm:ss")
    Add-content $Logfile -value "$TSS ERROR ${logstring}"
    Write-Host -foregroundcolor red "$TSS ERROR ${logstring}"
    if ($error.count -gt 0){  
	  $error.clear()
    }
    throw("Break because of previous Error")
   }
}
