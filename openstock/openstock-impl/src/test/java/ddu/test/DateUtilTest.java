package ddu.test;

import java.util.Date;

import daydayup.openstock.EnvUtil;
import junit.framework.TestCase;

public class DateUtilTest extends TestCase {

	public void test() {
		Date reportDate = new Date();
		reportDate = EnvUtil.floorHour(reportDate);
		System.out.println(reportDate);

	}
}
