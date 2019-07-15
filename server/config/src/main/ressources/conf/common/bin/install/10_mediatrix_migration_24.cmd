@ECHO OFF
TITLE Migration to MX 2.4
cd %cd% 
set JAVA_HOME=C:\Program Files\Java\jdk1.7\
Set path="%JAVA_HOME%\bin\;%path%"

rem FORFILES /s /p ..\..\conf\ /m mediatrix.properties /c "cmd /c cd %cd% & java -jar ..\..\libs\startup.jar -blackfile "**/mediatrix-client*.jar,**/mediatrix-services*.jar" ! de.ityx.mediatrix.db.updater.MediatrixUpdate --config @PATH -xml migrationDB_24.xml"
rem FORFILES /s /p ..\..\conf\ /m mediatrix.properties /c "cmd /c cd %cd% & java -jar ..\..\libs\startup.jar -blackfile "**/mediatrix-client*.jar,**/mediatrix-services*.jar" ! de.ityx.mediatrix.migration.SetArchiveSettings  --config @PATH"
rem FORFILES /s /p ..\..\conf\ /m mediatrix.properties /c "cmd /c cd %cd% & java -jar ..\..\libs\startup.jar -blackfile "**/mediatrix-client*.jar,**/mediatrix-services*.jar" ! de.ityx.mediatrix.migration.SetFiltersUuid  --config @PATH"
rem FORFILES /s /p ..\..\conf\ /m mediatrix.properties /c "cmd /c cd %cd% & java -jar ..\..\libs\startup.jar -blackfile "**/mediatrix-client*.jar,**/mediatrix-services*.jar" ! de.ityx.mediatrix.migration.TextobjectCategory  --config @PATH"
FORFILES /s /p ..\..\conf\ /m mediatrix.properties /c "cmd /c cd %cd% & java -jar ..\..\libs\startup.jar -blackfile "**/mediatrix-client*.jar,**/mediatrix-services*.jar" ! de.ityx.mediatrix.migration.RegenerateAccounts  --config @PATH"
rem FORFILES /s /p ..\..\conf\ /m mediatrix.properties /c "cmd /c cd %cd% & java -jar ..\..\libs\startup.jar -blackfile "**/mediatrix-client*.jar,**/mediatrix-services*.jar" ! de.ityx.mediatrix.migration.SetKeywordFilter  --config @PATH"
rem FORFILES /s /p ..\..\conf\ /m mediatrix.properties /c "cmd /c cd %cd% & java -jar ..\..\libs\startup.jar -blackfile "**/mediatrix-client*.jar,**/mediatrix-services*.jar" ! de.ityx.mediatrix.migration.CopyHolidays  --config @PATH"
pause
