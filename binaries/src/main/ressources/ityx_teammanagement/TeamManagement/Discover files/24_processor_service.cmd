@ECHO ON

TITLE Processor Server

set activemq.username=admin

set activemq.password=admin

set activemq.brokerUrl=tcp://localhost:61616

:: start application
java -Dactivemq.brokerUrl="%activemq.brokerUrl%" -Dactivemq.password="%activemq.password%" -Dactivemq.username="%activemq.username%" -jar "./../libs/customlibs/discover-server-1.1.4-jar-with-dependencies.jar" -jar "./../libs/common/discover-erms-messages-1.1.4.jar" -jar "./../libs/customlibs/discover-erms-processors-1.1.4.jar"
pause