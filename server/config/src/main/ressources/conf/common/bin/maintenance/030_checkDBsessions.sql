SELECT sysdate, SID, SERIAL#, STATUS , username , sid ||','||serial# sidserial
---, 'ALTER SYSTEM KILL SESSION '''|| sid ||','||serial# || ''' IMMEDIATE;', s.*
FROM V$SESSION s  WHERE USERNAME in ('ITYX_MX', 'ITYX_MX_USER','ITYX_CX', 'ITYX_CX_USER' );

exit