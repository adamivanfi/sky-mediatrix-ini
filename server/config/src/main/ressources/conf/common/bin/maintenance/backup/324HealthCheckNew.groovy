import de.cirquent.sky.ityx.common.*
import groovy.sql.Sql

Session session=null
Transport transport=null

//String recipients="gregor.meinusch@nttdata.com,dmitry.ibikus@nttdata.com,heino.kappher@sky.de"; // nicht im Urlaub :-) Heino.Kappher@nttdata.com
String recipients="gregor.meinusch@sky.de";
def ctxdba, ctxdb, mxdb

def queryTimeout=50;
String alerts=""

boolean critical=false;
int escalations=0
int errors=0
int warnings=0
int checks=0
def dbi=""

def sql = "select '001 DB-Problem' checkname, to_char(originating_timestamp,'DD.MM.RR HH24:MI:SS') statusdate, message_text description,  '3_ESCALACTION' currentstatus from sys.alertlog s where originating_timestamp > sysdate - interval '16' MINUTE  and (message_text like 'Starting ORACLE instance%'   or message_text like 'Global Enqueue Services Deadlock detected%' or message_text like 'ORA-%' or message_text like 'NOTE: ASMB terminating' or message_text like '%rror%' or message_text like 'Oracle Database%' or s.message_text like '%starting up%'  or s.message_text like '%ALTER%' or message_text like '%started%')  order by currentstatus desc, checkname, statusdate desc"
println  execDBScript('jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:MEXPRODN','MEIN49', 'gmeNTTgme94', 'oracle.jdbc.driver.OracleDriver', 'Mediatrix_Admin', sql) 
alerts += execDBScript('jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:CONPRODN','MEIN49', 'gmeNTTgme94', 'oracle.jdbc.driver.OracleDriver', 'Contex_Admin', sql) 

sql=          
" select sysdate statusdate, "+
"   '101 ctxwflproc 110_ImportLetter' checkname, "+
"   'Import of LetterContainerFailed' description, "+
"   nvl(max(count_wait),0) anzahl, "+
"   CASE "+
"     when nvl(max(count_wait),0) > 3 "+
"     then '3_ESCALACTION' "+
"     when nvl(max(count_wait),0) > 2 "+
"     then '2_ERROR' "+
"     when nvl(max(count_wait),0) > 0 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from ityx_cx.ntt_kpi_docpool "+
" where (sysdate - interval '16' MINUTE) < TIME "+
" and parameter                          ='110_ImportError_' "+
"  "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '102 ctxwflproc 210_Preprocessing_Mail' checkname, "+
"    'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 750 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 500 "+
"     then '2_ERROR' "+
"     when count(*) > 50 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='210_Preprocessing_Mail' "+
" group by parameter "+
"  "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '103 ctxwflproc 220_Preprocessing_OCR' checkname, "+
"   'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 3500 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 2200 "+
"     then '2_ERROR' "+
"     when count(*) > 1600 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='220_Preprocessing_OCR' "+
" group by parameter "+
"  "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '104 ctxwflproc 300_Classification' checkname, "+
"   'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 1000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 700 "+
"     then '2_ERROR' "+
"     when count(*) > 300 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter like '300_Classification%' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '105 ctxwflproc 400_CustomerIndexing' checkname, "+
"   'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 1500 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 500 "+
"     then '2_ERROR' "+
"     when count(*) > 300 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter like '400_CustomerIndexing%' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '106 ctxwflproc 500_CRM_Activity' checkname, "+
"   'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 1000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 500 "+
"     then '2_ERROR' "+
"     when count(*) > 200 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter like '500_CRM_Activity%' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '107 ctxwflproc 550_CRM_Callback' checkname, "+
"    'Process: 550_CRM_Callback' "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 1000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 800 "+
"     then '2_ERROR' "+
"     when count(*) > 550 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter like '550_%' "+
" group by status "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '108 ctxwflproc 600_MX_Injection' checkname, "+
"    'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 600 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 500 "+
"     then '2_ERROR' "+
"     when count(*) > 350 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter like '600_MX_Injection%' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '109 ctxwflproc 66X_MX_ManualIndexing' checkname, "+
"    'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 3000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 2000 "+
"     then '2_ERROR' "+
"     when count(*) > 900 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='662_MX_ManualIndexing' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '110 ctxwflproc WFL_GlobalBacklog' checkname, "+
"   'Global Documentbacklog exceeded threshold' || count(*) description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 5000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 3500 "+
"     then '2_ERROR' "+
"     when count(*) > 2500 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 and parameter not like 'SL3K_%' "+
" group by status "+
"  "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '111 ctxwflproc 670_MX_ManualIndexing_CRM_Data' checkname, "+
"     'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 700 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 500 "+
"     then '2_ERROR' "+
"     when count(*) > 300 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='670_MX_ManualIndexing_CRM_Data' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '112 ctxwflproc 810_Archiv' checkname, "+
"    'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 3000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 2000 "+
"     then '2_ERROR' "+
"     when count(*) > 900 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='810_Archiv' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '113 ctxwflproc 812_Archiv' checkname, "+
"      'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 3000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 2000 "+
"     then '2_ERROR' "+
"     when count(*) > 900 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='812_MoveFileToArchive' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '114 ctxwflproc 820_Outbound' checkname, "+
"      'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 3000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 2000 "+
"     then '2_ERROR' "+
"     when count(*) > 900 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='820_Outbound' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '115 ctxwflproc 830_Associate' checkname, "+
"    'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 3000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 2000 "+
"     then '2_ERROR' "+
"     when count(*) > 900 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='830_Associate' "+
" group by parameter "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '116 ctxwflproc WFL_Errors' checkname, "+
"   'Error documents threshold:' "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 1000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 500 "+
"     then '2_ERROR' "+
"     when count(*) > 400 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where status=16 "+
"  "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '117 ctxwflproc WFL_ProcessesToSlow' checkname, "+
"   'Processperformance: ' "+
"   ||parameter "+
"   || ' processed:' "+
"   || max( count_interval) "+
"   || ' wait:' "+
"   ||max(count_wait) description, "+
"   round(nvl(100-(round(sum(count_interval)*100/sum(count_wait))),'1'),0) anzahl, "+
"   CASE "+
"     when nvl(100-(round(sum(count_interval)*100/sum(count_wait))),'1') > 95 "+
"     then '3_ESCALACTION' "+
"     when nvl(100-(round(sum(count_interval)*100/sum(count_wait))),'1')>90 "+
"     then '2_ERROR' "+
"     when nvl(100-(round(sum(count_interval)*100/sum(count_wait))),'1')>80 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from ntt_kpi_docpool "+
" where (sysdate - interval '16' MINUTE) < TIME "+
" and count_wait                         >100 "+
" and parameter not like '550%' and parameter not like '220%' and parameter not like '810%' and parameter not like 'SL3K_%' and parameter not like 'Move_%' and parameter not like 'Archiv' "+
" group by parameter "+
"  "+
" union  "+
"  "+
" select sysdate timestamp, "+
"   '118 ctxwflproc WFL_ProcessesToSlow' checkname, "+
"   'Processperformance: ' "+
"   ||parameter "+
"   || ' processed:' "+
"   || max( count_interval) "+
"   || ' wait:' "+
"   ||max(count_wait) description, "+
"   round(nvl(100-(round(sum(count_interval)*100/sum(count_wait))),'1'),0) anzahl, "+
"   CASE "+
"     when nvl(100-(round(sum(count_interval)*100/sum(count_wait))),'1') > 95 "+
"     then '3_ESCALACTION' "+
"     when nvl(100-(round(sum(count_interval)*100/sum(count_wait))),'1')>90 "+
"     then '2_ERROR' "+
"     when nvl(100-(round(sum(count_interval)*100/sum(count_wait))),'1')>40 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from ntt_kpi_docpool "+
" where (sysdate - interval '16' MINUTE) < TIME "+
" and count_wait                         >350 "+
" and parameter  like '550%' "+
" group by parameter "+
"  "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '119 ctxwflproc 66X_ManuallIndexing_MxCxDiff' checkname, "+
"   'ManuallIndexing could not wake up CTX Processes' description, "+
"   abs(ctx_manuelle_indizierung.counter - mx_manuelle_indizierung.counter) anzahl , "+
"   CASE "+
"     when abs(ctx_manuelle_indizierung.counter - mx_manuelle_indizierung.counter) > 50 "+
"     then '3_ESCALACTION' "+
"     when abs(ctx_manuelle_indizierung.counter - mx_manuelle_indizierung.counter) > 25 "+
"     then '2_ERROR' "+
"     when abs(ctx_manuelle_indizierung.counter - mx_manuelle_indizierung.counter) > 6 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from "+
"   (select count(*) counter "+
"   from ityx_cx.cxdsg_cdocpool "+
"   where parameter like '66%' "+
"   and status=3 "+
"   ) ctx_manuelle_indizierung, "+
"   (select count(*) counter "+
"   from frage@MEXPROD.SKY.DE "+
"   where teilprojektid=1125 "+
"   and status        <> 'erledigt' "+
"   and geloeschtam    =0 "+
"   ) mx_manuelle_indizierung "+
"  "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '120 fuzzydb customer index' checkname, "+
"   'Synchronisation between NewDB and CTX-FuzzyDB NEWDB_CUSTOMER older than one day' description, "+
"   round (sysdate - max(operation_date),0) anzahl, "+
"   CASE "+
"     when sysdate - max(operation_date) > 3 "+
"     then '3_ESCALACTION' "+
"     when sysdate - max(operation_date) > 2 "+
"     then '2_ERROR' "+
"     when sysdate - max(operation_date) > 1 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from newdb_customer "+
"   "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '121 fuzzydb contract index' checkname, "+
"   'Synchronisation between NewDB and CTX-FuzzyDB NEWDB_CONTRACT older than one day' description, "+
"   round (sysdate - max(operation_date),0) anzahl, "+
"   CASE "+
"     when sysdate - max(operation_date) > 3 "+
"     then '3_ESCALACTION' "+
"     when sysdate - max(operation_date) > 2 "+
"     then '2_ERROR' "+
"     when sysdate - max(operation_date) > 1 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from newdb_contract "+
"   "+" union "+
"  "+
" select sysdate timestamp, "+
"   '122 fuzzydb asset index' checkname, "+
"   'Synchronisation between NewDB and CTX-FuzzyDB NEWDB_ASSET older than one day' description, "+
"   round (sysdate - max(operation_date),0) anzahl, "+
"   CASE "+
"     when sysdate - max(operation_date) > 3 "+
"     then '3_ESCALACTION' "+
"     when sysdate - max(operation_date) > 2 "+
"     then '2_ERROR' "+
"     when sysdate - max(operation_date) > 1 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from newdb_asset "+
"   "+
" union "+
"  "+
" select sysdate timestamp, "+
"   '124 fuzzydb campaign index' checkname, "+
"   'Synchronisation between NewDB and CTX-FuzzyDB NEWDB_CAMPAIGN older than some days' description, "+
"   round (sysdate - max(operation_date),0) anzahl, "+
"   CASE "+
"     when sysdate - max(operation_date) > 6 "+
"     then '3_ESCALACTION' "+
"     when sysdate - max(operation_date) > 4 "+
"     then '2_ERROR' "+
"     when sysdate - max(operation_date) > 2 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from newdb_campaign "+
"  "+
" union "+
"  "+
" select sysdate ts, "+
"    '125 KPI-CTX-Scheduler is Running, check of technical Account: aoscityx@PFAD.biz' checkname, "+
"     'LastRun: '||  max(time)  || ' checks in 16min: ' || count(*)  description, "+
"      count(*) anzahl, "+
"    CASE "+
"      when count(*) < 1 "+
"      then '1_WARNING' "+
"      else '0_OK' "+
"    END currentstatus "+
"  from ntt_kpi_docpool "+
"  where time > (sysdate - interval '17' minute) "+

" union "+
"  "+
/* " select sysdate, "+
"   '119 ctxwflproc to old documents in WFL' checkname, "+
"   'Documents in WFL longer than 1 day: ' "+
"   ||parameter "+
"   ||' Status:' "+
"   || status "+
"   ||' oldestdoc:' "+
"   ||ityxtimetodate(min(createtime)) "+
"   ||' mindocpoolid:' "+
"   || min(id) description, "+
"   nvl(count(*),0) anzahl, "+
"   CASE "+
"     when nvl(count(*),0) > 50 "+
"     then '3_ESCALACTION' "+
"     when nvl(count(*),0) > 20 "+
"     then '2_ERROR' "+
"     when nvl(count(*),0) > 0 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from ityx_cx.cxdsg_cdocpool "+
" where status  <>32 "+
" and createTIME < datetoityxtime(sysdate - interval '60' hour) "+
" group by parameter, "+
"   status "+
"  "+
" union "+
*/
"  "+
" select sysdate timestamp, "+
"   '201 mxmaildaemon MailDaemon_MXMailReturn' checkname, "+
"     'Process: '|| parameter "+
"   || ' Waits: ' || count(*)  description, "+
"   count(*) anzahl, "+
"   CASE "+
"     when count(*) > 3000 "+
"     then '3_ESCALACTION' "+
"     when count(*) > 2000 "+
"     then '2_ERROR' "+
"     when count(*) > 900 "+
"     then '1_WARNING' "+
"     else '0_OK' "+
"   END currentstatus "+
" from cxdsg_cdocpool "+
" where createtime > datetoityxtime (sysdate - interval '16' MINUTE)  "+
" and status=0 "+
" and parameter                          ='MXMailReturn' "+
" group by parameter "+
"  " + 
" union "+
" select sysdate statusdate,   "+
                 "'202 actual docpool running docs' checkname,   "+
                 "'Process:  '||parameter ||' ' || ' Count: ' || nvl(count(*),0)  description,   "+
                 "nvl(count(*),0) anzahl,  "+
                 "CASE   "+
                   "when nvl(count(*),0) > 10   "+
                   "then '3_ESCALACTION'   "+
                   "when nvl(count(*),0) > 2   "+
                   "then '2_ERROR'   "+
                   "when nvl(count(*),0) > 0   "+
                   "then '1_WARNING'   "+
                   "else '0_OK'   "+
                 "END currentstatus   "+
               "from ityx_cx.cxdsg_cdocpool   "+
               "where createtime <= datetoityxtime(sysdate - interval '24' hour) and parameter not in ('662_MX_ManualIndexing', '810_Archiv','SL3K_401_CustomerExtraction', 'SL3K_681_ManualIndexing_HandleMX', 'SL3K_221_OCR' ) "+
                    "and status=1 "+
              "group by parameter "+
" union "+ 
            " select sysdate statusdate,  "+
             "'203 - Actual docpool kpi entry exists' checkname,  "+
             "'Actual docpool kpi entry exist' description,  "+
             "nvl(count(*),0) anzahl, "+
             "CASE  "+
               "when nvl(count(*),0) <= 0  "+
               "then '3_ESCALACTION'  "+
               "else '0_OK'  "+
             "END currentstatus  "+
           "from ityx_cx.ntt_kpi_docpool  "+
           "where time >= to_date(sysdate - interval '16' minute) " + 
" union "+
" select sysdate statusdate,   "+
             "'205 - Actual error docs in docpool' checkname,   "+
             "'Actual error docs in docpool' description,   "+
             "nvl(count(*),0) anzahl,  "+
             "CASE   "+
               "when nvl(count(*),0) > 1000   "+
               "then '3_ESCALACTION'   "+
               "when count(*) > 500  "+
               "then '2_ERROR' "+
               "when count(*) > 400 "+
               "then '1_WARNING' "+
               "else '0_OK'                  "+
             "END currentstatus   "+
           "from ityx_cx.cxdsg_cdocpool "+
           "where status=16 and deletetime=0 "+           
" union "+
"  select sysdate statusdate,    "+
             "'206 - Actual incoming mails processed' checkname,    "+
             "'Actual incoming emails in docpool 210: ' description,    "+
             "NVL(SUM(count_interval),0)  anzahl,   "+
             "CASE    "+
               "when NVL(SUM(count_interval),1) = 0  "+
               "then '3_ESCALACTION'    "+
               "else '0_OK'                   "+
             "END currentstatus    "+
           "from ityx_cx.ntt_kpi_docpool  "+
           "where parameter = '210_Preprocessing_Mail' "+
"  AND TIME   >= (sysdate - interval '16' minute) "+
"  and to_char(to_timestamp(time),'hh24:mi:ss.FF3') between  '06:00:00.000' and '23:59:59.999' " + 
" union " + 
" select sysdate statusdate,     "+
              "'207 - Check old docpool locks' checkname,     "+
              "'Actual old docpool locks count: ' description,     "+
              "NVL(count(*),0)  anzahl,    "+
              "CASE     "+
                "when NVL(count(*),0) > 0   "+
                "then '2_ERROR'     "+
                "else '0_OK'                    "+
              "END currentstatus     "+
            "from ityx_cx.cxdsg_cdocpool   "+
            "where locking    IS NOT NULL "+
            " AND DECODE( NVL(locktime,datetoityxtime(sysdate)), 0, datetoityxtime(sysdate)) <= datetoityxtime(sysdate - interval '3' hour) " + 
" union " + 
" select sysdate statusdate,     "+
              "'208 - Check docpool has no empty parameter docs' checkname,     "+
              "'Actual empty parameter docs in docpool: ' description,     "+
              "NVL(count(*),0)  anzahl,    "+
              "CASE     "+
                "when NVL(count(*),0) > 0   "+
                "then '2_ERROR'     "+
                "else '0_OK'                    "+
              "END currentstatus     "+
            "from ityx_cx.cxdsg_cdocpool   "+
            "where parameter is null or parameter in (' ', '0', '')"
         
/*
" union "+
"   select sysdate statusdate,     "+
              "'207 - No old newdb staging entries' checkname,     "+
              "'Old staging entries count:' description,     "+
              "nvl(oldEntries,0) anzahl, "
              "CASE     "+
                "when nvl(oldEntries,0) > 0     "+
                "then '3_ESCALACTION'     "+
                "else '0_OK'                    "+
              "END currentstatus     "+
            "from  "+
    "(select count(*) oldEntries from newdb_customer_stage where operation_date  <= (operation_date - interval '24' hour) "+
    "union  "+
    "select count(*) oldEntries from newdb_contract_stage where operation_date  <= (operation_date - interval '24' hour) "+
    "union  "+
    "select count(*) oldEntries from newdb_campaign_stage where operation_date  <= (operation_date - interval '24' hour) ) "+     
*/    
//-- SQL TAIL
"order by currentstatus desc, checkname "

alerts+= execDBScript('jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:CONPRODN', 'ityx_cx', 'Sk3i12Pw', 'oracle.jdbc.driver.OracleDriver', 'Contex', sql) //ggf. oracle.jdbc.OracleDriver


sql="select sysdate statusdate, "+
"   '202 Check FRAGE - TeilprojektID 0 OR in Default TP ' checkname,  "+
"    'anzahl: ' || count(*)  description,  "+
"     count(*) anzahl,  "+
"   CASE  "+
"     when count(*) > 1  "+
"     then '1_WARNING' "+
"     else '0_OK'  "+
"   END currentstatus  "+
" from frage f "+
" where teilprojektid in (0,110) and geloeschtam=0 and gesperrtam=0 and status != 'erledigt' "+
" and exists (select e.id from email e where e.id=f.emailid and ityxtimetodate(emaildate) between sysdate-7 and sysdate-2) " +

" union "+

"      select sysdate statusdate,  "+
         "'203 Check actual entries in AR_MONITORING ' checkname,   "+
          "'Last 16 mins ar_mon count: ' || count(*)  description,   "+
           "count(*) anzahl,   "+
         "CASE   "+
           "when count(*) = 0   "+
           "then '2_ERROR'  "+
           "else '0_OK'   "+
         "END currentstatus   "+
       "from ar_monitoring "+
       "where datum >= datetoityxtime(sysdate - interval '22' minute) " + 
       
" union "+

"   select sysdate statusdate,  "+
     "'204 Check actual outgoing (answer) documents are completed' checkname,   "+
      "'anzahl completet outgoing docs: ' || count(*)  description,   "+
       "count(*) anzahl,   "+
     "CASE   "+
       "when count(*) = 0 "+
       "then '2_ERROR'  "+
       "when count(*) < 10  "+
       "then '1_WARNING' "+
       "else '0_OK'   "+
     "END currentstatus   "+
   "from antwort "+
   "where sendtime >= datetoityxtime(sysdate - interval '6' hour) and status in ('erledigt') " +

" union "+

"   select sysdate statusdate,  "+
           "'205 Check Duplicates (by subject) in TP Default' checkname,   "+
            "'Found duplicate count: ' || count(*)  description,   "+
            "count(*) anzahl,   "+
           "CASE   "+
             "when count(*) > 0 "+
             "then '2_ERROR'  "+
             "else '0_OK'   "+
           "END currentstatus   "+
    " from (select e.subject,e.sender, count(*) from ityx_mx.frage f, ityx_mx.email e where f.emailid = e.id and f.teilprojektid=110 and f.status not in ('erledigt','blockiert') and f.geloeschtam=0 group by e.subject,e.sender having count(*) > 1) " +

" union " + 
"   select sysdate statusdate,  "+
           "'206 Check MX KPI Actual entries' checkname,   "+
            "'Last interval (16 mins) count: ' || count(*)  description,   "+
             "count(*) anzahl,   "+
           "CASE   "+
             "when count(*) < 1 "+
             "then '2_ERROR'  "+
             "else '0_OK'   "+
           "END currentstatus   "+
         " from ntt_kpi_overview  "+
         " where ctimestamp >= to_date(sysdate - interval '16' minute) " +
         
" union " + 
" select sysdate statusdate,    "+
             "'207 Check MI Docs outside of MI TP' checkname,     "+
             "'Actual count MI Docs not in MI TP: ' || count(*)  description,     "+
             "count(*) anzahl,     "+
            "CASE     "+
              "when count(*) > 1   "+
              "then '2_ERROR'    "+
              "else '0_OK'     "+
            "END currentstatus     "+
         "from frage    "+
           "where status           = 'hold_klassifikation' "+
              "AND teilprojektid not in ( 1125, 16711)"+
              "AND geloeschtam    = 0 "   +
              
" union " +

" select sysdate statusdate,    "+
             "'208 Check that no projekt is locked for more than 5 min' checkname,     "+
             "'Actual old project lock count:  ' || count(*)  description,     "+
             "count(*) anzahl,     "+
            "CASE     "+
              "when count(*) >= 1   "+
              "then '3_ESCALACTION'    "+
              "else '0_OK'     "+
            "END currentstatus     "+
    " from projekt where gesperrtvon != 0  or ( gesperrtam != 0 and gesperrtam<=datetoityxtime(sysdate - interval '5' minute) ) "  
          
/* // Check MX/CX Formtype consistence (same formtypes defined) beforce activation 
   + " union " +
    " select sysdate statusdate,   "+
          "'208 Check MX/CX Formtype consistence ' checkname,    "+
            "'Actual formtype diff count: ' || count(*)  description,    "+
             "count(*) anzahl,    "+
           "CASE    "+
             "when count(*) > 0  "+
             "then '2_ERROR'   "+
             "else '0_OK'    "+
           "END currentstatus    "+
         "from  "+
"(select substr(name, 17) from schlagwort where name like '[ROOT, FormType%' and geloeschtam = 0 and projektid=110 and substr(name, 17) not in ('unclassified') "+
      "minus "+
" select name from CXADM_TNS@CONPROD.sky.de cx where cx.subcat = 4 order by 1) "  

*/
alerts+= execDBScript('jdbc:oracle:thin:@s-ng-pdmsx3-b.premiere.de:1521:MEXPRODN','ITYX_MX', 'Sk3i13Pw', 'oracle.jdbc.driver.OracleDriver', 'Mediatrix', sql) //ggf. oracle.jdbc.OracleDriver




def now= new GregorianCalendar();
def hour = now.get(Calendar.HOUR_OF_DAY) 
def dayofweek = now.get(Calendar.DAY_OF_WEEK) 

if ( // Working hours
    (    (hour > 8 && hour< 18 && dayofweek>1 && dayofweek<7 ) &&
         (escalations>0 || errors>0 || warnings>0)
    ) || // RB hours
    (    ((hour < 8 || hour> 18) && (dayofweek>1 || dayofweek<7) ) &&
         (escalations>0 || errors>0 || warnings>10)
    ) || // WEEKEND
    (    (dayofweek<2 || dayofweek>7 ) &&
         (escalations>0 || errors>0 || warnings>10)
    ) 
){
 String subject="[SKY_HCHK]"
 if (escalations > 0) {
    subject+= " cr: ${escalations}"
 }
 subject+=" er:${errors} war:${warnings} ok:${checks}" 
 def reportdate=String.format('%tY-%<tm-%<td %<tH:00', new GregorianCalendar())
 alerts+="<br/>Production Environment, Datum: ${reportdate}<br/>"
 sendTextMail(recipients, subject, alerts)
} 
  
  
def sendTextMail(String recipients, String subject, String txtmessage){
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
        println "sending message:" + recipients+":"+(subject)
        transport.send(message);
    } catch (MessagingException e) {
         e.printStackTrace();
    }
}
  
  
  
  
  

def execDBScript(dbConnString, dbUser, dbPass, dbDriver, dbInternalName, sql){
  String result=""
  def dbcon
  try{
    dbcon = Sql.newInstance(dbConnString,dbUser, dbPass,dbDriver) //ggf. oracle.jdbc.OracleDriver
   // dbcon.withStatement { 
   //    stmt -> stmt.queryTimeout = queryTimeout 
   // } 
    dbcon.eachRow(sql) { row ->
       if (row.currentstatus == '3_ESCALACTION'){
       //     critical=true;
       //     escalations++
       } else  if (row.currentstatus == '2_ERROR'){
       //     errors++
       } else if (row.currentstatus == '1_WARNING'){
       //     warnings++
       } else if (row.currentstatus == '0_OK'){
       //     checks++
       }
       result+="${row.statusdate} \t ${dbi}: ${row.checkname} MXDB \t ${row.currentstatus} \t ${row.description} \n\r \n\r"
    }
  }catch (Exception e){
     //critical=true;
     //escalations++;
     result+="Critical Problem with DB connection: "+ dbInternalName ;
     result+="\n\r \n\r"+e.message
  } finally {
    if (dbcon) dbcon.connection.close()
  }
  return result;
}