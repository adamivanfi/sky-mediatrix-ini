drop table NEWDB_CUSTOMER_ALL;
drop table NEWDB_CONTRACT_ALL;
drop table NEWDB_ASSET_ALL;


-- Erzeugt die FuzzyDB-Tabellen in der Contex-Datenbank
--------------------------------------------------------
--  DDL for Table NEWDB_CUSTOMER_ALL
--------------------------------------------------------

  CREATE TABLE "NEWDB_CUSTOMER_ALL" 
   (	"CUSTOMER_ID" VARCHAR2(15 BYTE), 
	"MOBILE_NUMBER" VARCHAR2(15 BYTE), 
	"EMAIL_ADDRESS" VARCHAR2(50 BYTE), 
	"FIRST_NAME" VARCHAR2(100 BYTE), 
	"LAST_NAME" VARCHAR2(100 BYTE), 
	"TELEPHONE_NUMBER" VARCHAR2(30 BYTE),
	"STATUS" VARCHAR2(30 BYTE),
	"ROW_ID" VARCHAR2(15 BYTE),
	"ROW_ID" VARCHAR2(15 CHAR)
   );
   
CREATE UNIQUE INDEX "INDEX1" ON "NEWDB_CUSTOMER_ALL"
  (
    "CUSTOMER_ID"
  );
   
--------------------------------------------------------
--  DDL for Table NEWDB_CONTRACT_ALL
--------------------------------------------------------

  CREATE TABLE "NEWDB_CONTRACT_ALL" 
   (	"CUSTOMER_ID" VARCHAR2(15 BYTE), 
	"CONTRACT_ID" VARCHAR2(30 BYTE), 
	"HOUSE_NUMBER" VARCHAR2(30 BYTE), 
	"STREET" VARCHAR2(200 BYTE), 
	"ZIPCODE" VARCHAR2(30 BYTE), 
	"CITY" VARCHAR2(40 BYTE),  -- increased from 30 to 40
	"COUNTRY" VARCHAR2(30 BYTE), 
	"ACCOUNT_NUMBER" VARCHAR2(50 BYTE), 
	"BANK_CODE" VARCHAR2(50 BYTE), 
	"BANK_ACCOUNT_HOLDER" VARCHAR2(50 BYTE),  --increased from 50 to 80 
	"STATUS" VARCHAR2(30 BYTE),
	"COUNTRY" VARCHAR2(30 BYTE)
   );
   
   CREATE INDEX "INDEX2" ON "NEWDB_CONTRACT_ALL"
  (
    "CUSTOMER_ID"
  );

--------------------------------------------------------
--  DDL for Table NEWDB_ASSET_ALL
--------------------------------------------------------

  CREATE TABLE "NEWDB_ASSET_ALL" 
   (	"CONTRACT_ID" VARCHAR2(15 BYTE), 
	"SERIAL_NUMBER" VARCHAR2(100 BYTE), 
	"CUSTOMER_ID" VARCHAR2(15 BYTE), 
	"STATUS" VARCHAR2(30 BYTE)
   );
   
   CREATE INDEX "INDEX3" ON "NEWDB_ASSET_ALL"
  (
    "CONTRACT_ID",
    "SERIAL_NUMBER"
  );

  CREATE INDEX "INDEX4" ON "NEWDB_ASSET_ALL"
  (
    "CUSTOMER_ID"
  )
  
CREATE TABLE "NEWDB_ASSET_DELTA"
  (
    "CONTRACT_ID"   VARCHAR2(30 BYTE),
    "SERIAL_NUMBER" VARCHAR2(100 BYTE),
    "CUSTOMER_ID"   VARCHAR2(15 BYTE),
    "STATUS"        VARCHAR2(30 BYTE),
    "OPERATION"     VARCHAR2(5 BYTE),
    "OPERATION_DATE" DATE
  )