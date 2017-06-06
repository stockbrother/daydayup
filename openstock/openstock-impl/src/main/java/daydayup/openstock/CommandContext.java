package daydayup.openstock;

import com.sun.star.uno.XComponentContext;

import daydayup.openstock.database.DataBaseService;

public class CommandContext {

	XComponentContext componentContext;

	public CommandContext(XComponentContext xcc) {
		this.componentContext = xcc;
	}

	public DataBaseService getDataBaseService() {
		return OpenStock.getInstance().getDataBaseService();
	}

	public XComponentContext getComponentContext() {
		return this.componentContext;
	}


}
