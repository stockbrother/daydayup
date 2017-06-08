package daydayup.openstock;

import com.sun.star.task.XStatusIndicator;

public abstract class CommandBase<T> {
	public T execute(CommandContext cc) {
		XStatusIndicator si = cc.getStatusIndicator();
		si.start("Running:" + this.toString(), 100);
		try {
			return this.doExecute(cc);
		} finally {
			si.end();
		}

	}

	protected abstract T doExecute(CommandContext cc);

}
