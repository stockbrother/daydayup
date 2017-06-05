package daydayup.openstock;

import com.sun.star.frame.XFrame;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.database.DataBaseService;

public class CommandContext {

	XComponentContext componentContext;
	XFrame frame;
	DataBaseService dbs;

	public CommandContext(XFrame frame, XComponentContext xcc, DataBaseService dbs) {
		this.frame = frame;
		this.componentContext = xcc;
		this.dbs = dbs;
	}

	public DataBaseService getDataBaseService() {
		return dbs;
	}

	public XComponentContext getComponentContext() {
		return this.componentContext;
	}

	public XFrame getFrame() {
		return frame;
	}

}
