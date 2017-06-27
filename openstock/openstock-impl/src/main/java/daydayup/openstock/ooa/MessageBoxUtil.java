package daydayup.openstock.ooa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.awt.MessageBoxType;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.WindowAttribute;
import com.sun.star.awt.WindowClass;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.XComponent;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class MessageBoxUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageBoxUtil.class);
	
	public static void showMessageBox(XComponentContext context, XFrame xFrame, String title, String text) {
		WindowDescriptor wd = new WindowDescriptor();
		wd.Type = WindowClass.MODALTOP;
		wd.WindowServiceName = "infobox";
		wd.ParentIndex = -1;
		wd.Bounds = new Rectangle(0, 0, 300, 200);
		wd.Parent = UnoRuntime.queryInterface(XWindowPeer.class, xFrame.getContainerWindow());
		wd.WindowAttributes = WindowAttribute.BORDER | WindowAttribute.CLOSEABLE | WindowAttribute.MOVEABLE;
		try {
			Object toolkit = context.getServiceManager().createInstanceWithContext("com.sun.star.awt.Toolkit", context);

			XToolkit xToolkit = UnoRuntime.queryInterface(XToolkit.class, toolkit);
			XWindowPeer wPeer = xToolkit.createWindow(wd);

			XMessageBoxFactory xMessageBoxFactory = (XMessageBoxFactory) UnoRuntime
					.queryInterface(XMessageBoxFactory.class, xToolkit);
			XMessageBox xMessageBox = xMessageBoxFactory.createMessageBox(wPeer, MessageBoxType.INFOBOX,
					com.sun.star.awt.MessageBoxButtons.BUTTONS_OK, title, text);
			XComponent xComponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, xMessageBox);
			try {
				short nResult = xMessageBox.execute();
			} finally {
				xComponent.dispose();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
