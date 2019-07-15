@echo off
TITLE ITyX License Server Update

SETLOCAL

set SCRIPT_DIR=%~dp0
for %%I in ("%SCRIPT_DIR%..") do set WORK_DIR=%%~dpfI

IF NOT DEFINED ITYX_CONF set ITYX_CONF=..\conf

IF NOT DEFINED ITYX_LIB set ITYX_LIB=%HOME%\.ityx

set CLASSPATH=%WORK_DIR%\lib\*;%WORK_DIR%\customlib\*;%ITYX_LIB%\*

java -cp "%CLASSPATH%" -Djava.library.path=%WORK_DIR%\nativelib -Dityx.conf=%ITYX_CONF% de.ityx.cxlicense.service.LicenseServiceUpdate

ENDLOCAL

pause

