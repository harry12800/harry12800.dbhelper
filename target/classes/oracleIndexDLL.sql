SELECT u.index_name,DBMS_METADATA.GET_DDL('INDEX',u.index_name)
FROM USER_INDEXES u