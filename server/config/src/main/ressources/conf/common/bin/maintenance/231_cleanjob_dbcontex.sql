alter session set nls_date_format='DD.MM.YYYY HH24:MI:SS';
set serveroutput on;

declare
 v_olderthanday constant timestamp:=(sysdate - interval '1' day);
 v_olderthandayI Number(15);
 v_olderthanweek constant timestamp:=(sysdate - interval '7' day);
 v_olderthanweekI Number(15);
 v_olderthantwoweeks constant timestamp:=(sysdate - interval '14' day);
 v_olderthantwoweeksI Number(15);
 v_olderthanmonth constant timestamp:=(sysdate - interval '1' month);
 v_olderthanmonthI Number(15);
 v_tmpcounter Number(15);
  dayofmonth Varchar2(5);
begin
--SET AUTOCOMMIT ON;
--dbms_output.enable;
SELECT DATETOITYXTIME(v_olderthanday) into v_olderthandayI from dual;
SELECT DATETOITYXTIME(v_olderthanweek) into v_olderthanweekI from dual;
SELECT DATETOITYXTIME(v_olderthantwoweeks) into v_olderthantwoweeksI from dual;
SELECT DATETOITYXTIME(v_olderthanmonth) into v_olderthanmonthI from dual;

 dbms_output.put_line('CleaningJob    START # ' || sysdate);
 dbms_output.put_line('CleaningJob    v_olderthanweekI  ' || v_olderthanweekI);
 dbms_output.put_line('CleaningJob    v_olderthantwoweeksI  ' || v_olderthantwoweeksI);
 dbms_output.put_line('CleaningJob    v_olderthanmonthI ' || v_olderthanmonthI);
   
  dbms_output.put_line('cxdsg_cdocpool older than month   START # ' || sysdate);
  select count(*) into v_tmpcounter 
             from cxdsg_cdocpool where createtime<v_olderthanmonthI and status=32 and parameter not in (  'SL3K_601_MX_Injection');
  dbms_output.put_line('cxdsg_cdocpool older than month   Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from cxdsg_cdocpool where createtime<v_olderthanmonthI and status=32 and parameter not in (  'SL3K_601_MX_Injection') and rownum < 100000;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_cdocpool older than month   Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
 dbms_output.put_line('cxdsg_cdocpool older than month   END # ' || sysdate); 
 
 
  dbms_output.put_line('cxdsg_cdocpool imported 2w START # ' || sysdate);
  -- erfolgreich ins mediatrix importierte dokumente
  select count(*) into v_tmpcounter from cxdsg_cdocpool where createtime<v_olderthantwoweeksI
      and collectionid in (select collectionid from cxdsg_cdocpool where parameter like '60%' and status=32)
      and collectionid is not null and parameter not like '60%'  and parameter not like '8%' and parameter not like 'SL3K_6%'  and parameter not like 'SL3K_8%' and parameter not like 'Archiv' and parameter not like 'MoveFileToArchive' and status=32;
  dbms_output.put_line('cxdsg_cdocpool imported 2w  Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from cxdsg_cdocpool where createtime<v_olderthantwoweeksI and rownum < 100000
      and collectionid in (select collectionid from cxdsg_cdocpool where parameter like '60%' and status=32)
      and collectionid is not null and parameter not like '60%'  and parameter not like '8%' and parameter not like 'SL3K_6%'  and parameter not like 'SL3K_8%' and parameter not like 'Archiv' and parameter not like 'MoveFileToArchive' and status=32;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_cdocpool imported 2w   Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
 dbms_output.put_line('cxdsg_cdocpool imported 2w  END # ' || sysdate); 

   dbms_output.put_line('cxdsg_cdocpool 8xx START # ' || sysdate);
  -- erfolgreich ins mediatrix importierte dokumente
  select count(*) into v_tmpcounter from cxdsg_cdocpool where createtime<v_olderthantwoweeksI 
      and parameter  like '8%' and status=32;
  dbms_output.put_line('cxdsg_cdocpool 8xx  Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from cxdsg_cdocpool where createtime<v_olderthantwoweeksI and rownum < 100000
        and parameter  like '8%' and status=32;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_cdocpool 8xx  Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
 dbms_output.put_line('cxdsg_cdocpool 8xx   END # ' || sysdate); 


  dbms_output.put_line('cxdsg_cdocpooldat START # ' || sysdate);
  -- erfolgreich ins mediatrix importierte dokumente
  select count(*) into v_tmpcounter from cxdsg_cdocpooldat where id< (select min(data_id) from cxdsg_cdocpool);
  dbms_output.put_line('cxdsg_cdocpooldat  Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from cxdsg_cdocpooldat  where id< (select min(data_id) from cxdsg_cdocpool);
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_cdocpooldat Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
 dbms_output.put_line('cxdsg_cdocpooldat   END # ' || sysdate); 
  
  
   dbms_output.put_line('cxdsg_cdocpooldat2 START # ' || sysdate);
  -- erfolgreich ins mediatrix importierte dokumente
  select count(*) into v_tmpcounter from cxdsg_cdocpooldat where id not in (select (data_id) from cxdsg_cdocpool);
  dbms_output.put_line('cxdsg_cdocpooldat2  Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from cxdsg_cdocpooldat  where id not in (select (data_id) from cxdsg_cdocpool);
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_cdocpooldat2 Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
 dbms_output.put_line('cxdsg_cdocpooldat2   END # ' || sysdate); 
 

  dbms_output.put_line('ntt_cx_report Interim   START # ' || sysdate);
  select count(*) into v_tmpcounter from ntt_cx_report where step like 'INTERIM%' and created < v_olderthanmonth;
  
  dbms_output.put_line('ntt_cx_report Interim    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from ntt_cx_report where step like 'INTERIM%' and created < v_olderthanmonth;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('ntt_cx_report Interim  Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
 dbms_output.put_line('ntt_cx_report Interim  END # ' || sysdate); 

 --- wegen FK
  dbms_output.put_line('cxdsg_stateinfo    START # ' || sysdate);
  select count(*) into v_tmpcounter 
            from cxdsg_stateinfo si where not exists ( select processid from cxdsg_cdocpool dp where dp.processid=si.cxdsg_procinfo_id) and startdate<v_olderthanweekI;
  dbms_output.put_line('cxdsg_stateinfo    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  
  loop     
      delete from cxdsg_stateinfo si where not exists ( select processid from cxdsg_cdocpool dp where dp.processid=si.cxdsg_procinfo_id) and startdate<v_olderthandayI and rownum < 100000;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_stateinfo    Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
  dbms_output.put_line('cxdsg_stateinfo    END # ' || sysdate); 


/*
  dbms_output.put_line('cxdsg_processinfo    START # ' || sysdate);
  select count(*) into v_tmpcounter from cxdsg_processinfo where enddate is not null and enddate<v_olderthandayI  ;
  dbms_output.put_line('cxdsg_processinfo    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from cxdsg_processinfo where enddate is not null and enddate<v_olderthandayI  and rownum < 100000;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_processinfo    Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
  dbms_output.put_line('cxdsg_processinfo    END # ' || sysdate);
*/


  dbms_output.put_line('cxdsg_processinfo    START # ' || sysdate);
  select count(*) into v_tmpcounter 
             from cxdsg_processinfo where startdate is not null and startdate<v_olderthandayI and id not in (select processid from cxdsg_cdocpool) ;
  dbms_output.put_line('cxdsg_processinfo    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from cxdsg_processinfo where startdate is not null and startdate<v_olderthandayI and id not in (select processid from cxdsg_cdocpool)  and rownum < 100000;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_processinfo    Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
  dbms_output.put_line('cxdsg_processinfo    END # ' || sysdate);


  dbms_output.put_line('cxdsg_stateinfo    START # ' || sysdate);
  select count(*) into v_tmpcounter 
            from cxdsg_stateinfo si where not exists ( select id from  cxdsg_processinfo pi where pi.id=si.cxdsg_procinfo_id) and startdate<v_olderthanweekI;
  dbms_output.put_line('cxdsg_stateinfo    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  
  loop     
      delete from cxdsg_stateinfo si where not exists ( select id from  cxdsg_processinfo pi where pi.id=si.cxdsg_procinfo_id) and startdate<v_olderthanweekI and rownum < 100000;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_stateinfo    Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
  dbms_output.put_line('cxdsg_stateinfo    END # ' || sysdate); 

  
  dbms_output.put_line('cxdsg_flowengdat    START # ' || sysdate);
  select count(*) into v_tmpcounter 
             from cxdsg_flowengdat where timestamp<v_olderthanweekI and processid not in (select id from cxdsg_processinfo);
  dbms_output.put_line('cxdsg_flowengdat    Items: ' ||  v_tmpcounter ||'  '|| sysdate);
  loop     
      delete from cxdsg_flowengdat where timestamp<v_olderthanweekI and processid not in (select id from cxdsg_processinfo) and rownum < 100000;
      exit when SQL%rowcount < 100000;
      commit;
      dbms_output.put_line('cxdsg_flowengdat    Deleted 100000 items  '|| sysdate);
  end loop;
  commit; 
  dbms_output.put_line('cxdsg_flowengdat    END # ' || sysdate);
 
 select to_char(sysdate, 'DD') into  dayofmonth from dual;
 if (dayofmonth='14') then
  dbms_output.put_line('CleaningJob    AlterTable Start # ' || sysdate);
	 EXECUTE IMMEDIATE 'alter table CXDSG_CDOCPOOLDAT modify lob (BINARYOBJECT) (shrink space)';
	 EXECUTE IMMEDIATE 'alter table CXDSG_FEBINDAT modify lob (DOCXML) (shrink space)';
 dbms_output.put_line('CleaningJob    AlterTable  FINISH # ' || sysdate);
end if;
 
 if (dayofmonth='28') then
  dbms_output.put_line('CleaningJob    AlterTable Start # ' || sysdate);
	 EXECUTE IMMEDIATE 'alter table CXDSG_CDOCPOOLDAT modify lob (BINARYOBJECT) (shrink space)';
	 EXECUTE IMMEDIATE 'alter table CXDSG_FEBINDAT modify lob (DOCXML) (shrink space)';
 dbms_output.put_line('CleaningJob    AlterTable  FINISH # ' || sysdate);
end if;
 
 dbms_output.put_line('CleaningJob    FINISH # ' || sysdate);


EXCEPTION
    WHEN OTHERS
    THEN
       DBMS_OUTPUT.PUT_LINE(SQLERRM);
 END;
/

exit

