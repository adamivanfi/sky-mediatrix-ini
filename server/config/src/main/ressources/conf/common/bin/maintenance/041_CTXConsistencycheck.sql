-- Wichtig: diese Checks dürfen nur während des Downtime (restart/deployment) ausgeführt werden


-- Vergleich der Fragen in Mx Mi und CTX MI
-- select ctx_manuelle_indizierung.counter - mx_manuelle_indizierung.counter MiMxCxDiff
-- from (select count(*) counter from cxdsg_cdocpool where parameter like '66%' and status=3) ctx_manuelle_indizierung,
--     (select count(*) counter from frage@Mediatrix.WORLD where teilprojektid=1125 and status <> 'erledigt' and geloeschtam=0) mx_manuelle_indizierung;


-- Setze alle Fragen die in MX MI abgeschlossen wurden aber die CTX-MI Prozesse nicht abgeschlossen sind auf Status Error diese werden dann von 320_1h korrigiert
--update cxdsg_cdocpool 
--set status=16
--where status=3 and parameter like '66%' 
--  and id not in (select e.extra2 from frage@Mediatrix.WORLD f, email@Mediatrix.WORLD e where f.emailid=e.id and teilprojektid=1125 and status <> 'erledigt' and geloeschtam=0)
--;
--commit;

-- Setze alle CDocs die während des neustarts in nicht stoppbaren Prozessen hingen auf status Wait
update cxdsg_cdocpool 
set status=0, locking=null, locktime=0, prio=8, processid=0, comment_text='Document hing in Runnning waehrend neustarts'
where status=1 
;
commit;

-- Entspere alle Fragen von Poollock
delete from cxdsg_poollock where timestamp< datetoityxtime(sysdate-interval '1' MINUTE);
;


update CXSCH_PROCESSORDER set status='CREATED' where status='WORKING';
update CXDSG_CDOCPOOL set status=0, LOCKING=null, LOCKTIME=0 where status=1;
update CXDSG_CDOCPOOL set LOCKING=null, LOCKTIME=0 where LOCKING is not null;


-- Nachbessern - automatismus einbauen
-- Consistency Check: 117 ctxwflproc 66X_ManuallIndexing_MxCxDiff
-- finde alle Fragen in CX die keine Frage in MX MI haben
select cx.* --, mx.emailid, mx.frageid
from
 (select id, collectionid, status, ityxtimetodate(createtime) from ityx_cx.cxdsg_cdocpool where parameter like '66%' and status=3) cx
 where cx.id not in  (select --f.id frageid, e.id emailid, 
 extra2 docpoolid from frage@Mediatrix.WORLD f, email@Mediatrix.WORLD e 
    where e.id=f.emailid and f.teilprojektid=1125 and status = 'klassifikation' and geloeschtam=0 and extra2 is not null)
;
--select * from cxdsg_cdocpool where id in (15307388);
--update cxdsg_cdocpool set status=0 where id =15307388 and status=3;commit;
   
-- finde alle Fragen in MXMI die keine Frage in CX haben    
select f.id frageid, f.vorgangid, f.status, e.id emaildid, e.extra2 docpoolid, ityxtimetodate(bearbeitungsende)
 from frage@Mediatrix.WORLD f, email@Mediatrix.WORLD e 
    where e.id=f.emailid and f.teilprojektid=1125 and status <> 'erledigt' and geloeschtam=0 and extra2 is not null
    and extra2 not in (select id --, collectionid, status, ityxtimetodate(createtime) 
    from ityx_cx.cxdsg_cdocpool where parameter like '66%' and status=3)
; 

commit;
exit