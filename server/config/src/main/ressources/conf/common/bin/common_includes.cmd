set JAVA_HOME=C:\Program Files\Java\jdk1.7\
Set path="%JAVA_HOME%\bin\;%path%"

if %PROCESSOR_ARCHITECTURE% == AMD64 (SET WIN=win64) else (SET WIN=win)

:: the system path used to locate binaries
set path=..\ocr\tesseract3;%path%

:: x arguments passed to java executable
set XARGS=-Xincgc

:: xx arguments passed to java executable
set XXARGS=-XX:MaxPermSize=256M -noverify

:: the max amount of ram to be used
set MAXMEM=1424m

:: this hosts hostname (defaults to local ip address)
set HOSTNAME=

:: hostname of management server. defaults to 'localhost'
set MANAGEMENT_SERVER=

:: additional arguments passed to java executable
set ARGS=

:: name for contex-cluster (leave empty for auto name based on database properties)
set CLUSTERNAME=idigindexcluster

:: the installation directory
set ITYX_HOME=..

:: the config directory
if NOT DEFINED ITYX_CONF set ITYX_CONF=%ITYX_HOME%\conf

:: additional system properties separated with blanks (' ')
set DARGS=-Djava.library.path=service/sigar-bin/;../libs/nativelibs;service/%WIN% -Djava.security.policy=service/rmi.policy -Djava.security.manager -Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8 -Dmail.mime.encodefilename=true

:: the unit (database/customer) to start with. Only applicable for clients (contex admin, ...)
set UNIT=DEFAULT
