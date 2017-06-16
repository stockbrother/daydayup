package daydayup.openstock.sse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.RtException;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.database.Tables;

public class SseCorpInfo2DbSheetCommand extends BaseSheetCommand<Object> {

	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected Object doExecute(SheetCommandContext scc) {
		File csvFile = new File("C:\\openstock\\sse\\sse.corplist.csv");

		scc.getDataBaseService().execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				loadCorpInfo2Db(csvFile, con, t);
				return null;
			}
		}, true);

		return "done";
	}

	public void loadCorpInfo2Db(File csvFile, Connection con, JdbcAccessTemplate t) {
		String sql = "merge into " + Tables.TN_CORP_INFO
				+ "(corpId,fullName,category)key(corpId)values(?,?,?)";

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

			// skip header2
			next = reader.readNext();
			while (true) {
				next = reader.readNext();
				if (next == null) {
					break;
				}

				String x0 = getValueByColumn(next, colIndexMap, "A股代码");				
				String x1 = getValueByColumn(next, colIndexMap, "公司全称");
				String x2 = getValueByColumn(next, colIndexMap, "所属行业");

				t.executeUpdate(con, sql, new Object[] { x0, x1, x2, });

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Date getDateValueByColumn(String[] line, Map<String, Integer> colIndexMap, String col,
			DateFormat df) {
		String str = getValueByColumn(line, colIndexMap, col);
		if (str == null) {
			return null;
		}
		try {
			return df.parse(str);
		} catch (ParseException e) {
			throw RtException.toRtException(e);
		}
	}

	private static String getValueByColumn(String[] line, Map<String, Integer> colIndexMap, String col) {
		Integer idx = colIndexMap.get(col);
		if (idx == null) {
			return null;
		}
		return line[idx];
	}

}
