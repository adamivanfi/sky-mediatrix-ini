create or replace
PACKAGE NEWDB AS 

  v_customer_row_count INTEGER;
  v_customer_commit_count INTEGER;

  
  procedure update_full_test;
  
  procedure update_full_customer;
  procedure update_full_contract;
  procedure update_full_asset;
  

  

END NEWDB;

create or replace
PACKAGE body NEWDB IS 
  
  -- TEST TEST TEST This is only for testing from CONTEX INT to CONTEX PROD
  procedure update_full_test is
  TYPE TObjectTable IS TABLE OF NEWDB_CUSTOMER_ALL%ROWTYPE;
  cursor c_customer is
      select CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS, FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, ROW_ID 
      from newdb_customer_all@contex_int;
  ObjectTable$ TObjectTable;
  begin
    
  
    open c_customer;
    loop
       fetch c_customer bulk collect into ObjectTable$ LIMIT 1000;
       forall x in ObjectTable$.First..ObjectTable$.Last
         insert into NEWDB_CUSTOMER_ALL (CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS, FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, ROW_ID)
         values (ObjectTable$(x).CUSTOMER_ID, ObjectTable$(x).MOBILE_NUMBER, ObjectTable$(x).EMAIL_ADDRESS, ObjectTable$(x).FIRST_NAME, ObjectTable$(x).LAST_NAME, ObjectTable$(x).TELEPHONE_NUMBER, ObjectTable$(x).STATUS, ObjectTable$(x).ROW_ID);   
      DBMS_OUTPUT.put_line(ObjectTable$.count || 'rows');
      commit;
       exit when c_customer%notfound;
   end loop;

   close c_customer;
  end;
  
  -- update from NEWDB using FETCH BULK COLLECT
  procedure update_full_customer is
  TYPE TObjectTable IS TABLE OF NEWDB_CUSTOMER_ALL%ROWTYPE;
  cursor c_customer is
      select CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS, FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, ROW_ID 
      from customer@NEWDB_PROD.SKY.DE
      where operation='I';
  ObjectTable$ TObjectTable;
  begin
    open c_customer;
    loop
      fetch c_customer bulk collect into ObjectTable$ LIMIT 1000;
      forall x in ObjectTable$.First..ObjectTable$.Last
        insert into NEWDB_CUSTOMER_ALL (CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS, FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, ROW_ID)
        values (ObjectTable$(x).CUSTOMER_ID, ObjectTable$(x).MOBILE_NUMBER, ObjectTable$(x).EMAIL_ADDRESS, ObjectTable$(x).FIRST_NAME, ObjectTable$(x).LAST_NAME, ObjectTable$(x).TELEPHONE_NUMBER, ObjectTable$(x).STATUS, ObjectTable$(x).ROW_ID);   
      exit when c_customer%notfound;
      commit;
   end loop;

   close c_customer;
   commit;
  end;
  
  -- update from NEWDB using FETCH BULK COLLECT
  procedure update_full_contract is
  TYPE TObjectTable IS TABLE OF NEWDB_CONTRACT_ALL%ROWTYPE;
  cursor c_contract is
      select CUSTOMER_ID, CONTRACT_ID, HOUSE_NUMBER, STREET, ZIPCODE, CITY, ACCOUNT_NUMBER, BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, COUNTRY 
      from contract@NEWDB_PROD.SKY.DE
      where operation='I';
  ObjectTable$ TObjectTable;
  begin
    open c_contract;
    loop
       fetch c_contract bulk collect into ObjectTable$ LIMIT 1000;
       forall x in ObjectTable$.First..ObjectTable$.Last
        insert into NEWDB_CONTRACT_ALL (CUSTOMER_ID, CONTRACT_ID, HOUSE_NUMBER, STREET, ZIPCODE, CITY, ACCOUNT_NUMBER, BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, COUNTRY)
        values (ObjectTable$(x).CUSTOMER_ID, ObjectTable$(x).CONTRACT_ID, ObjectTable$(x).HOUSE_NUMBER, ObjectTable$(x).STREET, ObjectTable$(x).ZIPCODE, ObjectTable$(x).CITY, ObjectTable$(x).ACCOUNT_NUMBER, ObjectTable$(x).BANK_CODE, ObjectTable$(x).BANK_ACCOUNT_HOLDER, ObjectTable$(x).STATUS, ObjectTable$(x).COUNTRY);
       exit when c_contract%notfound;
       commit;
   end loop;

   close c_contract;
   commit;
  end;
  
  -- update from NEWDB using FETCH BULK COLLECT
  procedure update_full_asset is
  TYPE TObjectTable IS TABLE OF NEWDB_ASSET_DELTA%ROWTYPE;
  cursor c_asset is
      select CONTRACT_ID, SERIAL_NUMBER, CUSTOMER_ID, STATUS, OPERATION, OPERATION_DATE 
      from asset@NEWDB_PROD.SKY.DE;
  ObjectTable$ TObjectTable;
  begin
    open c_asset;
    loop
       fetch c_asset bulk collect into ObjectTable$ LIMIT 1000;
       forall x in ObjectTable$.First..ObjectTable$.Last
        insert into NEWDB_ASSET_DELTA (CONTRACT_ID, SERIAL_NUMBER, CUSTOMER_ID, STATUS, OPERATION, OPERATION_DATE)
        values (ObjectTable$(x).CONTRACT_ID, ObjectTable$(x).SERIAL_NUMBER, ObjectTable$(x).CUSTOMER_ID, ObjectTable$(x).STATUS, ObjectTable$(x).OPERATION, ObjectTable$(x).OPERATION_DATE);
       exit when c_asset%notfound;
       commit;
   end loop;

   close c_asset;
   commit;
  end;
  
END NEWDB;

create or replace
PROCEDURE NEWDB_UPDATE_FULL_ASSET AS 
BEGIN
  NEWDB.UPDATE_FULL_ASSET();
END NEWDB_UPDATE_FULL_ASSET;

create or replace
PROCEDURE NEWDB_UPDATE_FULL_CONTRACT AS 
BEGIN
  NEWDB.UPDATE_FULL_CONTRACT();
END NEWDB_UPDATE_FULL_CONTRACT;

create or replace
PROCEDURE NEWDB_UPDATE_FULL_CUSTOMER AS 
BEGIN
  NEWDB.UPDATE_FULL_CUSTOMER();
END NEWDB_UPDATE_FULL_CUSTOMER;