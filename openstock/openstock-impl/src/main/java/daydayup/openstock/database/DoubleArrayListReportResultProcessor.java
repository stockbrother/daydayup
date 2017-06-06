package daydayup.openstock.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoubleArrayListReportResultProcessor implements ReportResultProcessor<List<Double[]>> {

	@Override
	public List<Double[]> process(int reportType, List<String> aliasList, ResultSet rs) throws SQLException {
		List<Double[]> rt = new ArrayList<>();
		while (rs.next()) {
			Double[] row = new Double[aliasList.size()];
			for (int i = 0; i < row.length; i++) {
				row[i] = rs.getDouble(i + 1);
			}
			rt.add(row);
		}
		return rt;
	}

}
