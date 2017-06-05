package daydayup.openstock.netease;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.util.DocUtil;

public class WashedFileLoadContext {

	public static class DataTypeContext {
		private String type;
		private AtomicInteger nextRow = new AtomicInteger(1);
		private XSpreadsheet xSheet;
		private List<String> header = new ArrayList<>();
		private Map<String, Integer> headerColumnMap = new HashMap<>();

		public DataTypeContext(String type, XSpreadsheet xSheet) {
			this.type = type;
			this.xSheet = xSheet;
		}

		public int getAndIncrementNextRow() {
			return nextRow.getAndIncrement();
		}

		public void writeRow(List<String> keyList, List<Object> valueList) {
			int row = this.getAndIncrementNextRow();

			for (int i = 0; i < keyList.size(); i++) {
				String key = keyList.get(i);
				Integer col = headerColumnMap.get(key);
				if (col == null) {
					col = header.size();
					header.add(key);
					headerColumnMap.put(key, col);
					DocUtil.setText(xSheet, col, 0, key);// write header.
				}
				DocUtil.setValue(xSheet, col, row, valueList.get(i));
			}

		}

		public String format(Object obj) {
			if (obj == null) {
				return "";
			} else if (obj instanceof Date) {
				return NeteaseUtil.DF.format((Date) obj);
			} else {
				return obj.toString();
			}

		}
	}

	public XSpreadsheetDocument xDoc;

	private Map<String, DataTypeContext> nextRowMap = new HashMap<>();

	public WashedFileLoadContext(XComponentContext cc) {
		xDoc = DocUtil.getSpreadsheetDocument(cc);
	}

	

	public DataTypeContext getOrCreateTypeContext(String type) {
		DataTypeContext rt = nextRowMap.get(type);
		if (rt == null) {
			XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(xDoc, type);
			rt = new DataTypeContext(type, xSheet);
			nextRowMap.put(type, rt);
		}
		return rt;
	}

}
