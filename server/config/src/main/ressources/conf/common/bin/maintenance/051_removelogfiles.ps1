$job="003_removelogfiles"

#Imports
. ./900_config.ps1

LogWrite "Removing Logfiles by $user on $machine"
#cd c:\mediatrix >> $Logfile 2>&1
$DEPLOYMENT_NAME=($TS)

Remove-Item  -ErrorAction:Continue -force -recurse "D:\mediatrix_data\ContexData\settings.trk*"


Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\logs\*.* >> $Logfile 2>&1

Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\logs\incident\*.* >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\conf\server\logs\*.*   >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\conf\server\Mediatrix\*.log  >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\conf\server\Mediatrix\*.log.*  >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\conf\server\Mediatrix\*.out   >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\conf\server\Mediatrix\RuleLogs\*.* >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\tomcat\logs\*.* >> $Logfile 2>&1


Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\bin\logs\*.* >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\bin\logs\incident\*.* >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\activemq\data\*.log >> $Logfile 2>&1
Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\es\logs\*.* >> $Logfile 2>&1

Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\Mediatrix\*.log >> $Logfile 2>&1

Remove-Item  -ErrorAction:Continue -force -recurse ${mediatrix_home}\tmp\* >> $Logfile 2>&1


checkWarn
LogWriteOk "Moving Logfiles finished"