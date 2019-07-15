set LOGFILE=c:\mediatrix\logs\90_maintenace_check_550_lostTags.log

D:
cd \tmp\groovySKY\groovy-2.0.1

echo +++ %date% %time%  started 654								>> %LOGFILE% 2>&1
echo.												>> %LOGFILE% 
.\bin\groovy.bat C:\mediatrix\bin\maintenance\322_500check.groovy >> %LOGFILE% 2>&1
echo.												>> %LOGFILE%
