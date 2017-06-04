package daydayup.openstock.netease;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.uno.UnoRuntime;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.openstock.util.DocUtil;

/**
 * <code>
 * 
 * 
 * 
 * </code> Process csv file,load data into db.
 * 
 * @author wu
 *
 * @param <T>
 * @param <I>
 */
public abstract class AbstractWashedFileProcessor extends FileProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractWashedFileProcessor.class);
	String sheetName;

	public AbstractWashedFileProcessor(String sheetName) {
		this.sheetName = sheetName;
	}

	@Override
	public void process(Reader fr, WashedFileLoadContext xContext) {

		XSpreadsheet xSheet = this.prepareSpreadsheet(xContext);

		CSVReader reader = new CSVReader(fr);
		try {
			CsvHeaderRowMap headers = new CsvHeaderRowMap();
			CsvRowMap body = new CsvRowMap();
			CsvRowMap currentMap = null;
			int lineNumber = 0;
			while (true) {
				lineNumber++;
				String[] next = reader.readNext();
				if (next == null) {
					break;
				}
				if ("Header".equals(next[0])) {
					currentMap = headers;
					continue;
				} else if ("Body".equals(next[0])) {
					currentMap = body;
					continue;
				}
				// the name of the item.
				String key = next[0];
				key = key.trim();
				if (key.length() == 0 && next.length <= 1) {
					// ignore this empty line.
					continue;
				}
				currentMap.put(key, new CsvRow(lineNumber, next));
			}
			//
			Date[] reportDateArray = headers.getReportDateArray();
			BigDecimal unit = headers.get("单位", true).getAsBigDecimal(1, true);
			String corpId = headers.get("公司代码", true).getString(1, true);
			List<String> itemKeyList = body.keyList;

			// TODO make sure the itemKeyList is the same sequence with all
			// other body in the same sheet.

			for (int i = 0; i < reportDateArray.length; i++) {

				Date reportDate = headers.get("报告日期", true).getAsDate(i + 1, headers.getDateFormat());
				if (reportDate == null) {
					break;
				}
				// one row:
				List<Object> rowData = new ArrayList<>();
				rowData.add(corpId);
				rowData.add(reportDate);

				for (String key : itemKeyList) {
					BigDecimal value = body.get(key, true).getAsBigDecimal(i + 1, false);
					if (value != null) {
						value = value.multiply(unit);
					}

					rowData.add(value);
				}

				writeRow(xContext.getAndIncrementNextRow(sheetName), rowData, xSheet);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void writeRow(int row, List<Object> rowData, XSpreadsheet xSheet) {
		for (int i = 0; i < rowData.size(); i++) {
			Object obj = rowData.get(i);
			String text = (obj == null ? "" : obj.toString());
			DocUtil.setText(xSheet, i, row, text);
		}

	}

	protected XSpreadsheet prepareSpreadsheet(WashedFileLoadContext xContext) {

		try {
			return (XSpreadsheet) UnoRuntime.queryInterface(XSpreadsheet.class,
					xContext.xDoc.getSheets().getByName(this.sheetName));
		} catch (NoSuchElementException e) {
			throw new RuntimeException(e);
		} catch (WrappedTargetException e) {
			throw new RuntimeException(e);
		}

	}
}
