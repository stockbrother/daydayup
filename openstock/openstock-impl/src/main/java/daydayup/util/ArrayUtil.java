/**
 * All right is from Author of the file,to be explained in comming days.
 * Nov 2, 2012
 */
package daydayup.util;

/**
 * @author wu
 * 
 */
public class ArrayUtil {

	public static String[] concat(String[] a1, String[] a2) {
		String[] rt = new String[a1.length + a2.length];
		System.arraycopy(a1, 0, rt, 0, a1.length);
		System.arraycopy(a2, 0, rt, a1.length, a2.length);

		return rt;
	}
}
