
-- VORGEHENSWEISE -------------------------------------------------------------
-- 1. ES MUESSEN UPDATE-STATEMENTS (6 STUECK - 3 FUER DIE TABELLE AR_FRAGE_JN 
--    UND 3 FUER DIE TABELLE AR_FRAGEZUAGENTUR_JN) UEBER EINEN SCHEDULE-JOB 
--    ALLE 15 MINUTEN AUSGEFUEHRT WERDEN - DIE SCHEDULE-JOBs SIND EINZURICHTEN
--    (S. AUCH DATEI X.CREATE.JOURNAL_TABELLEN.AUSLIEFERUNG.sql) UND SOLLTEN 
--    EINMAL PER HAND GESTARTET WERDEN
-------------------------------------------------------------------------------

-- UPDATEFUNKTIONEN AUF DEN JOURNAL-TABELLEN -- SCHEDULE -- ANFANG ------------
UPDATE ar_frage_jn up
   SET dauerende = ( SELECT MIN(created) 
                       FROM ar_frage_jn sp
                      WHERE up.frage = sp.frage
                        AND up.logid < sp.logid ) 
 WHERE dauerende is NULL
   AND EXISTS (SELECT 1 
                 FROM ar_frage_jn sp
                WHERE up.frage = sp.frage
                  AND up.logid < sp.logid )
;
COMMIT ;
UPDATE ar_frage_jn up
   SET dauerende = ityxtimetodate(bearbeitungsende)
 WHERE dauerende IS NULL
   AND bearbeitungsende IS NOT NULL
;
COMMIT ;
UPDATE ar_frage_jn up
   SET dauerfix = ROUND ((DauerEnde-CREATED)*60*24,0)
 WHERE dauerEnde IS NOT NULL
   AND dauerfix  IS NULL
;
COMMIT ;
-------------------------------------------------------------------------------
UPDATE ar_fragezuagentur_jn up
   SET DauerEnde = ( SELECT MIN(created) 
                       FROM ar_fragezuagentur_jn sp
                       WHERE up.frage = sp.frage
                         AND up.logid < sp.logid ) 
 WHERE dauerende IS NULL
   AND EXISTS (SELECT 1 FROM ar_fragezuagentur_jn sp
                WHERE up.frage = sp.frage
                  AND up.logid < sp.logid )
;
COMMIT ;
UPDATE ar_fragezuagentur_jn up
   SET dauerende = erledigtam 
 WHERE dauerende IS NULL
   AND erledigtam IS NOT NULL
;
COMMIT ;
UPDATE ar_fragezuagentur_jn up
   SET dauerfix = ROUND ((dauerende-created)*60*24,0)
 WHERE dauerende IS NOT NULL
   AND dauerfix  IS NULL
;
COMMIT ; 
-- UPDATEFUNKTIONEN AUF DEN JOURNAL-TABELLEN -- SCHEDULE -- ENDE --------------


update AR_FRAGEZUAGENTUR_JN o 
set (agentur, ZUGEWIESENAM , ROUTING) = (
    select agentur, ZUGEWIESENAM , ROUTING 
     from AR_FRAGEZUAGENTUR_JN u 
    where u.frage=o.frage 
      and u.logid = (select max(uu.logid) from AR_FRAGEZUAGENTUR_JN uu where uu.frage=u.frage  and uu.logid < o.logid)
)    
where Operation='A' and agentur is null and zugewiesenam is null and routing is null
;
commit;

exit

