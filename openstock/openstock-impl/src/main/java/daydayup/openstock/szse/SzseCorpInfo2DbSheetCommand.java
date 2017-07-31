package daydayup.openstock.szse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.database.Tables;
import daydayup.openstock.sse.SseCorpInfo2DbSheetCommand;

public class SzseCorpInfo2DbSheetCommand extends BaseSheetCommand<Object> {

	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected Object doExecute(SheetCommandContext scc) {
		final File csvFile = new File("C:\\openstock\\szse\\szse.corplist.csv");

		scc.getDataBaseService().execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {

				loadCorpInfo2Db(csvFile, con, t);
				return null;
			}
		}, true);

		return "done";
	}

	

	public static void loadCorpInfo2Db(File csvFile, Connection con, JdbcAccessTemplate t) {
		String sql = "merge into " + Tables.TN_CORP_INFO
				+ "(corpId,corpName,fullName,category,ipoDate,province,city,webSite,address)key(corpId)values(?,?,?,?,?,?,?,?,?)";

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

				String x0 = SseCorpInfo2DbSheetCommand.getValueByColumn(next, colIndexMap, "A股代码");
				String x1 = SseCorpInfo2DbSheetCommand.getValueByColumn(next, colIndexMap, "公司简称");
				String x2 = SseCorpInfo2DbSheetCommand.getValueByColumn(next, colIndexMap, "公司全称");
				String x3 = SseCorpInfo2DbSheetCommand.getValueByColumn(next, colIndexMap, "所属行业");
				Date x4 = SseCorpInfo2DbSheetCommand.getDateValueByColumn(next, colIndexMap, "A股上市日期", DF);
				String x5 = SseCorpInfo2DbSheetCommand.getValueByColumn(next, colIndexMap, "省份");
				String x6 = SseCorpInfo2DbSheetCommand.getValueByColumn(next, colIndexMap, "城市");
				String x7 = SseCorpInfo2DbSheetCommand.getValueByColumn(next, colIndexMap, "公司网址");
				String x8 = SseCorpInfo2DbSheetCommand.getValueByColumn(next, colIndexMap, "注册地址");

				t.executeUpdate(con, sql, new Object[] { x0, x1, x2, x3, x4, x5, x6, x7, x8 });

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
