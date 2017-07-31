package daydayup.openstock.cninfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.database.Tables;

public class CninfoCorpInfo2DbSheetCommand extends BaseSheetCommand<Object> {

	private static final Logger LOG = LoggerFactory.getLogger(CninfoCorpInfo2DbSheetCommand.class);

	public static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd");

	@Override
	protected Object doExecute(SheetCommandContext scc) {
		final File csvFile = new File("C:\\D\\data\\cninfo\\20170602111822.csv");

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
		String sql = "insert into " + Tables.TN_CORP_INFO + "(corpId,corpName)values(?,?)";

		try {
			Charset cs = Charset.forName("GBK");
			Reader fr = new InputStreamReader(new FileInputStream(csvFile), cs);
			CSVReader reader = new CSVReader(fr);

			// skip header1
			String[] next = reader.readNext();
			// skip header2
			next = reader.readNext();
			while (true) {
				next = reader.readNext();
				if (next == null) {
					break;
				}
				String code = next[0].trim();
				String name = next[1].trim();
				t.executeUpdate(con, sql, new Object[] { code, name });

			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
