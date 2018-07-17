package cn.harry12800.dbhelper;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.harry12800.dbhelper.entity.DBField;
import cn.harry12800.dbhelper.entity.DBTable;
import cn.harry12800.tools.FileUtils;
import cn.harry12800.tools.StringUtils;
import cn.harry12800.tree2word.core.Props;
import cn.harry12800.tree2word.memorydata.Record;
import cn.harry12800.tree2word.memorydata.RelationshipGuard;
import cn.harry12800.tree2word.memorydata.Table;

/**
 * racle Client Version : 11.2.0.1.0 Source Server : 140_sso_client Source
 * Server Version : 110200 Source Host : 121.201.38.140:1521 Source Schema :
 * SSO_CLIENT
 * 
 * @author harry12800
 */
public class OracleHelper implements Db {
	static Table DBRootTable = new Table("DBRootTable");
	static Table DBTableStructrue = new Table("DBTableStructrue");
	static Table DLLTable = new Table("DLLTable");
	/* 三个table的主外键关系，及逻辑条件的守护者 */
	static RelationshipGuard guard = new RelationshipGuard();
	public static String url = "jdbc:oracle:thin:@192.168.0.70:1521:testdb";
	public static String user = "pssm";
	public static String password = "pssm";

	// 连接数据库的方法
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	// 连接数据库的方法
	public static Connection getConnection(String url, String user,
			String password) {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public synchronized void generateDescFile(String url, String user,
			String pwd) throws Exception {
		 DBRootTable = new Table("DBRootTable");
		  DBTableStructrue = new Table("DBTableStructrue");
		  DLLTable = new Table("DLLTable");
		guard.clear();
		if (url != null)
			OracleHelper.url = url;
		if (url != null)
			OracleHelper.user = user;
		if (url != null)
			OracleHelper.password = pwd;
		Map<String, DBTable> map = getDBTableSturctureMap();
		Map<Object, List<Record>> map1 = getIndexDLLMap();
		realDBRootTable();
		realTableSturctureData(map);
		realDLLData(map1);
		addRelationShip();
		DBTableStructrue.setAdapter(new DBAdapter());
		DLLTable.setAdapter(new DBAdapter());
		DLLTable.setContent(true);
		DBRootTable.applyFileName(Props.wordPath, OracleHelper.user, "id", "1",
				guard);
	}

	@Override
	public  synchronized void generateDescFile(String url, String user, String pwd,
			String path) throws Exception {
		 DBRootTable = new Table("DBRootTable");
		  DBTableStructrue = new Table("DBTableStructrue");
		  DLLTable = new Table("DLLTable");
		guard = new RelationshipGuard();
		if (url != null)
			OracleHelper.url = url;
		if (url != null)
			OracleHelper.user = user;
		if (url != null)
			OracleHelper.password = pwd;
		Map<String, DBTable> map = getDBTableSturctureMap();
		Map<Object, List<Record>> map1 = getIndexDLLMap();
		realDBRootTable();
		realTableSturctureData(map);
		realDLLData(map1);
		addRelationShip();
		DBTableStructrue.setAdapter(new DBAdapter());
		DLLTable.setAdapter(new DBAdapter());
		DLLTable.setContent(true);
		DBRootTable.applyFileName(path, OracleHelper.user, "id", "1", guard);

	}

	private static void realDBRootTable() {
		Record r = new Record();
		r.put("id", "1");
		r.put("name", user + "数据库数据字典");
		DBRootTable.put(r);
	}

	private static void realDLLData(Map<Object, List<Record>> map1) {
		for (Entry<Object, List<Record>> entry : map1.entrySet()) {
			Record r1 = new Record();
			readDLLNode(r1, entry.getValue(), entry.getKey());
			r1.put("name", entry.getKey());
			DLLTable.put(r1);
		}
	}

	private static void readDLLNode(Record r1, List<Record> value, Object key) {
		TableDataMeta tableDataMeta = new TableDataMeta();
		ArrayList<List<String>> contents = new ArrayList<List<String>>();
		ArrayList<String> heads = new ArrayList<String>();
		tableDataMeta.setHeads(heads);
		tableDataMeta.setContents(contents);
		tableDataMeta.setTitle("索引名：" + key);
		heads.add("DLL语句");
		for (Record r : value) {
			ArrayList<String> a = new ArrayList<String>();
			a.add(String.valueOf(r.get("dll")));
			contents.add(a);
		}
		r1.put("dataDictionary", tableDataMeta);
		r1.put("parentId", "1");
	}

	private static Map<Object, List<Record>> getIndexDLLMap() throws Exception {
		String sql = FileUtils.getSqlStringByFileName("oracleIndexDLL.sql");
		StringUtils.errorln(sql);
		List<?> lst = query(sql);
		Map<Object, List<Record>> map = new HashMap<Object, List<Record>>();
		for (Object object : lst) {
			Object[] o = (Object[]) object;
			if (map.get(o[0]) == null) {
				List<Record> value = new ArrayList<Record>();
				map.put(o[0], value);
			}
			Record r = new Record();
			r.put("dll", o[1].toString().toLowerCase());
			map.get(o[0]).add(r);
		}

//		 for(Entry<Object,List<Record>> entry: map.entrySet()){
//		 StringUtils.errorln(entry.getValue().get(0));
//		 }
		return map;
	}

	/**
	 * oracle.sql.Clob类型转换成String类型
	 * 
	 * @param clob
	 * @return
	 */
	public static String ClobToString(Clob clob) {
		String reString = "";
		try {
			Reader is = clob.getCharacterStream();// 得到流
			BufferedReader br = new BufferedReader(is);
			String s = br.readLine();
			StringBuffer sb = new StringBuffer();
			while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
				sb.append(s);
				s = br.readLine();
			}
			reString = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reString;
	}

	/**
	 * 得到数据库表结构
	 * 
	 * @return
	 */
	public static Map<String, DBTable> getDBTableSturctureMap()
			throws Exception {
		String sql = FileUtils.getSqlStringByFileName("oracleDbDescSql.sql");
		System.out.println(sql);
		List<?> lst = query(sql);
		Map<String, DBTable> map = new HashMap<String, DBTable>(0);
		for (Object object : lst) {
			Object[] o = (Object[]) object;
			if (map.get(o[0]) == null) {
				DBTable table = new DBTable();
				map.put(o[0].toString(), table);
				table.setName(o[0].toString());
				table.setComment(o[1].toString());
			}
			DBField field = new DBField();
			field.addProp("TBDESC", o[1]);
			field.addProp("COLUMNNAME", o[2]);
			field.addProp("COLUMNTYPE", o[3]);
			field.addProp("LENGTH", o[4]);
			field.addProp("PRECISION", o[5]);
			field.addProp("NOTNULL", o[6]);
			field.addProp("COMMENTS", o[7]);
			field.addProp("UNIQUES", o[8]);
			field.addProp("INDEXNAME", o[9]);
			field.addProp("MASTERKEY", o[10]);
			field.addProp("SCALE", o[11]);
			field.addProp("SORT", o[12]);
			map.get(o[0]).addField(field);
		}
		sql = FileUtils.getSqlStringByFileName("oracleForeginKey.sql");
		System.out.println(sql);
		lst = query(sql);
		for (Object object : lst) {
			Object[] o = (Object[]) object;
			String FieldName = o[1].toString();
			DBTable table = map.get(o[2].toString());
			String fyFieldName = o[3].toString();
			String deleteRule = o[4].toString();
			map.get(o[0].toString()).addForeginKey(FieldName, table,
					fyFieldName, deleteRule);
		}
		return map;
	}

	/**
	 * 从查询语句到查询结果List
	 * 
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public static List<?> query(String sql) throws Exception {
		// 同样先要获取连接，即连接到数据库public Connection conn;
		try (Connection conn = getConnection();
				Statement st = (Statement) conn.createStatement();
				ResultSet rs = st.executeQuery(sql);) {
			// 创建用于执行静态sql语句的Statement对象，st属局部变量
			// 执行sql查询语句，返回查询数据的结果集
			return resultSetToList(rs);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("查询数据失败");
			throw new Exception("数据库操作失败！");
		}
	}

	public static List<?> resultSetToList(ResultSet rs) {
		if (rs == null)
			return Collections.EMPTY_LIST;
		List<Object[]> list = new ArrayList<Object[]>();
		try {
			ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
			int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数

			// Map rowData = new HashMap();
			while (rs.next()) {
				// rowData = new HashMap(columnCount);
				Object[] rowData = new Object[columnCount];
				for (int i = 1; i <= columnCount; i++) {
					// rowData.put(md.getColumnName(i), rs.getObject(i));
					Object obj = rs.getObject(i);
					if (obj instanceof java.sql.Clob) {
						rowData[i - 1] = ClobToString((Clob) obj);
					} else
						rowData[i - 1] = obj;
				}
				list.add(rowData);
				// StringUtils.outln("list:" + list.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void realTableSturctureData(Map<String, DBTable> map) {

		for (Entry<String, DBTable> entry : map.entrySet()) {
			Record r1 = new Record();
			readTableSturctureNode(r1, entry.getValue());
			r1.put("name", entry.getKey());
			DBTableStructrue.put(r1);
		}
	}

	/**
	 * 
	 * @param r1
	 * @param value
	 * @param title
	 */
	private static void readTableSturctureNode(Record r1, DBTable table) {
		TableDataMeta tableDataMeta = new TableDataMeta();
		ArrayList<List<String>> contents = new ArrayList<List<String>>();
		ArrayList<String> heads = new ArrayList<String>();
		tableDataMeta.setHeads(heads);
		tableDataMeta.setContents(contents);
		tableDataMeta.setTitle(table.getName());
		heads.add("字段名称");
		heads.add("是否主键");
		heads.add("字段类型");
		heads.add("是否为空");
		heads.add("唯一性");
		heads.add("长度");
		heads.add("精度");
		heads.add("索引名称");
		heads.add("注解");
		heads.add("表的描述");
		for (DBField r : table.getFields()) {
			ArrayList<String> a = new ArrayList<String>();
			a.add(String.valueOf(r.getName()));
			a.add(String.valueOf(r.isPrimaryKey()));
			a.add(String.valueOf(r.getType()));
			a.add(String.valueOf(r.isNotNull()));
			a.add(String.valueOf(r.isUnique()));
			a.add(String.valueOf(r.getLength()));
			a.add(String.valueOf(r.getPrecision()));
			a.add(String.valueOf(""));
			a.add(String.valueOf(r.getComment()));
			a.add(String.valueOf(table.getComment()));
			contents.add(a);
		}
		r1.put("dataDictionary", tableDataMeta);
		r1.put("parentId", "1");
	}

	/**
	 * 添加表与表之间的关系
	 */
	private static void addRelationShip() throws Exception {
		guard.addFatherSon(DBRootTable, "id", DBTableStructrue, "parentId");
		guard.addFatherSon(DBRootTable, "id", DLLTable, "parentId");
		guard.printFatherSon();
	}

	@Override
	public Map<String, List<String>> getTableAndColumns(String url,
			String user, String pwd) throws Exception {
		OracleHelper.url = url;
		OracleHelper.user = user;
		OracleHelper.password = pwd;
		String sql = "select TABLE_NAME,COLUMN_NAME from user_tab_columns";
		List<?> lst = query(sql);
		Map<String, List<String>> result = new HashMap<String, List<String>>(0);
		System.out.println(lst.size());
		for (int i = 0; i < lst.size(); i++) {
			Object[] obj = (Object[]) lst.get(i);
			String tableName = obj[0].toString();
			String columnName = obj[1].toString();
			if (result.get(tableName) == null) {
				ArrayList<String> l = new ArrayList<String>(0);
				result.put(tableName, l);
				l.add(columnName);
			} else {
				result.get(tableName).add(columnName);
			}
		}
		return result;
	}

	@Override
	public synchronized Map<String, String> getTableComments(String url,
			String user, String pwd) throws Exception {
		OracleHelper.url = url;
		OracleHelper.user = user;
		OracleHelper.password = pwd;
		String sql = "SELECT TABLE_NAME,COMMENTS FROM USER_TAB_COMMENTS WHERE TABLE_NAME IN (SELECT TABLE_NAME FROM USER_TABLES)";
		List<?> lst = query(sql);
		Map<String, String> result = new HashMap<String, String>(0);
		System.out.println(lst.size());
		for (int i = 0; i < lst.size(); i++) {
			Object[] obj = (Object[]) lst.get(i);
			String tableName = obj[0].toString();
			String comments = obj[1] == null ? "" : obj[1].toString();
			result.put(tableName, comments);
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		new OracleHelper().generateDescFile(
				"jdbc:oracle:thin:@192.168.0.205:1521:testdb", "myqdp02",
				"myqdp02");
	}

	@Override
	public List<DBTable> getTableDetail(String url, String user, String pwd)
			throws Exception {
		OracleHelper.url = url;
		OracleHelper.user = user;
		OracleHelper.password = pwd;
		Map<String, DBTable> dbTableSturctureMap = getDBTableSturctureMap();
		Collection<DBTable> values = dbTableSturctureMap.values();
		List<DBTable> sortTables = new ArrayList<DBTable>();
		boolean flag = true;
		while (flag) {
			Iterator<DBTable> iterator = values.iterator();
			Set<DBTable> set = new HashSet<DBTable>();
			boolean mark = false;
			while (iterator.hasNext()) {
				DBTable table = (DBTable) iterator.next();
				if (0 == table.getForeignKeyTableCount()) {
					set.add(table);
					if (!sortTables.contains(table))
						sortTables.add(table);
				} else {
					mark = true;
				}
			}
			flag = mark;
			iterator = values.iterator();
			while (iterator.hasNext()) {
				DBTable table = (DBTable) iterator.next();
				table.removeForeignKeyTable(set);
			}
		}
		return sortTables;
	}

	@Override
	public String getDDL(String url, String user, String pwd) throws Exception {
		return null;
	}

	@Override
	public String getCommentDDL(String url, String user, String pwd)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean testConnection() {
		Connection connection = getConnection();
		if (connection == null)
			return false;
		return true;
	}

	@Override
	public boolean testConnection(String url, String user, String pwd) {
		Connection connection = getConnection(url, user, pwd);
		if (connection == null)
			return false;
		return true;
	}

	@Override
	public void exeSql(String sql) throws Exception {
		// 同样先要获取连接，即连接到数据库public Connection conn;
		try (Connection conn = getConnection();
//			PreparedS/tatement st = conn.prepareStatement(sql);
				Statement st = (Statement) conn.createStatement();
				) {
			String[] split = sql.split(";");
			for (String string : split) {
				st.execute(string);
			}
			//st.executeUpdate(sql);
//			st.execute(sql); 
			// 创建用于执行静态sql语句的Statement对象，st属局部变量
			// 执行sql查询语句，返回查询数据的结果集
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("数据库操作失败！"+e.getMessage());
		}
	}

	@Override
	public void exeSql(String url, String user, String pwd, String sql) throws Exception {
		if (url != null)
			OracleHelper.url = url;
		if (url != null)
			OracleHelper.user = user;
		if (url != null)
			OracleHelper.password = pwd;
		exeSql(sql);
	}

}
