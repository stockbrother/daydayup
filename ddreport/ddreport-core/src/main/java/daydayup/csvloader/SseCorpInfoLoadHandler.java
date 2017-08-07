package daydayup.csvloader;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.Handler;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.RtException;
import daydayup.openstock.database.Tables;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SseCorpInfoLoadHandler extends Handler<String, Object> {

	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Object execute(String file) {
		final File csvFile = new File(file);

		this.getDdrContext().getDataBaseService().execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				loadCorpInfo2Db(csvFile, con, t);
				return null;
			}
		}, true);

		return "done";
	}

	public void loadCorpInfo2Db(File csvFile, Connection con, JdbcAccessTemplate t) {
		String sql = "merge into " + Tables.TN_CORP_INFO + "(corpId,corpName,ipoDate)key(corpId)values(?,?,?)";

		try {
			Charset cs = Charset.forName("UTF-8");
			Reader fr = new InputStreamReader(new FileInputStream(csvFile), cs);
			CSVReader reader = new CSVReader(fr);

			// skip header1
			String[] next = reader.readNext();
			Map<String, Integer> colIndexMap = new HashMap<>();
			for (int i = 0; i < next.length; i++) {
				String key = next[i];
				colIndexMap.put(key, i);
			}

			while (true) {
				next = reader.readNext();
				if (next == null) {
					break;
				}

				String x0 = getValueByColumn(next, colIndexMap, "A股代码");
				String x1 = getValueByColumn(next, colIndexMap, "A股简称");
				Date x2 = getDateValueByColumn(next, colIndexMap, "A股上市日期", DF, "-");

				t.executeUpdate(con, sql, new Object[] { x0, x1, x2, });

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Date getDateValueByColumn(String[] line, Map<String, Integer> colIndexMap, String col,
			DateFormat df) {
		return getDateValueByColumn(line, colIndexMap, col, DF, "");
	}

	public static Date getDateValueByColumn(String[] line, Map<String, Integer> colIndexMap, String col, DateFormat df,
			String asNull) {
		String str = getValueByColumn(line, colIndexMap, col, asNull);
		if (str == null || str.length() == 0) {
			return null;
		}
		try {
			return df.parse(str);
		} catch (ParseException e) {
			throw RtException.toRtException(e);
		}
	}

	public static String getValueByColumn(String[] line, Map<String, Integer> colIndexMap, String col) {
		return getValueByColumn(line, colIndexMap, col, "");
	}

	public static String getValueByColumn(String[] line, Map<String, Integer> colIndexMap, String col, String asNull) {
		Integer idx = colIndexMap.get(col);
		if (idx == null) {
			throw new RtException("no column found:" + col + ",all are:" + colIndexMap);
		}

		String rt = line[idx];
		if (rt == null) {
			return null;
		}

		rt = rt.trim();
		if (rt.equals(asNull)) {
			rt = null;
		}

		return rt;
	}

}
