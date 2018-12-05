package cn.harry12800.dbhelper;

 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.harry12800.dbhelper.entity.DBField;
import cn.harry12800.dbhelper.entity.DBTable;
import cn.harry12800.tools.FileUtils;
import cn.harry12800.tree2word.memorydata.Record;
import cn.harry12800.tree2word.memorydata.RelationshipGuard;
import cn.harry12800.tree2word.memorydata.Table;

/**
 * 
 * @author harry12800
 *
 */
public class PGSqlHelper implements Db{
	Table dbTable = new Table("db");
	Table oneTable = new Table("oneTable");
	/* 三个table的主外键关系，及逻辑条件的守护者 */
	RelationshipGuard guard = new RelationshipGuard();
	public static String url = "jdbc:postgresql://127.0.0.1:5432/postgres";
	public static String user = "postgres";
	public static String password = "admin123";
	
	// 连接数据库的方法
	public static Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			Connection conn = DriverManager.getConnection(url, user, password);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 
	/**
	 * 得到数据库表结构
	 * @return
	 */
	public static  List<DBTable> getDBTableSturctureMap() throws Exception {
		String sql = FileUtils.getSqlStringByFileName("mysqlDbDescSql.sql");
		System.out.println(sql);
		List<?> lst = query(sql);
		Map<String, DBTable> map = new HashMap<String, DBTable>(0);
		for (Object object : lst) {
			Object[] o = (Object[]) object;
			if (map.get(o[0]) == null) {
				DBTable table = new DBTable();
				map.put(o[0].toString(), table);
				table.setName(o[0].toString());
				table.setComment( o[12].toString());
			}
			DBField field = new DBField();
			System.err.println(o[2]);
			field.addProp("COLUMNNAME", o[1]);
			field.addProp("COLUMNTYPE", o[2]);
			field.addProp("LENGTH", o[3]);
			field.addProp("PRECISION", o[4]);
			field.addProp("NOTNULL", o[5]);
			field.addProp("COMMENTS", o[6]);
			field.addProp("UNIQUES", o[7]);
			field.addProp("INDEXNAME", o[8]);
			field.addProp("MASTERKEY", o[9]);
			field.addProp("SCALE", o[10]);
			field.addProp("SORT", o[11]);
			field.addProp("TBDESC", o[12]);
			map.get(o[0]).addField(field);
		}
//		sql = FileUtils.getSqlStringByFileName("ForeginKey.sql");
//		System.out.println(sql);
//		lst = query(sql);
//		for (Object object : lst) {
//			Object[] o = (Object[]) object;
//			String FieldName=o[1].toString();
//			DBTable table = map.get(o[2].toString());
//			String fyFieldName = o[3].toString();
//			String deleteRule = o[4].toString();
//			map.get(o[0].toString()).addForeginKey(FieldName,table,fyFieldName,deleteRule);
//		}
		 List<DBTable> list = new ArrayList<DBTable>();
		 list.addAll(map.values()  );
		return  list;
	}
	/**
	 * 获取数据库的word描述文件
	 * @throws Exception
	 */
	public void init() throws Exception {
		realData(PGSqlHelper.getDBTableSturctureMap());
		addRelationShip();
		oneTable.setAdapter(new  DBAdapter());
		dbTable.start("id", "1", guard);
	}
	/**
	 * 获取数据库的word描述文件
	 * @throws Exception
	 */
	public void init(String path) throws Exception {
		realData(PGSqlHelper.getDBTableSturctureMap());
		addRelationShip();
		oneTable.setAdapter(new  DBAdapter());
		dbTable.applyFileName(path, PGSqlHelper.user, "id", "1", guard);
	}

	/**
	 * 从查询语句到查询结果List
	 * @param sql
	 * @return
	 * @throws Exception 
	 */
	public static List<?> query(String sql) throws Exception {
		 // 同样先要获取连接，即连接到数据库public   Connection conn;
		try (Connection conn = getConnection();
			Statement	st = (Statement) conn.createStatement();
			ResultSet rs = st.executeQuery(sql);){
			 // 创建用于执行静态sql语句的Statement对象，st属局部变量
			 // 执行sql查询语句，返回查询数据的结果集
			return resultSetToList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("查询数据失败");
			throw new Exception("数据库操作失败！");
		} 
	}

	/**
	 * 将rs结果集转换成List
	 * @param rs
	 * @return
	 * @throws java.sql.SQLException
	 */
	public static List<?> resultSetToList(ResultSet rs)
			throws java.sql.SQLException {
		if (rs == null)
			return Collections.EMPTY_LIST;
		ResultSetMetaData md = rs.getMetaData(); // 得到结果集(rs)的结构信息，比如字段数、字段名等
		int columnCount = md.getColumnCount(); // 返回此 ResultSet 对象中的列数
		List<Object[]> list = new ArrayList<Object[]>();
		// Map rowData = new HashMap();
		while (rs.next()) {
			// rowData = new HashMap(columnCount);
			Object[] rowData = new Object[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				// rowData.put(md.getColumnName(i), rs.getObject(i));
				rowData[i - 1] = rs.getObject(i);
			}
			list.add(rowData);
		}
		return list;
	}

	public void realData(List<DBTable> list) {
		Record r = new Record();
		r.put("id", "1");
		r.put("name", "数据库数据字典");
		dbTable.put(r);
		for (  DBTable table : list) {
			Record r1 = new Record();
			readNode(r1, table);
			r1.put("name", table.getName());
			oneTable.put(r1);
		}
	}

	/**
	 * 
	 * @param r1
	 * @param table 
	 * @param value
	 * @param title
	 */
	private void readNode(Record r1, DBTable table ) {
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
		for (DBField field : table.getFields()) {
			ArrayList<String> a = new ArrayList<String>();
			a.add(String.valueOf(field.getName()));
			a.add(String.valueOf(field.isPrimaryKey));
			a.add(String.valueOf(field.getType()));
			a.add(String.valueOf(field.isNotNull()));
			a.add(String.valueOf(field.isUnique()));
			a.add(String.valueOf(field.getLength()));
			a.add(String.valueOf(field.getPrecision()));
			a.add(String.valueOf(""));
			a.add(String.valueOf(field.getComment()));
			a.add(String.valueOf(table.getComment()));
			contents.add(a);
		}
		r1.put("dataDictionary", tableDataMeta);
		r1.put("parentId", "1");
	}

	/**
	 * 添加表与表之间的关系
	 */
	private void addRelationShip() throws Exception {
		guard.addFatherSon(dbTable, "id", oneTable, "parentId");
		guard.printFatherSon();
	}
	@Override
	public  void generateDescFile(String url, String user, String pwd) throws Exception {
	
		if(url!=null)	PGSqlHelper.url= url;
		if(user !=null ) PGSqlHelper.user= user;
		if(pwd!=null) PGSqlHelper.password= pwd;
		new PGSqlHelper().init();
	}

	public static void main(String[] args) throws Exception {
		new PGSqlHelper().generateDescFile(null,null,null);
	}
	@Override
	public Map<String, List<String>> getTableAndColumns(String url,
			String user, String pwd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<String, String> getTableComments(String url, String user,
			String pwd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDDL(String url, String user, String pwd) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getCommentDDL(String url, String user, String pwd)
			throws Exception {
		return null;
	}
	@Override
	public List<DBTable> getTableDetail(String url, String user,
			String pwd) throws Exception {
		PGSqlHelper.url = url;
		PGSqlHelper.user = user;
		PGSqlHelper.password = pwd;
		return getDBTableSturctureMap();
	}
	@Override
	public boolean testConnection() {
		Connection connection = getConnection();
		if(connection==null)
			return false;
		return true;
	}

	@Override
	public boolean testConnection(String url, String user, String pwd) {
		Connection connection = getConnection(url, user, pwd);
		if(connection==null)
			return false;
		return true;
	}
	private static Connection getConnection(String url, String user, String pwd) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(url, user, pwd);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void generateDescFile(String url, String user, String pwd,
			String path) throws Exception {

		if(url!=null)	PGSqlHelper.url= url;
		if(user !=null ) PGSqlHelper.user= user;
		if(pwd!=null) PGSqlHelper.password= pwd;
		new PGSqlHelper().init(path);
	}
	@Override
	public void exeSql(String sql) throws Exception {
		// 同样先要获取连接，即连接到数据库public Connection conn;
		try (Connection conn = getConnection();
//				PreparedStatement st = conn.prepareStatement(sql);
				Statement st = (Statement) conn.createStatement();
				) {
			String[] split = sql.split(";");
			for (String string : split) {
				st.addBatch(string);
			}
			st.executeBatch();
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
	public void exeSql(String url, String user, String pwd, String sql) throws Exception{
		if(url!=null)	PGSqlHelper.url= url;
		if(user !=null ) PGSqlHelper.user= user;
		if(pwd!=null) PGSqlHelper.password= pwd;
		exeSql(sql);
	}
}
