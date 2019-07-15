create or replace TRIGGER tr_ebm_event_agentur AFTER INSERT OR UPDATE OR DELETE ON AR_FRAGEZUAGENTUR FOR EACH ROW DECLARE 
  v_event VARCHAR (10) ;
  v_prevLogid EBMF_EVENT_JN.LOGID%TYPE;
  v_prevAgency EBMF_EVENT_JN.ASSIGN_AGENCYID%TYPE;
  v_prevEvent EBMF_EVENT_JN.EVENT%TYPE;
  v_ftpid FRAGE.TEILPROJEKTID%TYPE;
  v_fstatus FRAGE.STATUS%TYPE;
  v_fgesperrtam FRAGE.GESPERRTAM%TYPE;
  v_exec_agentid FRAGE.GESPERRTVON%TYPE;
  v_EXEC_AGENCYID ar_mitarbeiter.AGENTUR%TYPE;
  v_QUESTIONID FRAGE.ID%TYPE;
  v_emailid FRAGE.EMAILID%TYPE;
  v_duration Number;
  V_AG_CREATED DATE;
  v_agency_maxliegezeit NUMBER;
  v_CHANNEL  EBMF_QUESTIONMETA.CHANNEL%TYPE;
  v_AG_INSLAUNTIL  EBMF_QUESTIONMETA.AG_INSLAUNTIL%TYPE;
  BEGIN
    v_QUESTIONID:=nvl(:new.frage, :old.frage);
    v_duration:=0;    
    begin
      select channel, AG_CREATED into v_channel, v_AG_CREATED from EBMF_QUESTIONMETA  WHERE QUESTIONID= v_QUESTIONID and rownum=1;
      
      select nvl(prevLogid,0), nvl(prevAgency,0), prevEvent, time_since(created), nvl(created,sysdate)
      into v_prevLogid,  v_prevAgency, v_prevEvent, v_duration, V_AG_CREATED
      from (select logid prevLogid, created,  ASSIGN_AGENCYID prevAgency ,event prevEvent, row_number()over (partition by QUESTIONID order by logid desc) rank
            from EBMF_EVENT_JN where QUESTIONID=v_QUESTIONID and event in  ('AF','AT', 'AD', 'DT', 'DF')) -- 'LT', 'LF',
      where rank=1;
       exception
        when NO_DATA_FOUND then
        v_prevLogid:=-1;
        v_prevAgency:=-1;      
    end;
    
    if (v_duration=0) then
      v_duration:=time_since(v_AG_CREATED);
    end if;
    
    SELECT TEILPROJEKTID, STATUS, GESPERRTAM , GESPERRTVON, nvl((select max(agentur) from ar_mitarbeiter where mxid>5 and mxid = GESPERRTVON),0), emailid
    into v_ftpid, v_fstatus, v_fgesperrtam, v_exec_agentid, v_EXEC_AGENCYID, v_emailid
    FROM frage f WHERE f.id=v_QUESTIONID and rownum=1;
        
    IF (DELETING) THEN
      if (:old.agentur=0 or :old.agentur is null) then
        v_event:='AD';
      else
        v_event:='AF';
      end if;  
    ELSIF (INSERTING and v_QUESTIONID>0) THEN
      IF (v_prevAgency=:new.agentur and v_prevEvent is not null and v_prevEvent='AF') then
        update EBMF_EVENT_JN set event='AX' where logid=v_prevLogid and event='AF' and QUESTIONID=v_QUESTIONID;
        v_event:='AX';
      elsif (:new.agentur=0 or :new.agentur is null) then
         v_event:='AD';
      else
        v_event:= 'AT';
        V_AG_CREATED:=sysdate;
      END IF;
    ELSIF (UPDATING and v_QUESTIONID>0) THEN
      IF (:new.erledigtam is not null and :new.erledigtam>0 and :new.ERLEDIGTAM <> :old.ERLEDIGTAM ) then
        v_event:='AE';
      ELSIF (:new.Agentur <> :old.Agentur) THEN
        if (:old.agentur<>0 and :old.agentur is not null) then
           v_event:='AF';
           INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID,EXEC_AGENCYID,exec_agentid, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, DURATION)
             VALUES (SQ_AR_EVENT.nextval, sysdate, v_QUESTIONID, v_ftpid, :old.agentur, v_EXEC_AGENCYID,v_exec_agentid, 'A', v_event, v_fstatus , v_exec_agentid, ityxtimetodate (v_fgesperrtam), v_duration) ;
        END IF;
        if (:new.agentur=0 or :new.agentur is null) then
         v_event:='AD';
        else
         v_event:='AT';
        end if;
        v_duration:=0;
        V_AG_CREATED:=sysdate;
      ELSE --      ELSIF (:new.Agentur = :old.Agentur or :new.Agentur = v_prevAgency) then
        v_event:='AU';
      END IF;
      
     --if (v_event<>'AU') THEN
    END IF;
    
   if (v_CHANNEL is null or v_CHANNEL='') then
       SELECT NVL((REGEXP_SUBSTR((NVL(TO_CHAR(SUBSTR(REGEXP_SUBSTR (headers, '^X\-Tagmatch:Channel=.*', 1, 1, 'm'), 20)), '0')), '([[:alpha:]]*)')), decode(typ, 0, 'EMAIL', 1, 'FAX', 6, 'BRIEF', 7, 'DOCUMENT', typ)) into v_CHANNEL  FROM email WHERE id = v_emailid;
   end if;
   SELECT (MIN (Liegezeit) ) into v_agency_maxliegezeit FROM ar_agentur_liegezeiten WHERE agentur = nvl(:new.agentur, :old.agentur) and typ=decode(v_CHANNEL,'DOCUMENT',7,'LETTER',6,'BRIEF',6,'FAX',1,'EMAIL',0,0);
   V_AG_INSLAUNTIL:=V_AG_CREATED+NUMTODSINTERVAL(v_agency_maxliegezeit, 'HOUR');
  
   INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID,EXEC_AGENCYID,exec_agentid, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, DURATION)
          VALUES(SQ_AR_EVENT.nextval, sysdate, v_QUESTIONID, v_ftpid, nvl(:new.agentur, :old.agentur), v_EXEC_AGENCYID,v_exec_agentid, 'A', v_event ,v_fstatus , v_exec_agentid, ityxtimetodate (v_fgesperrtam), v_duration);
      
   MERGE INTO EBMF_QUESTIONMETA USING dual ON (QUESTIONID = v_QUESTIONID)
      WHEN MATCHED THEN
       UPDATE SET MODIFIED=sysdate, lastevent=v_event, ASSIGN_AGENCYID=nvl(:new.agentur, :old.agentur), AG_CREATED=V_AG_CREATED, AG_INSLAUNTIL=V_AG_INSLAUNTIL
       WHERE QUESTIONID= v_QUESTIONID
      WHEN NOT MATCHED THEN
       INSERT(QUESTIONID, MODIFIED, lastevent, ASSIGN_AGENCYID, AG_CREATED, AG_INSLAUNTIL)
       VALUES(v_QUESTIONID, sysdate, v_event, nvl(:new.agentur, :old.agentur), V_AG_CREATED,  V_AG_INSLAUNTIL);
   
  END;
  
  
        -------------------------------------------------------------------------------
      -- Author: Gregor Meinusch, Heino Kappher, NTT Data
      -- Datum : 21.5.2013
      -------------------------------------------------------------------------------
      -- Inhalt           : Eventbasierten Journal schreiben
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
      -- 01 / 21.06.2013  / Meinusch, Kappher / Initial
	    -- 02 / 21.01.2015  / Meinusch / Anpassung an Dokumentation v031
      -------------------------------------------------------------------------------