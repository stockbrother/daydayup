package ddu.test;

import java.util.Date;

import daydayup.openstock.ooa.DocUtil;

public class Tmp {
	public static void main(String[] args) throws Exception {
		test();
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

	public static void test() throws Exception {
		System.out.println(DocUtil.DF.format(new Date()));
		//String s = "\u8425\u4e1a\u603b\u6536\u5165";
		//System.out.print(s);
		//com.sun.star.comp.servicemanager.ServiceManager sm = new com.sun.star.comp.servicemanager.ServiceManager();
		//sm.insert(OpenStockImpl.__getServiceFactory(OpenStockImpl.class.getName(), sm, null));
		//Object serObj = sm.createInstance(OpenStockImpl.__serviceName);
		//System.out.println(serObj);//
	}
	
}
