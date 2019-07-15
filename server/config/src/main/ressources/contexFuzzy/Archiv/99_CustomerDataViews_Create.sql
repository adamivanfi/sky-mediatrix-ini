-- erzeugt Views die in der Entwicklungsumgebung genutzt werden können
-- um den SiebelServiceProvider mit sinnvollen Daten zu befüllen 

CREATE or REPLACE VIEW CUSTOMERDATA_CUSTOMER AS
select CUSTOMER_ID, mobile_number, 'N' As CONTRACT_CONTENT, email_address, 'Dr.' As SALUTATION, first_name, last_name, telephone_number, 'STANDARD' As CATEGORY, 'N' As CAMPAIGNSTAMP, 'AKTIVIERUNG' As SR_CONTRACTCHANGECATEGORY, '2011-10-01T12:00:00+02:00' As SR_CONTRACTCHANGEDATE from NEWDB_CUSTOMER;

CREATE or REPLACE  VIEW CUSTOMERDATA_CONTRACT AS
select t.CUSTOMER_ID, contract_id, house_number, '' As STAIRCASE, '' As FLOOR, '' As FLATNUMBER, '' As UNEXPECTEDRETURNCONTRACT, '' As CONTRACTDATE, street, zipcode, city, account_number, bank_code, bank_account_holder, 'PREPAID' As PRICELIST, '' As SUBS_STARTDATE, '' As EARM_CANCELDATE, '' As POSS_CANCELDATE from NEWDB_CONTRACT t;

CREATE or REPLACE  VIEW CUSTOMERDATA_ASSET AS
select SERIAL_NUMBER, contract_id, customer_id, 'HDTYPE' As HARDDISK_TYPE, 'HDSERIAL' As HARDDISK_SERIAL, 'RCTYPE' As RECEIVER_TYPE, 'RCSERIAL' As RECEIVER_SERIAL, 'CIPTYPE' As CIPLUS_TYPE, 'CIPSERIAL' As CIPLUS_SERIAL from NEWDB_ASSET;