SELECT
d.TABLE_NAME tbName,
COALESCE(t.COMMENTS, ' ') tbDesc, 
a.COLUMN_NAME columnName, 
a.DATA_TYPE columnType, 
a.CHAR_LENGTH width, 
nvl(a.DATA_PRECISION,0) precision, 
decode(a.NULLABLE,'Y','0','1') notNull, 
COALESCE(m.COMMENTS, ' ') comments, 
decode(k.uniqueness,'UNIQUE','1','0') uniques,  
COALESCE(k.index_name, ' ') indexName, 
decode(k.key,'Y','1','0') masterKey,
nvl(a.DATA_SCALE,0) SCALE, 
nvl(a.COLUMN_ID,0) SORT 
FROM
user_tab_columns a
INNER JOIN user_tables d on a.TABLE_NAME=d.TABLE_NAME
LEFT JOIN user_tab_comments t ON t.TABLE_NAME=d.TABLE_NAME
LEFT JOIN user_col_comments m ON m.COLUMN_NAME=a.COLUMN_NAME AND m.TABLE_NAME=d.TABLE_NAME
LEFT JOIN
(
SELECT e.index_name,u.TABLE_NAME,u.COLUMN_NAME,e.uniqueness,decode(p.constraint_name,NULL,'N','Y') key
from user_indexes e INNER JOIN user_ind_columns u ON e.index_name=u.index_name
LEFT JOIN ( select constraint_name from user_constraints where constraint_type='P' ) p ON e.index_name=p.constraint_name
) k ON k.TABLE_NAME=a.TABLE_NAME and k.COLUMN_NAME=a.COLUMN_NAME
ORDER BY tbName,sort