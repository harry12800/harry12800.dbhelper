SELECT
	a.*, b.table_comment
FROM
	(
		SELECT
			TABLE_NAME AS s,
			COLUMN_NAME,
			DATA_TYPE,
			(
				ifnull(
					CHARACTER_MAXIMUM_LENGTH,
					''
				)
			) AS LENGTH,
			(
				ifnull(NUMERIC_PRECISION, '')
			) AS PRECISIONs,
			IS_NULLABLE,
			COLUMN_COMMENT,
			COLUMN_TYPE,
			COLLATION_NAME,
			(

				IF (
					COLUMN_KEY = 'PRI',
					'YES',
					'NO'
				)
			) AS isMaster,
		 NUMERIC_scale as scale,
		ordinal_position as sort
		FROM
			information_schema. COLUMNS
		WHERE
			table_schema = (SELECT DATABASE())
		AND table_name IN (
			SELECT
				table_name
			FROM
				information_schema. TABLES
			WHERE
				TABLE_SCHEMA = (SELECT DATABASE())
		)
	) a
LEFT JOIN (
	SELECT
		table_comment,
		table_name AS t
	FROM
		information_schema. TABLES
	WHERE
		TABLE_SCHEMA = (SELECT DATABASE())
) b ON b.t = a.s