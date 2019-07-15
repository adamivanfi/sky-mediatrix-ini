-- **********************************************************************
-- * Alf Schiche / 14.11.2012 / delete_dms_trg_results.sql              *
-- * ------------------------------------------------------------------ *
-- * Inhalt:       Routine: Lösche Trigger output                       *
-- * -------                                                            *
-- *                                                                    *
-- * Kurzbeschreibung:                                                  *
-- * -----------------                                                  *
-- * Routine PR_DELETE_MEDIATRIX_JN im Schema DWH_ETL auf Datenbank     *
-- * Mediatrix löscht Trigger Output Sätze, die älter als 14 Tage sind. *
-- * Das Skript sollte am besten einmal täglich laufen (Zeit egal).     *
-- * Aufruf als User DWH_ETL.                                           *
-- * ------------------------------------------------------------------ *
-- * Aufruf          : start delete_dms_trg_results                     *
-- * Shell-Skript    : -                                                *
-- * interne Aufrufe : -                                                *
-- * Transaktion     : -                                                *
-- * ------------------------------------------------------------------ *
-- * Aenderungsvermerk:                                                 *
-- * ------------------                                                 *
-- * Nr. !   Datum    !  Bearbeiter  ! Beschreibung                     *
-- * ------------------------------------------------------------------ *
-- *     !            !              !                                  *
-- **********************************************************************
declare
  v_days   number := 14;
begin
  DWH_ETL.PR_DELETE_MEDIATRIX_JN (v_days);
end;
/

exit