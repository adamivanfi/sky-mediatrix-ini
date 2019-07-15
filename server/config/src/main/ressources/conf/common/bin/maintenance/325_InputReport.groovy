import de.cirquent.sky.ityx.common.*
import groovy.sql.Sql

def now= new GregorianCalendar();
def hour = now.get(Calendar.HOUR_OF_DAY) 
def dayofweek = now.get(Calendar.DAY_OF_WEEK) 

def reportdate=String.format('%tY-%<tm-%<td %<tH:00', now)

//der Report sollte t�glich zu folgenden Zeiten an OpsCon geschickt werden: 7:00 Uhr, 10:00 Uhr, 13:00 Uhr, 16:00 Uhr, 19:00 Uhr, 22:00 Uhr und 00:00 Uhr (mit den kompletten Werten vom Vortag) 
if ( hour == 7|| hour == 10 ||  hour==13 ||  hour==16 || hour==19 || hour==22 || hour==0) {
 def sql_timeconstraint=" trunc(sysdate) "
 if (hour == 0){
    sql_timeconstraint=" trunc(sysdate-1) "
 }
 Session session=null
 Transport transport=null
 
 def cxdb
 String subject="[SKY_DMS_InputReport_PROD] ${reportdate} "
 String message="<h3>SKY DMS Input Report</h3> Production Environment<br/>Datum: ${reportdate}<br/>"
 String recipients="CSOperationsControl@sky.de, Robert.Koch@sky.de, Dorina.Krause@sky.de, gregor.meinusch@nttdata.com, heino.kappher@sky.de"; 
 //String recipients="gregor.meinusch@sky.de, gregor.meinusch@nttdata.com";
 try{
   cxdb = Sql.newInstance('jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:CONPRODN','ityx_cx', 'Sk3i12Pw', 'oracle.jdbc.driver.OracleDriver') //ggf. oracle.jdbc.OracleDriver
   sql="select to_char(trunc(created, 'HH24'), 'HH24:MI') Eingangsdatum, sum(case doctype when 'EMAIL' then 1 else 0 end) Emails, sum(case doctype when 'BRIEF' then 1 else 0 end) Briefe, sum(case doctype when 'FAX' then 1 else 0 end) FAX, count(*) Gesamt from ntt_cx_report r where  currentdocpool in (600,601,602) and step='END'  and created between "+sql_timeconstraint +" and trunc(sysdate, 'HH24')  group by rollup (trunc(created, 'HH24'))  order by 1 desc";          
   message+="<style type=\"text/css\">table {border:thin solid gray;border-collapse: collapse;}  tr {border:1px solid gray;} th {font-weight:bold; cellspacing:10px; cellpadding:10px; margin:10px; border:1px solid gray;} td{text-align:right;border:1px solid gray; cellspacing:10px; cellpadding:10px; margin:10px;}  td.sum{background-color:lightgray} </style>"
   message+="<table><tr ><th style=\"font-weight:bold\">Datum</th><th>Input Emails</th><th>Input Briefe</th><th>Input Fax</th><th>Input Gesamt</th></tr>"
   cxdb.eachRow(sql) { row ->
    if (row.Eingangsdatum){    
     message+="<tr><td>${row.Eingangsdatum}</td><td>${row.Emails}</td><td>${row.briefe}</td><td>${row.fax}</td><td class=\"sum\">${row.Gesamt}</td></tr>"     
    } else {
     message+="<tr><td class=\"sum\">Gesamt</td><td class=\"sum\">${row.Emails}</td><td class=\"sum\">${row.briefe}</td><td class=\"sum\">${row.fax}</td><td class=\"sum\">${row.Gesamt}</td></tr>"     
    }
   }
   message+="</table>" 
 }catch (java.sql.SQLException e){
   message+="\n\r Problem during execution of InputReport \n\r"+e.message
 } finally {
    if (cxdb)  cxdb.connection.close()  
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