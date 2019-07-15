create or replace TRIGGER tr_ebm_mitarbeiterlog AFTER INSERT ON mitarbeiterlog FOR EACH ROW DECLARE 
 v_event VARCHAR (10) ;
 v_event_p VARCHAR (10) ;
 v_ASSIGN_AGENCYID ar_mitarbeiter.AGENTUR%TYPE;
 v_ASSIGN_AGENCYIDOLD ar_mitarbeiter.AGENTUR%TYPE;
 v_EXEC_AGENCYID ar_mitarbeiter.AGENTUR%TYPE;
 v_lastlogid_to EBMF_EVENT_JN.logid%TYPE;
 v_lastlogid_from EBMF_EVENT_JN.logid%TYPE; 
 v_FTYPE EBMF_QUESTIONMETA.FTYPE%TYPE;
 BEGIN
 if (INSERTING) then
  IF(:new.aktion=3) then
    if (:new.parameter like '%Kenntnisnahme%') then
      v_event:='EK';
      v_ftype:=4; -- ExternalKentnissnamhe
    elsif (:new.parameter like '%mit Rückantwort%') then
      v_event:='EW';
      v_ftype:=5;  --ExternalWeiterleitung
    elsif (:new.parameter like '%ohne Rückantwort%') then
      v_event:='ET';
      v_ftype:=6;  --ExternalTransfer
    else
       v_event:='XX';
    end if;
    if (v_event<>'XX') then
      begin
       select nvl(logid,0) into v_lastlogid_from 
       from ( select logid, row_number() over (partition by QUESTIONID order by logid desc) rank
           from EBMF_EVENT_JN where QUESTIONID=:new.frageid -- and ASSIGN_AGENCYID=v_ASSIGN_AGENCYID
           and created>sysdate- interval '180' minute and event in ('FS', 'EK', 'EW', 'ET'))
        where rank=1;
        select agentur into v_EXEC_AGENCYID from ar_mitarbeiter where mxid=:new.mitarbeiterid and :new.mitarbeiterid>5;
      exception
        when NO_DATA_FOUND then
        v_lastlogid_from:=0;
        v_EXEC_AGENCYID:=0;
      end;
    
      if (v_lastlogid_from is not null and v_lastlogid_from>0) then
        update EBMF_EVENT_JN set EXEC_AGENTID=:new.mitarbeiterid, EBMF_EVENT_JN.EXEC_AGENCYID=v_EXEC_AGENCYID, EVENT=v_event, EVENTSRC='L'
          where logid=v_lastlogid_from;
         if (v_FTYPE is null or v_FTYPE=0) then
          begin
            select FTYPE into v_FTYPE from EBMF_QUESTIONMETA where QUESTIONID = nvl(:new.frageid,:old.frageid);
          exception
            when NO_DATA_FOUND then
            v_FTYPE:=1;            
          end;
         end if;
          
         MERGE INTO EBMF_QUESTIONMETA USING dual ON (QUESTIONID = nvl(:new.frageid,:old.frageid))
         WHEN MATCHED THEN 
         UPDATE SET MODIFIED=sysdate,  Lastevent=v_event, FTYPE=v_FTYPE
         WHERE QUESTIONID = nvl(:new.frageid,:old.frageid)
         WHEN NOT MATCHED THEN
         INSERT (QUESTIONID,  MODIFIED, Lastevent, FTYPE)
         VALUES ( nvl(:new.frageid,:old.frageid), sysdate , v_event, v_FTYPE);
       end if;
    end if;
 
  ELSIF(:new.aktion=45) then
   begin
    select id into v_ASSIGN_AGENCYID from ar_agentur where name=:new.parameter;
    
    begin
    select nvl(logid,0) into v_lastlogid_to 
    from ( select logid, row_number() over (partition by QUESTIONID order by logid desc) rank
           from EBMF_EVENT_JN where QUESTIONID=:new.frageid and ASSIGN_AGENCYID=v_ASSIGN_AGENCYID and created>sysdate- interval '180' minute and event='AT')
    where rank=1;
    
    select logid, ASSIGN_AGENCYID into v_lastlogid_from, v_ASSIGN_AGENCYIDOLD
    from ( select logid, ASSIGN_AGENCYID, row_number() over (partition by QUESTIONID order by logid desc) rank
           from EBMF_EVENT_JN where QUESTIONID=:new.frageid and event='AF' and logid<v_lastlogid_to)
    where rank=1;
     exception
        when NO_DATA_FOUND then
        v_lastlogid_to:=0;
        v_lastlogid_from:=0;
    end;
    
    if (v_lastlogid_to is not null and v_lastlogid_to>0) then
      begin
        select agentur into v_EXEC_AGENCYID from ar_mitarbeiter where mxid=:new.mitarbeiterid and :new.mitarbeiterid>5;
      exception
        when NO_DATA_FOUND then
        v_EXEC_AGENCYID:=0;
      end;
      if (v_ASSIGN_AGENCYIDOLD in (161, 261)) then -- wenn SKY
        v_event_p:='LF';
        v_event:='LT';        
      elsif (v_EXEC_AGENCYID is not null and v_EXEC_AGENCYID>0) then
        v_event_p:='DF';
        v_event:='DT';
      else 
        v_event_p:='AF';
        v_event:='AT';
      end if;
      
      if (v_lastlogid_from is not null and v_lastlogid_from>0) then
        update EBMF_EVENT_JN set EXEC_AGENTID=:new.mitarbeiterid, EBMF_EVENT_JN.EXEC_AGENCYID=v_EXEC_AGENCYID, EVENT=v_event_p, EVENTSRC='L'
          where logid=v_lastlogid_from;
      end if;
      
      update EBMF_EVENT_JN set EXEC_AGENTID=:new.mitarbeiterid, EBMF_EVENT_JN.EXEC_AGENCYID=v_EXEC_AGENCYID, EVENT=v_event, EVENTSRC='L'
        where logid=v_lastlogid_to;
        
      MERGE INTO EBMF_QUESTIONMETA USING dual ON (QUESTIONID = nvl(:new.frageid,:old.frageid))
      WHEN MATCHED THEN 
      UPDATE SET MODIFIED=sysdate,  Lastevent=v_event
      WHERE QUESTIONID = nvl(:new.frageid,:old.frageid)
      WHEN NOT MATCHED THEN
      INSERT (QUESTIONID,  MODIFIED, Lastevent)
      VALUES ( nvl(:new.frageid,:old.frageid), sysdate , v_event);
    end if;

    exception
        when NO_DATA_FOUND then
        v_ASSIGN_AGENCYID:=0;
    end;
  elsif (:new.aktion=18 and :new.parameter like '%State: erledigt%') then -- filter
    v_event_p:='FI';
    v_ftype:=7;  --AutomaticallyClosed

    INSERT INTO EBMF_EVENT_JN(LOGID, CREATED, QUESTIONID, EVENTSRC, EVENT) VALUES(SQ_AR_EVENT.nextval, sysdate, nvl(:new.frageid,:old.frageid), 'L', v_event_p);

    MERGE INTO EBMF_QUESTIONMETA USING dual ON (QUESTIONID = nvl(:new.frageid,:old.frageid))
    WHEN MATCHED THEN 
         UPDATE SET MODIFIED=sysdate,  Lastevent=v_event, FTYPE=v_FTYPE
         WHERE QUESTIONID = nvl(:new.frageid,:old.frageid)
    WHEN NOT MATCHED THEN
         INSERT (QUESTIONID,  MODIFIED, Lastevent, FTYPE)
         VALUES ( nvl(:new.frageid,:old.frageid), sysdate , v_event, v_FTYPE);
    
  end if;  
  end if;    
  END;