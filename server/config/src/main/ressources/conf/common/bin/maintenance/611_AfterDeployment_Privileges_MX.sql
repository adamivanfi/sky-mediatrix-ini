BEGIN
 -- TABLE AND VIEW GRANT
  FOR o IN (select * from user_objects  where status='VALID' and object_type in ('VIEW', 'TABLE'))
    LOOP
     EXECUTE  IMMEDIATE  'GRANT  SELECT  ON  ityx_mx.' || o.object_name || '    TO  ANDE42' ;
     EXECUTE  IMMEDIATE  'GRANT  SELECT  ON  ityx_mx.' || o.object_name || '    TO  ITYX_MX_USER' ;
   --EXECUTE  IMMEDIATE  'GRANT  SELECT  ON  ityx_mx.' || o.object_name || '    TO  BENE04';    -- Only PROD User
    END LOOP ;
END GRANT_SEL_SKY;
/


-- commit;
exit