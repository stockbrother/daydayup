package daydayup.openstock;

import com.sun.star.awt.Rectangle;
import com.sun.star.awt.WindowAttribute;
import com.sun.star.awt.WindowClass;
import com.sun.star.awt.WindowDescriptor;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.uno.UnoRuntime;

public class MessageBoxUtil {

	public static void showMessageBox(XToolkit xToolkit, XFrame xFrame, String title, String text) {
		WindowDescriptor wd = new WindowDescriptor();
		wd.Type = WindowClass.MODALTOP;
		wd.WindowServiceName = "infobox";
		wd.ParentIndex = -1;
		wd.Bounds = new Rectangle(0, 0, 300, 200);
		wd.Parent = UnoRuntime.queryInterface(XWindowPeer.class, xFrame.getContainerWindow());
		wd.WindowAttributes = WindowAttribute.BORDER | WindowAttribute.CLOSEABLE | WindowAttribute.MOVEABLE;
		try {
			XWindowPeer wPeer = xToolkit.createWindow(wd);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}
}
