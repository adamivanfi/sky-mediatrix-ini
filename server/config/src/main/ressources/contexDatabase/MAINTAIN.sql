create or replace
PACKAGE MAINTAIN AS 

  procedure cleanup_deleted;
  procedure cleanup_mxmailreturn;
  procedure cleanup_siebel;
  
  procedure kpi_docpool(param in VARCHAR2);

END MAINTAIN;

create or replace
PACKAGE body MAINTAIN IS 
  
  /*
    Löscht diverse Dokumente im Status DELETED"
  */
  
  procedure cleanup_deleted is
  type numbers is table of number;
  n    numbers;
  tmp_id number;
  begin 
  
    dbms_output.put_line('Delete WaitForMX,Outbound,Associate,Archiv,WaitForOCR  - DELETED');
    select id bulk collect into n
      from CXDSG_CDOCPOOL
       where (
        parameter in ('WaitForMX', 'Outbound','Associate','Archiv', 'WaitForOCR') 
        OR (parameter like 'WaitForContactId%')
      ) and status=32 and id in (select id from cxdsg_cdocpooldat); --status=32 => deleted
       --where (parameter in ('Archiv')) and status=32; -- status32=deleted
    for i in 1 .. n.count loop
     --dbms_output.put_line(n(i));
     select data_id into tmp_id from cxdsg_cdocpool where id = n(i);
     delete from cxdsg_cdocpool where id = n(i);
     delete from cxdsg_cdocpooldat where id = tmp_id;
     
   end loop;
   --dbms_output.put_line(n.count);
   
   
   --dbms_output.put_line('delete imported');  
   dbms_output.put_line('Delete Imported/DELETED (>48h)');
   select id bulk collect into n
      from CXDSG_CDOCPOOL
      where parameter in ('Imported')
      and status=32 
      and ((sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (86400000) - (select TO_CHAR(systimestamp, 'TZH') * 3600000 from dual) - deletetime) > 48*3600000;
      
   for i in 1 .. n.count loop
     --dbms_output.put_line(n(i));
     select data_id into tmp_id from cxdsg_cdocpool where id = n(i);
     delete from cxdsg_cdocpool where id = n(i);
     delete from cxdsg_cdocpooldat where id = tmp_id;
   end loop;
  end;
  
  /*
    Setzt Dokumente aus MXMailReturn/Running zurück nach MXMailReturn/Wait
  */
  procedure cleanup_mxmailreturn is
  type numbers is table of number;
  n    numbers;
  begin
    select id bulk collect into n
      from CXDSG_CDOCPOOL
      where parameter in ('MXMailReturn') 
      and locking is not null
      and status=1 --running=status 1
      and ((sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (86400000) - (select TO_CHAR(systimestamp, 'TZH') * 3600000 from dual) - locktime) > 15*60000;
    
    for i in 1 .. n.count loop
     --dbms_output.put_line(n(i));
     update cxdsg_cdocpool set locking=null, status=0 where id=n(i);
     --delete from cxdsg_cdocpool where id = n(i);
     --delete from cxdsg_cdocpooldat where id = n(i);
   end loop;
   commit;
  end;
  
  procedure cleanup_siebel is
  begin
    update cxdsg_cdocpool
    set parameter='Imported', status=16, comment_text='No Siebel Callback' -- status 16 = error, status 0=wait, status=0, 
    where ID in ( 
      select 
        id
      from (
          select 
            pool.ID, 
            pool.lastdoctime,
            ((sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * 86400000 - (select TO_CHAR(systimestamp, 'TZH') * 3600000 from dual) - pool.lastdoctime) as age
          from 
            cxdsg_cdocpool pool
          where 
            pool.parameter like 'WaitForContactId%' 
            and (pool.status=0)
          )
      where 
        age>24*3600000 --24h
      );
  end;
  
  procedure kpi_docpool(param in VARCHAR2) is
  type kpi_record IS RECORD (
    curdate DATE,
    parameter_ VARCHAR2(60),
    min_iv number,
    max_iv number,
    avg_iv number,
    count_iv number,
    count_wait number,
    count_deleted number,
    count_error number,
    count_other number
    
  );
  kpi   kpi_record;
  
  begin
    select 
      sysdate, 
      param, 
      nvl(round(min(deletetime-createtime)/1000,0),0),
      nvl(round(max(deletetime-createtime)/1000),0),
      nvl(round(avg(deletetime-createtime)/1000,0),0),
      count(*),
      -1,
      -1,
      -1,
      -1
    into
      kpi
    from cxdsg_cdocpool 
    where parameter like param 
    and deletetime>0
    and ((sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (86400000) - (select TO_CHAR(systimestamp, 'TZH') * 3600000 from dual) - deletetime) < 15*60000;
    
    select count(*)
    into kpi.count_wait
    from cxdsg_cdocpool where parameter like param and status=0;
    
    select count(*)
    into kpi.count_deleted
    from cxdsg_cdocpool where parameter like param and status=32;
    
    select count(*)
    into kpi.count_error
    from cxdsg_cdocpool where parameter like param and status=16;
    
    select count(*)
    into kpi.count_other
    from cxdsg_cdocpool where parameter like param and status not in (0,16,32);
    
    --dbms_output.put_line(kpi.curdate);
    --dbms_output.put_line(kpi.parameter_);
    --dbms_output.put_line(kpi.min_iv);
    --dbms_output.put_line(kpi.max_iv);
    --dbms_output.put_line(kpi.avg_iv);
    --dbms_output.put_line(kpi.count_iv);
    
    --dbms_output.put_line(kpi.count_deleted);
    --dbms_output.put_line(kpi.count_wait);
    --dbms_output.put_line(kpi.count_error);
    --dbms_output.put_line(kpi.count_other);
    
    insert into ntt_kpi_docpool kpi values(kpi.curdate, kpi.parameter_, kpi.count_iv, kpi.min_iv, kpi.max_iv, kpi.avg_iv, kpi.count_wait, kpi.count_deleted, kpi.count_error, kpi.count_other);
    
  end;
end MAINTAIN;