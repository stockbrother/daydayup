package daydayup.openstock;

import com.sun.star.task.XStatusIndicator;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.database.DataBaseService;

public class CommandContext {

	XComponentContext componentContext;
	XStatusIndicator statusIndicator;

	public CommandContext(XComponentContext xcc, XStatusIndicator statusIndicator) {
		this.componentContext = xcc;
		this.statusIndicator = statusIndicator;
	}

	public DataBaseService getDataBaseService() {
		return OpenStock.getInstance().getDataBaseService();
	}

	public XComponentContext getComponentContext() {
		return this.componentContext;
	}

	public XStatusIndicator getStatusIndicator() {
		return statusIndicator;
	}

}
