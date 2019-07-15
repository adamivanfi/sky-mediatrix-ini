#Imports
. ./900_config.ps1

LogWriteOk "${job060}: Started by $user on $machine"

$libbackupdir="${backupdir}\${DEPLOYMENT_NAME}"

if (Test-Path "${mediatrix_home}\libs.new") {
 #backup old libs 
 #mkdir -p "$libbackupdir\libs" >> $Logfile 2>&1
 rm -recurse -Force "${mediatrix_home}\libs.backup.until*" "${mediatrix_home}\license-service\lib.backup.until*" "${mediatrix_home}\license-service\nativelib.backup.until*" "${mediatrix_home}\activemq\lib.backup.until*" "${mediatrix_home}\tomcat\webapps\contex-ws.until*" "${mediatrix_home}\es\lib.backup.until*"
 CheckWarn
 $TTSB=(Get-Date -format "yyyyMMdd_HHmm")
 mv ${mediatrix_home}\libs "${mediatrix_home}\libs.backup.until${TTSB}"
 check
 mv "${mediatrix_home}\license-service\lib" "${mediatrix_home}\license-service\lib.backup.until${TTSB}"
 mv "${mediatrix_home}\license-service\nativelib" "${mediatrix_home}\license-service\nativelib.backup.until${TTSB}" 
 mv "${mediatrix_home}\activemq\lib" "${mediatrix_home}\activemq\lib.backup.until${TTSB}" 
 mv "${mediatrix_home}\es\lib" "${mediatrix_home}\es\lib.backup.until${TTSB}"
 mv "${mediatrix_home}\tomcat\webapps\contex-ws" "${mediatrix_home}\tomcat\webapps\contex-ws.until${TTSB}"
 
 robocopy /mir ${mediatrix_home}\libs.new ${mediatrix_home}\libs
 check
 robocopy /mir "${mediatrix_home}\license-service\lib.new" "${mediatrix_home}\license-service\lib"
 check
 robocopy /mir "${mediatrix_home}\license-service\nativelib.new" "${mediatrix_home}\license-service\nativelib"
 check 
 robocopy /mir "${mediatrix_home}\activemq\lib.new" "${mediatrix_home}\activemq\lib"
 check
 robocopy /mir "${mediatrix_home}\es\lib.new" "${mediatrix_home}\es\lib"
 check
 robocopy /mir "${mediatrix_home}\tomcat\webapps\contex-ws.new" "${mediatrix_home}\tomcat\webapps\contex-ws"
 check
 rm -recurse -Force ${mediatrix_home}\tmp\*
 rm  -recurse -Force  "${mediatrix_home}\tomcat\work\*"
 #cp -R ${mediatrix_home}\libs\common "${mediatrix_home}\tmp\common_groovy"
}else{
  LogWriteWarn "${job060}: libs.new not found on $machine. Everything ok?"
}


LogWriteOk "${job060}: Finished by $user on $machine"
