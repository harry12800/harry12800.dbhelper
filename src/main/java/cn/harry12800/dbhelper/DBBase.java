package cn.harry12800.dbhelper;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author harry12800
 * 
 */
public class DBBase {

	public String oracleUrl = "";
	public String db2Url = "";
	public String mysqlUrl = "";
	public String sybaseUrl = "";
	public String sqlserverUrl = "";
	public String informixUrl = "";
	public String accessUrl = "";
	public String PostgreSQLUrl = "";


	/**
	 * 得到jdbc的链接属性，暂时支持mysql ，oracle
	 * @param dbType
	 * @param ip
	 * @param port
	 * @param dbName
	 * @param userName
	 * @param pwd
	 * @return
	 */
	public static Map<String,String> getDBBase(DBType dbType,String ip,String port,String dbName,String userName,String pwd) {
		
		if(dbType == DBType.ORACLE){
			return Oracle.getMap( ip, port, dbName, userName, pwd);
		}
		if(dbType == DBType.MYSQL){
			return Mysql.getMap( ip, port, dbName, userName, pwd);
		}
		return null;
	}

	static class Oracle extends DBBase {
		public String oracleDriver = "oracle.jdbc.driver.OracleDriver";
		// Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		// String url="jdbc:oracle:thin:@localhost:1521:orcl"; //orcl为数据库的SID
		// String user="test";
		// String password="test";
		// Connection conn= DriverManager.getConnection(url,user,password);

		public static Map<String, String> getMap(String ip, String port,
				String dbName, String userName, String pwd) {
			Map<String,String> map = new HashMap<String,String>(4);
			map.put("jdbc.driver", "oracle.jdbc.driver.OracleDriver");
			map.put("jdbc.url", "jdbc:oracle:thin:@"+ip+":"+port+":"+dbName);
			map.put("jdbc.username", userName);
			map.put("jdbc.password", pwd);
			return map;
		}
	}

	class DB2 extends DBBase {
		public String db2Driver = "com.ibm.db2.jdbc.app.DB2Driver";
		// Class.forName("com.ibm.db2.jdbc.app.DB2Driver   ").newInstance();
		// String url="jdbc:db2://localhost:5000/sample"; //sample为你的数据库名
		// String user="admin";
		// String password="";
		// Connection conn= DriverManager.getConnection(url,user,password);

	}

	static class Mysql extends DBBase {

		public String mysqlDriver = "com.mysql.jdbc.Driver";
		// Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		// //或者Class.forName("com.mysql.jdbc.Driver");
		// String url
		// ="jdbc:mysql://localhost/myDB?user=soft&password=soft1234&useUnicode=true&characterEncoding=8859_1"
		// //myDB为数据库名
		// Connection conn= DriverManager.getConnection(url);
		//

		public static Map<String, String> getMap(String ip, String port,
				String dbName, String userName, String pwd) {
			Map<String,String> map = new HashMap<String,String>(4);
			map.put("jdbc.driver", "com.mysql.jdbc.Driver");
			map.put("jdbc.url", "jdbc:mysql://"+ip+":"+port+"/"+dbName);
			map.put("jdbc.username", userName);
			map.put("jdbc.password", pwd);
			return map;
		}
	}

	class sybase extends DBBase {
		public String sybaseDriver = "com.sybase.jdbc.SybDriver";
		// Class.forName("com.sybase.jdbc.SybDriver").newInstance();
		// String url ="   jdbc:sybase:Tds:localhost:5007/myDB";//myDB为你的数据库名
		// Properties sysProps = System.getProperties();
		// SysProps.put("user","userid");
		// SysProps.put("password","user_password");
		// Connection conn= DriverManager.getConnection(url, SysProps);
	}

	class sqlserver extends DBBase {

		public String sqlserverDriver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		// Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver").newInstance();
		// String
		// url="jdbc:microsoft:sqlserver://localhost:1433;DatabaseName=mydb";
		// //mydb为数据库
		// String user="sa";
		// String password="";
		// Connection conn= DriverManager.getConnection(url,user,password);
	}

	class Informix extends DBBase {

		public String InformixDriver = "com.informix.jdbc.IfxDriver";
		// Class.forName("com.informix.jdbc.IfxDriver").newInstance();
		// String url =
		// "jdbc:informix-sqli://123.45.67.89:1533/myDB:INFORMIXSERVER=myserver;
		// user=testuser;password=testpassword"; //myDB为数据库名
		// Connection conn= DriverManager.getConnection(url);
		//
	}

	class access extends DBBase {
		// 8、access数据库直连用ODBC的
		public String accessDriver = "sun.jdbc.odbc.JdbcOdbcDriver";
		// Class.forName("sun.jdbc.odbc.JdbcOdbcDriver") ;
		// String
		// url="jdbc:odbc:Driver={MicroSoft   Access   Driver   (*.mdb)};DBQ="+application.getRealPath("/Data/ReportDemo.mdb");
		// Connection conn = DriverManager.getConnection(url,"","");
	}

	class PostgreSQL extends DBBase {

		public String PostgreSQLDriver = "org.postgresql.Driver";

		// Class.forName("org.postgresql.Driver").newInstance();
		// String url ="jdbc:postgresql://localhost/myDB" //myDB为数据库名
		// String user="myuser";
		// String password="mypassword";
		// Connection conn= DriverManager.getConnection(url,user,password);
		//
	}
}
