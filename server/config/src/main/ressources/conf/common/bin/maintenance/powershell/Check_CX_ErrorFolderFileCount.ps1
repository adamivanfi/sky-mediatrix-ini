#20130805 - HEITEK - SCRIPT CREATION 
#Check Contex Error Folder Path file count

#IMPORT-SECTION
. ".\Send_Mail.ps1"

# SCRIPTSTART
$CX1BErrorFolderPath="\\s-ng-ctex1-b.pfad.biz\D$\mediatrix_data\err\DMSftp\scanner1"
$MX2BErrorFolderPath="\\s-ng-mtrix2-b\d$\mediatrix_data\err\DMSftp\scanner1"


$CX1B_ErrorFolderFiles = (Get-ChildItem $CX1BErrorFolderPath | where-object {-not ($_.PSIsContainer)} | Measure-Object)
$MX2B_ErrorFolderFiles = (Get-ChildItem $MX2BErrorFolderPath | where-object {-not ($_.PSIsContainer)} | Measure-Object)

#Write-Host "CX1B-Anzahl: " $CX1B_ErrorFolderFiles.Count
#Write-Host "MX2B-Anzahl: " $MX2B_ErrorFolderFiles.Count



#CX-1B-Error Folder Check
if ( $CX1B_ErrorFolderFiles.Count -ne 0  ) {
   # Do Error handling
   $ErrorMessage = "ERROR: Error folder in CX1-B is not empty!"
   sendmail $ErrorMessage "SKY-DMS-PROD HCHECK - " + $ErrorMessage
} else {
   # Do success handling
   #Write-Host "PASSED: Error folder in CX1-B is empty.";
}


#MTRIX2-B Error Folder Check
if ( $MX2B_ErrorFolderFiles.Count -ne 0  ) {
   # Do Error handling
   $ErrorMessage = "ERROR: Error folder in MX2-B is not empty!"
   sendmail $ErrorMessage "SKY-DMS-PROD HCHECK - " + $ErrorMessage  
} else {
   # Do success handling
   # Write-Host "PASSED: Error folder in MX2-B is empty.";
}



