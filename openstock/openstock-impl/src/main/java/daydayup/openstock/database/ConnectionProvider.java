package daydayup.openstock.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionProvider {
	public Connection openConnection() throws SQLException;

	public void dispose();
}
