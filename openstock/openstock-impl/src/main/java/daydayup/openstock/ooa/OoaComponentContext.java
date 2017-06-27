package daydayup.openstock.ooa;

import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.task.XStatusIndicator;
import com.sun.star.task.XStatusIndicatorFactory;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.RtException;
import daydayup.openstock.document.ComponentContext;
import daydayup.openstock.document.DocumentService;
import daydayup.openstock.document.StatusIndicator;

public class OoaComponentContext implements ComponentContext {
	private XComponentContext xContext;

	DocumentService documentService;

	public OoaComponentContext(XComponentContext xContext) {
		this.xContext = xContext;
		this.documentService  = new OoaDocService(xContext);
	}

	@Override
	public StatusIndicator createStatusIndicator() {
		return new OoaStatusIndicator(this.doCreateStatusIndicator());
	}

	public XStatusIndicator doCreateStatusIndicator() {
		Object desktop = null;
		try {
			desktop = this.xContext.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop",
					xContext);
		} catch (Exception e) {
			throw new RtException(e);
		}
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
		XFrame frame = xDesktop.getCurrentFrame();
		XStatusIndicatorFactory sif = UnoRuntime.queryInterface(XStatusIndicatorFactory.class, frame);
		XStatusIndicator si = sif.createStatusIndicator();
		return si;
	}

	@Override
	public DocumentService getDocumentService() {

			return this.documentService;
		}

}
