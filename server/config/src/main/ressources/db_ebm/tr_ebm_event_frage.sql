create or replace TRIGGER tr_ebm_event_frage AFTER INSERT OR UPDATE OR DELETE ON frage FOR EACH ROW DECLARE 
 v_QUESTIONID EBMF_QUESTIONMETA.QUESTIONID%TYPE;
 v_CASEID EBMF_QUESTIONMETA.CASEID%TYPE;
 v_DOCID EBMF_QUESTIONMETA.DOCID%TYPE;
 v_SUBPROJECTID  NUMBER;
 v_ASSIGN_AGENCYID  EBMF_QUESTIONMETA.ASSIGN_AGENCYID%TYPE;
 v_EXEC_AGENTID  EBMF_QUESTIONMETA.EXEC_AGENTID%TYPE;
 v_EXEC_AGENCYID  EBMF_QUESTIONMETA.EXEC_AGENCYID%TYPE;
 v_FIRSTCLOSING_AGENTID  EBMF_QUESTIONMETA.EXEC_AGENTID%TYPE;
 v_FIRSTCLOSING_AGENCYID  EBMF_QUESTIONMETA.EXEC_AGENCYID%TYPE;
 v_CHANNEL  EBMF_QUESTIONMETA.CHANNEL%TYPE;
 v_FTYPE EBMF_QUESTIONMETA.FTYPE%TYPE;
 v_EVENT  EBMF_QUESTIONMETA.LASTEVENT%TYPE;
 v_EVENT_f  EBMF_QUESTIONMETA.LASTEVENT%TYPE;
 v_EVENT_t  EBMF_QUESTIONMETA.LASTEVENT%TYPE;
 v_CREATED  EBMF_QUESTIONMETA.CREATED%TYPE;
 v_FINISHED  EBMF_QUESTIONMETA.FINISHED%TYPE;
 v_FU_CREATED  EBMF_QUESTIONMETA.FU_CREATED%TYPE;
 v_FU_FINISHED  EBMF_QUESTIONMETA.FU_FINISHED%TYPE;
 v_AG_CREATED  EBMF_QUESTIONMETA.AG_CREATED%TYPE;
 v_AG_FUTIME  EBMF_QUESTIONMETA.AG_FUTIME%TYPE;
 v_AG_HANDLINGTIME  EBMF_QUESTIONMETA.AG_HANDLINGTIME%TYPE;
 v_AG_HANDLINGTIMETMP  EBMF_QUESTIONMETA.AG_HANDLINGTIME%TYPE;
 v_AG_IDLETIME  EBMF_QUESTIONMETA.AG_IDLETIME%TYPE;
 v_AG_TOTALTIME  EBMF_QUESTIONMETA.AG_TOTALTIME%TYPE;
 v_AG_INSLA  EBMF_QUESTIONMETA.AG_INSLA%TYPE;
 v_AG_INSLAUNTIL  EBMF_QUESTIONMETA.AG_INSLAUNTIL%TYPE;
 V_GLOBAL_HANDLINGTIME  EBMF_QUESTIONMETA.GLOBAL_HANDLINGTIME%TYPE;
 V_GLOBAL_HANDLINGTIMETMP  EBMF_QUESTIONMETA.GLOBAL_HANDLINGTIME%TYPE;
 V_GLOBAL_IDLETIME  EBMF_QUESTIONMETA.GLOBAL_IDLETIME%TYPE;
 V_GLOBAL_TOTALTIME  EBMF_QUESTIONMETA.GLOBAL_TOTALTIME%TYPE;
 V_GLOBAL_FUTIME  EBMF_QUESTIONMETA.GLOBAL_FUTIME%TYPE;
 V_GLOBAL_INSLAUNTIL  EBMF_QUESTIONMETA.GLOBAL_INSLAUNTIL%TYPE;
 V_GLOBAL_INSLA  EBMF_QUESTIONMETA.GLOBAL_INSLA%TYPE;
 v_duration     NUMBER;
 v_lastlogid    NUMBER;
 v_icount       NUMBER;  
 v_agency_maxliegezeit NUMBER;
 v_offene_antworten NUMBER;
 v_QUESTIONID_parent EBMF_QUESTIONMETA.QUESTIONID%TYPE;
 v_fragemeta_pos EBMF_QUESTIONMETA%ROWTYPE;
 cursor c_fragemeta_pos (p_QUESTIONID in EBMF_QUESTIONMETA.QUESTIONID%TYPE) is
 select * from EBMF_QUESTIONMETA where QUESTIONID = p_QUESTIONID and rownum=1;
BEGIN
  v_QUESTIONID:=nvl(:new.id, :old.id);
  v_CASEID:=nvl(:new.vorgangid, :old.vorgangid);
  v_EVENT:= 'XX'; -- not catched event
     
   -- Filter for Insert/Update-Events
  IF (DELETING OR INSERTING OR
      (:new.id > 0 AND (:new.teilprojektid <> :old.teilprojektid OR :new.status <> :old.status 
                           OR :new.gesperrtam <> :old.gesperrtam OR :new.gesperrtvon <> :old.gesperrtvon OR :new.GELOESCHTAM <> :old.GELOESCHTAM OR :new.docid <> :old.docid OR :new.orgin <> :old.orgin 
                           OR :new.wiedervorlagezeit <> :old.wiedervorlagezeit OR  :new.COMMENTS <> :old.COMMENTS  OR  :new.COMMENTSD <> :old.COMMENTSD  ))) THEN

  OPEN c_fragemeta_pos(v_QUESTIONID);
  loop
  FETCH c_fragemeta_pos into v_fragemeta_pos;
  exit when c_fragemeta_pos%NOTFOUND;
  v_DOCID:=v_fragemeta_pos.DOCID;
  v_SUBPROJECTID:=v_fragemeta_pos.SUBPROJECTID;
  v_EXEC_AGENTID:=v_fragemeta_pos.EXEC_AGENTID;
  v_EXEC_AGENCYID:=v_fragemeta_pos.EXEC_AGENCYID;
  v_FIRSTCLOSING_AGENTID:=v_fragemeta_pos.FIRSTCLOSING_AGENTID;
  v_FIRSTCLOSING_AGENCYID:=v_fragemeta_pos.FIRSTCLOSING_AGENCYID;
  v_CHANNEL:=v_fragemeta_pos.CHANNEL;
  --v_EVENT:=v_fragemeta_pos.LASTEVENT;
  v_FTYPE:=v_fragemeta_pos.FTYPE;
  v_CREATED:=v_fragemeta_pos.CREATED;
  v_FINISHED:=v_fragemeta_pos.FINISHED;
  v_FU_CREATED:=v_fragemeta_pos.FU_CREATED;
  v_FU_FINISHED:=v_fragemeta_pos.FU_FINISHED;
  v_AG_CREATED:=v_fragemeta_pos.AG_CREATED;
  v_AG_FUTIME:=v_fragemeta_pos.AG_FUTIME;
  v_AG_HANDLINGTIME:=v_fragemeta_pos.AG_HANDLINGTIME;
  v_AG_IDLETIME:=v_fragemeta_pos.AG_IDLETIME;
  v_AG_TOTALTIME:=v_fragemeta_pos.AG_TOTALTIME;
  v_AG_INSLA:=v_fragemeta_pos.AG_INSLA;
  v_AG_INSLAUNTIL:=v_fragemeta_pos.AG_INSLAUNTIL;
  V_GLOBAL_HANDLINGTIME:=v_fragemeta_pos.GLOBAL_HANDLINGTIME;
  V_GLOBAL_IDLETIME:=v_fragemeta_pos.GLOBAL_IDLETIME;
  V_GLOBAL_TOTALTIME:=v_fragemeta_pos.GLOBAL_TOTALTIME;
  V_GLOBAL_FUTIME:=v_fragemeta_pos.GLOBAL_FUTIME;
  V_GLOBAL_INSLAUNTIL:=v_fragemeta_pos.GLOBAL_INSLAUNTIL;
  V_GLOBAL_InSLA:=v_fragemeta_pos.GLOBAL_INSLA;
  
  end loop;
  CLOSE c_fragemeta_pos;
      
   -- Common vars
   begin   
    SELECT MAX (agentur) INTO v_ASSIGN_AGENCYID FROM ar_fragezuagentur WHERE frage = v_QUESTIONID ;
   exception
     when NO_DATA_FOUND then
     v_ASSIGN_AGENCYID:=-1;
   end;
   if (v_ASSIGN_AGENCYID is null) then
    v_ASSIGN_AGENCYID:=-1;
   end if;
   v_icount:=0;

   begin   
      SELECT COUNT ( *) INTO v_offene_antworten FROM antwort WHERE status  IN ('ueberwacht', 'zwischenbescheid') AND frageid = v_QUESTIONID;
    exception
     when NO_DATA_FOUND then
     v_offene_antworten:=0;
   end;
   if (v_DOCID is null ) then --or v_DOCID='' or v_DOCID=' '
    v_DOCID:=nvl(:new.docid,:old.docid);
   end if;
   
   v_SUBPROJECTID:=nvl(:new.teilprojektid, :old.teilprojektid);
   begin   
     if (v_CHANNEL is null or v_CHANNEL='') then
      SELECT NVL((REGEXP_SUBSTR((NVL(TO_CHAR(SUBSTR(REGEXP_SUBSTR (headers, '^X\-Tagmatch:Channel=.*', 1, 1, 'm'), 20)), '0')), '([[:alpha:]]*)')), decode(typ, 0, 'EMAIL', 1, 'FAX', 6, 'BRIEF', 7, 'DOCUMENT', typ)) into v_CHANNEL  FROM email WHERE id = nvl(:new.emailid, :old.emailid);
     end if;
   exception
     when NO_DATA_FOUND then
     v_CHANNEL:=null;
   end;
   
   if (v_CREATED is null) then
      begin
        select nvl(nvl((select ityxtimetodate(emaildate) from email where id =nvl(:new.emailid, :old.emailid) and emaildate>0 and emaildate is not null), nvl((select ityxtimetodate(min(zeit)) from mitarbeiterlog where frageid= v_QUESTIONID), nvl((select min(created) from MX_REPORTING where frageid=v_QUESTIONID and created is not null),ityxtimetodate(nvl(:new.ESKALATIONSTART,:old.ESKALATIONSTART))))),sysdate)
        into v_CREATED from dual;
      exception
      when NO_DATA_FOUND then
        v_CREATED:=sysdate;
      end;
      select v_CREATED + NUMTODSINTERVAL(nvl((select max(VALUE_NUM) from ebm_config where name like 'SKYSERVICELEVELZEIT_'||decode(v_CHANNEL,'DOCUMENT','LETTER','BRIEF','LETTER', 'EMAIL','EMAIL','FAX','FAX','LETTER','LETTER','EMAIL')) ,1), 'HOUR')
        into V_GLOBAL_inslauntil from dual;      
    elsif (V_GLOBAL_inslauntil is null) then
      select v_CREATED + NUMTODSINTERVAL(nvl((select max(VALUE_NUM) from ebm_config where name like 'SKYSERVICELEVELZEIT_'||decode(v_CHANNEL,'DOCUMENT','LETTER','BRIEF','LETTER', 'EMAIL','EMAIL','FAX','FAX','LETTER','LETTER','EMAIL')) ,1), 'HOUR')
        into V_GLOBAL_inslauntil from dual; 
    end if;
      
   -- Mitarbeiter/Agentur
   IF (:new.gesperrtvon > 5) THEN
    begin
      SELECT max(agentur) INTO v_EXEC_AGENCYID FROM ar_mitarbeiter WHERE mxid = :new.gesperrtvon;
    exception
      when NO_DATA_FOUND then
      v_EXEC_AGENCYID:=-1;
    end;
    v_EXEC_AGENTID := :new.gesperrtvon;
   ELSIF (:old.gesperrtvon > 5) THEN
    begin
     SELECT max(agentur) INTO v_EXEC_AGENCYID FROM ar_mitarbeiter WHERE mxid = :old.gesperrtvon;
    exception
     when NO_DATA_FOUND then
     v_EXEC_AGENCYID:=-1;
    end;
    v_EXEC_AGENTID := :old.gesperrtvon;
   ELSE
    v_EXEC_AGENCYID:=0;
    v_EXEC_AGENTID:=nvl(:new.gesperrtvon,:old.gesperrtvon);
   END IF;
  
   v_QUESTIONID_parent:=nvl(:new.orgin, :old.orgin);
   if (v_QUESTIONID_parent>0)then
    if v_ASSIGN_AGENCYID is null then
      begin
        select agentur into v_ASSIGN_AGENCYID from ar_fragezuagentur where frage=v_QUESTIONID_parent;
        exception
          when NO_DATA_FOUND then
          v_ASSIGN_AGENCYID:=-1;
        end;
    end if;
   if v_docid is null then
      begin
        select docid into v_docid from EBMF_QUESTIONMETA where QUESTIONID=v_QUESTIONID_parent;
      exception
          when NO_DATA_FOUND then
          v_docid:=null;
        end;
    end if;
    IF (v_EXEC_AGENTID<10) THEN
      begin
        SELECT EXEC_AGENTID, EXEC_AGENCYID INTO v_EXEC_AGENTID, v_EXEC_AGENCYID FROM EBMF_QUESTIONMETA WHERE QUESTIONID=v_QUESTIONID_parent;
      exception
          when NO_DATA_FOUND then
           v_EXEC_AGENCYID:=-1;
           v_EXEC_AGENTID:=nvl(:new.gesperrtvon,:old.gesperrtvon);
      end;
    end if;
   end if;
   
  
   IF (UPDATING) THEN
     v_EVENT := 'FU'; 
     -- TP-Events
     IF ( :old.teilprojektid>0 AND :new.teilprojektid  <> :old.teilprojektid AND (:new.gesperrtvon <> 2)) THEN
      -- TP Wegnahme
       if (v_EXEC_AGENCYID=0 and v_exec_agentid>10) then
        v_event:='TW';       
       else 
        v_EVENT := 'TF'; 
       end if;
      IF (:old.teilprojektid > 0) THEN
        INSERT INTO EBMF_EVENT_JN (LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON, DURATION,exec_agentid, EXEC_AGENCYID)
             VALUES( SQ_AR_EVENT.nextval, sysdate, :new.id, :old.teilprojektid, v_ASSIGN_AGENCYID, 'F', v_EVENT, :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit), v_duration, v_EXEC_AGENTID, v_EXEC_AGENCYID);
        SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND EVENT IN ('TT', 'FS') ;
       END IF;
       
      -- TP Weiterleitung
      IF (:new.teilprojektid IN (1125, 16710, 16711)) THEN
         v_EVENT:= 'MS' ;
         v_ftype:=2;
      elsif (v_EXEC_AGENCYID=0 and v_exec_agentid>10) then
        v_event:='TH';            
      ELSE
         v_EVENT:= 'TT' ; 
      END IF;
      INSERT INTO EBMF_EVENT_JN (LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON,   DURATION, exec_agentid, EXEC_AGENCYID)
           VALUES( SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID, 'F', v_EVENT, :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),  v_duration, v_EXEC_AGENTID, v_EXEC_AGENCYID);
      v_EVENT:= 'FU' ; 
     END IF ;
     
     -- WV
     IF (:new.status = 'wiedervorlage' AND :old.status <> :new.status) THEN
       v_EVENT:= 'WS';
       v_FU_CREATED := sysdate;
       INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON,  DURATION, exec_agentid, EXEC_AGENCYID)
              VALUES(SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID, 'F', v_EVENT, :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit), v_duration, v_EXEC_AGENTID, v_EXEC_AGENCYID);
     ELSIF (:old.status = 'wiedervorlage' AND :old.status <> :new.status) THEN
       v_EVENT:= 'WE';
       V_FU_FINISHED:=sysdate;
       
       SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND EVENT = 'WS';
       V_AG_FUTIME:=V_AG_FUTIME+v_duration;
       V_GLOBAL_FUTIME:=V_GLOBAL_FUTIME+v_duration;
       
       INSERT INTO EBMF_EVENT_JN (LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON,   DURATION, exec_agentid, EXEC_AGENCYID)
            VALUES (SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID, 'F', v_EVENT, :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),   v_duration, v_EXEC_AGENTID, v_EXEC_AGENCYID) ;
     
     ELSIF (:new.wiedervorlagezeit <> :old.wiedervorlagezeit) THEN
       v_EVENT:= 'WU';
       v_FU_CREATED:= sysdate;
       SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND EVENT = 'WS';
     END IF;
     
  
     -- EW
     IF (:new.status = 'extern-weitergeleitet' AND :old.status <> :new.status) THEN
        v_EVENT:= 'EW';        
        v_ftype:=5;
     ELSIF (:old.status = 'extern-weitergeleitet' and :new.status='extern-beantwortet') THEN
        v_EVENT:= 'EB';
        SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND EVENT = 'EW';
     ELSIF (:new.teilprojektid    < 1 AND (:new.gesperrtvon = 2 OR :new.gesperrtvon = 0)) THEN
        v_EVENT:= 'XX'; -- bearbeitung in Contex
     ELSIF (:new.teilprojektid in (1125, 16710, 16711)  AND :new.gesperrtvon in (0,2) AND :new.status in ('neu', 'klassifikation') AND :new.bearbeitungsende IS NULL AND (:old.teilprojektid is null or :old.teilprojektid<=0)) THEN
        v_EVENT:= 'MS';
        v_CREATED:=sysdate;
     ELSIF (:new.gesperrtvon in (0,2) AND :new.status in ('blockiert') AND :old.status in ('neu') AND :new.bearbeitungsende IS NULL ) THEN
        v_EVENT:= 'FP';
        update EBMF_EVENT_JN set EVENT=v_event, EVENTSRC='F' where EVENT='FS' and QUESTIONID=v_QUESTIONID;
          
     ELSIF (:new.teilprojektid > 0 AND :new.gesperrtvon in (0,2) AND :new.status = 'neu' AND :new.bearbeitungsende IS NULL AND (:old.teilprojektid<=0) and (:new.status <> :old.status or :old.teilprojektid=0)) THEN
        v_CREATED:=sysdate;
        IF (:new.teilprojektid IN (1125, 16710, 16711)) THEN
         v_EVENT:= 'MS' ;
         v_ftype:=2;
        else 
          v_EVENT:= 'FS';
          v_ftype:=1;
        end if;
        
     ELSIF (:new.status in ('erledigt', 'extern-weitergeleitet') and ( (:new.status <> :old.status) or (:new.GESPERRTVON = 0 AND :old.GESPERRTVON = 2 AND v_offene_antworten = 0) ) ) THEN
         
          if (:new.status ='erledigt' and :old.status ='neu' and :old.teilprojektid <= 0 and :new.GESPERRTVON = 0) then
            INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON, DURATION, exec_agentid, EXEC_AGENCYID)
                 VALUES(SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID , 'F', 'FS', :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),   v_duration,  v_EXEC_AGENTID, v_EXEC_AGENCYID);
            INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON, DURATION, exec_agentid, EXEC_AGENCYID)
                 VALUES(SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID , 'F', 'FI', :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),   v_duration,  v_EXEC_AGENTID, v_EXEC_AGENCYID);
            v_ftype:=7; -- automatisch geschlossen
            v_EVENT             := 'FE';
         
          elsif (:new.status ='erledigt' and :new.orgin=0  and :new.weitergeleitetan is not null) then
            INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON, DURATION, exec_agentid, EXEC_AGENCYID)
                 VALUES(SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID , 'F', 'FT', :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),   v_duration,  v_EXEC_AGENTID, v_EXEC_AGENCYID);
            v_EVENT             := 'FE';
          elsif (:new.status ='erledigt' and :new.orgin>0  and :new.weitergeleitetan is not null) then
            if (:old.status in ('teilbeantwortet', 'erledigt', 'neu')) then
            INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON, DURATION, exec_agentid, EXEC_AGENCYID)
                 VALUES(SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID , 'F', 'ET', :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),   v_duration,  v_EXEC_AGENTID, v_EXEC_AGENCYID);
                 -- external transfer - bei sofortiger Schließung
          ELSIF (v_offene_antworten    > 0) THEN
            v_ftype:=6; 
            end if;
            v_EVENT             := 'EE'; -- duplicate End
            v_EVENT              := 'QS';  -- wenn offene antwort dann in überwachung schicken
            if (v_FIRSTCLOSING_AGENTID is null) then
                v_FIRSTCLOSING_AGENTID:=v_EXEC_AGENTID;
                v_FIRSTCLOSING_AGENCYID:=v_EXEC_AGENCYID;
            end if;
          ELSIF (:new.teilprojektid in (1125, 16710, 16711)) THEN
            v_EVENT              := 'ME'; -- Abschluss
             SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND EVENT = 'MS';
          ELSIF (:new.erledigtvon = 0 AND :new.bearbeitungszeit = 0 and :new.globalerstatus = 'Servicecenter') THEN
            INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON, DURATION, exec_agentid, EXEC_AGENCYID)
                 VALUES(SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID , 'F', 'IS', :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),   v_duration,  v_EXEC_AGENTID, v_EXEC_AGENCYID);
            v_ftype:=3;
            v_EVENT              := 'IE'; -- Abschluss           
            v_duration           := 0;            
          ELSE
            v_EVENT := 'FE'; -- Abschluss                  
            if (v_EXEC_AGENCYID is null or v_EXEC_AGENCYID=0) then
               select nvl(max(logid),0) into v_lastlogid from EBMF_EVENT_JN where QUESTIONID=:new.id and event in ('AF', 'PS','PL', 'PE','PR', 'WS', 'WE');
               if (v_lastlogid>0) then
                     select EXEC_AGENCYID into v_EXEC_AGENCYID  from EBMF_EVENT_JN where logid= v_lastlogid ;
               end if;
            end if;
            if (((v_ASSIGN_AGENCYID is null) or (v_EXEC_AGENCYID<>v_ASSIGN_AGENCYID)) and v_EXEC_AGENCYID>0 ) then
              
                if (v_ASSIGN_AGENCYID in (161, 261)) then -- wenn SKY
                  v_event_f:='LF';
                  v_event_t:='LT';        
                elsif (v_EXEC_AGENCYID is not null and v_EXEC_AGENCYID>0) then
                  v_event_f:='DF';
                  v_event_t:='DT';
                else 
                  v_event_f:='AF';
                  v_event_t:='AT';
                end if;
        
              if (v_ASSIGN_AGENCYID is not null ) then
               SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND ASSIGN_AGENCYID=v_ASSIGN_AGENCYID AND EVENT in ('AT');
               INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON,   DURATION, exec_agentid, EXEC_AGENCYID )
               VALUES(SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID, 'F', v_event_f, :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit), v_duration, v_EXEC_AGENTID, v_EXEC_AGENCYID) ;
              end if;
              v_duration:=0;
              INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON,    DURATION, exec_agentid, EXEC_AGENCYID )
              VALUES(SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_EXEC_AGENCYID, 'F', v_event_t, :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),   v_duration, v_EXEC_AGENTID, v_EXEC_AGENCYID) ;
              v_ASSIGN_AGENCYID:=v_EXEC_AGENCYID;
             end if;          
             --- ff duration
             SELECT time_since (MAX (created)) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND EVENT in ('FS');
          END IF;
     elsif (:new.GESPERRTVON > 5 AND :old.GESPERRTVON = 0) THEN
          v_EVENT              := 'PL';
     elsif (:new.GESPERRTVON = 0 AND :old.GESPERRTVON > 5) THEN
          v_EVENT              := 'PR';
          SELECT NVL (time_since (MAX (created)), 0) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND EVENT in ('PS', 'PL');
     elsif (:new.GELOESCHTAM > 0) THEN
          v_EVENT              := 'FD';
          SELECT NVL (time_since (MAX (created)), 0) INTO v_duration FROM EBMF_EVENT_JN WHERE QUESTIONID = :new.id AND EVENT in('PS', 'PL');
     END IF;
     
     IF (v_EVENT in ('PE', 'PS', 'PR', 'PL')) then
        if (((v_ASSIGN_AGENCYID is null) or (v_EXEC_AGENCYID<>v_ASSIGN_AGENCYID)) and v_EXEC_AGENCYID>0  ) then
             v_ASSIGN_AGENCYID:=v_EXEC_AGENCYID;
          end if;             
      end if;
    ELSIF (INSERTING) THEN
     -- Events beim Insert (FrageStart, FragePre)
     IF (:new.teilprojektid IN (1125, 16710, 16711)) THEN
         v_EVENT:= 'MS' ;
         v_ftype:=2;
     ELSIF (:new.teilprojektid < 1 AND (:new.gesperrtvon = 2 OR :new.gesperrtvon = 0)) THEN
       v_EVENT:= 'FP' ;
     ELSIF (:new.teilprojektid > 0 AND :new.gesperrtvon in (0,2) AND :new.status = 'neu' ) THEN
       v_EVENT:= 'FS' ;
       v_ftype:=1;
     ELSIF (:new.teilprojektid > 0 AND :new.gesperrtvon in (0,2) AND :new.status = 'teilbeantwortet' ) THEN
       v_EVENT:= 'EK' ;
       v_ftype:=4;
     else   
       v_EVENT:= 'FX' ;
     END IF ;
    ELSIF (DELETING) THEN
     v_EVENT:= 'FD';
            ityxtimetodate (:old.bearbeitungsende), ityxtimetodate (:old.wiedervorlagezeit), v_duration,:old.erledigtvon, nvl((select max(agentur) from ar_mitarbeiter where mxid=:old.erledigtvon),0)) ;
     INSERT INTO EBMF_EVENT_JN (
            LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON, DURATION, exec_agentid, EXEC_AGENCYID
          ) VALUES(SQ_AR_EVENT.nextval, sysdate, :old.id, :old.teilprojektid, (SELECT MAX (agentur) FROM ar_fragezuagentur WHERE frage = :old.id), 'F', v_EVENT, :old.status, :old.gesperrtvon, ityxtimetodate (:old.gesperrtam), 
    ELSE
       v_EVENT:= 'FU' ;
   END IF;-- Insert/Update/Delete
  end if; --choosen factors
   
      -- FU soll nicht protokoliert werden
      -- WS/WF/WU kommen häufig mit anderen Events (TP-Wechsel) zusammen, und werden daher vorher in die DB reingeschrieben
   
   ---
   IF ((not DELETING) and (v_EVENT NOT IN ('XX','FU','WS', 'WE', 'WU'))) then --AND  (:new.status IN ('neu', 'blockiert') AND :new.gesperrtvon IN (0, 2, 5)
         INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, SUBPROJECTID, ASSIGN_AGENCYID, EVENTSRC, EVENT, STATUS, LOCKEDBY, LOCKEDSINCE, MXLASTACTIONON, FOLLOWUPON,  DURATION, exec_agentid, EXEC_AGENCYID)
         VALUES (SQ_AR_EVENT.nextval, sysdate, :new.id, :new.teilprojektid, v_ASSIGN_AGENCYID , 'F', v_EVENT, :new.status, :new.gesperrtvon, ityxtimetodate (:new.gesperrtam), ityxtimetodate (:new.bearbeitungsende), ityxtimetodate (:new.wiedervorlagezeit),  v_duration, v_EXEC_AGENTID, v_EXEC_AGENCYID);
   END IF;
  
   
    IF (v_event in ('FE','DE','FD','ME','IE','EE')) then
          v_FINISHED:=sysdate;
          -- AG: AG_FUTIME ,AG_HANDLINGTIME ,AG_IDLETIME  ,AG_TOTALTIME  ,AG_INSLA ,AG_INSLAUNTIL
          
          -- Logid of last agency change
          SELECT NVL (MAX (logid),0) into v_lastlogid  FROM EBMF_EVENT_JN WHERE QUESTIONID = v_QUESTIONID AND event IN ('AT','AF','AW', 'FS', 'MS');
          
          SELECT (MAX(created)) into V_AG_CREATED FROM EBMF_EVENT_JN
          WHERE QUESTIONID = v_QUESTIONID AND ASSIGN_AGENCYID = v_EXEC_AGENCYID AND EVENT IN ('AT','AH', 'LT', 'DT', 'FS', 'MS')
                AND logid >=v_lastlogid;    
         
          /*
          IF(V_AG_START is null) then
            SELECT (MAX(created)) into V_AG_START FROM EBMF_EVENT_JN 
            WHERE QUESTIONID = v_QUESTIONID AND EXEC_AGENCYID = v_EXEC_AGENCYID AND EVENT IN ('AS', 'AT','AH', 'FS', 'MS')
                AND logid >= v_lastlogid;    
          END IF;
          */
          
          IF(V_AG_CREATED is null) then
            SELECT (MAX(created)) into V_AG_CREATED FROM EBMF_EVENT_JN
            WHERE QUESTIONID = v_QUESTIONID AND ASSIGN_AGENCYID >0 AND EVENT IN ('AT','AH', 'LT', 'DT', 'FS', 'MS')
                AND logid >= v_lastlogid;    
          END IF;
          
          V_AG_TOTALTIME:=time_since(V_AG_CREATED);
                    
          SELECT nvl(SUM (DURATION),0) into V_AG_HANDLINGTIMETMP FROM EBMF_EVENT_JN
          WHERE QUESTIONID = v_QUESTIONID AND EXEC_AGENCYID = v_EXEC_AGENCYID AND EVENT IN ('PR','PE')
              AND logid >= v_lastlogid;
              
          SELECT (V_AG_HANDLINGTIMETMP + nvl(time_since (MAX(created)),0)) into V_AG_HANDLINGTIME FROM EBMF_EVENT_JN
          WHERE QUESTIONID = v_QUESTIONID AND EXEC_AGENCYID = v_EXEC_AGENCYID AND EVENT IN ('PL','PS')
              AND logid> NVL ( (SELECT MAX (logid) FROM EBMF_EVENT_JN WHERE QUESTIONID = v_QUESTIONID AND event IN ('PR','PE')), 0);
          
          SELECT nvl(SUM (DURATION),0) into v_AG_FUTIME FROM EBMF_EVENT_JN
          WHERE QUESTIONID = v_QUESTIONID AND EVENT IN ('WE')
              AND logid>=v_lastlogid;
     
          IF (V_AG_TOTALTIME>0) THEN
            V_AG_IDLETIME:= V_AG_TOTALTIME-V_AG_HANDLINGTIME-V_AG_FUTIME;
          ELSIF (V_AG_HANDLINGTIME>0) THEN
            V_AG_IDLETIME:=0;
            V_AG_TOTALTIME:=V_AG_HANDLINGTIME;
          else 
            V_AG_IDLETIME:=0;
          end IF;
     
          -- Decode(e.typ, 0,'EMAIL', 1,'FAX', 6, 'BRIEF', 7,'DOCUMENT'),
          SELECT (MIN (Liegezeit) ) into v_agency_maxliegezeit FROM ar_agentur_liegezeiten WHERE agentur = v_ASSIGN_AGENCYID and typ=decode(v_CHANNEL,'DOCUMENT',7,'LETTER',6,'BRIEF',6,'FAX',1,'EMAIL',0,0);
          
          V_AG_INSLAUNTIL:=V_AG_CREATED+NUMTODSINTERVAL(v_agency_maxliegezeit, 'HOUR');
                    
          IF (V_AG_INSLAUNTIL>sysdate) THEN
             v_AG_InSLA:=1;
          ELSE 
            v_AG_InSLA:=0;
          END if;
          
          -- GLOBAL_HANDLINGTIME  ,GLOBAL_IDLETIME  ,GLOBAL_TOTALTIME  ,GLOBAL_FUTIME
          SELECT nvl(time_since (MIN(created)),0) into V_GLOBAL_TOTALTIME FROM EBMF_EVENT_JN 
          WHERE QUESTIONID = v_QUESTIONID AND EVENT IN ('FS', 'MS');
        
          SELECT nvl(SUM (DURATION),0) into V_GLOBAL_HANDLINGTIMETMP FROM EBMF_EVENT_JN
          WHERE QUESTIONID = v_QUESTIONID AND EVENT IN ('PR','PE');
          
          SELECT (V_GLOBAL_HANDLINGTIMETMP + nvl(time_since (MAX(created)),0)) into V_GLOBAL_HANDLINGTIME FROM EBMF_EVENT_JN
          WHERE QUESTIONID = v_QUESTIONID AND EVENT IN ('PL', 'PS')
              AND logid> NVL ( (SELECT MAX (logid) FROM EBMF_EVENT_JN WHERE QUESTIONID = v_QUESTIONID AND event IN ('PR','PE')), 0);
          
          SELECT nvl(SUM (DURATION),0) into V_GLOBAL_FUTIME FROM EBMF_EVENT_JN
          WHERE QUESTIONID = v_QUESTIONID AND EVENT IN ('WE');
          into V_GLOBAL_inslauntil FROM EBMF_EVENT_JN WHERE QUESTIONID = v_QUESTIONID AND EVENT IN ('FS', 'MS');
               
          V_GLOBAL_IDLETIME:= V_GLOBAL_TOTALTIME-V_GLOBAL_HANDLINGTIME-V_GLOBAL_FUTIME;
          select MIN(created  + NUMTODSINTERVAL(nvl((select max(VALUE_NUM) from ebm_config where name like 'SKYSERVICELEVELZEIT_'||decode(v_CHANNEL,'DOCUMENT','LETTER','BRIEF','LETTER', 'EMAIL','EMAIL','FAX','FAX','LETTER','LETTER','EMAIL')) ,1), 'HOUR'))
          
          if (V_GLOBAL_inslauntil > sysdate) THEN
             V_GLOBAL_InSLA:=1;
          ELSE 
            V_GLOBAL_InSLA:=0;
          END if;
          
          if (v_FIRSTCLOSING_AGENTID is null) then
            v_FIRSTCLOSING_AGENTID:=v_EXEC_AGENTID;
            v_FIRSTCLOSING_AGENCYID:=v_EXEC_AGENCYID;
          end if;
   END IF;
  
   if (v_event in ('XX') and  :new.COMMENTS = :old.COMMENTS  AND  :new.COMMENTSD  = :old.COMMENTSD) then
    v_event:=v_event; --   not catched Event should not modify the MetaTable
   elsif (v_event in ('PL', 'PR', 'XU')) then
    MERGE INTO EBMF_QUESTIONMETA USING dual ON (QUESTIONID = v_QUESTIONID)
      WHEN MATCHED THEN
       UPDATE SET lastevent = v_EVENT, MODIFIED = sysdate, ASSIGN_AGENCYID=v_ASSIGN_AGENCYID, EXEC_AGENTID=v_EXEC_AGENTID, EXEC_AGENCYID=v_EXEC_AGENCYID,SUBPROJECTID = v_SUBPROJECTID
       WHERE QUESTIONID = v_QUESTIONID
      WHEN NOT MATCHED THEN
       INSERT(QUESTIONID, CASEID, docid, MODIFIED, lastevent, CHANNEL, ASSIGN_AGENCYID,EXEC_AGENTID,EXEC_AGENCYID, SUBPROJECTID, CREATED,FINISHED, FU_CREATED, GLOBAL_inslauntil,
               FU_FINISHED , AG_CREATED  ,AG_FUTIME ,AG_HANDLINGTIME ,AG_IDLETIME  ,AG_TOTALTIME  ,AG_INSLA ,AG_INSLAUNTIL ,GLOBAL_HANDLINGTIME  ,GLOBAL_IDLETIME  ,GLOBAL_TOTALTIME  ,GLOBAL_FUTIME, GLOBAL_InSLA ,FIRSTCLOSING_AGENTID, FIRSTCLOSING_AGENCYID
       )
       VALUES(v_QUESTIONID, v_CASEID, v_DOCID, sysdate, v_EVENT, v_CHANNEL,v_ASSIGN_AGENCYID,v_EXEC_AGENTID,v_EXEC_AGENCYID,v_SUBPROJECTID,v_CREATED,v_FINISHED,v_FU_CREATED,
            (v_CREATED + NUMTODSINTERVAL(nvl((select VALUE_NUM from ebm_config where name like 'SKYSERVICELEVELZEIT_'||decode(v_CHANNEL,'DOCUMENT','LETTER','BRIEF','LETTER', 'EMAIL','EMAIL','FAX','FAX','LETTER','LETTER','EMAIL')) ,24),'HOUR')),
              v_FU_FINISHED , v_AG_CREATED  , v_AG_FUTIME , v_AG_HANDLINGTIME , v_AG_IDLETIME  ,v_AG_TOTALTIME  , v_AG_INSLA , v_AG_INSLAUNTIL , V_GLOBAL_HANDLINGTIME  , V_GLOBAL_IDLETIME  , V_GLOBAL_TOTALTIME  , V_GLOBAL_FUTIME, V_GLOBAL_InSLA, v_FIRSTCLOSING_AGENTID, v_FIRSTCLOSING_AGENCYID  ) ;
    
   else
    MERGE INTO EBMF_QUESTIONMETA USING dual ON (QUESTIONID = v_QUESTIONID)
      WHEN MATCHED THEN
       UPDATE SET lastevent = v_EVENT, MODIFIED = sysdate, ASSIGN_AGENCYID=v_ASSIGN_AGENCYID, EXEC_AGENTID=v_EXEC_AGENTID,CREATED=v_CREATED,FINISHED=v_FINISHED, FU_CREATED=v_FU_CREATED,
        FU_FINISHED =v_FU_FINISHED, AG_CREATED  =v_AG_CREATED, AG_FUTIME =v_AG_FUTIME, AG_HANDLINGTIME =v_AG_HANDLINGTIME, AG_IDLETIME  =v_AG_IDLETIME, AG_TOTALTIME  =v_AG_TOTALTIME, AG_INSLA =v_AG_INSLA, AG_INSLAUNTIL =v_AG_INSLAUNTIL,
        GLOBAL_HANDLINGTIME  =V_GLOBAL_HANDLINGTIME, GLOBAL_IDLETIME  =V_GLOBAL_IDLETIME, GLOBAL_TOTALTIME  =V_GLOBAL_TOTALTIME, GLOBAL_FUTIME  =V_GLOBAL_FUTIME, GLOBAL_INSLAUNTIL  =V_GLOBAL_INSLAUNTIL,GLOBAL_InSLA=V_GLOBAL_InSLA,
        EXEC_AGENCYID=v_EXEC_AGENCYID, SUBPROJECTID = v_SUBPROJECTID, CASEID=v_CASEID, docid=v_DOCID, CHANNEL=v_CHANNEL, FIRSTCLOSING_AGENTID=v_FIRSTCLOSING_AGENTID, FIRSTCLOSING_AGENCYID=v_FIRSTCLOSING_AGENCYID, ftype=v_ftype
       WHERE QUESTIONID = v_QUESTIONID
      WHEN NOT MATCHED THEN
       INSERT(QUESTIONID, CASEID, docid, MODIFIED, lastevent, CHANNEL, ASSIGN_AGENCYID,EXEC_AGENTID,EXEC_AGENCYID, SUBPROJECTID, CREATED,FINISHED, FU_CREATED, GLOBAL_inslauntil,
               FU_FINISHED , AG_CREATED  ,AG_FUTIME ,AG_HANDLINGTIME ,AG_IDLETIME  ,AG_TOTALTIME  ,AG_INSLA ,AG_INSLAUNTIL ,GLOBAL_HANDLINGTIME  ,GLOBAL_IDLETIME  ,GLOBAL_TOTALTIME  ,GLOBAL_FUTIME, GLOBAL_InSLA ,FIRSTCLOSING_AGENTID, FIRSTCLOSING_AGENCYID, ftype
       )
       VALUES(v_QUESTIONID, v_CASEID, v_DOCID, sysdate, v_EVENT, v_CHANNEL,v_ASSIGN_AGENCYID,v_EXEC_AGENTID,v_EXEC_AGENCYID,v_SUBPROJECTID,v_CREATED,v_FINISHED,v_FU_CREATED,
            (v_CREATED + NUMTODSINTERVAL(nvl((select VALUE_NUM from ebm_config where name like 'SKYSERVICELEVELZEIT_'||decode(v_CHANNEL,'DOCUMENT','LETTER','BRIEF','LETTER', 'EMAIL','EMAIL','FAX','FAX','LETTER','LETTER','EMAIL')) ,24),'HOUR')),
              v_FU_FINISHED , v_AG_CREATED  , v_AG_FUTIME , v_AG_HANDLINGTIME , v_AG_IDLETIME  ,v_AG_TOTALTIME  , v_AG_INSLA , v_AG_INSLAUNTIL , V_GLOBAL_HANDLINGTIME  , V_GLOBAL_IDLETIME  , V_GLOBAL_TOTALTIME  , V_GLOBAL_FUTIME, V_GLOBAL_InSLA, v_FIRSTCLOSING_AGENTID, v_FIRSTCLOSING_AGENCYID,v_ftype  ) ;
    end if;   
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
      -- Aenderungsvermerke
      -- Nr. / Datum / Bearbeiter / Beschreibung
      -------------------------------------------------------------------------------
      -- 01 / 21.06.2013 / Meinusch, Kappher / Initial
	    -- 02 / 21.01.2015 / Meinusch / Anpassung an Dokumentation v031
      -- 03 / 18.02.2015 / Meinusch / FS bei Faxen und Briefen
      -------------------------------------------------------------------------------