package daydayup.util;

public class ExceptionUtil {

	public static RuntimeException toRuntimeException(Throwable e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		} else {
			return new RuntimeException(e);
		}
	}
}
