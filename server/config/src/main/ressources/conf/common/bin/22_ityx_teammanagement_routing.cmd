@ECHO OFF
if %PROCESSOR_ARCHITECTURE% == AMD64 (SET WIN=win64) else (SET WIN=win)
cd %CD%
.\service\%WIN%\wrapper.exe -c ..\..\..\conf\service\ityx-teammanagement-routing.conf
pause