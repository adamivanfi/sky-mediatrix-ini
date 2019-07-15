#20130731 - HEITEK - SCRIPT CREATION 
# WARNING - Performance problems during script execution !!!
# WARNING - Script has a serious execution time (> 1 Min), gci takes long to get all files of the share diretory (> 3.000 files on each share ...)


# IMPORT-SECTION
. ".\Send_Mail.ps1"


# SCRIPTSTART
$PrimaryFaxdir = "\\kms-hh-dmsfax1.pfad.biz\Faxe\mtrix"

#Test-Path $PrimaryFaxdir 
#$latestPrimaryFAX = Get-ChildItem -Path $PrimaryFaxdir -include *.inc | Sort-Object LastWriteTime -Descending | Select-Object -First 1
#$latestPrimaryFAX = Get-ChildItem -Path $PrimaryFaxdir | Sort-Object LastWriteTime -Descending | Select-Object -First 1  # OLD WAY - Performance ISSUS !!!
$latestPrimaryFAX = Get-ChildItem -Path $PrimaryFaxdir -Filter "*.inc"  | Sort-Object LastWriteTime -Descending | Select-Object -First 1
#Write-Host $latestPrimaryFAX.LastWriteTime

$SecondaryFaxdir = "\\p-hh-mrs1.pfad.biz\Faxe\mtrix"
#Test-Path $SecondaryFaxdir
#$latestSecondaryFAXFile = Get-ChildItem -Path $SecondaryFaxdir | Sort-Object LastWriteTime -Descending | Select-Object -First 1
$latestSecondaryFAXFile = Get-ChildItem -Path $SecondaryFaxdir -Filter "*.inc" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
#Write-Host $latestSecondaryFAXFile.LastWriteTime

$dateIntervallBorder = (Get-Date).AddMinutes(-30)

if (($latestPrimaryFAX.LastWriteTime -lt $dateIntervallBorder) -and ($latestSecondaryFAXFile.LastWriteTime -lt $dateIntervallBorder)) {
   $ErrorMessage = "ERROR: Beide FAX-Server haben keine aktuelle Files!"
   Write-Host $ErrorMessage
   sendmail $ErrorMessage "SKY-DMS-PROD HCHECK - " + $ErrorMessage + "\n Check \\kms-hh-dmsfax1.pfad.biz\Faxe\mtrix and \\p-hh-mrs1.pfad.biz\Faxe\mtrix"
} else {
   #Write-Host "PASSED: Ein FAX-Server hat aktuelle Files";
}




