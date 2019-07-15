create or replace TRIGGER tr_ebm_event_antwort AFTER  INSERT OR UPDATE OR DELETE ON antwort FOR EACH ROW DECLARE 
  v_duration  NUMBER ; 
  v_QUESTIONID   NUMBER ;
  v_RESPONSEID NUMBER ;
  v_EVENT  EBMF_QUESTIONMETA.LASTEVENT%TYPE;
  v_EXEC_AGENTID  EBMF_QUESTIONMETA.EXEC_AGENTID%TYPE;
  v_EXEC_AGENCYID  EBMF_QUESTIONMETA.EXEC_AGENCYID%TYPE;
  v_ASSIGN_AGENCYID  EBMF_QUESTIONMETA.ASSIGN_AGENCYID%TYPE;
  v_SUBPROJECTID EBMF_QUESTIONMETA.SUBPROJECTID%TYPE;
  
  v_fragemeta_pos EBMF_QUESTIONMETA%ROWTYPE;
  cursor c_fragemeta_pos (p_QUESTIONID in EBMF_QUESTIONMETA.QUESTIONID%TYPE) is
   select * from EBMF_QUESTIONMETA where QUESTIONID = p_QUESTIONID and rownum=1;
 BEGIN  
  v_QUESTIONID:=nvl(:new.QUESTIONID, :old.frageid);
  v_RESPONSEID:=nvl(:new.id, :old.id);
  v_duration:=0;
  v_event := 'RU' ;
  IF (DELETING) THEN
   v_event := 'RD' ;
  ELSIF (INSERTING) THEN  
   if (:new.status = 'erledigt') THEN
      v_event := 'RI'; -- closed immidiatelly
   else
      v_event := 'RS';
   end if;
  ELSIF (UPDATING AND (:new.id <> 0 AND (:new.frageid <> :old.frageid OR :new.status <> :old.status OR :new.gesperrtvon <> :old.gesperrtvon OR :new.gesperrtam <> :old.gesperrtam OR :new.geloeschtam <> :old.geloeschtam OR :new.sendtime <> :old.sendtime))) THEN
   v_event := 'RU' ;
   IF (:old.frageid = 0 AND :new.frageid > 0) THEN
     v_event := 'RS' ;
   ELSIF (:new.status <> :old.status) THEN
     CASE
      WHEN :new.status = 'ueberwacht' THEN
        v_event := 'RQ' ;
      WHEN :new.status = 'absenden' THEN
        v_event := 'RA' ;
      WHEN :new.status = 'erledigt' THEN
        v_event := 'RE' ;
      WHEN :new.status = 'fragment' THEN
        v_event := 'RZ' ;
      WHEN :new.status = 'zwischenbescheid' THEN
        v_event := 'RZ' ;
      ELSE
        v_event := 'RU' ;
      END CASE ;
   ELSIF (:new.status = 'ueberwacht' AND :old.gesperrtvon = 0 AND :new.gesperrtvon = 2) THEN
    v_event := 'RA' ; -- Process start
   ELSIF (:old.gesperrtvon = 0 AND :new.gesperrtvon > 0) THEN
    v_event := 'RL' ; -- Process start
   ELSIF (:new.gesperrtvon = 0 AND :old.gesperrtvon > 0) THEN
    v_event := 'RR' ; -- Process ende  - bearbeitet
    SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN e WHERE e.QUESTIONID = v_QUESTIONID and e.RESPONSEID = v_RESPONSEID AND e.event ='RL' ;
   ELSIF (:new.geloeschtam IS NOT NULL AND :new.geloeschtam > 0 AND :new.geloeschtam <> :old.geloeschtam) THEN
    v_event := 'RD' ;          
   end IF;
  END  IF;  
  
  IF (v_event in ('RE','RD')) THEN
    SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN e WHERE e.QUESTIONID = v_QUESTIONID and e.RESPONSEID = v_RESPONSEID AND e.event ='RS' ;
  END IF;
    
  IF ( v_QUESTIONID <> 0 ) then
   IF (:new.gesperrtvon > 5) THEN
    begin 
     SELECT max(agentur) INTO v_EXEC_AGENCYID FROM ar_mitarbeiter WHERE mxid = :new.gesperrtvon;
    exception
     when NO_DATA_FOUND then
     v_ASSIGN_AGENCYID:=-1;
    end;
    v_EXEC_AGENTID := :new.gesperrtvon;
   ELSIF (:old.gesperrtvon > 5) THEN
    begin 
      SELECT max(agentur) INTO v_EXEC_AGENCYID FROM ar_mitarbeiter WHERE mxid = :old.gesperrtvon;
    exception
       when NO_DATA_FOUND then
       v_ASSIGN_AGENCYID:=-1;
     end;
    v_EXEC_AGENTID := :old.gesperrtvon;
   ELSE
    v_EXEC_AGENCYID:=0;
    v_EXEC_AGENTID:=:new.gesperrtvon;
   END IF;
   
   OPEN c_fragemeta_pos(v_QUESTIONID);
   loop
   FETCH c_fragemeta_pos into v_fragemeta_pos;
   exit when c_fragemeta_pos%NOTFOUND;
        v_ASSIGN_AGENCYID:= v_fragemeta_pos.ASSIGN_AGENCYID;
        v_SUBPROJECTID:=v_fragemeta_pos.SUBPROJECTID;
   end loop;
   CLOSE c_fragemeta_pos;
   if v_ASSIGN_AGENCYID is null then
    begin 
      select agentur into v_ASSIGN_AGENCYID from ar_fragezuagentur where frage=v_QUESTIONID;
     exception
       when NO_DATA_FOUND then
       v_ASSIGN_AGENCYID:=-1;
     end;
   end if;
   if v_SUBPROJECTID is null then
    select teilprojektid into v_SUBPROJECTID from frage where id=v_QUESTIONID;
   end if;
   INSERT INTO EBMF_EVENT_JN (LOGID, CREATED, QUESTIONID, RESPONSEID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, DURATION)
      VALUES(SQ_AR_EVENT.nextval, sysdate, v_QUESTIONID, v_RESPONSEID, v_SUBPROJECTID, v_ASSIGN_AGENCYID , 'R', v_event, nvl(:new.status, :old.status), nvl(:new.gesperrtvon, :old.gesperrtvon), ityxtimetodate (nvl(:new.gesperrtam,:old.gesperrtam)), ityxtimetodate(nvl(:new.GELOESCHTAM, :old.GELOESCHTAM)), v_duration);

    MERGE INTO EBMF_QUESTIONMETA USING dual ON (QUESTIONID = v_QUESTIONID)
      WHEN MATCHED THEN
       UPDATE SET lastevent = v_EVENT, MODIFIED = sysdate, EXEC_AGENTID=v_EXEC_AGENTID, EXEC_AGENCYID=v_EXEC_AGENCYID
       WHERE QUESTIONID = v_QUESTIONID
     ;
 END IF;
  END ;
  
  
      /*------------------------------------------------------------------------------
      -- Author: Gregor Meinusch, Heino Kappher, NTT Data
      -- Datum : 03-07-2013
      -------------------------------------------------------------------------------
      -- Inhalt           : Eventbasiertes Journal beschreiben
      -- Kurzbeschreibung :
      --
      -------------------------------------------------------------------------------
      -- Input Parameter  : -
      -- Output Parameter : -
      -------------------------------------------------------------------------------
      -- Aenderungsvermerk:
      --
      -- Nr. / Datum / Bearbeiter / Beschreibung
      -------------------------------------------------------------------------------
      -- 01 / 03-07.2013  / Meinusch, Kappher / Initial
	  -- 02 / 21.01.2015  / Meinusch / Anpassung an Dokumentation v031
      -------------------------------------------------------------------------------
      */