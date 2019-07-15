
declare
 v_lastmidnight constant timestamp:=trunc(sysdate);
 v_lastmidnightI Number(15);

begin
  SELECT DATETOITYXTIME(v_lastmidnight ) into v_lastmidnightI from dual;
  dbms_output.put_line('MALOG Workaround    START # ' || sysdate);

  for rec in (select id,bearbeitungsende,erledigtvon from frage where status='erledigt' and bearbeitungsende> v_lastmidnightI ) loop  
	insert into mitarbeiterlog (mitarbeiterid, frageid,antwortid,aktion,parameter,zeit,oper) values (rec.erledigtvon, rec.id, 0, 19, 'DWH - Dailycheck', rec.bearbeitungsende, 0);
        dbms_output.put_line('MALOG Workaround    Insert # ' || rec.id || ' ' ||rec.bearbeitungsende);
  end loop;  
  commit;

  dbms_output.put_line('MALOG Workaround    FINISH# ' || sysdate);

end;
/


exit