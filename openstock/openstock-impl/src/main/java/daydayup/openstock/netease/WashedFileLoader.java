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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.XComponentContext;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.util.DocUtil;

public class WashedFileLoader {
	public static abstract class DataTypeContext {
		private String type;

		public DataTypeContext(String type) {
			this.type = type;
		}

		public abstract void writeRow(String corpId, Date reportDate, List<String> keyList, List<BigDecimal> valueList);

	}

	public static class DbDataTypeContext extends DataTypeContext {
		public static Map<String, Integer> typeMap = new HashMap<>();
		static {
			typeMap.put("ZCFZB", 1);
			typeMap.put("LRB", 2);
			typeMap.put("XJLLB", 3);

		}

		DataBaseService dbs;
		int reportType;

		public DbDataTypeContext(String type, DataBaseService dbs) {
			super(type);
			this.reportType = typeMap.get(type);
			this.dbs = dbs;
		}

		@Override
		public void writeRow(String corpId, Date reportDate, List<String> keyList, List<BigDecimal> valueList) {

			dbs.mergeReport(reportType, corpId, reportDate, keyList, valueList);

		}

	}

	public static class DocDataTypeContext extends DataTypeContext {
		private AtomicInteger nextRow = new AtomicInteger(1);
		private XSpreadsheet xSheet;
		private List<String> header = new ArrayList<>();
		private Map<String, Integer> headerColumnMap = new HashMap<>();

		public DocDataTypeContext(String type, XSpreadsheet xSheet) {
			super(type);
			this.xSheet = xSheet;

		}

		@Override
		public void writeRow(String corpId, Date reportDate, List<String> keyList, List<BigDecimal> valueList) {
			int row = nextRow.getAndIncrement();

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

	public static abstract class WashedFileLoadContext {
		protected Map<String, DataTypeContext> nextRowMap = new HashMap<>();

		public DataTypeContext getOrCreateTypeContext(String type) {
			DataTypeContext rt = nextRowMap.get(type);
			if (rt == null) {
				rt = createTypeContext(type);
				nextRowMap.put(type, rt);
			}
			return rt;
		}

		public abstract DataTypeContext createTypeContext(String type);
	}

	public static class DocWashedFileLoadContext extends WashedFileLoadContext {

		public XSpreadsheetDocument xDoc;

		public DocWashedFileLoadContext(XComponentContext cc) {
			xDoc = DocUtil.getSpreadsheetDocument(cc);
		}

		@Override
		public DataTypeContext createTypeContext(String type) {
			XSpreadsheet xSheet = DocUtil.getSpreadsheetByName(xDoc, type, false);
			if (xSheet == null) {
				DocUtil.createSheet(xDoc, type);
			}
			return new DocDataTypeContext(type, xSheet);
		}

	}

	public static class DbWashedFileLoadContext extends WashedFileLoadContext {

		public DataBaseService dbs;

		public DbWashedFileLoadContext(DataBaseService dbs) {
			this.dbs = dbs;
		}

		@Override
		public DataTypeContext createTypeContext(String type) {

			return new DbDataTypeContext(type, this.dbs);
		}

	}

	public static class WashedFileProcessor {

		private static final Logger LOG = LoggerFactory.getLogger(WashedFileProcessor.class);

		String sheetName;

		public WashedFileProcessor(String sheetName) {
			this.sheetName = sheetName;
		}

		public void process(File file, WashedFileLoadContext xContext) {
			LOG.info("processor:" + this.getClass().getName() + " going to process file:" + file.getAbsolutePath());

			InputStream is;
			try {
				is = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			Charset cs = Charset.forName("UTF-8");
			Reader reader = new InputStreamReader(is, cs);
			this.process(reader, xContext);//
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
					xContext.getOrCreateTypeContext(sheetName).writeRow(corpId, reportDate, keyList, valueList);
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

	}

	private static final Logger LOG = LoggerFactory.getLogger(WashedFileLoader.class);

	private Map<String, WashedFileProcessor> processMap = new HashMap<>();

	private int maxSize = -1;

	private int processed;

	private boolean interrupted;

	public WashedFileLoader(XSpreadsheetDocument xDoc) {

		processMap.put("zcfzb", new WashedFileProcessor("ZCFZB"));
		processMap.put("lrb", new WashedFileProcessor("LRB"));
		processMap.put("xjllb", new WashedFileProcessor("XJLLB"));

	}

	public void load(File dir, WashedFileLoadContext xContext) {

		if (dir.isFile()) {
			File f = dir;
			String fname = f.getName();
			String[] fnames = fname.split("\\.");

			if (fnames[fnames.length - 1].equals("csv")) {
				String ftype = fnames[fnames.length - 2];
				WashedFileProcessor fp = processMap.get(ftype);
				if (fp == null) {
					LOG.warn("no processor found for file:" + f.getAbsolutePath() + ",type:" + ftype);
				} else {

					if (LOG.isTraceEnabled()) {
						LOG.trace("process file:" + f.getAbsolutePath());
					}
					fp.process(f, xContext);//
					if (this.maxSize >= 0 && this.processed++ > this.maxSize) {
						this.interrupted = true;
					}
					

				}
			}
			return;
		}
		// is directory
		if (this.interrupted) {
			LOG.warn("interrupted.");
			return;
		}
		for (File f : dir.listFiles()) {
			// is directory
			if (this.interrupted) {
				LOG.warn("interrupted.");
				return;
			}
			this.load(f, xContext);
		}

	}

}
