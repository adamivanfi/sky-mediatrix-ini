net use o: \\Client\Z$
SET MEDIATRIX_BUILD=o:\CurrentBuild
SET MEDIATRIX_DST=\\s-ng-tdms1\c$\mediatrix

net use \\s-ng-tdms1\c$ /user:admmein49
C:\Install\robocopy /mir %MEDIATRIX_DST%\libs %MEDIATRIX_DST%\libs.new
C:\Install\robocopy /mir %MEDIATRIX_BUILD%\libs.new %MEDIATRIX_DST%\libs.new

echo finished

	