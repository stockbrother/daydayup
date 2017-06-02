package ddu.test.netease;

import java.io.File;

import daydayup.openstock.netease.NeteaseCollector;
import daydayup.openstock.netease.NeteasePreprocessor;

public class NeteaseTest {

	public static void main(String[] args) {
		File root1 = new File(
				"target" + File.separator + "163" + File.separator + "raw" + File.separator + "2016_year");
		File root2 = new File(
				"target" + File.separator + "163" + File.separator + "washed" + File.separator + "2016_year");
		NeteaseCollector c = new NeteaseCollector(root1).types(new String[] { NeteaseCollector.TYPE_zcfzb })
				.corpCodes("000001");
		c.execute();
		NeteasePreprocessor p = new NeteasePreprocessor(root1,root2);
		p.execute();
		
	}
}
