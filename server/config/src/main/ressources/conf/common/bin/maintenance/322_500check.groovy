import de.cirquent.sky.ityx.common.TagMatchDefinitions
import de.ityx.base.DBConnection
import de.ityx.contex.dbo.designer.Designer_documentpool
import de.ityx.contex.dbo.designer.Designer_documentpooldata
import de.ityx.contex.impl.document.CDocumentContainer
import de.ityx.contex.interfaces.document.CDocument
import de.ityx.contex.interfaces.extag.TagMatch
import de.ityx.contexdesigner.utils.DBODocumentPool
import groovy.sql.Sql

import java.sql.Connection

//def mxdb = Sql.newInstance('jdbc:oracle:thin:@s-ng-pdmsx.premiere.de:1521/MEXPRODAPP.sky.de','ITYX_MX', 'Sk3i13Pw', 'oracle.jdbc.driver.OracleDriver') //ggf. oracle.jdbc.OracleDriver
//def cxdb = Sql.newInstance('jdbc:oracle:thin:@s-ng-pdmsx.premiere.de:1521/CONPRODAPP.sky.de','ityx_cx', 'Sk3i12Pw', 'oracle.jdbc.driver.OracleDriver') //ggf. oracle.jdbc.OracleDriver

def mxdb = Sql.newInstance('jdbc:oracle:thin:@s-ng-ddmsx1.premiere.de:1521/MEXTST02','ITYX_MX', 'Sk3i13Pw', 'oracle.jdbc.driver.OracleDriver') //ggf. oracle.jdbc.OracleDriver
def cxdb = Sql.newInstance('jdbc:oracle:thin:@s-ng-ddmsx1.premiere.de:1521/CONTST02','ityx_cx', 'Sk3i12Pw', 'oracle.jdbc.driver.OracleDriver') //ggf. oracle.jdbc.OracleDriver

def sql = "select id docpoolid, collectionid ityxdocumentid, ityxtimetodate(createtime) createtime, parameter docpoolparam, comment_text commenttext from cxdsg_cdocpool where  (( (parameter like '500_CRM_Activity%' and status in (16)) or (parameter like '550_CRM_Callback_%' and status in (0,16)) ) or ((parameter like '6%' ) and status in (16)))  and ((locktime between 1 and datetoityxtime( sysdate - interval '90' Minute)) or (createtime between 1 and datetoityxtime( sysdate - interval '30' Minute ))) and processid=0 and prio < 10 order by docpoolid"

DBConnection dbConnection
Connection connection

try{
System.setProperty("user.home","C:\\mediatrix\\conf\\server");
dbConnection = new DBConnection();
connection = dbConnection.getConnection();
def metacon = API.getServerAPI().getMetaInformationAPI()
DBODocumentPool y=new DBODocumentPool();
def datum=String.format('%tY%<tm%<td_%<tH%<tM',new java.util.Date())
def dlist = []
//File file = new File("D:/tmp/groovySKY/groovy-2.0.1/scripts/out/maintenace_check_550_lostTags_${datum}.sql");
File file = new File("D:/tmp/groovySKY/groovy-2.0.1/scripts/out/maintenace_check_550_lostTags_lost_${datum}.sql");
int i=0;
int recovered=0;
int lostdata=0;
cxdb.eachRow(sql) { row ->
        docpoolid=(int) row.docpoolid.intValueExact()
        //println docpoolid
        Designer_documentpool d = y.getDocById(docpoolid);
        Designer_documentpooldata c = d.getData();
        ObjectInputStream bis = new ObjectInputStream(new ByteArrayInputStream(c.getBinaryObject()));
        CDocumentContainer<CDocument> docContainer = (CDocumentContainer<CDocument>) bis.readObject();
        CDocument document = docContainer.getDocument(0);
        boolean done=false;
        String msg=""
        sqlstatement=""
        String formtype=document.getFormtype();
        //println formtype
        
        String ityxdocid = row.ityxdocumentid
        if (!ityxdocid){
             ityxdocid=document.getNote('DocumentID')
        }
        if(formtype!=null&&formtype.length()>0&&!formtype.equals("systemdefault")&&!formtype.equals("unclassified")){
          for(TagMatch tm:document.getTags()) {
            String identifier = tm.getIdentifier();
            String val = tm.getTagValue();

            if(identifier.equals(TagMatchDefinitions.CUSTOMER_ID) && val!=null && val.length()>0) {
                done=true
                msg=" CustomerData from document:"+ formtype+", "+val
                sqlstatement="update cxdsg_cdocpool set status=0, parameter='500_CRM_Activity', collectionid=${ityxdocid},  comment_text='Retry Siebel call "+datum+"' , prio=(prio+1) where id=${row.docpoolid} and parameter='"+row.docpoolparam+"' and status in (0,16)" //; -- ${row.ityxdocumentid} \r\n"
                recovered++
            } 
          }  
        }
     
        if(!done) {
           //println 'docid:'+ityxdocid
        
           def String like_exp="%${ityxdocid}%"
           def sql_log_mi="select parameter from mitarbeiterlog where aktion=28 and parameter like ${like_exp} and frageid>1770000 order by zeit desc"
           
          // println 'sql:'+sql_log_mi
           def k=0
           def thisok=false
           mxdb.eachRow(sql_log_mi) { updaterow ->
               done=true
               def comment=updaterow.parameter;
               if (!thisok){
                   msg=" CustomerData from MILog: "+comment;
                   sqlstatement="update cxdsg_cdocpool set status=0, parameter='691_Recovery_ManuelleIndizierung', collectionid=${ityxdocid},  comment_text=${comment} , prio=(prio+1) where id=${row.docpoolid} and parameter=${row.docpoolparam} and status in (0,16)" //-- ${row.ityxdocumentid} \r\n"
                   recovered++
                   thisok=true
                  // println 'sql2: '+sqlstatement
               }
           }
          }
           
         if(!done) {
             msg=" LOST CustomerData"
             sqlstatement="update cxdsg_cdocpool set status=0, parameter='300_Classification', collectionid=${ityxdocid}, prio=(prio+1) where id=${row.docpoolid} and parameter=${row.docpoolparam} and status in (0,16)"
             lostdata++ 
             file << ( '--'+ msg+' \r\n' )
             file << ( sqlstatement+'\n\r' )
     }else{
            cxdb.execute sqlstatement
         cxdb.commit()
   }
       println(i+":"+docpoolid+":"+msg+sqlstatement);
        i++
     }

println "processed: ${i} revocerred: ${recovered} lostdata:${lostdata} date: ${datum} - "+ String.format('%tY%<tm%<td_%<tH%<tM',new java.util.Date())
  
 
  
} finally {
    mxdb.connection.close()  
    cxdb.connection.close()  
    
    connection.close()
   // dbConnection.close()    
}
  
  