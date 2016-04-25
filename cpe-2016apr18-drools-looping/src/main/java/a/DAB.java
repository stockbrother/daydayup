package a;

import org.drools.runtime.rule.FactHandle;

public class DAB {

	public static void main(String[] args) {

		MySessionWrapper sw = new MySessionWrapper();
		sw.start();

		MyObject mo = new MyObject();
		FactHandle fh = sw.insert(mo);
		
		mo.setJustInsterted(false);
		
		sw.update(mo);//

	}
}
