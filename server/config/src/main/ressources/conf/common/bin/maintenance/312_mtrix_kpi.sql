

INSERT INTO NTT_KPI_OVERVIEW (
      ctimestamp ,
      ityx_alle_fragen ,
      ityx_neue_fragen ,
      ityx_alte_fragen ,
      ityx_user_loggedin ,
      ityx_user_actions ,
      db_max_cursor_session ,
      db_all_cursors,
      db_ityx_mx_sessions,
      ityx_double_sends )
  SELECT 
    (SELECT sysdate ctimestamp FROM dual) ,
    (SELECT COUNT(*) ityx_alle_fragen FROM frage) ,
    (SELECT COUNT(*) ityx_neue_fragen FROM frage WHERE status LIKE '%neu%') ,
    (SELECT COUNT(*) ityx_alte_fragen FROM frage WHERE status NOT LIKE '%neu%'),
    (SELECT COUNT(*) ityx_user_loggedin FROM sessions WHERE endtime=0),
    (SELECT COUNT(*) ityx_user_actions FROM mitarbeiterlog where  zeit > datetoityxtime(TRUNC(sysdate))),
 0,0,0,
    (select count(*) from (select frageid, antwortid, count(*) from mitarbeiterlog where aktion=43 and  zeit > datetoityxtime(sysdate -interval '1' DAY) group by frageid, antwortid having count(*)>1))
    FROM dual ;


commit;


exit