package cn.harry12800.dbhelper;

import java.util.ArrayList;
import java.util.List;

public class TableDataMeta {

	public String title = "标题";
	public ArrayList<String> heads = new ArrayList<String>();
	public ArrayList<List<String>> contents = new ArrayList<List<String>>();
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public ArrayList<String> getHeads() {
		return heads;
	}
	public void setHeads(ArrayList<String> heads) {
		this.heads = heads;
	}
	public ArrayList<List<String>> getContents() {
		return contents;
	}
	public void setContents(ArrayList<List<String>> contents) {
		this.contents = contents;
	}
}
