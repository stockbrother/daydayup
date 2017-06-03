package daydayup.openstock.executor;

public class TaskConflictException extends Exception {

	public TaskConflictException() {
		super();
	}

	public TaskConflictException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TaskConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	public TaskConflictException(String message) {
		super(message);
	}

	public TaskConflictException(Throwable cause) {
		super(cause);
	}

}
