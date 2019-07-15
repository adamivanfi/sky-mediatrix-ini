

alter session set nls_date_format='DD.MM.YYYY HH24:MI:SS';


select sysdate from dual;

exec KPI_OVERVIEW();
select sysdate from dual;


exec AR_COMPLETED_FIX();
select sysdate from dual;

-- exec AR_NAME_FIX();  -- HEITEK 20120914 Deactivated - takes too long.
exec LETTER_MONITORING_FIX();
select sysdate from dual;

exit

