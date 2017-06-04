package daydayup.openstock.netease;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.util.DocUtil;

public class WashedFileLoadContext {

	public XSpreadsheetDocument xDoc;

	private Map<String, AtomicInteger> nextRowMap = new HashMap<>();

	public WashedFileLoadContext(XComponentContext cc) {
		xDoc = DocUtil.getSpreadsheetDocument(cc);
	}

	public int getAndIncrementNextRow(String sheetName) {
		AtomicInteger rt = nextRowMap.get(sheetName);
		if (rt == null) {
			rt = new AtomicInteger();
			nextRowMap.put(sheetName, rt);
		}
		return rt.getAndIncrement();
	}

}
