alter session set nls_date_format='DD.MM.YYYY HH24:MI:SS';
set serveroutput on;

declare
 v_olderthanweek constant timestamp:=(sysdate - interval '7' day);
 v_olderthanweekI Number(15); 

 v_olderthanthreehours constant timestamp:=(sysdate - interval '3' hour);
 v_olderthanthreehoursI Number(15); 

begin
  SELECT DATETOITYXTIME(v_olderthanweek) into v_olderthanweekI from dual;
  SELECT DATETOITYXTIME(v_olderthanthreehours) into v_olderthanthreehoursI from dual;


  dbms_output.put_line('LETTER_MONITORING_FIX   START # ' || sysdate);

-- vormals LETTER_MONITORING_FIX();
  for rec in (select distinct(a.id) aid from antwort a, email e, mitarbeiterlog m 
                where e.id=a.emailid and a.id=m.antwortid 
                and e.typ=6 and m.aktion=43 and a.status='ueberwacht'
                and e.emaildate> v_olderthanweekI  and e.emaildate<v_olderthanthreehoursI ) loop  
      dbms_output.put_line('Correct Antwort:'||rec.aid);
      update antwort set status='erledigt' where id=  rec.aid;
  end loop;  commit;

-- Führt zur PRoblemen mit Überwachten Fragen in der Tabelle ar_fragezuragentur
-- dbms_output.put_line('AR_COMPLETED_FIX   START # ' || sysdate);
-- vormals exec AR_COMPLETED_FIX();
--  for rec in (select f.id frageid, bearbeitungsende from ar_fragezuagentur a, frage f where a.erledigtam=0 and f.status='erledigt' and f.id=a.frage) loop  
--      dbms_output.put_line('Correct Frage:'||rec.frageid||' bearbeitungsende:' || rec.bearbeitungsende);
--      update ar_fragezuagentur set erledigtam=rec.bearbeitungsende where frage=rec.frageid;
--  end loop;
--commit;

 dbms_output.put_line('AR_Zuordnung_FIX   START # ' || sysdate);
-- Laufzeit ca. 1 Minute
  for rec in (select f.id frageid, e.extra10 agenturold, aa.name agenturnew, e.id emailid
     from frage f, ar_fragezuagentur a, ar_agentur aa, (select id, extra10 from  email where emaildate>v_olderthanweekI) e
     where f.emailid=e.id and f.id=a.frage and aa.id=a.agentur and aa.name<>e.extra10) loop  
      dbms_output.put_line('Correct Frage:'||rec.frageid||' from agency: ' || rec.agenturold || ' to agency:' ||rec.agenturnew);
      update email set extra10=rec.agenturnew where id = rec.emailid;
  end loop;
 COMMIT;

dbms_output.put_line('MTX_FIX   ENDE# ' || sysdate);

END;
/

exit