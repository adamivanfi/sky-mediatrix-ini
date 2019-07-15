CREATE OR REPLACE SYNONYM EBM_CONFIG FOR DWH_ETL.EBM_CONFIG;
CREATE OR REPLACE SYNONYM EBMD_EVENTGROUP_DSC FOR DWH_ETL.EBMD_EVENTGROUP_DSC;
CREATE OR REPLACE SYNONYM EBMD_EVENT_DSC FOR DWH_ETL.EBMD_EVENT_DSC;
CREATE OR REPLACE SYNONYM EBMD_QTYPE_DESC FOR DWH_ETL.EBMD_QTYPE_DESC;
CREATE OR REPLACE SYNONYM EBMD_CHANNEL FOR DWH_ETL.EBMD_CHANNEL;
CREATE OR REPLACE SYNONYM EBMD_TENANT FOR DWH_ETL.EBMD_TENANT;
CREATE OR REPLACE SYNONYM EBMD_SUBPROJECT FOR DWH_ETL.EBMD_SUBPROJECT;
CREATE OR REPLACE SYNONYM EBMD_AGENCY FOR DWH_ETL.EBMD_AGENCY;
CREATE OR REPLACE SYNONYM EBMD_KEYWORD FOR DWH_ETL.EBMD_KEYWORD;
CREATE OR REPLACE SYNONYM EBMD_KEYWORDGROUP FOR DWH_ETL.EBMD_KEYWORDGROUP;
CREATE OR REPLACE SYNONYM EBMD_AGENT FOR DWH_ETL.EBMD_AGENT;
CREATE OR REPLACE SYNONYM EBMD_AGENCY_SLA FOR DWH_ETL.EBMD_AGENCY_SLA;
CREATE OR REPLACE SYNONYM EBMF_EVENT_JN FOR DWH_ETL.EBMF_EVENT_JN;
CREATE OR REPLACE SYNONYM EBMF_QUESTIONMETA FOR DWH_ETL.EBMF_QUESTIONMETA;
CREATE OR REPLACE SYNONYM EBMF_QUESTIONKEYWORD_JN FOR DWH_ETL.EBMF_QUESTIONKEYWORD_JN;
  INSERT
  INTO EBM_CONFIG
    (
      NAME,
      VALUE_NUM,
      VALUE_STRING,
      UNITTYPE
    )
    VALUES
    (
      'SKYSERVICELEVELZEIT_FAX',
      '48',
      NULL,
      'STUNDEN'
    );
  INSERT
  INTO EBM_CONFIG
    (
      NAME,
      VALUE_NUM,
      VALUE_STRING,
      UNITTYPE
    )
    VALUES
    (
      'SKYSERVICELEVELZEIT_LETTER',
      '48',
      NULL,
      'STUNDEN'
    );
  INSERT
  INTO EBM_CONFIG
    (
      NAME,
      VALUE_NUM,
      VALUE_STRING,
      UNITTYPE
    )
    VALUES
    (
      'SKYSERVICELEVELZEIT_EMAIL',
      '24',
      NULL,
      'STUNDEN'
    );
  INSERT
  INTO EBMD_EVENTGROUP_DSC
    (
      EVENTGROUP,
      DESCRIPTION
    )
    VALUES
    (
      'P',
      'Processbezogene Events'
    );
  INSERT
  INTO EBMD_EVENTGROUP_DSC
    (
      EVENTGROUP,
      DESCRIPTION
    )
    VALUES
    (
      'A',
      'Agenturzuweisungsevents'
    );
  INSERT
  INTO EBMD_EVENTGROUP_DSC
    (
      EVENTGROUP,
      DESCRIPTION
    )
    VALUES
    (
      'T',
      'Teilprojektevents'
    );
  INSERT
  INTO EBMD_EVENTGROUP_DSC
    (
      EVENTGROUP,
      DESCRIPTION
    )
    VALUES
    (
      'M',
      'Mitarbeiterbezogene Events'
    );
  INSERT
  INTO EBMD_EVENTGROUP_DSC
    (
      EVENTGROUP,
      DESCRIPTION
    )
    VALUES
    (
      'F',
      'Fragebezogene Events'
    );
  INSERT
  INTO EBMD_EVENTGROUP_DSC
    (
      EVENTGROUP,
      DESCRIPTION
    )
    VALUES
    (
      'C',
      'Kunden-Zuordnung Events'
    );
  INSERT
  INTO EBMD_EVENTGROUP_DSC
    (
      EVENTGROUP,
      DESCRIPTION
    )
    VALUES
    (
      'R',
      'Antwortbezogene Events'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'AD',
      'A',
      'AgenturDeleted',
      'Die Zuweisung einer Frage zur Agentur wurde w�hrend eines Technischen Events aufgel�st'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'AE',
      'A',
      'AgenturEnde',
      'Die Zuweisung einer Frage zur Agentur wurde nach Abschluss der Frage aufgel�st'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'AF',
      'A',
      'AgenturFrom',
      'Frage wurde einer Agentur entnommen (AS Daemon, Manuell) � �nderungtracking der FrageZuAgentur Verkn�pfung'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'AT',
      'A',
      'AgenturTo',
      'Frage wurde einer neuen Agentur zugeleitet (AS Daemon, Manuell) - �nderungtracking der FrageZuAgentur Verkn�pfung'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'AU',
      'A',
      'AgenturUpdate',
      'Es fand ein Update der FrageZuAgentur-Zuordnungstabelle statt, welcher keinen fachlichen Event zugeordnet werden kann'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'AX',
      'A',
      'AgencyReassigment',
      'Aufl�sung der Agenturzuweisung und sofortige Zuweisung der vorherrigen Agentur (Technisches Event)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'DF',
      'A',
      'DirectContactFrom',
      'Weiterleitung von einen anderen Agentur verursacht durch Telefonticket (Ticket einer anderen Agentur wurde �ber die Suche gefunden und von Bearbeiter einer anderen Agentur abgeschlossen)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'DT',
      'A',
      'DirectContactTo',
      'Weiterleitung zur eigenen Agentur verursacht durch Telefonticket (Ticket einer anderen Agentur wurde �ber die Suche gefunden und von Bearbeiter einer anderen Agentur abgeschlossen)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'LF',
      'A',
      'AgencyLessFrom',
      'Eine Frage die der Agentur Sky zugewiesen ist, wird durch eine Andere Agentur bearbeitet. Entspricht AF wenn die vorherrige Agentur ''SKY'' war. (''Agenturloses/Agentur�bergreifendes Routing'')'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'LT',
      'A',
      'AgencyLessTo',
      'Eine Frage die der Agentur Sky zugewiesen ist, wird durch eine Andere Agentur bearbeitet. Entspricht AT wenn die vorherrige Agentur ''SKY'' war. (''Agenturloses/Agentur�bergreifendes Routing'')'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'CD',
      'C',
      'CustomerDeindexed',
      'Zuweisung einer Frage zum Kunden wurde aufgel�st'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'CI',
      'C',
      'CustomerIndexed',
      'Frage wurde einem Kunden zugewiesen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'CR',
      'C',
      'CustomerReindexing',
      'Zuweisung einer Frage zum Kunden wurde ge�ndert (neuer Kunde wurde indiziert)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'CU',
      'C',
      'CusomterIndexingUpdate',
      'Ein Attribut der Kundenindizierung wurde ge�ndert (technisches Event ohne funktionelle Bedeutung)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'EB',
      'F',
      'ExternBeantwortet',
      'Frage wurde extern beantwortet'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'EE',
      'F',
      'ExternalEnd',
      'Die ExternWeitergeleitete Frage wurde abgeschlossen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'EK',
      'F',
      'ExternKentnisnahme',
      'Frage wurde extern Weitergeleitet mit dem Attribut: zur Kentnissnamhe'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'ET',
      'F',
      'ExternTransfered (ohne R�ckantwort)',
      'Frage extern weitergeleitet, ohne auf eine R�ckantwort zu warten'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'EW',
      'F',
      'ExternWeitergeleitet (R�ckantwort)',
      'Frage extern Weitergeleitet, wartet auf R�ckantwort'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'FD',
      'F',
      'FrageDeleted',
      'Frage wurde als gel�scht markiert'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'FE',
      'F',
      'FrageEnd',
      'Abschlussevent einer Frage � die Frage wurde abgeschlossen (ggf. nach Qualit�tssicherung). Nicht zwingend als letztes Event.'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'FI',
      'F',
      'FrageImmediatelyClosed',
      'Frage wurde automatisch (Sofort) abgeschlossen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'FP',
      'F',
      'FragePreStart',
      'Event f�r Zeitpunkt des Abholens der Frage von Email-Daemon und �bergabe an Backend-Verarbeitung an CONTEX [technisch, optional, nur f�r Channel:Email]'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'FS',
      'F',
      'FrageStart',
      'Eingang einer gew�hnlicher (TP-) Frage ins Mediatrix mit Freigabe zur Bearbeitung (Preprocessing des Dokuments wurde in Contex abgeschlossen)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'FU',
      'F',
      'FrageUpdate',
      'Technisches Event ohne funktionelle Bedeutung, wird erzeugt durch Attributupdate [technical]'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'IE',
      'F',
      'IndivudualkorrespondenzEnde',
      'Markiert den Abschluss der im Mediatix ge�fnetten Individual Korrespondenz-Fragen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'IS',
      'F',
      'IndividualkorrespondenzStart',
      'Markiert den Startpunkt der im Mediatix ge�fnetten Individual Korrespondenz-Fragen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'ME',
      'F',
      'ManuelleIndizierungEnde',
      'Dieses Event signalisiert den Abschluss einer Frage der manueller Indizierung.'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'MS',
      'F',
      'ManuelleIndizierungStart',
      'Eingang einer Fragen der speziellen Projekten der Manueller Indizierung (die organisatorisch zur Backend-Verarbeitung geh�ren) werden speziell ausgewiesen. Dieser Event definiert die Erzeugung der MI-Frage in Mediatrix'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'QE',
      'F',
      'QualiEnd',
      'Qualit�tspr�fung wurde abgeschlosen / ist abgelaufen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'QS',
      'F',
      'QualiStart',
      'Fragen die seitens der Agenten abgeschlossen wurden und ins Qualit�tscheck-Status �bergeben wurden'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'WE',
      'F',
      'WiedervorlageEnd',
      'Die Wiedervorlage f�r eine Frage wurde beendet'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'WS',
      'F',
      'WiedervorlageStart',
      'Frage wurde ins Wiedervorlage-Status gestellt'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'WU',
      'F',
      'WiedervorlageUpdate',
      'Attribut einer Wiedervorlage wurde ge�ndert - neue Wiedervorlage Zeit'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'PL',
      'P',
      'ProcessLock BearbeitungsStart',
      'Frage wurde von einem Agenten ins Bearbeitung genommen (f�r die Bearbeitung gesperrt)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'PR',
      'P',
      'ProcessRelease BearbeitungsEnde',
      'Frage wurde von einem Agenten nach der Bearbeitung freigegeben (nach der Bearbeitung entsperrt)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RA',
      'R',
      'ResponseInAbsendung',
      'Die Antwort wird gesendet/abgeschlossen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RD',
      'R',
      'ReponseDelete',
      'Antwort wurde gel�scht'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RE',
      'R',
      'ResponseEnd',
      'Antwort wird Abegeschlossen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RI',
      'R',
      'ResponseImmediatelyClosed',
      'Antwort wurde sofort geschlossen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RL',
      'R',
      'ResponseLock',
      'Antwort wurde von einem Mitarbeiter in Bearbeitung genommen'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RQ',
      'R',
      'ResponseQualiCheck',
      'Antwort wurde in Qualit�tssicherung �bergeben'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RR',
      'R',
      'ResponseReleased',
      'Die Antwort zur einer Frage wurde nach der Bearbeitung freigegeben/entsperrt'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RS',
      'R',
      'ResponseStart',
      'Eine (Teil-/Zwischen-/Platzhalter-) Antwort zur einer Frage wird initial erstellt'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RU',
      'R',
      'ResponseUpdate',
      'Ein Attribut der Antwort hat sich ge�ndert (technisches Event ohne funktionelle Bedeutugn)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'RZ',
      'R',
      'ResponseTeilantwortZwischenbescheid',
      'Die Antwort ist in Teilantwort/Zwischenbescheid-Status'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'TF',
      'T',
      'TeilprojektWeiterleitungFrom',
      'Frage wurde von einem Teilprojekt in ein anderes weitergeleitet. Dieser Event dokumentiert die Wegnahme der Frage von dem letzten Teilprojekt.'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'TT',
      'T',
      'TeilprojektWeiterletungTo',
      'Frage wurde von einem Teilprojekt in ein anderes weitergeleitet. Dieser Event dokumentiert den Zugang in das neue Teilprojekt.'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'TH',
      'T',
      'TeilprojektHinzuleitung',
      'Frage wurde einem Teilprojekt hinzugeleitet (TT) wenn der Wechsel der Teilprojekte von einem Agenten ohne Agenturzugeh�rigkeit erfolgte (OpsCon)'
    );
  INSERT
  INTO EBMD_EVENT_DSC
    (
      EVENT,
      EVENTGROUP,
      NAME,
      DESCRIPTION
    )
    VALUES
    (
      'TW',
      'T',
      'TeilprojektWegnahme',
      'Frage wurde einem Teilprojekt entzogen (TF) wenn der Wechsel der Teilprojekte von einem Agenten ohne Agenturzugeh�rigkeit erfolgte (OpsCon)'
    );
  INSERT
  INTO EBMD_QTYPE_DESC
    (
      FTYPE,
      DESCRIPTION
    )
    VALUES
    (
      '1',
      'Standard TP-Frage'
    );
  INSERT
  INTO EBMD_QTYPE_DESC
    (
      FTYPE,
      DESCRIPTION
    )
    VALUES
    (
      '2',
      'Manuelle Indizierung (MS)'
    );
  INSERT
  INTO EBMD_QTYPE_DESC
    (
      FTYPE,
      DESCRIPTION
    )
    VALUES
    (
      '3',
      'IndividualCorrespondence (IS)'
    );
  INSERT
  INTO EBMD_QTYPE_DESC
    (
      FTYPE,
      DESCRIPTION
    )
    VALUES
    (
      '4',
      'Extern weitergeleitet Kentnisnahme (EK)'
    );
  INSERT
  INTO EBMD_QTYPE_DESC
    (
      FTYPE,
      DESCRIPTION
    )
    VALUES
    (
      '5',
      'Extern weitergeleitet mit R�ckantwort (EW)'
    );
  INSERT
  INTO EBMD_QTYPE_DESC
    (
      FTYPE,
      DESCRIPTION
    )
    VALUES
    (
      '6',
      'Extern weitergeleitet ohne R�ckantwort (ET)'
    );
  INSERT
  INTO EBMD_QTYPE_DESC
    (
      FTYPE,
      DESCRIPTION
    )
    VALUES
    (
      '7',
      'Automatisch verarbeitet (FI)'
    );
  INSERT INTO EBMD_CHANNEL
    (CHANNEL,DESCRIPTION
    ) VALUES
    ('EMAIL','Email'
    );
  INSERT INTO EBMD_CHANNEL
    (CHANNEL,DESCRIPTION
    ) VALUES
    ('FAX','Fax'
    );
  INSERT INTO EBMD_CHANNEL
    (CHANNEL,DESCRIPTION
    ) VALUES
    ('BRIEF','Letter'
    );
  INSERT
  INTO EBMD_CHANNEL
    (
      CHANNEL,
      DESCRIPTION
    )
    VALUES
    (
      'DOCUMENT',
      'Other whitepaper correspondence'
    );
  INSERT
  INTO EBMD_TENANT
    (
      TENANTID,
      TENANT,
      TENANTNAME
    )
    VALUES
    (
      '110',
      'SCS',
      'Sky Customer Service (B2C)'
    );
  INSERT
  INTO EBMD_TENANT
    (
      TENANTID,
      TENANT,
      TENANTNAME
    )
    VALUES
    (
      '120',
      'SBS',
      'Sky Business Service (B2B)'
    );
  INSERT
  INTO DWH_ETL.EBMD_SUBPROJECT
    (
      SUBPROJECTID,
      TENANTID,
      SUBPROJECTNAME,
      CREATED,
      MODIFIED,
      DELETED,
      DELETIONFLAG
    )
  SELECT id,
    projektid,
    name,
    TO_DATE('01-01-2012','DD-MM-YYYY'),
    sysdate,
    ityxtimetodate(geloeschtam),
    CASE
      WHEN (geloeschtam>1)
      THEN 1
      ELSE 0
    END
  FROM teilprojekt ;
CREATE OR REPLACE TRIGGER tr_ebmd_subproject AFTER
  INSERT OR
  UPDATE OR
  DELETE ON teilprojekt FOR EACH ROW DECLARE v_teilprojektid teilprojekt.ID%TYPE;
  BEGIN
    v_teilprojektid                                       :=NVL(:new.id, :old.id);
    MERGE INTO EBMD_SUBPROJECT USING dual ON (SUBPROJECTID = v_teilprojektid)
  WHEN MATCHED THEN
    UPDATE
    SET TENANTID    =:new.projektid,
      SUBPROJECTNAME=:new.name,
      MODIFIED      =sysdate,
      DELETED       =ityxtimetodate(:new.geloeschtam),
      DELETIONFLAG  =(
      CASE
        WHEN (:new.geloeschtam>1)
        THEN 1
        ELSE 0
      END)
    WHERE SUBPROJECTID= v_teilprojektid WHEN NOT MATCHED THEN
    INSERT
      (
        SUBPROJECTID,
        TENANTID,
        SUBPROJECTNAME,
        CREATED,
        modified,
        DELETED,
        DELETIONFLAG
      )
      VALUES
      (
        v_teilprojektid,
        :new.projektid,
        :new.name,
        sysdate,
        sysdate,
        ityxtimetodate(:new.geloeschtam),
        (
        CASE
          WHEN (:new.geloeschtam>1)
          THEN 1
          ELSE 0
        END)
      );
  END;
  /
  INSERT
  INTO DWH_ETL.EBMD_AGENCY
    (
      AGENCYID,
      AGENCYNAME,
      TENANTID,
      CREATED,
      MODIFIED
    )
  SELECT id,
    name,
    projekt,
    TO_DATE('01-01-2012','DD-MM-YYYY'),
    sysdate
  FROM AR_AGENTUR ;
CREATE OR REPLACE TRIGGER tr_ebmd_agency AFTER
  INSERT OR
  UPDATE OR
  DELETE ON AR_AGENTUR FOR EACH ROW DECLARE v_agenturid AR_AGENTUR.ID%TYPE;
  BEGIN
    v_agenturid:=NVL(:new.id, :old.id);
    IF (DELETING) THEN
      UPDATE EBMD_AGENCY
      SET MODIFIED  =sysdate,
        DELETED     =sysdate,
        DELETIONFLAG=1
      WHERE AGENCYID= v_agenturid;
    ELSE
      MERGE INTO EBMD_AGENCY USING dual ON (AGENCYID = v_agenturid)
    WHEN MATCHED THEN
      UPDATE
      SET TENANTID  =:new.projekt,
        AGENCYNAME  =:new.name,
        MODIFIED    =sysdate
      WHERE AGENCYID= v_agenturid WHEN NOT MATCHED THEN
      INSERT
        (
          AGENCYID,
          TENANTID,
          AGENCYNAME,
          CREATED,
          modified
        )
        VALUES
        (
          v_agenturid,
          :new.projekt,
          :new.name,
          sysdate,
          sysdate
        );
    END IF;
  END;
  /
  INSERT
  INTO DWH_ETL.EBMD_KEYWORD
    (
      KEYWORDID,
      KEYWORD,
      PARENTID,
      TENANTID,
      CREATED,
      MODIFIED,
      DELETED,
      DELETIONFLAG
    )
  SELECT id,
    name,
    parentid,
    PROJEKTID,
    TO_DATE('01-01-2012','DD-MM-YYYY'),
    sysdate,
    ityxtimetodate(geloeschtam),
    CASE
      WHEN (geloeschtam>1)
      THEN 1
      ELSE 0
    END
  FROM SCHLAGWORT ;
CREATE OR REPLACE TRIGGER tr_ebmd_keyword AFTER
  INSERT OR
  UPDATE OR
  DELETE ON SCHLAGWORT FOR EACH ROW DECLARE v_keywordid SCHLAGWORT.ID%TYPE;
  BEGIN
    v_keywordid                                     :=NVL(:new.id, :old.id);
    MERGE INTO EBMD_KEYWORD USING dual ON (KEYWORDID = v_keywordid)
  WHEN MATCHED THEN
    UPDATE
    SET TENANTID  =:new.PROJEKTID,
      KEYWORD     =:new.name,
      PARENTID    =:new.PARENTID,
      MODIFIED    =sysdate,
      DELETED     =ityxtimetodate(:new.geloeschtam),
      DELETIONFLAG=(
      CASE
        WHEN (:new.geloeschtam>1)
        THEN 1
        ELSE 0
      END )
    WHERE KEYWORDID= v_keywordid WHEN NOT MATCHED THEN
    INSERT
      (
        KEYWORDID,
        TENANTID,
        KEYWORD,
        PARENTID,
        CREATED,
        modified,
        DELETED,
        DELETIONFLAG
      )
      VALUES
      (
        v_keywordid,
        :new.PROJEKTID,
        :new.name,
        :new.PARENTID,
        sysdate,
        sysdate,
        ityxtimetodate(:new.geloeschtam),
        (
        CASE
          WHEN (:new.geloeschtam>1)
          THEN 1
          ELSE 0
        END )
      );
  END;
  /
  --select * from SCHLAGWORTKATEGORIE;
  INSERT
  INTO DWH_ETL.EBMD_KEYWORDGROUP
    (
      KEYWORDGROUPID,
      KEYWORDGROUP,
      PARENTID,
      TENANTID,
      CREATED,
      MODIFIED,
      DELETED,
      DELETIONFLAG
    )
  SELECT id,
    name,
    parentid,
    PROJEKTID,
    TO_DATE('01-01-2012','DD-MM-YYYY'),
    sysdate,
    ityxtimetodate(geloeschtam),
    CASE
      WHEN (geloeschtam>1)
      THEN 1
      ELSE 0
    END
  FROM SCHLAGWORTKATEGORIE ;
CREATE OR REPLACE TRIGGER tr_EBMD_KEYWORDGROUP AFTER
  INSERT OR
  UPDATE OR
  DELETE ON SCHLAGWORTKATEGORIE FOR EACH ROW DECLARE v_schlagwort SCHLAGWORTKATEGORIE.ID%TYPE;
  BEGIN
    v_schlagwort                                              :=NVL(:new.id, :old.id);
    MERGE INTO EBMD_KEYWORDGROUP USING dual ON (KEYWORDGROUPID = v_schlagwort)
  WHEN MATCHED THEN
    UPDATE
    SET TENANTID  =:new.PROJEKTID,
      KEYWORDGROUP=:new.name,
      PARENTID    =:new.PARENTID,
      MODIFIED    =sysdate,
      DELETED     =ityxtimetodate(:new.geloeschtam),
      DELETIONFLAG=(
      CASE
        WHEN (:new.geloeschtam>1)
        THEN 1
        ELSE 0
      END )
    WHERE KEYWORDGROUPID= v_schlagwort WHEN NOT MATCHED THEN
    INSERT
      (
        KEYWORDGROUPID,
        TENANTID,
        KEYWORDGROUP,
        PARENTID,
        CREATED,
        modified,
        DELETED,
        DELETIONFLAG
      )
      VALUES
      (
        v_schlagwort,
        :new.PROJEKTID,
        :new.name,
        :new.PARENTID,
        sysdate,
        sysdate,
        ityxtimetodate(:new.geloeschtam),
        (
        CASE
          WHEN (:new.geloeschtam>1)
          THEN 1
          ELSE 0
        END )
      );
  END;
  /
  SELECT * FROM Mitarbeiter;
  INSERT
  INTO DWH_ETL.EBMD_AGENT
    (
      AGENTID,
      AGENTNAME,
      AGENTLOGIN,
      AGENTUSERID,
      AGENTEMAIL,
      AGENTROLEDESC,
      AGENCYID,
      MONITORED ,
      MONITORINGDURATION,
      EXTERNALFLAG,
      ACTIVE,
      PWEXPIRATIONON ,
      ROUTINGMODELLIST ,
      CREATED,
      MODIFIED,
      DELETED,
      DELETIONFLAG
    )
  SELECT id,
    name,
    LOGINNAME,
    USER_ID,
    EMAIL
    ROUTINGMODELLIST,
    TO_DATE('01-01-2012','DD-MM-YYYY'),
    sysdate,
    ityxtimetodate(geloeschtam),
    CASE
      WHEN (geloeschtam>1)
      THEN 1
      ELSE 0
    END
  FROM Mitarbeiter ;
CREATE OR REPLACE TRIGGER tr_EBMD_AGENT AFTER
  INSERT OR
  UPDATE OR
  DELETE ON Mitarbeiter FOR EACH ROW DECLARE v_agentid Mitarbeiter.ID%TYPE;
  BEGIN
    v_agentid                                          :=NVL(:new.id, :old.id);
    MERGE INTO EBMD_AGENT USING dual ON (KEYWORDGROUPID = v_agentid)
  WHEN MATCHED THEN
    UPDATE
    SET TENANTID  =:new.PROJEKTID,
      KEYWORDGROUP=:new.name,
      PARENTID    =:new.PARENTID,
      MODIFIED    =sysdate,
      DELETED     =ityxtimetodate(:new.geloeschtam),
      DELETIONFLAG=(
      CASE
        WHEN (:new.geloeschtam>1)
        THEN 1
        ELSE 0
      END )
    WHERE KEYWORDGROUPID= v_agentid WHEN NOT MATCHED THEN
    INSERT
      (
        KEYWORDGROUPID,
        TENANTID,
        KEYWORDGROUP,
        PARENTID,
        CREATED,
        modified,
        DELETED,
        DELETIONFLAG
      )
      VALUES
      (
        v_agentid,
        :new.PROJEKTID,
        :new.name,
        :new.PARENTID,
        sysdate,
        sysdate,
        ityxtimetodate(:new.geloeschtam),
        (
        CASE
          WHEN (:new.geloeschtam>1)
          THEN 1
          ELSE 0
        END )
      );
  END;
  /
