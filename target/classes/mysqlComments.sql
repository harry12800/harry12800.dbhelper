SELECT     
concat(    
    'alter table ',     
    table_schema, '.', table_name,     
    ' modify column ', column_name, ' ', column_type, ' ',     
    if(is_nullable = 'YES', ' ', 'not null '),     
    if(column_default IS NULL, '',     
        if(    
            data_type IN ('char', 'varchar')     
            OR     
            data_type IN ('date', 'datetime', 'timestamp') AND column_default != 'CURRENT_TIMESTAMP',     
            concat(' default ''', column_default,''''),     
            concat(' default ', column_default)    
        )    
    ),     
    if(extra is null or extra='','',concat(' ',extra)),  
    ' comment ''', column_comment, ''';'    
) s    
FROM information_schema.columns    
WHERE table_schema = '${schema}'    
    AND table_name = '${tablename}'   