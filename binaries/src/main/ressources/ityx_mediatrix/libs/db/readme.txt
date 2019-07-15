Finding the appropriate jdbc driver for your system

First of all figure out which database type you are going to use.


If you want to use 

MySQL:
browse: http://www.mysql.de/products/connector/
and follow the link "JDBC Driver for MySQL (Connector/J)" -> Download

Microsoft SQL Server:
browse: http://www.microsoft.com/download/en/details.aspx?displaylang=en&id=21599 
and select the appropriate driver for your SQL Server version.

Oracle:
browse: http://www.oracle.com/technetwork/database/features/jdbc/index-091264.html
and select the appropriate driver for your oracle version. You need to register at oracle.com to be able to download any files.

DB2:
browse: http://www-01.ibm.com/software/data/db2/express/download.html
Follow: "Get Database drivers". You need a valid db2 installation with a serial number to be able to download any files.

Next thing to do: copy the jar file to this directory: libs/db

Warning: if you update the Mediatrix/Contex system by removing the libs directory and copy a new libs directory, you have to keep the jdbc-jar or otherwise
the system will fail with a "NoClassDefFoundException".