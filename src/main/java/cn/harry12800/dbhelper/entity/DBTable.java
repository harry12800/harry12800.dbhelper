package cn.harry12800.dbhelper.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.harry12800.dbhelper.DBType;
import cn.harry12800.tools.EntityMent;
import cn.harry12800.tools.StringUtils;

public class DBTable {

	String name;
	String comment;
	boolean hasForeignKey;
	List<String> foreignKey = new ArrayList<>(0);
	Set<DBTable> foreignKeyTable = new HashSet<>(0);
	/**
	 * 数据库字段名
	 * @return
	 */
	public List<String> getAllDBFieldName(){
		List<String> list = new ArrayList<String>(0);
		for (DBField field : getFields()) {
			String dbColumnName = field.getName();
			list.add(dbColumnName);
		}
		return list;
	}
	/**
	 * java字段名
	 * @return
	 */
	public List<String> getAllFieldName(){
		List<String> list = new ArrayList<String>(0);
		for (DBField field : getFields()) {
			String dbColumnName = field.getName();
			String attrName = EntityMent
					.columnName2EntityAttrName(dbColumnName);
			attrName = attrName.replaceAll(" ", "");
			list.add(attrName);
		}
		return list;
	}
	public DBTable() {
	}

	/**
	 * 获取comment
	 *	@return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * 设置comment
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * 获取name
	 *	@return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置name
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	private LinkedHashSet<DBField> fields = new LinkedHashSet<DBField>(0);

	/**
	 * 获取fields
	 *	@return the fields
	 */
	public LinkedHashSet<DBField> getFields() {
		return fields;
	}

	/**
	 * 设置fields
	 * @param fields the fields to set
	 */
	public void setFields(LinkedHashSet<DBField> fields) {
		this.fields = fields;
	}

	public String getCreateDDL(DBType dbType) throws Exception {
		if(dbType == DBType.ORACLE)
		{
			return getDDLOracle();
		}
		if(dbType == DBType.MYSQL)
		{
			return getDDLMYSQL();
		}
		return "";
	}

	private String getDDLMYSQL() throws Exception {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE ").append(name).append("(\r\n");
		for (DBField e : fields) {
			sBuffer.append("\t").append(e.getCreateDDL(DBType.MYSQL)).append(",\r\n");
		}
		if(hasForeignKey){
			for (String str : foreignKey) {
				sBuffer.append("\t").append(str).append(",\r\n");
			}
		}
		sBuffer.deleteCharAt(sBuffer.length() - 1);
		sBuffer.deleteCharAt(sBuffer.length() - 1);
		sBuffer.deleteCharAt(sBuffer.length() - 1);
		
		boolean hasPrimaryKey = false;
		for (DBField e : fields) {
			if (e.isPrimaryKey && !hasPrimaryKey) {
				sBuffer.append(",\r\n\tPRIMARY KEY(").append(e.name);
				hasPrimaryKey = true;
			} else if (e.isPrimaryKey) {
				sBuffer.append(",").append(e.name);
			}
		}
		if (hasPrimaryKey)
			sBuffer.append(")");
		sBuffer.append("\r\n)");
		if(!StringUtils.isNull(comment)){
			sBuffer.append("COMMENT='"+comment+"'");
		}
		sBuffer.append(";");
		return sBuffer.toString();
	}

	private String getDDLOracle() throws Exception {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("CREATE TABLE ").append(name).append("(\r\n");
		for (DBField e : fields) {
			sBuffer.append("\t").append(e.getCreateDDL(DBType.ORACLE)).append(",\r\n");
		}
		if(hasForeignKey){
			for (String str : foreignKey) {
				sBuffer.append("\t").append(str).append(",\r\n");
			}
		}
		sBuffer.deleteCharAt(sBuffer.length() - 1);
		sBuffer.deleteCharAt(sBuffer.length() - 1);
		sBuffer.deleteCharAt(sBuffer.length() - 1);
		
		boolean hasPrimaryKey = false;
		for (DBField e : fields) {
			if (e.isPrimaryKey && !hasPrimaryKey) {
				sBuffer.append(",\r\n\tPRIMARY KEY(").append(e.name);
				hasPrimaryKey = true;
			} else if (e.isPrimaryKey) {
				sBuffer.append(",").append(e.name);
			}
		}
		if (hasPrimaryKey)
			sBuffer.append(")");
		sBuffer.append("\r\n);");
		return sBuffer.toString();
	}

	public String getCreateCommentDDL(DBType dbType) throws Exception {

		switch (dbType) {
		case MYSQL:
			return mysqlCommentDDL();
		case ORACLE:
			return oracleCommentDDL();
		default:
			break;
		}
		return "";
	}

	private String mysqlCommentDDL() {
		StringBuffer sBuffer = new StringBuffer();
		if(comment!=null)
		sBuffer.append("ALTER TABLE '").append(name).append("' COMMENT ")
				.append("'" + comment + "';\r\n");
		for (DBField e : fields) {
			if(!StringUtils.isNull(e.comment))
			{
				sBuffer.append("ALTER TABLE '").append(name).append("' MODIFY COLUMN '")
				.append(e.name).append("' COMMENT ")
				.append("'" + e.comment+ "';\r\n");
			}
		}
		return sBuffer.toString();
	}

	private String oracleCommentDDL() {
		StringBuffer sBuffer = new StringBuffer();
		if(comment==null){
			comment = "";
		}
		if(comment!=null)
		sBuffer.append("COMMENT ON TABLE ").append(name).append(" IS ")
				.append("'" + comment + "';\r\n");
		for (DBField e : fields) {
			if(StringUtils.isNull(e.comment)){
				e.comment = "...";
			}
			if(!StringUtils.isNull(e.comment))
			{
				sBuffer.append("COMMENT ON COLUMN ").append(name).append(".")
				.append(e.name).append(" IS ")
				.append("'" + e.comment+ "';\r\n");
			}
		}
		return sBuffer.toString();
	}

	public void addField(DBField field) {
		fields.add(field);
	}
	 
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBTable other = (DBTable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void addForeginKey(String fieldName, DBTable table,
			String fyFieldName,String deleteRule) {
		if(table !=this)
		foreignKeyTable.add(table);
		hasForeignKey = true;
		String string;
		if("cascade".equalsIgnoreCase(deleteRule))
		  string= "FOREIGN KEY("+fieldName+") REFERENCES "+table.getName()+"("+fyFieldName+") ON DELETE CASCADE";
		else{
		  string= "FOREIGN KEY("+fieldName+") REFERENCES "+table.getName()+"("+fyFieldName+") ";
		}
		foreignKey.add(string);
	}
	public void removeForeignKeyTable(DBTable table){
		foreignKeyTable.remove(table);
	}
	public void removeForeignKeyTable(Set<DBTable> tables){
		foreignKeyTable.removeAll(tables);
	}
	public int getForeignKeyTableCount( ){
		return foreignKeyTable.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DBTable [name=" + name + "]";
	}
	
}
