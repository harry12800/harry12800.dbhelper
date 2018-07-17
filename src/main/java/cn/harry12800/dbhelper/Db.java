package cn.harry12800.dbhelper;
 

import java.util.List;
import java.util.Map;

import cn.harry12800.dbhelper.entity.DBTable;

/**
 * 
 * @author harry12800
 *
 */
public interface Db {
	public String getDDL(String url,String user,String pwd) throws Exception;
	public String getCommentDDL(String url,String user,String pwd) throws Exception;
	/**
	 * 生成数据库的描述文件。word文档。
	 * @param url
	 * @param user
	 * @param pwd
	 * @throws Exception
	 */
	public void generateDescFile(String url,String user,String pwd) throws Exception;
	public void generateDescFile(String url,String user,String pwd,String path) throws Exception;
	/**
	 * 得到数据库实例的用户表和表的所有字段。
	 * @param url
	 * @param user
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public Map<String,List<String>> getTableAndColumns(String url,String user,String pwd) throws Exception;
	public Map<String,String>  getTableComments(String url,String user, String pwd) throws Exception ;
	
	/**
	 * Map<Object, List<Record>>  键是表名，值是List，存放每一个字段。
	 * @param url
	 * @param user
	 * @param pwd
	 * @return  Map<Object, List<Record>>  键是表名，值是List，存放每一个字段。
	 */
	public List<DBTable> getTableDetail(String url, String user, String pwd)  throws Exception;
	public boolean testConnection();
	public boolean testConnection(String url, String user, String pwd);
	public void exeSql(String sql) throws Exception;
	public void exeSql(String url, String user, String pwd, String sql) throws Exception;
	  
}
