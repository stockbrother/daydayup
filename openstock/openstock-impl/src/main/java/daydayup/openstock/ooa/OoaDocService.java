package daydayup.openstock.ooa;

import com.sun.star.frame.XDesktop;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XInterface;

import daydayup.openstock.RtException;
import daydayup.openstock.document.DocumentService;
import daydayup.openstock.document.SpreadsheetDocument;

public class OoaDocService implements DocumentService {

	XComponentContext xcc;
	SpreadsheetDocument doc;

	public OoaDocService(XComponentContext xcc) {
		this.xcc = xcc;
	}

	@Override
	public SpreadsheetDocument getDocument() {
		if (doc != null) {
			return doc;
		}

		Object desktop = null;
		try {
			desktop = xcc.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop", xcc);
		} catch (Exception e) {
			throw new RtException(e);
		}
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
		XComponent xComp = xDesktop.getCurrentComponent();

		XInterface xDoc = UnoRuntime.queryInterface(XInterface.class, xComp);
		XSpreadsheetDocument xDoc2 = UnoRuntime.queryInterface(XSpreadsheetDocument.class, xDoc);

		return new OoaSpreadsheetDocument(xDoc2, this);
	}

}
