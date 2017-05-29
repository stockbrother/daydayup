package daydayup.macro;

import com.sun.star.awt.MessageBoxButtons;
import com.sun.star.awt.MessageBoxType;
import com.sun.star.awt.XMessageBox;
import com.sun.star.awt.XMessageBoxFactory;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.XModel;
import com.sun.star.script.provider.XScriptContext;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.UnoRuntime;

public class OpenStock {
	public static String helloWorld(XScriptContext xScriptContext) {
		XModel xDocModel = xScriptContext.getDocument();

		// getting the text document object
		XSpreadsheetDocument xDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(XSpreadsheetDocument.class,
				xDocModel);
		System.out.println("Hello,,,Print");
		
		 XWindow parentWindow = xDocModel.getCurrentController().getFrame().getContainerWindow();
		 
		 XWindowPeer parentWindowPeer = (XWindowPeer) UnoRuntime.queryInterface(XWindowPeer.class, parentWindow);

		XMessageBoxFactory xMessageBoxFactory = (XMessageBoxFactory) UnoRuntime.queryInterface(XMessageBoxFactory.class,parentWindowPeer.getToolkit());
		XMessageBox mbox = xMessageBoxFactory.createMessageBox(parentWindowPeer, MessageBoxType.MESSAGEBOX, MessageBoxButtons.BUTTONS_OK, "Message!", "Hello world! in message box.");
		mbox.execute();
		return "Hello World!(in java)";
	}
}
