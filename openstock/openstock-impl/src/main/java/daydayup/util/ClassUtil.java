/**
 * Jun 15, 2012
 */
package daydayup.util;

/**
 * @author wuzhen
 * 
 */
public class ClassUtil {
	public static <T> T newInstance(Class<T> cls) {
		return newInstance(cls, new Class<?>[] {}, new Object[] {});
	}

	public static Object newInstance(String cName) {
		return newInstance(cName, new Class<?>[] {}, new Object[] {});
	}

	public static <T> T newInstance(String cName, Class<?>[] paramTypes,
			Object[] paramValues) {

		Class<T> c = forName(cName);
		return newInstance(c, paramTypes, paramValues);
	}

	public static <T> T newInstance(Class<T> cls, Class<?>[] paramTypes,
			Object[] paramValues) {
		T rt;
		try {
			rt = cls.getConstructor(paramTypes).newInstance(paramValues);

		} catch (Exception e) {
			throw new RuntimeException("[cannot instance:class:" + cls
					+ ",may not public?]", e);
		}
		return rt;
	}

	public static <T> Class<T> forName(String cName) {

		try {
			return (Class<T>) Class.forName(cName);
		} catch (ClassNotFoundException e) {

			throw ExceptionUtil.toRuntimeException(e);

		}

	}
}
