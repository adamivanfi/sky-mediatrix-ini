select * 
from ar_agentur
where name
in ('Teltow', 'Schwerin', 'D+S');

select count(1) num, q.channel channel,a.name name
from ebmf_event_jn j
join ar_agentur a
on a.id=j.EXEC_AGENCYID
join EBMF_QUESTIONMETA q
on q.QUESTIONID=j.QUESTIONID
where 
a.name in ('Teltow', 'Schwerin', 'D+S')
and j.CREATED>='02.06.16'
and j.created <'03.06.16'
group by q.channel,a.name
order by a.name,num desc
;

select count(1) num, a.name name
from ebmf_event_jn j
join ar_agentur a
on a.id=j.EXEC_AGENCYID
join EBMF_QUESTIONMETA q
on q.QUESTIONID=j.QUESTIONID
where 
a.name in ('Teltow', 'Schwerin', 'D+S')
and j.CREATED>='02.06.16'
and j.created <'03.06.16'
group by a.name
order by name
;

select count(1) num, q.channel channel,a.name name
from ebmf_event_jn j
join ar_agentur a
on a.id=j.EXEC_AGENCYID
join EBMF_QUESTIONMETA q
on q.QUESTIONID=j.QUESTIONID
where 
a.name not in ('Teltow', 'Schwerin', 'D+S')
and j.CREATED>='02.06.16'
and j.created <'03.06.16'
group by q.channel,a.name
order by a.name,num desc
;

select count(1) num, a.name name
from ebmf_event_jn j
join ar_agentur a
on a.id=j.EXEC_AGENCYID
join EBMF_QUESTIONMETA q
on q.QUESTIONID=j.QUESTIONID
where 
a.name not in ('Teltow', 'Schwerin', 'D+S')
and j.CREATED>='02.06.16'
and j.created <'03.06.16'
group by a.name
order by name
;