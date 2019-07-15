#20130801 - HEITEK - SCRIPT CREATION 

# IMPORT-SECTION
. ".\Send_Mail.ps1"

# SCRIPTSTART
$ASLogDir = "\\s-ng-ocr2-b\c$\mediatrix\logs"
$YARDLOG = (get-item $ASLogDir"\ityxyard.log")
$MONLOG  = (get-item $ASLogDir"\ityxmonitoring.log")


$dateIntervallBorder = (Get-Date).AddMinutes(-5)
$dateIntervallBorder2 = (Get-Date).AddMinutes(-16)

#$YARDLOG.LastWriteTime
#$MONLOG.LastWriteTime

if (($YARDLOG.LastWriteTime  -lt $dateIntervallBorder) -or ($MONLOG.LastWriteTime -lt $dateIntervallBorder2)) {
   $ErrorMessage="ERROR: YARD-Log or Monitoring-Log haven't been updated recently !!!"
   Write-Host $ErrorMessage;	
   sendmail $ErrorMessage "SKY-DMS-PROD HCHECK - " + $ErrorMessage
} else {
    Write-Host "PASSED: YARD-Log and Monitoring-Log have been updated recently.";
}




