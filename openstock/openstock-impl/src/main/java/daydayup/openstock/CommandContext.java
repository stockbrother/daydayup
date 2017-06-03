package daydayup.openstock;

import com.sun.star.frame.XFrame;
import com.sun.star.uno.XComponentContext;

public class CommandContext {

	XComponentContext componentContext;
	XFrame frame;

	public CommandContext(XFrame frame, XComponentContext xcc) {
		this.frame = frame;
		this.componentContext = xcc;
	}

	public XComponentContext getComponentContext() {
		return this.componentContext;
	}

	public XFrame getFrame() {
		return frame;
	}

}
