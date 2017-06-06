package ddu.test.database;

import java.io.File;

import daydayup.openstock.database.DataBaseService;
import junit.framework.TestCase;

public class DataBaseTest extends TestCase {

	public void test() {
		File dbHome = new File("target" + File.separator + "db");
		String dbName = "test";
		DataBaseService dbs = DataBaseService.getInstance(dbHome, dbName);
		
	}

}
