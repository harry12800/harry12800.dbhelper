package cn.harry12800.dbhelper;

import java.util.ArrayList;

import cn.harry12800.tree2word.adapter.INodeAdapter;
import cn.harry12800.tree2word.core.INode;
import cn.harry12800.tree2word.core.NodeType;
import cn.harry12800.tree2word.memorydata.Record;
import cn.harry12800.tree2word.memorydata.RelationshipGuard;
import cn.harry12800.tree2word.memorydata.Table;
import cn.harry12800.tree2word.style.NodeStyleType;

/**
 * node数据的适配器，生成word文档中的一个节点。
 * 
 * @author harry12800
 */
public class DBAdapter extends INodeAdapter {
	public DBAdapter() {
	}

	private Record record;

	public DBAdapter(Record record, Table table, RelationshipGuard guard)
			throws Exception {
		this.record = record;
		loadChildData(table);
	}

	@Override
	public String getWordTitle() {
		return "" + record.get("name");
	}

	@Override
	public String getWordValue() {
		return null;
	}

	/**
	 * 加载当前节点的信息。
	 * 
	 * @param table
	 * @throws CustomException
	 */
	private void loadChildData(Table table) throws Exception {
		if (record == null)
			throw new Exception("节点为空！");
		/**
		 * 获取名称信息。
		 */
		name = String.valueOf(record.get("name"));
	}

	@Override
	public ArrayList<INode> getContentChild() {
		return contentList;
	}

	@Override
	public String getSuperLink() {
		return superLink;
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.NODE;
	}

	@Override
	public INodeAdapter generateCurrentNode(Record record, Table table,
			RelationshipGuard guard) throws Exception {
		INodeAdapter node = new DBAdapter(record, table, guard);
		return node;
	}

	@Override
	public ArrayList<NodeStyleType> getNodeStyleType() {
		ArrayList<NodeStyleType> styles = new ArrayList<NodeStyleType>();
		TableDataMeta TableDataMeta =   (cn.harry12800.dbhelper.TableDataMeta) record.get("dataDictionary");
		if (TableDataMeta == null)
			return null;
		DBTableNodeStyleType e = new DBTableNodeStyleType(TableDataMeta);
		styles.add(e);
		return styles;
	}

}
