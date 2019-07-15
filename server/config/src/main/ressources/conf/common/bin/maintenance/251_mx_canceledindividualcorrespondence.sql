alter session set nls_date_format='DD.MM.YYYY HH24:MI:SS';
SET SERVEROUTPUT ON SIZE UNLIMITED;
-- DBMS_OUTPUT.ENABLE (buffer_size => NULL);

declare
 v_olderthanhour constant timestamp:=(sysdate - interval '1' hour);
 v_olderthanhourI Number(15);
 v_olderthanday constant timestamp:=(sysdate - interval '1' day);
 v_olderthandayI Number(15);
 v_now constant timestamp:=(sysdate );
 v_nowI Number(15);
 
 v_tmpcounter Number(15);
begin
--SET AUTOCOMMIT ON;
--dbms_output.enable;
SELECT DATETOITYXTIME(v_olderthanhour) into v_olderthanhourI from dual;
SELECT DATETOITYXTIME(v_olderthanday) into v_olderthandayI from dual;
SELECT DATETOITYXTIME(v_now) into v_nowI from dual;

  dbms_output.put_line('CleaningJob    START # ' || sysdate);
  dbms_output.put_line('CleaningJob    v_nowI  ' || v_nowI);
  dbms_output.put_line('CleaningJob    v_olderthanhourI  ' || v_olderthanhourI);
  dbms_output.put_line('CleaningJob    v_olderthandayI  ' || v_olderthandayI);
 
  dbms_output.put_line('MX:AbgebrocheneSiebelKorrespondenzDELETE   START # ' || sysdate);
  select  count(*) into v_tmpcounter  --f.id frageid, e.id emailid 
  from frage f, email e  where f.emailid=e.id and vorgangid=0 and globalerstatus='Servicecenter' and status='erledigt' and comments='Cleanup AbgebrocheneSiebelKorrespondenz' and e.emaildate<v_olderthandayI;
  dbms_output.put_line('MX:AbgebrocheneSiebelKorrespondenzDELETE   Items #' ||  v_tmpcounter ||'  '|| sysdate);
  
  for rec in (select f.id frageid, e.id emailid, f.erledigtvon erledigtvon, e.emaildate emaildate 
  from frage f, email e  where f.emailid=e.id and vorgangid=0 and globalerstatus='Servicecenter' and status='erledigt' and comments='Cleanup AbgebrocheneSiebelKorrespondenz' and e.emaildate<v_olderthandayI order by f.id) loop  
	      dbms_output.put_line('AbgebrocheneSiebelKorrespondenzDELETE DELETION # ' || rec.frageid || ' ' ||rec.emaildate);
        --delete from mitarbeiterlog where frageid=rec.frageid;;
        --delete from frage where id=rec.frageid;        
  end loop;  
  commit;
  dbms_output.put_line('MX:AbgebrocheneSiebelKorrespondenzDELETE   DONE # ' || sysdate);
 
  dbms_output.put_line('MX:AbgebrocheneSiebelKorrespondenz   START # ' || sysdate);
  select  count(*) into v_tmpcounter  --f.id frageid, e.id emailid 
  from frage f, email e  where f.emailid=e.id and vorgangid=0 and globalerstatus='Servicecenter' and status='neu'  and subject is null and length(body)<=1 and e.emaildate<v_olderthanhourI;
  dbms_output.put_line('MX:AbgebrocheneSiebelKorrespondenz   Items #' ||  v_tmpcounter ||'  '|| sysdate);
  
  for rec in (select f.id frageid, e.id emailid, f.erledigtvon erledigtvon, e.emaildate emaildate from frage f, email e   
              where f.emailid=e.id and vorgangid=0 and globalerstatus='Servicecenter' and status='neu'  and subject is null and length(body)<=1 and e.emaildate<v_olderthanhourI order by f.id) loop  
	      dbms_output.put_line('AbgebrocheneSiebelKorrespondenz    MarkAsDeleted # ' || rec.frageid || ' ' ||rec.emaildate);
        update frage set status='erledigt', geloeschtam=v_nowI, bearbeitungsende=rec.emaildate, comments='Cleanup AbgebrocheneSiebelKorrespondenz' where id=rec.frageid;
        insert into mitarbeiterlog (mitarbeiterid, frageid,antwortid,aktion,parameter,zeit,oper) values (rec.erledigtvon, rec.frageid, 0, 19, 'DWH - Cleanup AbgebrocheneSiebelKorrespondenz', rec.emaildate, 0);
  end loop;  
  commit;
  dbms_output.put_line('MX:AbgebrocheneSiebelKorrespondenz   DONE # ' || sysdate);

EXCEPTION
    WHEN OTHERS
    THEN
       DBMS_OUTPUT.PUT_LINE(SQLERRM);
 END;
/

exit

