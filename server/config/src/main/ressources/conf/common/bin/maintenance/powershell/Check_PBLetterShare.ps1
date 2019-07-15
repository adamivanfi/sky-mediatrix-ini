#20130801 - HEITEK - SCRIPT CREATION 
#Checks PB-Share (\\s-ng-ocr1-b\DMSftp\letters_scanner_pbms) for actual CSV fils (maximum age of one day)
#Performance tested: PASSED - EXCEUTION TIME < 5 secs

# IMPORT-SECTION
. ".\Send_Mail.ps1"

# SCRIPTSTART
$PrimaryPBdir = "\\s-ng-ocr1-b\DMSftp\letters_scanner_pbms\*"
$latestPrimaryCSV = Get-ChildItem -Path $PrimaryPBdir -Filter "*.csv" | Sort-Object LastWriteTime -Descending | Select-Object -First 1
#$latestPrimaryCSV.LastWriteTime


$dateIntervallBorder = Get-Date
$dateIntervallBorder = $dateIntervallBorder.AddDays(-1)
#$dateIntervallBorder


if ( $latestPrimaryCSV.LastWriteTime -lt $dateIntervallBorder) {
   #Write-Host "ERROR: PB-Share haben keine aktuelle (CSV) Files !!!";	
   $ErrorMessage = "ERROR: PB-Share haben keine aktuelle (CSV) Files !!!"
   sendmail $ErrorMessage "SKY-DMS-PROD - HCHECK - "$ErrorMessage  
} else {
    # Write-Host "PASSED: PB-Share haben aktuelle (CSV) Files";
}



