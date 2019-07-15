import de.cirquent.sky.ityx.common.*
import groovy.json.*
import groovy.sql.*

import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager

String recipients="gregor.meinusch@nttdata.com,mariusz.pieniazek@nttdata.com,mariusz.pieniazek@sky.de,heino.kappher@sky.de";
String recipientsEscal="gregor.meinusch@nttdata.com,gregor.meinusch@sky.de,mariusz.pieniazek@nttdata.com,mariusz.pieniazek@sky.de,heino.kappher@sky.de";
String recipientsRB="gregor.meinusch@nttdata.com,mariusz.pieniazek@nttdata.com,mariusz.pieniazek@sky.de,heino.kappher@sky.de,ingo.schwalbe@nttdata.com,dieter.lippkow@nttdata.com,bernd.brinkmeier@nttdata.com";

recipients="gregor.meinusch@nttdata.com,Heino.Kappher@sky.de,Heino.Kappher@nttdata.com,RamiroAndres.LopezPazmino@nttdata.com"

recipientsEscal="gregor.meinusch@nttdata.com,Heino.Kappher@sky.de,Heino.Kappher@nttdata.com"
recipientsRB="gregor.meinusch@nttdata.com,Heino.Kappher@sky.de,Heino.Kappher@nttdata.com"

String alerts="<style type=\"text/css\">table {border:thin solid gray;border-collapse: collapse;}  tr {border:1px solid gray;} th {font-weight:bold; cellspacing:10px; cellpadding:10px; margin:10px; border:1px solid gray;} td{border:1px solid gray; cellspacing:10px; cellpadding:10px; margin:10px;}  td.escalation{background-color:red} td.error{background-color:orange} td.warning{background-color:yellow} td.ok{} </style>"

int escalations=0
int errors=0
int warnings=0
int info=0
int ok=0
int disabled=0
def monitoringComponentList=[]

def currcheck="";
def criticalChecks="";
def errorChecks="";

try{
    monitoringComponentList.add(new DBMonitoringComponent('DmsCxDB', 'DMS Contex Datenbank Prod', 'jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:CONPRODN', 'ityx_cx', 'Sk3i12Pw', 'oracle.jdbc.driver.OracleDriver', 60))
    monitoringComponentList.add(new DBMonitoringComponent('DmsCxDBAdm', 'DMS Contex Datenbank Prod Admin', 'jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:CONPRODN', 'MEIN49', 'gmeNTTgme94', 'oracle.jdbc.driver.OracleDriver', 30))
    //monitoringComponentList.add(new DBMonitoringComponent('DmsCxDBAdmNode2', 'DMS Contex Datenbank Prod N2', 'jdbc:oracle:thin:@s-ng-pdmsx.premiere.de:1521:CONPROD2', 'MEIN49', 'gmeNTTgme94', 'oracle.jdbc.driver.OracleDriver', 30))
    monitoringComponentList.add(new DBMonitoringComponent('DmsMxDB', 'DMS Mediatrix Datenbank Prod', 'jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:MEXPRODN','ITYX_MX', 'Sk3i13Pw', 'oracle.jdbc.driver.OracleDriver', 30))
    monitoringComponentList.add(new DBMonitoringComponent('DmsMxDBAdm', 'DMS Mediatrix Datenbank Prod Admin', 'jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:MEXPRODN', 'MEIN49', 'gmeNTTgme94', 'oracle.jdbc.driver.OracleDriver', 30))
    //monitoringComponentList.add(new DBMonitoringComponent('DmsMxDBAdmNode2', 'DMS Mediatrix Datenbank Prod N2', 'jdbc:oracle:thin:@s-ng-pdmsx.premiere.de:1521:MEXPROD2', 'MEIN49', 'gmeNTTgme94', 'oracle.jdbc.driver.OracleDriver', 30))
    monitoringComponentList.add(new MonitoringComponent('FileSystemChecks', 'DMS FileSystemChecks'))
    monitoringComponentList.add(new DBMonitoringComponent('DmsMxAgMonDB', 'DMS Mediatrix AgMonStaging Datenbank', 'jdbc:oracle:thin:@s-ng-ddmsx1.premiere.de:1521:MEXTST02','ITYX_AGMON', '62aE241a', 'oracle.jdbc.driver.OracleDriver', 30))

    println "#InitializeChecks"
    new File('C:\\mediatrix\\bin\\maintenance\\').eachFileMatch(~/^hcheck_.*\.json$/) { file ->
        //def inputFile = new File("C:\\mediatrix\\bin\\maintenance\\hcheck_cx.json")
        currcheck=file.name
        def json = new JsonSlurper().parseText(file.text)
        (json.checks).each{ jc ->
           def check
           if (jc.type=='DB'){
               check=new DBMonitoringCheck(jc.id, jc.name, jc.description,  jc.thresholddimension,  jc.threshold_info, jc.threshold_warn, jc.threshold_error, jc.threshold_escal, jc.solutions, jc.sql, jc.active)              
            }else if (jc.type=='DBP'){
               check=new DBMonitoringCheckPercentage(jc.id, jc.name, jc.description,  jc.thresholddimension,  jc.threshold_info, jc.threshold_warn, jc.threshold_error, jc.threshold_escal, jc.solutions, jc.sql, jc.active)              
            } else if (jc.type=='FSC'){             
               check=new FileCountMonitoringCheck(jc.id, jc.name, jc.description,  jc.thresholddimension,  jc.threshold_info, jc.threshold_warn, jc.threshold_error, jc.threshold_escal, jc.solutions, jc.directory,jc.filter,jc.delay, jc.active)             
            } else if (jc.type=='FSS'){
               check=new FileSystemSpaceMonitoringCheck(jc.id, jc.name, jc.description,  jc.thresholddimension,  jc.threshold_info, jc.threshold_warn, jc.threshold_error, jc.threshold_escal, jc.solutions, jc.directory, jc.active)
           } else {
               println "unknown type for check ${file.name}/${jc.id}/${jc.type}/${jc.res}"
           }
           boolean resfound=false;
           monitoringComponentList.each{comp->
            if (comp.name==jc.res){
               def active=jc.active
               if (!active || active=='true' || active==1){
                    comp.addCheck(check);
                    resfound=true;
                   
                if (!resfound ){             
                    println " ResNotFound ${file.name}/${jc.id}/${jc.type}/${jc.res}"
                }else{
                    println " initialize ${file.name}/${jc.type}/${jc.res}/${jc.id} ${active}"
                }
               }else{
                println " ommiting check ${file.name}/${jc.type}/${jc.res}/${jc.id}"
               }
               
            }
           }
          
        }
     } 
    alerts+="<table><tr><th>State<br/>CheckID<br/>Threshold</th><th>Check/Problem</th></tr>";          
    monitoringComponentList.each{comp->
       println "#Executing check: ${comp.name}"
       currcheck=comp.name
       try{
           comp.executeChecks()
       }catch (Exception e){
        println e
        println e.message
        if (e.message.contains('ORA-01013') ){
            criticalChecks+=currcheck+"Timeout "    
            currcheck+=" >"+comp.currcheck+"< "            
            warnings++        
            alerts+="<tr><td class=\"warning\">Warning:<br/>${currcheck}</td><td  class=\"warning\"><b>${e.message}</td></tr>";
            alerts+="<tr><td class=\"warning\">Diagnosis:</td><td class=\"warning\">"
            alerts+="<p><b>Problem ORA-01013:</b> ORA-01013 Timeout bei der Verbindung zur Datenbank<br/>"        
            alerts+="<b>Solution A: Bei einem fehlgeschlagenen Check:</b> Performance des Checs optimieren<br/>"
            alerts+="<b>Solution B: Bei mehreren fehlgeschlagenen Checks:</b> Performance der Datenbank stimmt nicht: Bitte die DB-Verbindung �berpr�fen und bei Problemen Ticket bei DB-Team er�ffnen</p>"        
            alerts+="</td></tr>";    
        }else{
            criticalChecks+=currcheck+"CheckException "    
            currcheck+=" >"+comp.currcheck+"< "            
            escalations++
            alerts+="<tr><td class=\"escalation\">CRITICAL<br/>${currcheck}</td><td  class=\"escalation\"><b>${e.message}</td></tr>";
            alerts+="<tr><td class=\"escalation\">Diagnosis:</td><td class=\"escalation\">"
            alerts+="<p><b>Problem:</b> Probleme mit der Verbindung zur Datenbank<br/>"        
            alerts+="<b>Solution:</b>Bitte die DB-Verbindung �berpr�fen, Logs nach Timeouts/nicht Erreichbarkeit der DB pr�feb, bei Problemen Ticket bei DB-Team er�ffnen</p>"        
            alerts+="</td></tr>";
        }
       }
       alerts+=comp.generateOutput()
       escalations+=comp.escalations
       errors+=comp.errors
       warnings+=comp.warnings
       info+=comp.info
       ok+=comp.ok
       disabled+=comp.disabled
       criticalChecks+=comp.criticalIDs
     }
     alerts+="</table>"
 }catch (Exception e){
   println 'o'+e
   println 'o'+e.message
   criticalChecks+=currcheck+"ComponentException "    
   alerts+="</table><h2>CRITICAL Exception:</h2>${currcheck}:${e.message}<br/>"
   escalations++
   println alerts
 }finally{
   monitoringComponentList.each{comp->
      comp.finalize()
   }
}
def now= new GregorianCalendar();
def hour = now.get(Calendar.HOUR_OF_DAY) 
def dayofweek = now.get(Calendar.DAY_OF_WEEK) 

String subjectpreafix="[SKY_HCHK]"
String subject=""
String subjectescal=""
if (escalations > 0) {
   subjectescal= "cr:${escalations} "
}
subject="${subjectpreafix} ${subjectescal}er:${errors} wr:${warnings} ${criticalChecks}" 
def reportdate=String.format('%tY-%<tm-%<td %<tH:%<tM', new GregorianCalendar())
alerts+="<br/>Production Environment, Datum: ${reportdate}<br/>" 

//println "cr: ${escalations} er:${errors} war:${warnings} info:${info} h: ${hour} d:${dayofweek} " 

def deployFile=new File('C:/mediatrix/logs/', 'Deployment.txt')
def currStatus=new File('C:/mediatrix/logs/', 'dms_ntt_Status.txt')
def currStatusHTML=new File('C:/mediatrix/logs/', 'dms_ntt_Status.html')

def isRecovery=false
def isAlertNeeded=true
def rbhours=(hour <= 9 || hour>= 18) || (dayofweek<2 || dayofweek>6 )

def statusmsg="OK"
def weHaveAProblem=(escalations + errors +warnings > 0) 

if (currStatus.exists()){
 def oldsubject=currStatus.getText() 
 def sometimeago=new Date(System.currentTimeMillis() - 4*  60*60*1000)  //60 Minuten 
 
 if (escalations>0){
  sometimeago=new Date(System.currentTimeMillis() - 4*   15*60*1000)  //15 Minuten 
 }else if (errors>0){
  sometimeago=new Date(System.currentTimeMillis() - 4*   1*60*60*1000)  //1h Minuten 
 }else if (warnings>0){
  sometimeago=new Date(System.currentTimeMillis() - 4*  3*60*60*1000)  //3h Minuten 
 }
 
 if (rbhours){
    weHaveAProblem=escalations + errors > 0 
    sometimeago=new Date(System.currentTimeMillis() - 4* 5*60*60*1000)  //5h Minuten ausserhalb der Arbeitszeiten
    if (escalations>0){
        sometimeago=new Date(System.currentTimeMillis() - 4*  2*60*60*1000)  //2h Minuten ausserhalb der Arbeitszeiten
    }
 }
 
 if (!weHaveAProblem && !oldsubject.equals(subject)){
    isRecovery=true
    isAlertNeeded=true
    currStatus.delete()
    if (currStatusHTML.exists()){    
        currStatusHTML.delete()
    }
    statusmsg="R"
	statusmsgL="Recovery"
    println "CheckAction:recovery"    
 } else if (weHaveAProblem && !oldsubject.equals(subject)){
    isAlertNeeded=true
    currStatus.write(subject)
    currStatusHTML.write(alerts)
    statusmsg="C"
	statusmsgL="CHANGED"
 } else if (weHaveAProblem &&  sometimeago.after(new Date(currStatus.lastModified()))){
    isAlertNeeded=true
    currStatus.write(subject)
    currStatusHTML.write(alerts)
	statusmsg="E"
    statusmsgL="STILLEXISTS"
 } else {
    isAlertNeeded=false
    println("Problem still exists:" + subject) 
    currStatusHTML.write(alerts)
    statusmsg="O"
	statusmsgL="OLD"
 }
} else if (weHaveAProblem){
    currStatus.write(subject)
    currStatusHTML.write(alerts)
    isAlertNeeded=true
    statusmsg="N"
	statusmsgL="NEW"
} else {
    isAlertNeeded=false
    statusmsg="O"
	statusmsgL="OK"
}

println "currStatExists:${statusmsg}"
subject="${statusmsg} ${subjectpreafix} ${subjectescal}er:${errors} war:${warnings} info:${info} ${criticalChecks} (${statusmsgL})" 
def finalrecipients=recipients

if (isAlertNeeded){
 if (deployFile.exists() ){
    println "DEPLOYMENT (notification to Manager only): "+subject
    sendHtmlMail("gregor.meinusch@nttdata.com", subject, alerts)    
    
    def longtimeago=new Date(System.currentTimeMillis() - 4*  7*60*60*1000)  // max 7h nach deployment die Datei l�schen
    if (longtimeago.after(new Date(deployFile.lastModified()))){
        deployFile.delete()
    }
    
 } else if (rbhours){
    sendHtmlMail(recipientsRB, subject, alerts)    
 }else { // Working hours // (hour >9 && hour < 18 && dayofweek>1 && dayofweek<7 ) && (escalations>0 || errors>0 || warnings>0 || info>3 || statusmsg=="RECOVERY")
    sendHtmlMail(recipients, subject, alerts)
 }
} 


def sendTextMail(String recipients, String subject, String txtmessage){
    Session session=null
    Transport transport=null
    try {
        Properties properties = new Properties()
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
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("skydmsint@sky.de"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        message.setSubject(subject);
        message.setText(txtmessage)
   //   message.setContent(txtmessage, "text/html")
        println "sending message:" + recipients+":"+(subject)
        transport.send(message);
     } catch (MessagingException e) {
         e.printStackTrace();
     } finally {
        if (transport) transport.close()
     }
}


def sendHtmlMail(String recipients, String subject, String txtmessage){
    Session session=null
    Transport transport=null
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
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("skydmsint@sky.de"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
        message.setSubject(subject);
//     message.setText(txtmessage)
        message.setContent(txtmessage, "text/html")
        println "sending message:" + recipients+":"+(subject) //+txtmessage
        transport.send(message);
    } catch (MessagingException e) {
         e.printStackTrace();
    }
}

enum CheckStatus {
   DISABLED,
   UNKNOWN,
   OK, 
   INFO, 
   WARN,      //warning
   ERROR,     //prio2
   ESKALATION //prio1
}
  
class MonitoringCheck{
    def timestamp=new GregorianCalendar()
    def executiontime=0
    CheckStatus currentStatus=CheckStatus.UNKNOWN
    def id=""
    def name=""
    def description=""
    def threshold=0.0
    def checkmsg=""
    String thresholddimension=""
    double threshold_info=0
    double threshold_warn=0
    double threshold_error=0
    double threshold_escal=0
    def solutions
    def active
        
      
    MonitoringCheck(aid, aname, adescription, athresholddimension, athreshold_info, athreshold_warn, athreshold_error,athreshold_escal, asolutions, aactive){
     id=aid
     name=aname
     description=adescription
     thresholddimension=athresholddimension
     threshold_info=athreshold_info
     threshold_warn=athreshold_warn
     threshold_error=athreshold_error
     threshold_escal=athreshold_escal
     solutions=asolutions
     active=aactive
    }
    
    double execute(object){
        return -1
    }
        
    CheckStatus executeCheck(object){
       threshold=execute(object)
    if (threshold == -2){
        currentStatus= CheckStatus.DISABLED
    }else if (threshold < 0){
            currentStatus= CheckStatus.UNKNOWN
        }else if ( threshold >= threshold_escal){
             currentStatus= CheckStatus.ESKALATION
    }else if ( threshold >= threshold_error){
             currentStatus= CheckStatus.ERROR
    }else if ( threshold >= threshold_warn){
             currentStatus= CheckStatus.WARN
     }else if ( threshold >= threshold_info){
             currentStatus= CheckStatus.INFO
        }else {
             currentStatus= CheckStatus.OK
        }        
        return currentStatus
    }
    String generateOutput(){
        String result=""
        String cssclass=""
        boolean log=false
        if (currentStatus==CheckStatus.ESKALATION){
              cssclass='escalation'
              log=true
        }else if (currentStatus==CheckStatus.ERROR){
              cssclass='error'
              log=true
        }else if (currentStatus==CheckStatus.WARN){
              cssclass='warning'
              log=true
        }else if (currentStatus==CheckStatus.INFO){
              cssclass='ok'
          log=true
        }else if (currentStatus==CheckStatus.OK){
              cssclass='ok'              
        }
       if (log){
           result+="<tr><td class=\"${cssclass}\">${currentStatus}<br/>${id}<br/>${threshold} ${thresholddimension}</td><td class=\"${cssclass}\"><b>${name}</b> <br/> ${checkmsg}</td></tr>";
           result+="<tr><td class=\"${cssclass}\">Description:</td><td class=\"${cssclass}\">${description}</td></tr>";
           if (solutions){
               result+="<tr><td class=\"${cssclass}\">Diagnosis:</td><td  class=\"${cssclass}\">"
               int i=1
           solutions.each{sol->
                    result+="<p>" 
            if (sol.problem != null){
             result+= "<b>Problem #${i}:</b> ${sol.problem}: <br/>"
            }
            result+="<b>Solution #${i}:</b> ${sol.desc}</p>"
            i++
               }
               result+="</td></tr>";
           }
       }
       return result
    }
    
}

class DBMonitoringCheck extends MonitoringCheck{
    def sql=""
    DBMonitoringCheck(aid, aname, adescription,  athresholddimension, athreshold_ok, athreshold_info, athreshold_warn, athreshold_error, asolutions, asql, aactive){
        super(aid, aname, adescription,  athresholddimension, athreshold_ok, athreshold_info, athreshold_warn, athreshold_error, asolutions, aactive)
        sql=asql
    }
    double execute(con){
      def addtreshold =0
      con.eachRow(sql+" -- ${id} ${name}") { row ->
          addtreshold+=row.threshold
          checkmsg+=row.msg+"<br/>"   // ("+row.timestamp +")<br/>"          
      }
      return addtreshold 
    }
}

class DBMonitoringCheckPercentage extends DBMonitoringCheck{
    DBMonitoringCheckPercentage(aid, aname, adescription,  athresholddimension, athreshold_ok, athreshold_info, athreshold_warn, athreshold_error, asolutions, asql, aactive){
        super(aid, aname, adescription,  athresholddimension, athreshold_ok, athreshold_info, athreshold_warn, athreshold_error, asolutions, asql, aactive)

    }
    double execute(con){
      def addtreshold =0
        con.eachRow(sql+" -- ${id} ${name}") { row ->
          if (row.threshold>=addtreshold){
            addtreshold=row.threshold
            checkmsg=row.msg+"<br/>"   // ("+row.timestamp +")<br/>"          
          }
        }
      return addtreshold 
    }
}

class FileCountMonitoringCheck extends MonitoringCheck{
    def file=""
    def filter
    def delay=60
    FileCountMonitoringCheck(aid, aname, adescription,  athresholddimension, athreshold_ok, athreshold_info, athreshold_warn, athreshold_error, asolutions, afile, afilter,adelay, aactive){
        super(aid, aname, adescription,  athresholddimension, athreshold_ok, athreshold_info, athreshold_warn, athreshold_error, asolutions, aactive)
        file=afile
        delay= adelay

        if (afilter && afilter=="zip"){
            filter=/(?i).*?\.zip/
        }else if (afilter && afilter=="tif"){
            filter=/(?i).*?\.tiff?/
        }        
    }
    double execute(object){
      def addtreshold =0
      if (active==null || active=="true"){ 
       def quarterago=new Date(System.currentTimeMillis() - (delay*60*1000))  
       def dir=new File(file)
       if (dir && dir.exists()){
         def flist=(dir.listFiles(
                [accept:{f->(f && f.isFile() && f ==~ filter && quarterago.after(new Date(f.lastModified())))}] as FileFilter
            ))
         if(flist){
            addtreshold=flist.size()
         }
         checkmsg=" ${addtreshold} files older than ${delay}min"
       }else{
          addtreshold=49
          checkmsg="Problem accessing directory: ${file}"
       }
       
       return addtreshold        
      }
    }
}


class FileSystemSpaceMonitoringCheck extends MonitoringCheck{
    def file=""    
    FileSystemSpaceMonitoringCheck(aid, aname, adescription,  athresholddimension, athreshold_ok, athreshold_info, athreshold_warn, athreshold_error, asolutions, afile, aactive){
        super(aid, aname, adescription,  athresholddimension, athreshold_ok, athreshold_info, athreshold_warn, athreshold_error, asolutions, aactive)
        file=afile
    }
    double execute(object){
      def addtreshold =200
      if (active==null || active=="true"){ 
       def f1=new File(file)
       def freespace=f1.getFreeSpace()
       def totalspace=f1.getTotalSpace()
       
       if (freespace && freespace>0 && totalspace && totalspace>0){
            addtreshold = ((int) (((totalspace-freespace)/totalspace)*100))
            checkmsg=" ${addtreshold}% used space on filesystem ${file}"
       }else{
            checkmsg="FS nicht zug�nglich.  ${addtreshold}% used space on filesystem ${file}"
       }
       
       return addtreshold 
      }
    }
}


class MonitoringComponent {
    String name=""
    String description=""
    def checks = []
    
    def escalations=0
    def errors=0
    def warnings=0
    def info=0
    def ok=0
    def disabled=0
    def criticalIDs=""
    def currcheck=""
       
    MonitoringComponent(aname, adescription){
        this.name=aname
        this.description=adescription
    }
    def getRessourceRef(){
        //println "not implemented"
        return null
    }
    def executeChecks(){
        escalations=0
        errors=0
        warnings=0
        ok=0
        disabled=0
        
       
        checks.each{ checkitem ->
       currcheck=checkitem.id
           checkitem.executeCheck(getRessourceRef())
        println " "+ checkitem.currentStatus + ":" + checkitem.id + ":" + checkitem.threshold
           switch (checkitem.currentStatus){
            case CheckStatus.ESKALATION:
              escalations++
          criticalIDs+=checkitem.id+" "
          break
            case CheckStatus.ERROR:
              errors++
          criticalIDs+=checkitem.id+" "
          break
            case CheckStatus.WARN:
              warnings++
              criticalIDs+=checkitem.id+" "
              break
            case CheckStatus.INFO:
              info++
              break
            case CheckStatus.OK:
              ok++
              break
        case CheckStatus.DISABLED:
              disabled++
          break
            }
        }
    }    
    def addCheck(MonitoringCheck check){
      checks.add(check)
    }    
    String generateOutput(){
        String result=''
        checks.each{ checkitem ->
           result+=checkitem.generateOutput()
        }
        return result
    }
}
  
class DBMonitoringComponent extends MonitoringComponent {
  String dbConnString
  String  dbUser
  String  dbPass 
  String  dbDriver
  int queryTimeout
  def dbcon //private
  
  DBMonitoringComponent(aname, adescription, String adbConnString, String  adbUser, String adbPass, String  adbDriver,  int aqueryTimeout){
    super(aname, adescription)
    dbConnString= adbConnString
    dbUser=adbUser
    dbPass=adbPass
    dbDriver=adbDriver
    queryTimeout=aqueryTimeout
  } 
  
  def getRessourceRef(){
    if (dbcon ){    
        return dbcon
    } else {
       //mit timeout in [S] f�r login
        System.setProperty("user.language","en");
        Locale.setDefault(Locale.ENGLISH);
        Class.forName(dbDriver);
       // DriverManager.setLoginTimeout((int) (queryTimeout/2));        
        DriverManager.setLoginTimeout((int) (10));        
        Connection con=DriverManager.getConnection(dbConnString,dbUser, dbPass);    
        dbcon = new Sql(con)

        //standard
        //dbcon = Sql.newInstance(dbConnString,dbUser, dbPass,dbDriver) //ggf. oracle.jdbc.OracleDriver
        dbcon.withStatement { 
           stmt -> stmt.queryTimeout = queryTimeout 
        }        
    }
    return dbcon  
  }
  void finalize() { 
   if (dbcon) {
        dbcon.connection.close()
       // println "Verbindung ${name} aufger�umt" 
   }
  } 
}