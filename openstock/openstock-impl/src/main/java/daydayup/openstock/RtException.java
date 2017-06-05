package daydayup.openstock;

public class RtException extends RuntimeException {

	public RtException() {
		super();
	}

	public RtException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RtException(String message, Throwable cause) {
		super(message, cause);
	}

	public RtException(String message) {
		super(message);
	}

	public RtException(Throwable cause) {
		super(cause);
	}

	public static RuntimeException toRtException(Exception e) {
		if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		} else {
			return new RtException(e);
		}
	}

}
