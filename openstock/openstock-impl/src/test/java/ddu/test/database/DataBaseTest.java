package ddu.test.database;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;

import daydayup.openstock.EnvUtil;
import daydayup.openstock.database.DataBaseService;
import junit.framework.TestCase;

public class DataBaseTest extends TestCase {

	public void test() {
		File dbHome = new File("target" + File.separator + "db");
		String dbName = "test";
		DataBaseService dbs = DataBaseService.getInstance(dbHome, dbName);
		int reportType = 99;
		String corpId = "corp1";
		Date reportDate = EnvUtil.newDateOfYearLastDay(2016);

		List<String> aliasList = new ArrayList<>();
		List<BigDecimal> valueList = new ArrayList<>();
		aliasList.add("key1");
		valueList.add(new BigDecimal("123456.789"));
		aliasList.add("key2");
		valueList.add(new BigDecimal("456789.012"));

		dbs.addOrUpdateReport(reportType, corpId, reportDate, aliasList, valueList);

		List<Double> dL = dbs.getReport(reportType, corpId, reportDate, aliasList);
		Assert.assertEquals(aliasList.size(), dL.size());
		for (int i = 0; i < valueList.size(); i++) {
			BigDecimal valueI = valueList.get(i);

			double d1 = valueI.doubleValue();
			double d2 = dL.get(i);
			Assert.assertEquals(d1, d2, 0.001D);

		}
	}

}
