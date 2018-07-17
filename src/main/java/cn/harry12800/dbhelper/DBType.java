package cn.harry12800.dbhelper;

public enum DBType {

	ORACLE("ORACLE"), DB2("DB2"), MYSQL("MYSQL"), SYBASE("SYBASE"), SQLSERVER(
			"SQLSERVER"), INFORMIX("INFORMIX"), ACCESS("ACCESS"), POSTGRESQL(
			"POSTGRESQL");
	String name;

	DBType(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

}
