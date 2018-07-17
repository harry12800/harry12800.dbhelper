package cn.harry12800.dbhelper.sample;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cn.harry12800.dbhelper.Db;
import cn.harry12800.dbhelper.MysqlHelper;
import cn.harry12800.dbhelper.OracleHelper;

/**
 * 使用数据库链接 ，将Oracle或者Mysql数据库的表信息 展示在word文档中。生成数据字典，使用poi，形成有图片，表格，目录。字体等的word文档
 * ，其中使用了harry12800.tree2word项目。完整的项目jdk1.7.导入直接运行，有sample
 * @author harry12800
 *
 */
public class Sample {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * Mysql的数据库生成数据字典工具
		 */
		Db db  = new MysqlHelper();
		/**
		 * 设置为null 将使用代码里默认的链接
		 */
		 
		String url = "jdbc:mysql://192.168.0.109:3306/nytm";
		String user = "root";
		String pwd = "admin";
		//203.110.160.90:33899
		  url = "jdbc:oracle:thin:@203.110.160.90:1521:orcl";
		  user = "myqdp_pharm";
		  pwd = "myqdp_pharm";
		//db.generateDescFile(url, user, pwd);
		/**
		 * oracle 的数据库生成数据字典工具
		 */
		db  = new OracleHelper();
		//db.generateDescFile(url, user, pwd);
		Map<String, List<String>> a = db.getTableAndColumns(url, user, pwd);
		Set<Entry<String,List<String>>> entrySet = a.entrySet();
		for (Entry<String, List<String>> entry : entrySet) {
			
			System.out.println(entry.getKey());
			List<String> value = entry.getValue();
			for (String string : value) {
				System.out.println("	"+string); 
			}
		}
	}
}
