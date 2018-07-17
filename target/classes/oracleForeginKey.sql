SELECT a.table_name,a.COLUMN_NAME,b.TABLE_NAME,b.COLUMN_NAME,a.DELETE_RULE FROM (SELECT cu.TABLE_NAME,COLUMN_NAME,au.R_CONSTRAINT_NAME,AU.DELETE_RULE 
  FROM USER_CONS_COLUMNS CU, USER_CONSTRAINTS AU
 WHERE CU.CONSTRAINT_NAME = AU.CONSTRAINT_NAME
   AND AU.CONSTRAINT_TYPE = 'R') a, USER_CONS_COLUMNS b WHERE a.R_CONSTRAINT_NAME = b.CONSTRAINT_NAME