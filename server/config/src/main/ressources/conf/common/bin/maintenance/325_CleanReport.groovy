import de.cirquent.sky.ityx.common.*
import groovy.sql.Sql

def now= new GregorianCalendar();
def hour = now.get(Calendar.HOUR_OF_DAY) 
def dayofweek = now.get(Calendar.DAY_OF_WEEK) 

def reportdate=String.format('%tY-%<tm-%<td %<tH:00', now)

//der Report sollte t�glich zu folgenden Zeiten an OpsCon geschickt werden: 7:00 Uhr, 10:00 Uhr, 13:00 Uhr, 16:00 Uhr, 19:00 Uhr, 22:00 Uhr und 00:00 Uhr (mit den kompletten Werten vom Vortag) 
if ( hour == 7|| hour == 10 ||  hour==13 ||  hour==16 ||  hour==17 || hour==19 || hour==22 || hour==0) {
 def sql_timeconstraint=" trunc(sysdate) "
 if (hour == 0){
    sql_timeconstraint=" trunc(sysdate-1) "
 }
 Session session=null
 Transport transport=null
 
 def mxdb
 String subject="[SKY_DMS_CleanReport_PROD] ${reportdate} "
 String message="<h3>SKY DMS Clean Report</h3> Production Environment<br/>Datum: ${reportdate}<br/>"
 String recipients="gregor.meinusch@nttdata.com, gregor.meinusch@sky.de"; 
 //String recipients="gregor.meinusch@sky.de, gregor.meinusch@nttdata.com";
 try{
//   cxdb = Sql.newInstance('jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:CONPRODN','ityx_cx', 'Sk3i12Pw', 'oracle.jdbc.driver.OracleDriver') //ggf. oracle.jdbc.OracleDriver
   mxdb = Sql.newInstance('jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:MEXPRODN','ityx_mx', 'Sk3i13Pw', 'oracle.jdbc.driver.OracleDriver') //ggf. oracle.jdbc.OracleDriver
 
   sql="select to_char(DMSDELETEDRUN, 'yyyy.mm.dd hh24') rundate,  count(case when DMSDELETED < -2 then '1' else  null end ) zumLoeschen,  count(case when DMSDELETED in (1,3) then '1' else  null end ) geloeschGesammt ,  count(case when DMSDELETED in (2) then '1' else  null end ) error   from NTT_ATF_POSTPROCESSING where DMSDELETED in (1,3,4,5, -1, -3, -4, -5) and DMSDELETED is not null group by  rollup(to_char(DMSDELETEDRUN, 'yyyy.mm.dd hh24')) order by 1 desc"
   message+="<style type=\"text/css\">table {border:thin solid gray;border-collapse: collapse;}  tr {border:1px solid gray;} th {font-weight:bold; cellspacing:10px; cellpadding:10px; margin:10px; border:1px solid gray;} td{text-align:right;border:1px solid gray; cellspacing:10px; cellpadding:10px; margin:10px;}  td.sum{background-color:lightgray} </style>"
   message+="<table><tr ><th style=\"font-weight:bold\">Datum</th><th>ZumLoeschen</th><th>Geloecht</th><th>Error</th></tr>"
   mxdb.eachRow(sql) { row ->
    if (row.rundate){    
     message+="<tr><td>${row.rundate}</td><td>${row.zumLoeschen}</td><td>${row.geloeschGesammt}</td><td>${row.error}</td></tr>"     
    } else {
     message+="<tr><td class=\"sum\">Gesamt</td><td>${row.zumLoeschen}</td><td>${row.geloeschGesammt}</td><td>${row.error}</td></tr>"     
    }
   }
   message+="</table>" 
 }catch (java.sql.SQLException e){
   message+="\n\r Problem during execution of InputReport \n\r"+e.message
 } finally {
     
    if (mxdb)  mxdb.close()  
 }

 try {
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
      // mailmessage.setText(message)
     mailmessage.setContent(message, "text/html")

     println "sending message:" + recipients+":"+(subject)
     //println alerts         
     transport.send(mailmessage);
 } catch (MessagingException e) {
  e.printStackTrace();
   message="\n\r Problem during sending of InputReport \n\r"+e.message+"  " +message
 } finally {
    if (transport) transport.close()
 }
} else {
    println "TimeNotDefined for Sending of MailReports"
}