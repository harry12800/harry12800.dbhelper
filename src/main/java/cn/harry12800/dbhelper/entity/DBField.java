package cn.harry12800.dbhelper.entity;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.harry12800.dbhelper.DBType;
import cn.harry12800.dbhelper.PGSqlHelper;
import cn.harry12800.tools.StringUtils;
import cn.harry12800.tree2word.memorydata.Record;

public class  DBField implements Comparable<DBField> {
	public String name;
	public String type;
	public boolean isUnique;
	public boolean isNotNull;
	public int length;
	public int precision;
	public String comment;
	public boolean isPrimaryKey;
	public Integer sort;
	public Integer scale;
	public DBField() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		try {
			List<?> query = PGSqlHelper.query("select * from test");
			int size = query.size();
			System.out.println(size);
			for (Object object : query) {
				Object[] objs = (Object[]) object;
				for (Object object2 : objs) {
					System.out.println(object2.getClass());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	/**
	 * 获取type
	 *	@return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置type
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取isUnique
	 *	@return the isUnique
	 */
	public boolean isUnique() {
		return isUnique;
	}

	/**
	 * 设置isUnique
	 * @param isUnique the isUnique to set
	 */
	public void setUnique(boolean isUnique) {
		this.isUnique = isUnique;
	}

	/**
	 * 获取isNotNull
	 *	@return the isNotNull
	 */
	public boolean isNotNull() {
		return isNotNull;
	}

	/**
	 * 设置isNotNull
	 * @param isNotNull the isNotNull to set
	 */
	public void setNotNull(boolean isNotNull) {
		this.isNotNull = isNotNull;
	}

	/**
	 * 获取length
	 *	@return the length
	 */
	public int getLength() {
		return length;
	}

	/**
	 * 设置length
	 * @param length the length to set
	 */
	public void setLength(int length) {
		this.length = length;
	}

	/**
	 * 获取precision
	 *	@return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * 设置precision
	 * @param precision the precision to set
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
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
	 * 获取isPrimaryKey
	 *	@return the isPrimaryKey
	 */
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	/**
	 * 设置isPrimaryKey
	 * @param isPrimaryKey the isPrimaryKey to set
	 */
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	/**
	 * 获取sort
	 *	@return the sort
	 */
	public Integer getSort() {
		return sort;
	}

	/**
	 * 设置sort
	 * @param sort the sort to set
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}

	/**
	 * 获取scale
	 *	@return the scale
	 */
	public Integer getScale() {
		return scale;
	}

	/**
	 * 设置scale
	 * @param scale the scale to set
	 */
	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public DBField(Record record) {
		Set<Entry<String, Object>> entrySet = record.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			if(key.equals("precision")) {
				precision= Integer.valueOf(entry.getValue()+"");
			}
			if(key.equals("columnType")) {
				type = entry.getValue()+"";
			}
			if(key.equals("uniques")) {
				isUnique = (""+entry.getValue()).equals("1")? true:false;
			}
			if(key.equals("indexName")){
				entry.getValue();
			}
			if(key.equals("columnName")){
				name = entry.getValue()+"";
			}
			if(key.equals("notNull")){
				isNotNull = (""+entry.getValue()).equals("1")? true:false;
			}
			if(key.equals("LENGTH")){
				length = Integer.valueOf(entry.getValue()+"");
			}
			if(key.equals("masterKey")){
				isPrimaryKey = (""+entry.getValue()).equals("1")? true:false;
			}
			if(key.equals("comments")){
				if(entry.getValue()!=null&&!"".equals(entry.getValue() ))
					{
					comment = entry.getValue()+"";
					}
			}
			if(key.equals("tbDesc")){
				entry.getValue();
			}
			if(key.equals("sort")){
				sort = Integer.valueOf(entry.getValue()+"");
			}
			if(key.equals("scale")){
				scale = Integer.valueOf(entry.getValue()+"");
			}
		}
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Field [name=" + name + ", type=" + type + ", isUnique="
				+ isUnique + ", isNotNull=" + isNotNull + ", length=" + length
				+ ", precision=" + precision + ", comment=" + comment
				+ ", isPrimaryKey=" + isPrimaryKey + "]";
	}
	public String getCreateDDL(DBType type) throws Exception{
		switch (type) {
		case MYSQL:
			return name+"\t\t"+getMYSQLType() +" "+ getAttachInfo(DBType.MYSQL);
		default:
			return name+"\t\t"+getOracleType( ) +" "+ getAttachInfo(DBType.ORACLE);
		}
	}
	private String getAttachInfo(DBType type) {
		String info = "";
		if(isNotNull){
			info +=  "NOT NULL";
		}
		if(isUnique&&!isPrimaryKey){
			info += " UNIQUE";
		}
		if(type == DBType.MYSQL&&!StringUtils.isNull(comment)){
			info += " COMMENT '"+comment+"'";
		}
		return info;
		
	}
	private String getOracleType() throws Exception {
		System.err.println(type);
		if("number".equalsIgnoreCase(type)){
		{
			if(precision>0)
				return "NUMBER("+precision+")";
			else {
				return "NUMBER";
			}
		}
		}else if("char".equalsIgnoreCase(type)){
			return "CHAR("+length+")";
		}else if("varchar2".equalsIgnoreCase(type)){
			return "VARCHAR2("+length+")";
		}else if("varchar".equalsIgnoreCase(type)){
			return "VARCHAR2("+length+")";
		}else if("long".equalsIgnoreCase(type)){
			return "LONG";
		} else if("nvarchar2".equalsIgnoreCase(type)){
			return "NVARCHAR2("+length+")";
		}else if("TIMESTAMP(6)".equalsIgnoreCase(type)){
			return "TIMESTAMP";
		}else if("BLOB".equalsIgnoreCase(type)){
			return "BLOB";
		}else if("CLOB".equalsIgnoreCase(type)){
			return "CLOB";
		}else if("int".equalsIgnoreCase(type)){
			return "NUMBER";
		}else if("datetime".equalsIgnoreCase(type)){
			return "TIMESTAMP";
		}else if("tinyint".equalsIgnoreCase(type)){
			return "NUMBER";
		}else if("text".equalsIgnoreCase(type)){
			return "VARCHAR";
		}else if("bigint".equalsIgnoreCase(type)){
			return "NUMBER";
		}else if("longtext".equalsIgnoreCase(type)){
			return "VARCHAR";
		}else if("longblob".equalsIgnoreCase(type)){
			return "CLOB";
		}else if("timestamp".equalsIgnoreCase(type)){
			return "TIMESTAMP";
		}
		throw new Exception("需要增加类型！"+type);
	}
	
	private String getMYSQLType() throws Exception {
		if("number".equalsIgnoreCase(type)){
		{
			if(precision>0)
				return "DECIMAL("+precision+")";
			else {
				return "DECIMAL";
			}
		}
		}else if("char".equalsIgnoreCase(type)){
			return "CHAR("+length+")";
		}else if("varchar2".equalsIgnoreCase(type)){
			return "VARCHAR("+length+")";
		}else if("long".equalsIgnoreCase(type)){
			return "long";
		} else if("nvarchar2".equalsIgnoreCase(type)){
			return "VARCHAR("+length+")";
		}else if("TIMESTAMP(6)".equalsIgnoreCase(type)){
			return "TIMESTAMP";
		}else if("BLOB".equalsIgnoreCase(type)){
			return "BLOB";
		} else if(matcherVarchar(type)){
			return "VARCHAR("+length+")";
		}else if(matcherInt(type)){
			return "INT";
		}else if("mediumtext".equalsIgnoreCase(type)){
			return "MEDIUMTEXT";
		}else if("date".equalsIgnoreCase(type)){
			return "timestamp";
		} else if("datetime".equalsIgnoreCase(type)){
			return "timestamp";
		} else if("text".equalsIgnoreCase(type)){
			return "text";
		}else if("longtext".equalsIgnoreCase(type)){
			return "longtext";
		}else if("longblob".equalsIgnoreCase(type)){
			return "BLOB";
		} else if("timestamp".equalsIgnoreCase(type)){
			return "TIMESTAMP";
		} 
		throw new Exception("需要增加类型！"+type);
	}
	private boolean matcherVarchar(String type) {
		Pattern p = Pattern.compile("varchar(.*?)");
		Matcher matcher = p.matcher(type);
		return matcher.find();
	}
	private boolean matcherInt(String type) {
		Pattern p = Pattern.compile("int(.*?)");
		Matcher matcher = p.matcher(type);
		return matcher.find();
	}
	@Override
	public int compareTo(DBField field) {
		return this.sort-field.sort;
	}
	public void addProp(String propName, Object object) {
		if(propName.equals("PRECISION")) {
			try {
				precision= Integer.valueOf(object+"");
			} catch (Exception e) {
				precision=0;
			}
		}
		if(propName.equals("COLUMNTYPE")) {
			type = object+"";
		}
		if(propName.equals("UNIQUES")) {
			isUnique = (""+object).equals("1")? true:false;
		}
		if(propName.equals("INDEXNAME")){
		}
		if(propName.equals("COLUMNNAME")){
			name = object+"";
		}
		if(propName.equals("NOTNULL")){
			isNotNull = (""+object).equals("1")? true:false;
		}
		if(propName.equals("LENGTH")){
			try {
				length= Integer.valueOf(object+"");
			} catch (Exception e) {
				length=0;
			}
		}
		if(propName.equals("MASTERKEY")){
			isPrimaryKey = (""+object).equals("1")? true:false;
			if(!isPrimaryKey){
				isPrimaryKey = (""+object).equals("YES")? true:false;
			}
		}
		if(propName.equals("COMMENTS")){
			if(object!=null&&!"".equals(object))
				comment = object+"";
		}
		if(propName.equals("TBDESC")){
		}
		if(propName.equals("SORT")){
			sort = Integer.valueOf(object+"");
		}
		if(propName.equals("SCALE")){
			try {
				scale= Integer.valueOf(object+"");
			} catch (Exception e) {
				scale=0;
			}
		}
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
		DBField other = (DBField) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
}
