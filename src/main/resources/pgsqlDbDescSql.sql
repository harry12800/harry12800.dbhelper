SELECT col_description(a.attrelid,a.attnum) as comment,format_type(a.atttypid,a.atttypmod) as type,a.attname as name, a.attnotnull as notnull1
FROM pg_class as c,pg_attribute as a where c.relname = 'test' and a.attrelid = c.oid and a.attnum>0

select relname as tabname, cast (obj_description (relfilenode,'pg_class' ) as varchar ) as comment FROM
pg_class  WHERE relname = 'r' and relname not like 'pg_%' and relname not like 'sql_%' order by relname
