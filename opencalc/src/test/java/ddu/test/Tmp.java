package ddu.test;

public class Tmp {
	public static void main(String[] args) throws Exception {
		// String command = "C:/Program Files (x86)/OpenOffice 4/sdk/bin/idlc";

		String[] cmdarray = new String[] {// 
				//"C:\\Program Files (x86)\\OpenOffice 4\\sdk\\bin\\idlc", //				
				"C:/Program Files (x86)/OpenOffice 4/sdk/bin/idlc", //
				"-C",//
				//"-OD:\\git\\daydayup\\opencalc\\target", //
				"-Otarget", //
				"-IC:/Program Files (x86)/OpenOffice 4/sdk/idl", //
				"src/openoffice/open-stock.idl",//
		};
		Process p = Runtime.getRuntime().exec(cmdarray);
		int rt = p.waitFor();
		System.out.println(rt);
		
	}
}
