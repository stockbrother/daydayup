package ddu.test;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;

import junit.framework.TestCase;

public class DateTest extends TestCase {

	public void test() {
		Date date0 = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date0);
		c.add(Calendar.YEAR, -1);
		Date date1 = c.getTime();
		
		Assert.assertEquals(date0.getYear(), date1.getYear() + 1);
		Assert.assertEquals(date0.getMonth(), date1.getMonth() );
		Assert.assertEquals(date0.getDate(), date1.getDate() );
		Assert.assertEquals(date0.getHours(), date1.getHours() );
		Assert.assertEquals(date0.getMinutes(), date1.getMinutes() );
		Assert.assertEquals(date0.getSeconds(), date1.getSeconds() );
	}

}
