/** 
Script f?r vollst?ndiges und inkrementelles Rebuild der FuzzyDB-Tabellen aus NewDB 

Vollst?ndiges Rebuild dauert in der Produktion 3h auf DMS-Seite, detaliert
- CG braucht ca 15 Miunuten auf der NewDB-Seite um den Load durchzuf?hren
- 2h f?r die ?bertragung in Staging-tabellen, 
- 35' f?r Nachbearbeitung 
- 25' Neuaufbau Fuzzyindex 

Inkrementelles Rebuild Dauert:
- 15' Staging und Nachbearbeitung
- 25' Neuaufbau FuzzyIndex

Tasks:
 - bevor CG startet einmal den 241 job manuell starten damit im Fallbackscenario m?glichst kleine differenzen da sind
 - CG muss in NewDB muss inkrementelle bef?llung stoppen, die initialbef?llung ansto?en und die inkrementellen Dienste starten
 - In der Nacht sichert man die CX-Newdb-Tabellen und anschlie?end leert man diese
 - Dann den Mainenance 241 ansto?en der sich um Staging, verarbeitung und aufbau fuzzyindex k?mmert
Wichtig: Bis die Tabellen bef?llt sind, kann es zur Problemen bei Redindex und allen Prozessen die Enrichment Bean ben?tigen kommen.

Fallback:
 - Die weggesicherten Tabellen wiederherstellen
 - Index generieren

ToDo's:
- Protokollierung oder eine Art Journaling w?rde hier hilfreich sein

Autor: Gregor Meinusch, NTT Data
Datum 2013-04-09

*/

alter session set nls_date_format='DD.MM.YYYY HH24:MI:SS';
SET SERVEROUTPUT ON SIZE UNLIMITED;
-- DBMS_OUTPUT.ENABLE (buffer_size => NULL);
SET AUTOCOMMIT OFF;
--SET AUTOCOMMIT 10000;

declare
 v_tmpcounter Number(15);
 v_CUSTOMER_ID VARCHAR2(15 CHAR);
 l_exst number;
 v_updatetime constant timestamp := sysdate; 
 --v_updatetime date:=to_date('24.04.13 10:00:00', 'DD.MM.RR HH24:MI:SS');

 v_i  pls_integer  := 0;
 c_commitsize constant PLS_INTEGER := 5000;
begin
--dbms_output.enable;
dbms_output.put_line('NewDB Import    START STAGING# ' || sysdate); 


select count(*) into v_tmpcounter from CUSTOMER@NEWDB.SKY.DE ;
dbms_output.put_line('NEWDB CUSTOMER-Actions    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from CONTRACT@NEWDB.SKY.DE ;
dbms_output.put_line('NEWDB CONTRACT-Actions    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from ASSET@NEWDB.SKY.DE ;
dbms_output.put_line('NEWDB ASSET-Actions       Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from CAMPAIGN@NEWDB.SKY.DE ;
dbms_output.put_line('NEWDB CAMPAIGN-Actions     Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from CAMPAIGN_CONF@NEWDB.SKY.DE ;
dbms_output.put_line('NEWDB CAMPAIGN_CONF-Actions Items: ' ||  v_tmpcounter ||'  '|| sysdate);

-- Erster Durchlauf -- bei gr??eren ?nderungen kann bis zur 1h dauern, full-reload 3h in der Nacht 8h ?ber Tag
-- stage CAMPAIGN_CONF
 dbms_output.put_line('NewDB Import    STAGE CAMPAIGN_CONF # ' || sysdate); 
  v_i  := 0;
 for rec in (SELECT CAMPAIGN_ID, CAMPAIGN_TYPE, STATUS, START_DATE, END_DATE, OPERATION, OPERATION_DATE  FROM CAMPAIGN_CONF@NEWDB.SKY.DE ) loop  
 INSERT INTO NEWDB_CAMPAIGN_CONF_STAGE(CAMPAIGN_ID, CAMPAIGN_TYPE, STATUS, START_DATE, END_DATE, OPERATION, OPERATION_DATE) VALUES
 (rec.CAMPAIGN_ID, rec.CAMPAIGN_TYPE, rec.STATUS, rec.START_DATE, rec.END_DATE, rec.OPERATION, rec.OPERATION_DATE);
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import    Finished STAGE CAMPAIGN_CONF # '  || v_i || ' ' || sysdate); 
 
-- stage CAMPAIGN
 dbms_output.put_line('NewDB Import    STAGE CAMPAIGN # ' || sysdate); 
 v_i  := 0;
 for rec in (SELECT CUSTOMER_ID, CAMPAIGN_ID, OPERATION,OPERATION_DATE  FROM CAMPAIGN@NEWDB.SKY.DE ) loop  
 INSERT INTO NEWDB_CAMPAIGN_STAGE(CUSTOMER_ID, CAMPAIGN_ID, OPERATION,OPERATION_DATE) VALUES
 (rec.CUSTOMER_ID, rec.CAMPAIGN_ID, rec.OPERATION,rec.OPERATION_DATE);
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
   END IF; 
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import    Finished STAGE CAMPAIGN # '  || v_i || ' ' || sysdate); 

 
-- stage asset
 dbms_output.put_line('NewDB Import    STAGE ASSET # ' || sysdate); 
 v_i  := 0;
 for rec in (SELECT CUSTOMER_ID, CONTRACT_ID, SERIAL_NUMBER,  STATUS, OPERATION, OPERATION_DATE FROM asset@NEWDB.SKY.DE ) loop  
 INSERT INTO NEWDB_ASSET_STAGE(CUSTOMER_ID, CONTRACT_ID, SERIAL_NUMBER,  STATUS, OPERATION, OPERATION_DATE) VALUES
(rec.CUSTOMER_ID, rec.CONTRACT_ID, rec.SERIAL_NUMBER,  rec.STATUS, rec.OPERATION, rec.OPERATION_DATE);
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
dbms_output.put_line('NewDB Import    Finished STAGE ASSET # '  || v_i || ' ' || sysdate);

-- stage contract
 dbms_output.put_line('NewDB Import    STAGE CONTRACT # ' || sysdate); 
 v_i  := 0;
  for rec in (SELECT CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATION, OPERATION_DATE, WM_FLG, RECEPTION_TYPE RECEPTION, OPERATOR, PLATFORM FROM CONTRACT@NEWDB.SKY.DE ) loop  
 INSERT INTO NEWDB_CONTRACT_STAGE(CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATION, OPERATION_DATE, WM_FLG, RECEPTION, OPERATOR, PLATFORM) VALUES
(rec.CONTRACT_ID, rec.CUSTOMER_ID, rec.HOUSE_NUMBER,  rec.STREET, rec.ZIPCODE, rec.CITY,rec.COUNTRY, rec.ACCOUNT_NUMBER,rec.BANK_CODE, rec.BANK_ACCOUNT_HOLDER, rec.STATUS, rec.OPERATION, rec.OPERATION_DATE, rec.WM_FLG, rec.RECEPTION, rec.OPERATOR, rec.PLATFORM);
 v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import    Finished STAGE CONTRACT # '  || v_i || ' ' || sysdate); 

-- stage customer
 dbms_output.put_line('NewDB Import    STAGE CUSTOMER # ' || sysdate); 
 v_i  := 0;
 for rec in (SELECT ROW_ID, CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS,  FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, OPERATION, OPERATION_DATE   FROM customer@NEWDB.SKY.DE ) loop  
 INSERT INTO NEWDB_CUSTOMER_STAGE(ROW_ID, CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS,  FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, OPERATION, OPERATION_DATE) VALUES
(rec.ROW_ID, rec.CUSTOMER_ID, rec.MOBILE_NUMBER, rec.EMAIL_ADDRESS,  rec.FIRST_NAME, rec.LAST_NAME, rec.TELEPHONE_NUMBER, rec.STATUS, rec.OPERATION, rec.OPERATION_DATE);
 v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF; 
 end loop;
 commit;
 dbms_output.put_line('NewDB Import    Finished STAGE CUSTOMER # '  || v_i || ' ' || sysdate); 
 
 -- Zweiter Durchlauf um delta seit dem Start des ersten Durchlaufes aufzufangen -- um bei gr??eren ?nderungen die nach dem Start des ersten Durchlaufes eingetroffenen ?nderungen nicht zu verlieren
-- stage CAMPAIGN_CONF
 dbms_output.put_line('NewDB Import2 ' || v_updatetime || '   STAGE CAMPAIGN_CONF # ' || sysdate); 
  v_i  := 0;
 for rec in (SELECT CAMPAIGN_ID, CAMPAIGN_TYPE, STATUS, START_DATE, END_DATE, OPERATION, OPERATION_DATE FROM CAMPAIGN_CONF@NEWDB.SKY.DE WHERE OPERATION_DATE > v_updatetime) loop  
 INSERT INTO NEWDB_CAMPAIGN_CONF_STAGE(CAMPAIGN_ID, CAMPAIGN_TYPE, STATUS, START_DATE, END_DATE, OPERATION, OPERATION_DATE) VALUES
 (rec.CAMPAIGN_ID, rec.CAMPAIGN_TYPE, rec.STATUS, rec.START_DATE, rec.END_DATE, rec.OPERATION, rec.OPERATION_DATE);
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import2    Finished STAGE CAMPAIGN_CONF # '  || v_i || ' ' || sysdate); 
 
-- stage CAMPAIGN
 dbms_output.put_line('NewDB Import2    STAGE CAMPAIGN # ' || sysdate); 
 v_i  := 0;
 for rec in (SELECT CUSTOMER_ID, CAMPAIGN_ID, OPERATION,OPERATION_DATE  FROM CAMPAIGN@NEWDB.SKY.DE WHERE OPERATION_DATE > v_updatetime) loop  
 INSERT INTO NEWDB_CAMPAIGN_STAGE(CUSTOMER_ID, CAMPAIGN_ID, OPERATION,OPERATION_DATE) VALUES
 (rec.CUSTOMER_ID, rec.CAMPAIGN_ID, rec.OPERATION,rec.OPERATION_DATE);
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
   END IF; 
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import2    Finished STAGE CAMPAIGN # '  || v_i || ' ' || sysdate); 

 
-- stage asset
 dbms_output.put_line('NewDB Import    STAGE ASSET # ' || sysdate); 
 v_i  := 0;
 for rec in (SELECT CUSTOMER_ID, CONTRACT_ID, SERIAL_NUMBER,  STATUS, OPERATION, OPERATION_DATE FROM asset@NEWDB.SKY.DE WHERE OPERATION_DATE > v_updatetime) loop  
 INSERT INTO NEWDB_ASSET_STAGE(CUSTOMER_ID, CONTRACT_ID, SERIAL_NUMBER,  STATUS, OPERATION, OPERATION_DATE) VALUES
(rec.CUSTOMER_ID, rec.CONTRACT_ID, rec.SERIAL_NUMBER,  rec.STATUS, rec.OPERATION, rec.OPERATION_DATE);
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
dbms_output.put_line('NewDB Import2    Finished STAGE ASSET # '  || v_i || ' ' || sysdate); 

-- stage contract
 dbms_output.put_line('NewDB Import2    STAGE CONTRACT # ' || sysdate); 
 v_i  := 0;
  for rec in (SELECT CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATION, OPERATION_DATE, WM_FLG, RECEPTION_TYPE RECEPTION, OPERATOR, PLATFORM FROM CONTRACT@NEWDB.SKY.DE WHERE OPERATION_DATE > v_updatetime ) loop  
 INSERT INTO NEWDB_CONTRACT_STAGE(CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATION, OPERATION_DATE, WM_FLG, RECEPTION, OPERATOR, PLATFORM) VALUES
(rec.CONTRACT_ID, rec.CUSTOMER_ID, rec.HOUSE_NUMBER,  rec.STREET, rec.ZIPCODE, rec.CITY,rec.COUNTRY,rec.ACCOUNT_NUMBER,rec.BANK_CODE, rec.BANK_ACCOUNT_HOLDER, rec.STATUS, rec.OPERATION, rec.OPERATION_DATE, rec.WM_FLG, rec.RECEPTION, rec.OPERATOR, rec.PLATFORM);
 v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import2    Finished STAGE CONTRACT # '  || v_i || ' ' || sysdate); 

-- stage customer
 dbms_output.put_line('NewDB Import2    STAGE CUSTOMER # ' || sysdate); 
 v_i  := 0;
 for rec in (SELECT ROW_ID, CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS,  FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, OPERATION, OPERATION_DATE FROM customer@NEWDB.SKY.DE WHERE OPERATION_DATE > v_updatetime) loop  
 INSERT INTO NEWDB_CUSTOMER_STAGE(ROW_ID, CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS, FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, OPERATION, OPERATION_DATE) VALUES
(rec.ROW_ID, rec.CUSTOMER_ID, rec.MOBILE_NUMBER, rec.EMAIL_ADDRESS,  rec.FIRST_NAME, rec.LAST_NAME, rec.TELEPHONE_NUMBER, rec.STATUS, rec.OPERATION, rec.OPERATION_DATE);
 v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF; 
 commit;
 end loop;
 dbms_output.put_line('NewDB Import2    Finished STAGE CUSTOMER # '  || v_i || ' ' || sysdate); 
 
 
dbms_output.put_line('NewDB Import    STAGING DONE # ' || sysdate); 
-- set new TS
NEWDB.DMS_STOP_PROC@NEWDB.SKY.DE(v_updatetime);
dbms_output.put_line('NewDB Import    TS SET DONE # ' || sysdate); 

select count(*) into v_tmpcounter from NEWDB_CUSTOMER_STAGE ;
dbms_output.put_line('CXStaging CUSTOMER-Actions    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from NEWDB_CONTRACT_STAGE ;
dbms_output.put_line('CXStaging CONTRACT-Actions    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from NEWDB_ASSET_STAGE ;
dbms_output.put_line('CXStaging ASSET-Actions       Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from NEWDB_CAMPAIGN_STAGE ;
dbms_output.put_line('CXStaging CAMPAIGN-Actions       Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from NEWDB_CAMPAIGN_CONF_STAGE ;
dbms_output.put_line('CXStaging CAMPAIGN_CONF-Actions       Items: ' ||  v_tmpcounter ||'  '|| sysdate);

-- processdelta customer
 dbms_output.put_line('NewDB Import    START PROCESS CUSTOMER # ' || sysdate); 
   v_i  := 0;
 for rec in (SELECT ROW_ID, CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS,  FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, OPERATION, OPERATION_DATE   FROM NEWDB_CUSTOMER_STAGE ORDER BY OPERATION_DATE) loop  
 SELECT count(*)  into l_exst FROM NEWDB_CUSTOMER  WHERE  ROW_ID = rec.ROW_ID and CUSTOMER_ID = rec.CUSTOMER_ID;   
IF rec.OPERATION = 'D' THEN
  DELETE from NEWDB_CUSTOMER  where ROW_ID = rec.ROW_ID and CUSTOMER_ID = rec.CUSTOMER_ID;
ELSIF (rec.OPERATION ='U') OR ((l_exst>0) AND (rec.OPERATION ='I')) THEN
  UPDATE NEWDB_CUSTOMER  set MOBILE_NUMBER=rec.MOBILE_NUMBER, EMAIL_ADDRESS=rec.EMAIL_ADDRESS,  FIRST_NAME=rec.FIRST_NAME, LAST_NAME=rec.LAST_NAME, TELEPHONE_NUMBER=rec.TELEPHONE_NUMBER, STATUS=rec.STATUS, OPERATION_DATE=rec.OPERATION_DATE
  where ROW_ID = rec.ROW_ID and CUSTOMER_ID = rec.CUSTOMER_ID;
ELSIF rec.OPERATION ='I' THEN
  INSERT INTO NEWDB_CUSTOMER (ROW_ID, CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS,  FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, OPERATION_DATE) 
  VALUES (rec.ROW_ID, rec.CUSTOMER_ID, rec.MOBILE_NUMBER, rec.EMAIL_ADDRESS,  rec.FIRST_NAME, rec.LAST_NAME, rec.TELEPHONE_NUMBER, rec.STATUS, rec.OPERATION_DATE);
ELSE
  dbms_output.put_line('NewDB Import    UNSUPPORTED OPERATION: # ' || sysdate || ': '|| rec.ROW_ID || ',' ||  rec.CUSTOMER_ID || ',' ||  rec.MOBILE_NUMBER || ',' ||  rec.EMAIL_ADDRESS || ',' ||   rec.FIRST_NAME || ',' ||  rec.LAST_NAME || ',' ||  rec.TELEPHONE_NUMBER || ',' ||  rec.STATUS || ',' ||  rec.OPERATION_DATE ); 
END IF;
 v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import    FINISHED PROCESS CUSTOMER # ' || v_i || ' '  || sysdate); 
--DELETE from NEWDB_CUSTOMER_STAGE where rowid=rec.rowid;

delete from newdb_customer_stage where operation_date<(select max(operation_date) from newdb_customer);
commit;

 dbms_output.put_line('NewDB Import    FINISHED CLEANING CUSTOMER STAGE # ' || sysdate); 



-- processdelta contract
 dbms_output.put_line('NewDB Import    START PROCESS CONTRACT # ' || sysdate); 
 v_i  := 0;
 for rec in (SELECT CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATION, OPERATION_DATE, WM_FLG, RECEPTION, OPERATOR, PLATFORM FROM NEWDB_CONTRACT_STAGE ORDER BY OPERATION_DATE) loop  

SELECT count(*)  into l_exst FROM NEWDB_CONTRACT  where CONTRACT_ID = rec.CONTRACT_ID and CUSTOMER_ID = rec.CUSTOMER_ID;   
IF rec.OPERATION = 'D' THEN
  DELETE from NEWDB_CONTRACT  where CONTRACT_ID = rec.CONTRACT_ID and CUSTOMER_ID = rec.CUSTOMER_ID;
ELSIF (rec.OPERATION ='U') OR ((l_exst>0) AND (rec.OPERATION ='I')) THEN
  UPDATE NEWDB_CONTRACT  set HOUSE_NUMBER=rec.HOUSE_NUMBER,  STREET=rec.STREET, ZIPCODE=rec.ZIPCODE, CITY=rec.CITY ,COUNTRY=rec.COUNTRY ,ACCOUNT_NUMBER = rec.ACCOUNT_NUMBER,BANK_CODE=rec.BANK_CODE, BANK_ACCOUNT_HOLDER=rec.BANK_ACCOUNT_HOLDER, STATUS=rec.STATUS, OPERATION_DATE=rec.OPERATION_DATE , WM_FLG=rec.WM_FLG, RECEPTION=rec.RECEPTION, OPERATOR=rec.OPERATOR, PLATFORM=rec.PLATFORM
  where CONTRACT_ID = rec.CONTRACT_ID and CUSTOMER_ID = rec.CUSTOMER_ID;
ELSIF rec.OPERATION ='I' THEN
  INSERT INTO NEWDB_CONTRACT (CONTRACT_ID, CUSTOMER_ID, HOUSE_NUMBER,  STREET, ZIPCODE, CITY,COUNTRY,ACCOUNT_NUMBER,BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, OPERATION_DATE, WM_FLG, RECEPTION, OPERATOR, PLATFORM) 
  VALUES (rec.CONTRACT_ID, rec.CUSTOMER_ID, rec.HOUSE_NUMBER,  rec.STREET, rec.ZIPCODE, rec.CITY,rec.COUNTRY,rec.ACCOUNT_NUMBER,rec.BANK_CODE, rec.BANK_ACCOUNT_HOLDER, rec.STATUS, rec.OPERATION_DATE, rec.WM_FLG, rec.RECEPTION, rec.OPERATOR, rec.PLATFORM);
ELSE
  dbms_output.put_line('NewDB Import    UNSUPPORTED OPERATION: # ' || sysdate || ': '|| rec.CONTRACT_ID || ',' ||  rec.CUSTOMER_ID|| ',' ||  rec.HOUSE_NUMBER|| ',' ||   rec.STREET|| ',' ||  rec.ZIPCODE|| ',' ||  rec.CITY || ',' ||  rec.COUNTRY || ',' || rec.ACCOUNT_NUMBER || ',' || rec.BANK_CODE || ',' ||  rec.BANK_ACCOUNT_HOLDER || ',' ||  rec.STATUS || ',' ||  rec.OPERATION_DATE || ',' || rec.WM_FLG || ',' || rec.RECEPTION || ',' || rec.OPERATOR || ',' || rec.PLATFORM);
END IF;
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
   END IF;
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import    FINISHED PROCESS CONTRACT # ' || v_i || ' '  || sysdate); 
delete from newdb_contract_stage where operation_date<(select max(operation_date) from newdb_contract);
commit;
 dbms_output.put_line('NewDB Import    FINISHED CLEANING CONTRACT STAGE # ' || sysdate); 

-- processdelta asset
 dbms_output.put_line('NewDB Import    START PROCESS ASSET # ' || sysdate); 
  v_i  := 0;
 for rec in (SELECT CUSTOMER_ID, CONTRACT_ID, SERIAL_NUMBER,  STATUS, OPERATION, OPERATION_DATE FROM NEWDB_ASSET_STAGE ORDER BY OPERATION_DATE) loop  

SELECT count(*)  into l_exst FROM NEWDB_ASSET  WHERE SERIAL_NUMBER = rec.SERIAL_NUMBER and CONTRACT_ID=rec.CONTRACT_ID;   
IF rec.OPERATION = 'D' THEN
  DELETE from NEWDB_ASSET  
  WHERE SERIAL_NUMBER = rec.SERIAL_NUMBER and CONTRACT_ID=rec.CONTRACT_ID;   
ELSIF (rec.OPERATION ='U') OR ((l_exst>0) AND (rec.OPERATION ='I')) THEN
  UPDATE NEWDB_ASSET  set CUSTOMER_ID=rec.CUSTOMER_ID, CONTRACT_ID=rec.CONTRACT_ID, STATUS=rec.STATUS, OPERATION_DATE=rec.OPERATION_DATE
  where SERIAL_NUMBER = rec.SERIAL_NUMBER ;
ELSIF rec.OPERATION ='I' THEN
  INSERT INTO NEWDB_ASSET (CUSTOMER_ID, CONTRACT_ID, SERIAL_NUMBER,  STATUS, OPERATION_DATE) 
  VALUES (rec.CUSTOMER_ID, rec.CONTRACT_ID, rec.SERIAL_NUMBER, rec.STATUS, rec.OPERATION_DATE);
ELSE
dbms_output.put_line('NewDB Import    UNSUPPORTED OPERATION: # ' || sysdate || ': '|| rec.CUSTOMER_ID || ',' || rec.CONTRACT_ID || ',' || rec.SERIAL_NUMBER || ',' || rec.STATUS || ',' || rec.OPERATION_DATE); 
END IF;
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
 dbms_output.put_line('NewDB Import    FINISHED PROCESS ASSET # ' || v_i || ' '  || sysdate); 
delete from newdb_asset_stage where operation_date<(select max(operation_date) from newdb_asset);
commit;
 dbms_output.put_line('NewDB Import    FINISHED CLEANING ASSET STAGE # ' || sysdate); 
 
 
 -- processdelta CAMPAIGN    
dbms_output.put_line('NewDB Import    START PROCESS CAMPAIGN # ' || sysdate); 
 v_i  := 0;
for rec in (SELECT CUSTOMER_ID SBLCUSTOMER_ID, CAMPAIGN_ID, OPERATION, OPERATION_DATE FROM NEWDB_CAMPAIGN_STAGE ORDER BY OPERATION_DATE) loop  

SELECT count(*) into l_exst FROM NEWDB_CAMPAIGN  WHERE SBLCUSTOMER_ID = rec.SBLCUSTOMER_ID and CAMPAIGN_ID=rec.CAMPAIGN_ID;   
IF rec.OPERATION = 'D' THEN
  DELETE from NEWDB_CAMPAIGN  
  WHERE SBLCUSTOMER_ID = rec.SBLCUSTOMER_ID and CAMPAIGN_ID=rec.CAMPAIGN_ID;
ELSIF (rec.OPERATION ='U') OR ((l_exst>0) AND (rec.OPERATION ='I')) THEN
  UPDATE NEWDB_CAMPAIGN  
  set OPERATION_DATE=rec.OPERATION_DATE
  WHERE  SBLCUSTOMER_ID = rec.SBLCUSTOMER_ID and CAMPAIGN_ID=rec.CAMPAIGN_ID;
ELSIF rec.OPERATION ='I' THEN
  INSERT INTO NEWDB_CAMPAIGN (SBLCUSTOMER_ID, CAMPAIGN_ID, OPERATION_DATE)
  VALUES (rec.SBLCUSTOMER_ID, rec.CAMPAIGN_ID, rec.OPERATION_DATE);
ELSE
dbms_output.put_line('NewDB Import    UNSUPPORTED OPERATION: # ' || sysdate || ': '|| rec.SBLCUSTOMER_ID || ',' ||  rec.OPERATION || ','||  rec.OPERATION_DATE); 
END IF;
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
dbms_output.put_line('NewDB Import    FINISHED PROCESS CAMPAIGN # ' || v_i || ' '  || sysdate); 


v_i  := 0;
for rec in (select c.customer_id, c.row_id sblcustomer_id from newdb_customer  c inner join (select sblcustomer_id from NEWDB_CAMPAIGN  where customer_id is null) ca on c.row_id=ca.sblcustomer_id ) loop  
  UPDATE NEWDB_CAMPAIGN  set customer_id=rec.customer_id where sblcustomer_id=rec.sblcustomer_id;
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;

dbms_output.put_line('NewDB Import    FINISHED Enrichment CustomerID for CAMPAIGN # ' || v_i || ' '  || sysdate); 

delete from newdb_campaign_stage where operation_date<(select max(operation_date) from newdb_campaign);
commit;

 dbms_output.put_line('NewDB Import    FINISHED CLEANING CAMPAIGN STAGE # ' || sysdate); 
 
 
 -- processdelta CAMPAIGN_CONF    
dbms_output.put_line('NewDB Import    START PROCESS CAMPAIGN_CONF # ' || sysdate); 
 v_i  := 0;
for rec in (SELECT CAMPAIGN_ID, CAMPAIGN_TYPE, STATUS, START_DATE, END_DATE, OPERATION, OPERATION_DATE FROM NEWDB_CAMPAIGN_CONF_STAGE ORDER BY OPERATION_DATE) loop  
SELECT count(*) into l_exst FROM NEWDB_CAMPAIGN_CONF  WHERE CAMPAIGN_ID = rec.CAMPAIGN_ID;   
IF rec.OPERATION = 'D' THEN
  DELETE from NEWDB_CAMPAIGN_CONF 
  WHERE CAMPAIGN_ID = rec.CAMPAIGN_ID;
ELSIF (rec.OPERATION ='U') OR ((l_exst>0) AND (rec.OPERATION ='I')) THEN
  UPDATE NEWDB_CAMPAIGN_CONF  
  set  CAMPAIGN_TYPE=rec.CAMPAIGN_TYPE, STATUS=rec.STATUS, START_DATE=rec.START_DATE, END_DATE=rec.END_DATE, OPERATION_DATE=rec.OPERATION_DATE
  WHERE  CAMPAIGN_ID = rec.CAMPAIGN_ID; 
ELSIF rec.OPERATION ='I' THEN
  INSERT INTO NEWDB_CAMPAIGN_CONF (CAMPAIGN_ID, CAMPAIGN_TYPE, STATUS, START_DATE, END_DATE,OPERATION_DATE) 
  VALUES (rec.CAMPAIGN_ID, rec.CAMPAIGN_TYPE, rec.STATUS, rec.START_DATE, rec.END_DATE,rec.OPERATION_DATE);
ELSE
dbms_output.put_line('NewDB Import    UNSUPPORTED OPERATION: # ' || sysdate || ': '|| rec.CAMPAIGN_ID || ',' ||  rec.OPERATION || ','||  rec.OPERATION_DATE); 
END IF;
  v_i  := v_i  + 1;
  if MOD(v_i, c_commitsize)=0 THEN
   commit;
  END IF;
 end loop;  
 commit;
dbms_output.put_line('NewDB Import    FINISHED PROCESS CAMPAIGN_CONF # ' || v_i || ' '  || sysdate); 

delete from newdb_campaign_conf_stage where operation_date<(select max(operation_date) from newdb_campaign_conf);
commit;
 dbms_output.put_line('NewDB Import    FINISHED CLEANING CAMPAIGN_CONF STAGE # ' || sysdate); 
 


 
select count(*) into v_tmpcounter from NEWDB_CUSTOMER  ;
dbms_output.put_line('FuzzyDB: CUSTOMER    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from NEWDB_CONTRACT  ;
dbms_output.put_line('FuzzyDB: CONTRACT    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from NEWDB_ASSET  ;
dbms_output.put_line('FuzzyDB: ASSET    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from NEWDB_CAMPAIGN  ;
dbms_output.put_line('FuzzyDB: CAMPAIGN    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
select count(*) into v_tmpcounter from NEWDB_CAMPAIGN_CONF  ;
dbms_output.put_line('FuzzyDB: CAMPAIGN_CONF    Items: ' ||  v_tmpcounter ||'  '|| sysdate);

dbms_output.put_line('NewDB Import    DONE # ' || sysdate); 
commit;



EXCEPTION
WHEN OTHERS
THEN
   DBMS_OUTPUT.PUT_LINE(SQLERRM);
 END;
/

exit
