#20130801 - HEITEK - SCRIPT CREATION 
#DEFERRED with FILTER option use - WARNING - Script has unsolved performance problems - gci to share dir takes several miutes to list all files and after that pick only the newest one
#PERFORAMCE TESTED: Execution time < 10 seconds
#Check Lettershop SRC-DIR for actual PDF/LCK/SKYARC file creations

$PrimaryLSdir = "\\s-ng-ctex1-b\ContexShare\mediatrix_data\outbound\*"
#$latestPDF = Get-ChildItem -Path $PrimaryLSdir -include *.pdf    | Sort-Object LastWriteTime -Descending | Select-Object -First 1		# include operation is too slow - replaced by filter operation for better exec times
#$latestARC = Get-ChildItem -Path $PrimaryLSdir -include *.skyarc | Sort-Object LastWriteTime -Descending | Select-Object -First 1		#	dito
#$latestLCK = Get-ChildItem -Path $PrimaryLSdir -include *.lck    | Sort-Object LastWriteTime -Descending | Select-Object -First 1		# 	dito
$latestPDF = Get-ChildItem -Path $PrimaryLSdir -Filter *.pdf    | Sort-Object LastWriteTime -Descending | Select-Object -First 1
$latestARC = Get-ChildItem -Path $PrimaryLSdir -Filter *.skyarc | Sort-Object LastWriteTime -Descending | Select-Object -First 1
$latestLCK = Get-ChildItem -Path $PrimaryLSdir -Filter *.lck    | Sort-Object LastWriteTime -Descending | Select-Object -First 1
#$latestPrimaryPDF.LastWriteTime


$dateIntervallBorder = Get-Date
$dateIntervallBorder = $dateIntervallBorder.AddMinutes(-60)


if ( ($latestPDF.LastWriteTime -lt $dateIntervallBorder) -or ($latestARC.LastWriteTime -lt $dateIntervallBorder) -or ($latestLCK.LastWriteTime -lt $dateIntervallBorder)  ) {
   Write-Host "ERROR: Lettershop-Share hat keine aktuellen PDF/LCK/SKYARC Files !!!";	
} else {
    #Write-Host "PASSED: Lettershop-Share hat aktuelle PDF/LCK/SKYARC Files";
}




