package cn.harry12800.dbhelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import cn.harry12800.tree2word.adapter.INodeAdapter;
import cn.harry12800.tree2word.core.WordStyleManager;
import cn.harry12800.tree2word.style.NodeStyleType;

public class DBTableNodeStyleType implements NodeStyleType {

	protected TableDataMeta tableDataMeta = null;
	public DBTableNodeStyleType(TableDataMeta tableDataMeta  ) {
		this.tableDataMeta = tableDataMeta;
	}
	
	/** 
     * @Description: 跨列合并 
     */  
    public  void mergeCellsHorizontal(XWPFTable table, int row, int fromCell, int toCell) {  
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {  
            XWPFTableCell cell = table.getRow(row).getCell(cellIndex);  
            if ( cellIndex == fromCell ) {  
                // The first merged cell is set with RESTART merge value  
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);  
            } else {  
                // Cells which join (merge) the first one, are set with CONTINUE  
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);  
            }  
        }  
    }  
    /** 
     * @Description: 跨列合并 
     */  
    public  void mergeCellsVorizontal(XWPFTable table, int col, int fromCell, int toCell) {  
        for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {  
            XWPFTableCell cell = table.getRow(cellIndex).getCell(col);  
            if ( cellIndex == fromCell ) {  
                // The first merged cell is set with RESTART merge value  
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);  
            } else {  
                // Cells which join (merge) the first one, are set with CONTINUE  
                cell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);  
            }  
        }  
    }  
	@Override
	public INodeAdapter addStyle(INodeAdapter node, XWPFDocument doc)
			throws Exception {
		int cols = tableDataMeta.heads.size();
		int rows = tableDataMeta.contents.size();
		XWPFTable table = doc.createTable(rows + 2, cols);
		realTitle(table);
		XWPFTableRow firstRow = table.getRow(1);
		firstRow.setHeight(380); 
		XWPFTableCell firstCell;
		for (int j = 0; j < cols; j++) {
			firstCell = firstRow.getCell(j);
			setCellTextB(firstCell, tableDataMeta.heads.get(j), "cccccc", 1600);
		}
		for (int i = 2; i < rows + 2; i++) {
			firstRow = table.getRow(i);
			firstRow.setHeight(380);
			int j = 0;
			for (; j < cols; j++) {
				firstCell = firstRow.getCell(j);
				if(i!=2||j!=cols-1)
				setCellText(firstCell,  tableDataMeta.contents.get(i - 2).get(j), "FFFFFF",
						1600);
			}
			if(i==2)createHSpanCell(firstRow.getCell(j-1),tableDataMeta.contents.get(i - 2).get(j-1), "CCCCCC", 1600,
					STMerge.RESTART);
		}
		// createSimpleTableNormal(doc);
		return node;
	}

	private void realTitle(XWPFTable table) {
		XWPFTableRow firstRow = table.getRow(0);
		firstRow.setHeight(380);  
		XWPFTableCell firstCell = firstRow.getCell(0);
		setCellTextB(firstCell,  tableDataMeta.title, "cccccc", 1600);
		mergeCellsHorizontal(table, 0, 0,   tableDataMeta.heads.size()-1);
		mergeCellsVorizontal(table, tableDataMeta.contents.get(0).size()-1, 2,tableDataMeta.contents.size()+1);
	}

	// 表格正常边框
	public void createSimpleTableNormal(XWPFDocument doc) throws Exception {
		List<String> columnList = new ArrayList<String>();
		columnList.add("序号");
		columnList.add("姓名信息|姓甚|名谁");
		columnList.add("名刺信息|籍贯|营生");
		XWPFTable table = doc.createTable(2, 5);

		CTTbl ttbl = table.getCTTbl();
		CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl
				.getTblPr();
		CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr
				.addNewTblW();
		CTJc cTJc = tblPr.addNewJc();
		cTJc.setVal(STJc.Enum.forString("center"));
		tblWidth.setW(new BigInteger("8000"));
		tblWidth.setType(STTblWidth.DXA);

		XWPFTableRow firstRow = null;
		XWPFTableRow secondRow = null;
		XWPFTableCell firstCell = null;
		XWPFTableCell secondCell = null;

		for (int i = 0; i < 2; i++) {
			firstRow = table.getRow(i);
			firstRow.setHeight(380);
			for (int j = 0; j < 5; j++) {
				firstCell = firstRow.getCell(j);
				setCellText(firstCell, "测试", "FFFFC9", 1600);
			}
		}

		firstRow = table.insertNewTableRow(0);
		secondRow = table.insertNewTableRow(1);
		firstRow.setHeight(380);
		secondRow.setHeight(380);
		for (String str : columnList) {
			if (str.indexOf("|") == -1) {
				firstCell = firstRow.addNewTableCell();
				secondCell = secondRow.addNewTableCell();
				createVSpanCell(firstCell, str, "CCCCCC", 1600, STMerge.RESTART);
				createVSpanCell(secondCell, "", "CCCCCC", 1600, null);
			} else {
				String[] strArr = str.split("\\|");
				firstCell = firstRow.addNewTableCell();
				createHSpanCell(firstCell, strArr[0], "CCCCCC", 1600,
						STMerge.RESTART);
				for (int i = 1; i < strArr.length - 1; i++) {
					firstCell = firstRow.addNewTableCell();
					createHSpanCell(firstCell, "", "CCCCCC", 1600, null);
				}
				for (int i = 1; i < strArr.length; i++) {
					secondCell = secondRow.addNewTableCell();
					setCellText(secondCell, strArr[i], "CCCCCC", 1600);
				}
			}
		}
	}

	public void setCellText(XWPFTableCell cell, String text, String bgcolor,
			int width) {
		CTTc cttc = cell.getCTTc();
		XWPFParagraph p = cell.getParagraphs().get(0);
		XWPFRun pRun = p.createRun();
		WordStyleManager.setParagraphRunFontInfo(p, "", pRun, text, "宋体", "18");
		CTTcPr cellPr = cttc.addNewTcPr();
		cellPr.addNewTcW().setW(BigInteger.valueOf(width));
		 
		CTTcPr ctPr = cttc.addNewTcPr();
		CTShd ctshd = ctPr.addNewShd();
		ctshd.setFill(bgcolor);
		ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
		cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.LEFT);
		//cell.setText(text);
		CTPPr ds =   cttc.getPList().get(0).addNewPPr();
		
		ds.addNewRPr().addNewI().setVal(STOnOff.TRUE );
		ds.addNewRPr().addNewB().setVal(STOnOff.TRUE);
	}
	public void setCellTextB(XWPFTableCell cell, String text, String bgcolor,
			int width) {
		CTTc cttc = cell.getCTTc();
		XWPFParagraph p = cell.getParagraphs().get(0);
		XWPFRun pRun = p.createRun();
		WordStyleManager.setParagraphRunFontInfoB(p, "", pRun, text, "宋体", "18");
		CTTcPr cellPr = cttc.addNewTcPr();
		cellPr.addNewTcW().setW(BigInteger.valueOf(width));
		 
		CTTcPr ctPr = cttc.addNewTcPr();
		CTShd ctshd = ctPr.addNewShd();
		ctshd.setFill(bgcolor);
		ctPr.addNewVAlign().setVal(STVerticalJc.CENTER);
		cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
		//cell.setText(text);
		CTPPr ds =   cttc.getPList().get(0).addNewPPr();
		
		ds.addNewRPr().addNewI().setVal(STOnOff.TRUE );
		ds.addNewRPr().addNewB().setVal(STOnOff.TRUE);
	}
	public void createHSpanCell(XWPFTableCell cell, String value,
			String bgcolor, int width, STMerge.Enum stMerge) {
		CTTc cttc = cell.getCTTc();
		CTTcPr cellPr = cttc.addNewTcPr();
		cellPr.addNewTcW().setW(BigInteger.valueOf(width));
		cell.setColor(bgcolor);
		cellPr.addNewHMerge().setVal(stMerge);
		cellPr.addNewVAlign().setVal(STVerticalJc.CENTER);
		cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
		cttc.getPList().get(0).addNewR().addNewT().setStringValue(value);
	}

	public void createVSpanCell(XWPFTableCell cell, String value,
			String bgcolor, int width, STMerge.Enum stMerge) {
		CTTc cttc = cell.getCTTc();
		CTTcPr cellPr = cttc.addNewTcPr();
		cellPr.addNewTcW().setW(BigInteger.valueOf(width));
		cell.setColor(bgcolor);
		cellPr.addNewVMerge().setVal(stMerge);
		cellPr.addNewVAlign().setVal(STVerticalJc.CENTER);
		cttc.getPList().get(0).addNewPPr().addNewJc().setVal(STJc.CENTER);
		cttc.getPList().get(0).addNewR().addNewT().setStringValue(value);
	}
}
