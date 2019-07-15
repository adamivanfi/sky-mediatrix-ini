import de.cirquent.sky.ityx.common.*
import groovy.sql.Sql

def now= new GregorianCalendar();
def hour = now.get(Calendar.HOUR_OF_DAY) 
def dayofweek = now.get(Calendar.DAY_OF_WEEK) 

def reportdate=String.format('%tY-%<tm-%<td %<tH:00', now)
def treportdate=String.format('%tY%<tm%<td_%<tH%<tM', now)


//der Report sollte t�glich zu folgenden Zeiten an OpsCon geschickt werden: 7:00 Uhr, 10:00 Uhr, 13:00 Uhr, 16:00 Uhr, 19:00 Uhr, 22:00 Uhr und 00:00 Uhr (mit den kompletten Werten vom Vortag) 
if (hour >=7  && hour<= 23) { //( hour == 7|| hour == 10 ||  hour==13 ||  hour==16 || hour==19 || hour==22 || hour==0) {
 def sql_timeconstraint=" trunc(sysdate) "
 if (hour == 0){
    sql_timeconstraint=" trunc(sysdate-1) "
 }
 Session session=null
 Transport transport=null
 
 def mxdb
 String subject="[SKY_DMS_ForwardReport_PROD] ${reportdate} "
 String message=""
 String recipients="gregor.meinusch@sky.de"; 
 //String recipients="gregor.meinusch@sky.de, gregor.meinusch@nttdata.com";
 try{
   mxdb = Sql.newInstance('jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:MEXPRODN','ityx_mx', 'Sk3i13Pw', 'oracle.jdbc.driver.OracleDriver') //ggf. oracle.jdbc.OracleDriver
   sql="SELECT ai.idate TIntervall, ai.agenturname Agenturname,  nvl(j.Anzahl_Weiterleitungen,0) anzahlweiterleitungen FROM   (SELECT PINTERVAL_HALFHOUR(jn.created) idate,    DECODE(jn.EXEC_AGENTURID, NULL, 101, 0, 101, jn.EXEC_AGENTURID) agenturid,   COUNT(*) Anzahl_Weiterleitungen  FROM EBMF_EVENT_JN jn WHERE created>TRUNC(sysdate)  AND event    ='TF'  GROUP BY PINTERVAL_HALFHOUR(created),    DECODE(jn.EXEC_AGENTURID, NULL, 101, 0, 101, jn.EXEC_AGENTURID)  ) j,  (SELECT a.id agenturid, a.name agenturname, i.idate    FROM ar_agentur a,    (SELECT (TRUNC (sysdate) + NUMTODSINTERVAL( (30*(rownum-1)),'MINUTE') ) idate    FROM dual      CONNECT BY level <= 48    ) i    where i.idate<PINTERVAL_HALFHOUR(sysdate)  ) ai WHERE ai.agenturid =j.agenturid (+) AND ai.idate=j.idate (+) ORDER BY 1,2 ";          
   message+="\"Intervall\";\"Agentur\";\"Weiterleitungen\"\r\n"
   mxdb.eachRow(sql) { row ->
      message+="\"${row.TIntervall}\";\"${row.Agenturname}\";\"${row.anzahlweiterleitungen}\"\r\n"         
   }
   message+="" 
 }catch (java.sql.SQLException e){
   message+="\n\r Problem during execution of ForwardReport \n\r"+e.message
 } finally {
    if (mxdb)  mxdb.connection.close()  
 }
 
 def myFile= new File('\\\\pfad.biz\\Com\\CService_C\\Mediatrix\\Wtl_Report\\DMS_FReport_'+treportdate+'.csv')
 if (myFile.exists()){
     myFile.delete();
 }
 myFile.write(message, "UTF-8")

 
/* try {
  Properties properties = new Properties();
  properties.put("mail.smtp.port", "25");
  properties.put("mail.smtp.host", "p-ng-ux07-emmi.premiere.de");    
  properties.put("mail.transport.protocol", "smtp");              
  Authenticator auth = new Authenticator() {
    public PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication("skydmsprod",  "bAntarw8");
    }
  }
  session = new Session(properties, auth); 
  transport=session.getTransport()
  Message mailmessage = new MimeMessage(session);
     mailmessage.setFrom(new InternetAddress("skydmsint@sky.de"));
     mailmessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
     mailmessage.setSubject(subject);
     mailmessage.setText(message)
     //mailmessage.setContent(message, "text/html")

     println "sending message:" + recipients+":"+(subject)
     //println alerts         
     transport.send(mailmessage);
 } catch (MessagingException e) {
  e.printStackTrace();
   message="\n\r Problem during sending of InputReport \n\r"+e.message+"  " +message
 } finally {
    if (transport) transport.close()
 }
 */
} else {
    println "TimeNotDefined for Sending of MailReports"
}