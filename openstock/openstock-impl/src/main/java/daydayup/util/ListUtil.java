/**
 * All right is from Author of the file,to be explained in comming days.
 * Dec 6, 2012
 */
package daydayup.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wu
 * 
 */
public class ListUtil {

	public static <T> List<T> minus(List<T> l1,List<T> l2){
		
		List<T> rt = new ArrayList<T>();
		
		for (T t : l1) {
			if (l2.contains(t)) {
				continue;
			}
			rt.add(t);
		}
		
		return rt;	
	}
	
	public static <T> List<T> merge(List<T> l1, List<T> l2) {
		List<T> rt = new ArrayList<T>(l1);
		for (T t : l2) {
			if (rt.contains(t)) {
				continue;
			}
			rt.add(t);
		}
		return rt;
	}

	public static String listToCsv(List<String> idL) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < idL.size(); i++) {
			String s = idL.get(i);
			sb.append(s);

			if (i != idL.size() - 1) {
				sb.append(",");
			}

		}
		return sb.toString();
	}

}
