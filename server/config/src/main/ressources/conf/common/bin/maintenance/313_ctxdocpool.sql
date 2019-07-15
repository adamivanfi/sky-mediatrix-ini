--- Achtung! auch vom 120_consistencycheck_ctx verwendet


-- 400 Docs die aufgrund von Timeout auf's Error laufen 
update cxdsg_cdocpool 
   set status=0, parameter = '400_CustomerIndexingE', locking=null, locktime=0 
 where status=16 and parameter like '4%' 
  and  locktime <datetoityxtime(sysdate - interval '1' hour)
  and  createtime <datetoityxtime(sysdate - interval '2' hour)
;
commit;

-- 66x Docs die mit Status 48 abgeschlossen werden aber korrekt über 600 Prozess ins Mediatrix weitergeleitet wurden
update cxdsg_cdocpool
set    status=32, comment_text='Status automatisch korrigiert am '||sysdate
where status=48
  and parameter like '66%_MX_ManualIndexing'
  and collectionid in (select ctx_documentid from ntt_cx_report where collectionid =ctx_documentid and currentdocpool=600 and step='END') 
  and  locktime <datetoityxtime(sysdate - interval '2' hour) and  createtime <datetoityxtime(sysdate - interval '2' hour)
;

commit;
exit