package daydayup.csvloader;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.Handler;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.DdrContext;
import daydayup.openstock.database.Tables;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class SseCorpInfoExtendedLoadedHandler extends Handler<String,Object> {

	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Object execute(String file) {
		final File csvFile = new File("C:\\openstock\\sse\\sse.corplist2.csv");

		DdrContext.get().getDataBaseService().execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				loadCorpInfo2Db(csvFile, con, t);
				return null;
			}
		}, true);

		return "done";
	}

	public void loadCorpInfo2Db(File csvFile, Connection con, JdbcAccessTemplate t) {
		String sql = "merge into " + Tables.TN_CORP_INFO + "(corpId,fullName,category)key(corpId)values(?,?,?)";

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

				String x0 = SseCorpInfoLoadHandler.getValueByColumn(next, colIndexMap, "A股代码");
				if ("-".equals(x0)) {
					continue;
				}
				String x1 = SseCorpInfoLoadHandler.getValueByColumn(next, colIndexMap, "公司全称");
				String x2 = SseCorpInfoLoadHandler.getValueByColumn(next, colIndexMap, "所属行业");

				t.executeUpdate(con, sql, new Object[] { x0, x1, x2, });

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
