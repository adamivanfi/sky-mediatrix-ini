create or replace TRIGGER "ITYX_MX"."TR_EBM_MXREPORTING" AFTER INSERT OR UPDATE OR DELETE ON mx_reporting FOR EACH ROW
 DECLARE
  -- Constant(s):
  c_questionid constant number := nvl(:new.FRAGEID,:old.FRAGEID); -- Questionid PK-Variable
  c_documentid constant mx_reporting.DOCUMENTID%TYPE := nvl(:new.DOCUMENTID,:old.DOCUMENTID); -- Document-ID
  c_channel    constant mx_reporting.CHANNEL%TYPE := nvl(:new.CHANNEL,:old.CHANNEL); -- Channel
  -- Variable(s):
  v_event           VARCHAR2(10);
  v_logid           number;
  v_lastcustomerid  mx_reporting.CUSTOMERID%TYPE;
  v_newcustomerid   mx_reporting.CUSTOMERID%TYPE := trim(:new.CUSTOMERID);
  v_subprojectid    ebmf_questionmeta.SUBPROJECTID%TYPE;
  -- v_assign_agencyid ebmf_questionmeta.ASSIGN_AGENCYID%TYPE;
  -- v_exec_agentid    ebmf_questionmeta.EXEC_AGENTID%TYPE;
  -- v_exec_agencyid   ebmf_questionmeta.EXEC_AGENCYID%TYPE;
  -- v_err_num         NUMBER;
  -- v_err_msg         VARCHAR2(250 byte);

 BEGIN
  execute immediate 'ALTER SESSION SET TIME_ZONE = ''CET''';

  --insert into XXX_DWH_TRIGGER_TEST (FRAGEID, CUSTOMERID_OLD, CUSTOMERID_NEW, CUSTOMERID_FRAGE, CREATED_OLD, CREATED_NEW, ANMERKUNG)
  --  values (c_questionid, :old.CUSTOMERID, :new.CUSTOMERID, (select EMAIL_EXTRA3 from frage where "ID" = c_questionid and TEILPROJEKTID<>1125 and rownum < 2), :old.CREATED, :new.CREATED, '...');

  if (1 < c_questionid) then -- needed to avoid problems during initialization of Questions
    v_logid := SQ_AR_EVENT.nextval; -- get next logid for event journal

    -- Enrich data for insert into EBMF_EVENT_JN:
    begin
    -- As EBMF_QUESTIONMETA is not written in this trigger (see MERGE-comment below!) it's CUSTOMERID is not guaranteed!
    --  select CUSTOMERID, SUBPROJECTID, ASSIGN_AGENCYID, EXEC_AGENTID, EXEC_AGENCYID
    --    into v_lastcustomerid, v_subprojectid, v_assign_agencyid, v_exec_agentid, v_exec_agencyid
    --    from ebmf_questionmeta where QUESTIONID=c_questionid and rownum=1;
      select trim(EMAIL_EXTRA3), TEILPROJEKTID into v_lastcustomerid, v_subprojectid
        from frage where "ID" = c_questionid and rownum < 2;
    exception
      when NO_DATA_FOUND then
        v_subprojectid := null;
        v_lastcustomerid := null;
    --    v_exec_agentid := null;
    --    v_exec_agencyid := null;
    --    v_assign_agencyid := null;
    --    v_err_num := SQLCODE;
    --    v_err_msg := SQLERRM(v_err_num)
    end;

    IF (DELETING OR nvl(v_newcustomerid,'0') = '0') then
      v_event := 'CD';
    elsif (v_lastcustomerid is not null and v_lastcustomerid = v_newcustomerid) then
      v_event := 'CU';
    elsif (v_lastcustomerid is not null and v_lastcustomerid <> v_newcustomerid) then
      v_event := 'CR';
    else
      v_event := 'CI';
    end if;

    --insert into XXX_DWH_TRIGGER_TEST (FRAGEID, CUSTOMERID_OLD, CUSTOMERID_NEW, CUSTOMERID_FRAGE, CREATED_OLD, CREATED_NEW, ANMERKUNG, EVENT)
    --  values (c_questionid, v_lastcustomerid, v_newcustomerid, (select EMAIL_EXTRA3 from frage where "ID" = c_questionid and TEILPROJEKTID<>1125 and rownum < 2), :old.CREATED, :new.CREATED, null, v_event);

    -- Insert the event into eventjournal
    INSERT INTO csdwh_ebm.EBMF_EVENT_JN (LOGID, CREATED, QUESTIONID,  EVENTSRC, EVENT,  SUBPROJECTID) --, ASSIGN_AGENCYID, EXEC_AGENTID, EXEC_AGENCYID)
        VALUES (SQ_AR_EVENT.nextval, sysdate, c_questionid, 'M', v_event, v_subprojectid); --, v_assign_agencyid, v_exec_agentid, v_exec_agencyid);

    -- insert the association into questioncustomer
    INSERT INTO csdwh_ebm.EBMF_QUESTIONCUSTOMER(LOGID, CREATED, QUESTIONID, DOCID, CUSTOMERID, CONTRACTID, CONTACTID )
      VALUES (v_logid, sysdate, c_questionid, :new.DOCUMENTID, v_newcustomerid, :new.CONTRACTID, :new.CONTACTID);

    -- within this trigger it is important to skip the questionmeta merge, otherwise you will get deadlocks
    --MERGE INTO EBMF_QUESTIONMETA USING dual ON (QUESTIONID = c_questionid)
    --  WHEN MATCHED THEN
    --  UPDATE SET DOCID=c_documentid, CUSTOMERID=:new.customerid, CONTRACTID=:new.contractid, CHANNEL=c_channel, MODIFIED=sysdate, CONTACTID=:new.CONTACTID, LASTEVENT=v_event
    --  WHERE QUESTIONID = c_questionid
    --  WHEN NOT MATCHED THEN
    --  INSERT (QUESTIONID, DOCID, CUSTOMERID, CONTRACTID, CHANNEL, MODIFIED, CONTACTID, LASTEVENT)
    --  VALUES (c_questionid, c_documentid, :new.CUSTOMERID, :new.CONTRACTID, c_channel, sysdate ,:new.CONTACTID, v_event);

  end if;
END;