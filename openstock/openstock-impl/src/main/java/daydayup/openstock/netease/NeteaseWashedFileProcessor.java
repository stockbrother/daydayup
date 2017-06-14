package daydayup.openstock.netease;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;
import daydayup.openstock.wash.WashedFileProcessor;

public class NeteaseWashedFileProcessor implements WashedFileProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(NeteaseWashedFileProcessor.class);

	String fileType;

	public NeteaseWashedFileProcessor(String sheetName) {
		this.fileType = sheetName;
	}

	

	public void process(Reader fr, WashedFileLoadContext xContext) {

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
				List<String> keyList = new ArrayList<>();
				List<BigDecimal> valueList = new ArrayList<>();

				for (String key : itemKeyList) {
					BigDecimal value = body.get(key, true).getAsBigDecimal(i + 1, false);
					if (value != null) {
						value = value.multiply(unit);
					}
					keyList.add(key);
					valueList.add(value);
				}
				xContext.getOrCreateTypeContext(fileType).writeRow(corpId, reportDate, keyList, valueList);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}