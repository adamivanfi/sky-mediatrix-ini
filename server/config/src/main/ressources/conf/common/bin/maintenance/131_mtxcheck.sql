
-- Flow engine Fehler in der MX nach einem Neustart während betriebs
update frage 
set geloeschtam=datetoityxtime(sysdate), status='erledigt', comments='Automatisch korrigert wegen FlowEngineFehler'||sysdate
where id in 
(select f.id --, e.id, e.extra1, e.extra2, d.id, d.collectionid, d.status 
from frage f, email e, cxdsg_cdocpool@CONPROD.SKY.DE d
where
f.emailid=e.id and
e.extra2=d.id and
f.status='hold_klassifikation'
and geloeschtam=0
and d.status not in (3)
);

commit;

exit