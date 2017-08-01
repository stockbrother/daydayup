package ddu.test;

import java.io.File;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import daydayup.jdbc.JdbcAccessTemplate;
import daydayup.jdbc.JdbcAccessTemplate.JdbcOperation;
import daydayup.openstock.EnvUtil;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.database.Tables;
import daydayup.openstock.ooa.DocUtil;
import daydayup.openstock.wash.WashedFileLoader;
import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;

public class Tmp {
	public static void main(String[] args) throws Exception {

		DataBaseService dbs = DataBaseService.getInstance(EnvUtil.getDataDir(), EnvUtil.getDbName());
		dbs.execute(new JdbcOperation<Object>() {

			@Override
			public Object execute(Connection con, JdbcAccessTemplate t) {
				String sql = "select * from " + Tables.TN_ALIAS_INFO;
				List<Object[]> rst = t.executeQuery(con, sql);
				for (Object[] row : rst) {
					System.out.println(Arrays.asList(row));
				}
				return rst;
			}
		}, false);

		WashedFileLoadContext flc = new WashedFileLoadContext(dbs);
		String folderS = "xueqiu\\washed";
		File folder = new File(EnvUtil.getDataDir().getAbsolutePath() + File.separator + folderS);
		new WashedFileLoader().load(folder, flc);
	}

	public static void mainx(String[] args) throws Exception {
		// String command = "C:/Program Files (x86)/OpenOffice 4/sdk/bin/idlc";

		String[] cmdarray = new String[] { //
				// "C:\\Program Files (x86)\\OpenOffice 4\\sdk\\bin\\idlc", //
				"C:/Program Files (x86)/OpenOffice 4/sdk/bin/idlc", //
				"-C", //
				// "-OD:\\git\\daydayup\\opencalc\\target", //
				"-Otarget", //
				"-IC:/Program Files (x86)/OpenOffice 4/sdk/idl", //
				"src/openoffice/open-stock.idl",//
		};
		Process p = Runtime.getRuntime().exec(cmdarray);
		int rt = p.waitFor();
		System.out.println(rt);

	}

	public static void doTest() throws Exception {
		System.out.println(DocUtil.DF.format(new Date()));
		// String s = "\u8425\u4e1a\u603b\u6536\u5165";
		// System.out.print(s);
		// com.sun.star.comp.servicemanager.ServiceManager sm = new
		// com.sun.star.comp.servicemanager.ServiceManager();
		// sm.insert(OpenStockImpl.__getServiceFactory(OpenStockImpl.class.getName(),
		// sm, null));
		// Object serObj = sm.createInstance(OpenStockImpl.__serviceName);
		// System.out.println(serObj);//
	}

}
