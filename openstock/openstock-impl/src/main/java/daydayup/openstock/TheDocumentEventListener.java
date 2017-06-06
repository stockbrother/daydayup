package daydayup.openstock;

import com.sun.star.document.DocumentEvent;
import com.sun.star.document.XDocumentEventListener;
import com.sun.star.lang.EventObject;
import com.sun.star.sheet.XSpreadsheetDocument;

public class TheDocumentEventListener implements XDocumentEventListener {
	ProtocolHandlerImpl ph;

	public TheDocumentEventListener(ProtocolHandlerImpl ph) {
		this.ph = ph;
	}

	@Override
	public void disposing(EventObject arg0) {

	}

	@Override
	public void documentEventOccured(DocumentEvent arg0) {
		if ("OnLoad".equals(arg0.EventName)) {
			if (arg0.Source instanceof XSpreadsheetDocument) {
				XSpreadsheetDocument xDoc = (XSpreadsheetDocument) arg0.Source;
				//ph.execute(new CorpsApply2MemoryCommand(), ph.xContext);
			}

		}
	}

}
