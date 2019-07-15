create or replace
PROCEDURE                                                             NEWDB_UPDATE AS 
BEGIN

  -- Utilities
  -- Find entries in NEWDB_ASSET that have an unknown customer:
  -- select CUSTOMER_ID from NEWDB_ASSET  tt where tt.CUSTOMER_ID not in (select CUSTOMER_ID from NEWDB_CUSTOMER cc);
  
  -- cleanup NEWDB_*_ALL tables
  -- delete from newdb_asset_all;
  -- delete from newdb_contract_all;
  -- delete from newdb_customer_all;

  -- Add new entries 
  insert into NEWDB_CUSTOMER_ALL select CUSTOMER_ID, MOBILE_NUMBER, EMAIL_ADDRESS, FIRST_NAME, LAST_NAME, TELEPHONE_NUMBER, STATUS, ROW_ID from customer@database_newdb tcu where tcu.CUSTOMER_ID is not null and tcu.OPERATION='I';
  insert into NEWDB_CONTRACT_ALL select CUSTOMER_ID, CONTRACT_ID, HOUSE_NUMBER, STREET, ZIPCODE, CITY, ACCOUNT_NUMBER, BANK_CODE, BANK_ACCOUNT_HOLDER, STATUS, COUNTRY from contract@database_newdb tco where tco.OPERATION='I';
  insert into NEWDB_ASSET_ALL select CONTRACT_ID, SERIAL_NUMBER, CUSTOMER_ID, STATUS from asset@database_newdb ta where ta.OPERATION='I';
  
  -- Delete obsolete assets. Delete will only occur in NEWDB_ASSET  
  -- Thus integrity will not be broken by deletion.
  delete from NEWDB_ASSET_ALL WHERE SERIAL_NUMBER IN (select SERIAL_NUMBER from asset@database_newdb where OPERATION = 'D');

  -- Update customer
  Update newdb_customer_all t1
  Set (t1.CUSTOMER_ID, t1.MOBILE_NUMBER, t1.EMAIL_ADDRESS, t1.FIRST_NAME, t1.LAST_NAME, t1.TELEPHONE_NUMBER, t1.STATUS, t1.ROW_ID) = (select t2.CUSTOMER_ID, t2.MOBILE_NUMBER, t2.EMAIL_ADDRESS, t2.FIRST_NAME, t2.LAST_NAME, t2.TELEPHONE_NUMBER, t2.STATUS, t2.ROW_ID from customer@database_newdb t2 where t1.customer_id = t2.customer_id)
  Where t1.customer_id in (select t2.customer_id from customer@database_newdb t2 where t2.CUSTOMER_ID is not null and t2.operation = 'U');
  -- Update contract
  Update newdb_contract_all t1
  Set (t1.CUSTOMER_ID, t1.CONTRACT_ID, t1.HOUSE_NUMBER, t1.STREET, t1.ZIPCODE, t1.CITY, t1.ACCOUNT_NUMBER, t1.BANK_CODE, t1.BANK_ACCOUNT_HOLDER, t1.STATUS, t1.COUNTRY) = (select t2.CUSTOMER_ID, t2.CONTRACT_ID, t2.HOUSE_NUMBER, t2.STREET, t2.ZIPCODE, t2.CITY, t2.ACCOUNT_NUMBER, t2.BANK_CODE, t2.BANK_ACCOUNT_HOLDER, t2.STATUS, t2.COUNTRY from contract@database_newdb t2 where t1.contract_id = t2.contract_id)
  Where t1.contract_id in (select t2.contract_id from contract@database_newdb t2 where t2.CONTRACT_ID is not null and t2.operation = 'U');
  -- Update asset
  Update newdb_asset_all t1
  Set (t1.contract_id, t1.serial_number, t1.customer_id, t1.status) = (select t2.contract_id, t2.serial_number, t2.customer_id, t2.status from asset@database_newdb t2 where t1.serial_number = t2.serial_number)
  Where t1.serial_number in (select t2.serial_number from asset@database_newdb t2 where t2.SERIAL_NUMBER is not null and  t2.operation = 'U');

  DMS_STOP_PROC@database_newdb(sysdate);
  
END NEWDB_UPDATE;