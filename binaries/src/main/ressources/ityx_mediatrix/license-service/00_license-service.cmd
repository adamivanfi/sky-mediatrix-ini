@echo off
TITLE ITyX License Server

SETLOCAL

set WORK_DIR=%~dp0

IF NOT DEFINED ITYX_CONF set ITYX_CONF=.\conf

IF NOT DEFINED ITYX_LIB set ITYX_LIB="%HOME%\.ityx"

set CLASSPATH="%WORK_DIR%\lib\*;%WORK_DIR%\customlib\*;%ITYX_LIB%\*"

java -cp "%CLASSPATH%" -Djava.library.path="%WORK_DIR%\nativelib" -Dityx.conf="%ITYX_CONF%" -Dde.ityx.license.tracker.datadir="%WORK_DIR%\data" -Dcom.mchange.v2.c3p0.cfg.xml="%ITYX_CONF%\c3p0-config.xml" de.ityx.cxlicense.service.LicenseService

if NOT ERRORLEVEL 1 goto finally

:error
pause

:finally
ENDLOCAL