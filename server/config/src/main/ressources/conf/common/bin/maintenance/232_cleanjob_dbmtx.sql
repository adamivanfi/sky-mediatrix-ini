
-- Entsperre alle gesperrten Fragen die zur abgeschlossenen Sessions zugeordnet sind welche seit 2h nicht mehr aktiv sind

--select ityxtimetodate(f.gesperrtam), f.* from frage f
update frage f set f.gesperrtam=0, f.gesperrtvon=0 
where f.gesperrtvon not in (0,2, 18800) 
and f.geloeschtam=0
and f.gesperrtam between 1 and datetoityxtime(sysdate -interval '3' hour)
and not exists (select * from sessions s where f.gesperrtvon=mitarbeiterid and endtime=0 and lasthit > datetoityxtime(sysdate -interval '2' hour))
;
commit;

-- workaround für Probleme mit agenturzuweisung nach weiterletiugn
delete from ar_agentur_blacklist where agentur = 0; 
commit;

-- Bereinigung der AgMon Journaltabellen
delete from ar_event_jn jn
 where jn.created < (sysdate - interval '10' day)
   and exists ( select *
                  from frage f
                 where f.status = 'erledigt' 
                 and f.bearbeitungsende < datetoityxtime(sysdate - interval '10' day)
                   and f.id = jn.frageid ) 
;
commit;


/*delete from ar_monitoring 
where
(ZUGANG +
ZUGANG_UMTYPISIERUNG +
BEARBEITET +
BEARBEITET_SLA+
UMTYPISIERT+
ABGANG+
OFFEN+
OFFEN_SLA+
ERZEUGTE_WIEDERVORLAGEN+
OFFENE_WIEDERVORLAGEN+
ESKALIERT_1+
ESKALIERT_2+
BEARBEITUNGSZEIT+
LIEGEZEIT+
BEARBEITET_24+
BEARBEITET_SLA_24)=0
and datum < datetoityxtime( sysdate - 2)
;
commit;
*/

exit;