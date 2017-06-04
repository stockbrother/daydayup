package daydayup.util;

import java.lang.reflect.Method;

public class BeanUtil {

	public static String getPropertyNameFromGetMethod(Method m) {
		String mname = m.getName();

		String rt = mname.substring("get".length());
		rt = rt.substring(0, 1).toLowerCase() + rt.substring(1);
		return rt;
	}
}
