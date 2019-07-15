create or replace
PROCEDURE DOCPOOL_SIEBEL_ERROR AS 
BEGIN
update cxdsg_cdocpool
  set parameter='Imported', status=16
  where ID in ( 
select ID from (
  select 
    pool.ID, 
    pool.lastdoctime, 
    ((sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (86400000) - 3600000 - pool.lastdoctime) as age
  from cxdsg_cdocpool pool
  where 
    parameter like 'WaitForContactId%' 
    and (status=0)
  ) where (age>300000)
);
END DOCPOOL_SIEBEL_ERROR;