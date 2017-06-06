package daydayup.openstock.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ReportResultProcessor<T> {
	public T process(int reportType, List<String> aliasList, ResultSet rs) throws SQLException;
}
