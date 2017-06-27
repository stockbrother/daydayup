package daydayup.openstock;

import daydayup.openstock.document.StatusIndicator;

public abstract class CommandBase<T> {
	public T execute(CommandContext cc) {
		StatusIndicator si = cc.getComponentContext().createStatusIndicator();
		si.start("Running:" + this.toString(), 100);
		try {
			cc.setStatusIndicator(si);
			return this.doExecute(cc);
		} finally {
			si.end();
		}

	}

	protected abstract T doExecute(CommandContext cc);

}
