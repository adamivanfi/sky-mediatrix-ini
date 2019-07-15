:: includes
call common_includes.cmd

IF DEFINED HOSTNAME (set HOSTNAME=-Djava.rmi.server.hostname=%HOSTNAME%)

IF DEFINED MANAGEMENT_SERVER (set MANAGEMENT_SERVER=-Dde.ityx.managementserver.host=%MANAGEMENT_SERVER%)

IF DEFINED CLUSTERNAME (set CLUSTERNAME=-Dde.ityx.service.cluster.name=%CLUSTERNAME%)

IF DEFINED MAXMEM (set MAXMEM=-Xmx%MAXMEM%)

IF DEFINED USER_HOME (set USER_HOME=-Duser.home=%USER_HOME%)

IF DEFINED UNIT (set UNIT=-Dde.ityx.cloud.unit=%UNIT%)

"%JAVA_HOME%/bin/java.exe" %XXARGS% %XARGS% %MAXMEM% %USER_HOME% %CLUSTERNAME% %HOSTNAME% %MANAGEMENT_SERVER% %DARGS% -Dityx.home=%ITYX_HOME% -Dcom.mchange.v2.c3p0.cfg.xml=..\conf\c3p0-config.xml -Dityx.conf=%ITYX_CONF% %UNIT% %ARGS% -jar ../libs/startup.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
