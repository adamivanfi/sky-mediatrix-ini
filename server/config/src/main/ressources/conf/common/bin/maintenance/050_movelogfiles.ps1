$job050="050_movelogfiles"

#Imports
. ./900_config.ps1

$TTS=(Get-Date -format "yyyyMMdd_HHmm")
$LOGDEPLOYMENT_NAME="${TTS}"

Remove-Item -force -recurse "D:\mediatrix_data\ContexData\settings.trk*"

$logbackupdir="${backupdir}\${LOGDEPLOYMENT_NAME}\logs"
LogWrite "Moving Logfiles by $user on $machine"
LogWrite "mhome: ${mediatrix_home} backdir: ${logbackupdir}"

 New-Item -type directory -path "${logbackupdir}\mtxlogs\incident" >> $Logfile 2>&1
Move-Item ${mediatrix_home}\logs\*.* "${logbackupdir}\mtxlogs\" >> $Logfile 2>&1
Move-Item ${mediatrix_home}\logs\incident\*.* "${logbackupdir}\mtxlogs\incident" >> $Logfile 2>&1

 New-Item -type directory -path  "${logbackupdir}\conflogs" >> $Logfile 2>&1
Move-Item ${mediatrix_home}\conf\logs\*.*  "${logbackupdir}\conflogs\"  >> $Logfile 2>&1
Move-Item ${mediatrix_home}\conf\Mediatrix\*.log "${logbackupdir}\conflogs\" >> $Logfile 2>&1
Move-Item ${mediatrix_home}\conf\Mediatrix\*.log.* "${logbackupdir}\conflogs\" >> $Logfile 2>&1
Move-Item ${mediatrix_home}\conf\Mediatrix\*.out "${logbackupdir}\conflogs\"  >> $Logfile 2>&1
Move-Item ${mediatrix_home}\conf\Mediatrix\RuleLogs\*.* "${logbackupdir}\conflogs\" >> $Logfile 2>&1


Move-Item ${mediatrix_home}\bin\logs\*.* "${logbackupdir}\" >> $Logfile 2>&1
Move-Item ${mediatrix_home}\bin\logs\incident\*.* "${logbackupdir}\" >> $Logfile 2>&1

Move-Item ${mediatrix_home}\bin\logs\incident\*.* "${logbackupdir}\" >> $Logfile 2>&1
Move-Item ${mediatrix_home}\activemq\data\*.log "${logbackupdir}\" >> $Logfile 2>&1
Move-Item ${mediatrix_home}\es\logs\*.* "${logbackupdir}\" >> $Logfile 2>&1


if (Test-Path "${mediatrix_home}\tomcat\logs"){
 New-Item -type directory -path  "${logbackupdir}\tomcatlogs" >> $Logfile 2>&1
 Move-Item ${mediatrix_home}\tomcat\logs\*.* "${logbackupdir}\tomcatlogs\" >> $Logfile 2>&1
 checkWarn
}

if (Test-Path "${scriptdir}\logs\"){
 New-Item -type directory -path "${logbackupdir}\amlogs"  >> $Logfile 2>&1
 get-childitem -Path "${scriptdir}\logs\" | where-object {$_.CreationTime -lt (Get-Date).AddDays(-1)} | %{LogWrite  (${_}.CreationTime+" Move file: "+${_}.name );$_}  | Move-Item -Force -Destination "${logbackupdir}\amlogs"
 checkWarn
}
New-Item -type directory -path "${logbackupdir}\Mediatrix"  >> $Logfile 2>&1
Move-Item ${mediatrix_home}\Mediatrix\*.* "${logbackupdir}\Mediatrix\" >> $Logfile 2>&1

del ${mediatrix_home}\tmp\* >> $Logfile 2>&1

LogWriteOk "Moving Logfiles finished"