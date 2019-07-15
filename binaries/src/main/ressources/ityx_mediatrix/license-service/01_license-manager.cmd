@echo off
TITLE ITyX License Manager GUI

SETLOCAL

set WORK_DIR=%~dp0

IF NOT DEFINED ITYX_CONF set ITYX_CONF=.\conf

IF NOT DEFINED ITYX_LIB set ITYX_LIB=%HOME%\.ityx

set CLASSPATH=%WORK_DIR%\lib\*;%WORK_DIR%\customlib\*;%ITYX_LIB%\*

java -cp "%CLASSPATH%" -Djava.library.path=%WORK_DIR%\nativelib -Dityx.conf=%ITYX_CONF% de.ityx.license.ui.swing.CustomerLicenseManager

if NOT ERRORLEVEL 1 goto finally

:error
pause

:finally
ENDLOCAL