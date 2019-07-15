
-- automatic correct the outgoing queues

update cxdsg_cdocpool
    set  status=0, comment_text='Automatic Restart by maintenance task', prio=(prio+1)
    where parameter in ('810_Archiv', '830_Associate', '820_Outbound') and status=16
    and prio < 10;

commit;
exit
  

