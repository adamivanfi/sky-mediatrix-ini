h:
cd \Settings
rem net use o: \\Client\Z$
SET MEDIATRIX_BUILD=U:\Users\meinusch\Development\Sky\CurrentBuild
SET MEDIATRIX_DST=\\s-ng-tdms1\c$\mediatrix

SET ROBOCOPY=C:\Install\robocopy

net use \\s-ng-tdms1\c$ /user:admmein49
rem C:\Install\robocopy /mir %MEDIATRIX_DST%\libs %MEDIATRIX_DST%\libs.new
  %ROBOCOPY% /mir %MEDIATRIX_BUILD%\libs.new %MEDIATRIX_DST%\libs.new

   %ROBOCOPY% /mir %MEDIATRIX_BUILD%\license-service\lib.new %MEDIATRIX_DST%\license-service\lib.new
  %ROBOCOPY% /mir %MEDIATRIX_BUILD%\license-service\nativelib.new %MEDIATRIX_DST%\license-service\nativelib.new
  %ROBOCOPY% /mir %MEDIATRIX_BUILD%\activemq\lib.new %MEDIATRIX_DST%\activemq\lib.new
  %ROBOCOPY% /mir %MEDIATRIX_BUILD%\es\lib.new %MEDIATRIX_DST%\es\lib.new

rem xcopy %MEDIATRIX_BUILD%\tomcat\webapps\contex-ws.war.new %MEDIATRIX_DST%\tomcat\webapps\contex-ws.war.new
rem C:\Install\robocopy /mir %MEDIATRIX_BUILD%\tomcat\webapps\cx-ws.new %MEDIATRIX_DST%\tomcat\webapps\cx-ws.new
  %ROBOCOPY% /mir %MEDIATRIX_BUILD%\tomcat\webapps\contex-ws.new %MEDIATRIX_DST%\tomcat\webapps\contex-ws.new

rem C:\Install\robocopy /mir %MEDIATRIX_BUILD%\tomcat\webapps\contex-ws.new\WEB-INF\classes %MEDIATRIX_DST%\tomcat\webapps\contex-ws.new\WEB-INF\classes

echo finished

	