package daydayup.openstock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNamed;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.sheet.XSheetCellCursor;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uno.XInterface;

import daydayup.openstock.util.XObjectUtil;

/**
 * see <br>
 * <code>https://wiki.openoffice.org/wiki/Documentation/DevGuide/WritingUNO/
 * Providing_a_Single_Factory_Using_a_Helper_Method</codef>
 * 
 * RegistrationClassName: daydayup.openstock.OpenStockImpl
 * 
 * @author wu
 *
 */
public class OpenStockFunctionServiceImpl extends com.sun.star.lib.uno.helper.WeakBase
		implements XFunctions, XServiceInfo {

	private static final Logger LOG = LoggerFactory.getLogger(OpenStockFunctionServiceImpl.class);

	private final XComponentContext xContext;

	public static String SERVICE_NAME = "daydayup.openstock.FunctionService";

	// see:
	// https://wiki.openoffice.org/wiki/Documentation/DevGuide/WritingUNO/Create_Instance_with_Arguments

	public OpenStockFunctionServiceImpl(XComponentContext xCompContext) {
		this.xContext = xCompContext;
	}

	@Override
	public String getImplementationName() {

		return getClass().getName();
	}

	@Override
	public String[] getSupportedServiceNames() {

		return new String[] { SERVICE_NAME };
	}

	@Override
	public boolean supportsService(String serviceName) {
		return serviceName.equals(this.SERVICE_NAME);
	}

	@Override
	public int osFirst(XPropertySet arg0) {
		try {
			Object desktop = this.xContext.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop",
					this.xContext);
			XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
			XComponent xComp = xDesktop.getCurrentComponent();
			
			XInterface xDoc = UnoRuntime.queryInterface(XInterface.class, xComp);
			XSpreadsheetDocument xDoc2 = UnoRuntime.queryInterface(XSpreadsheetDocument.class, xDoc);
			
			XModel xModel = UnoRuntime.queryInterface(XModel.class, xComp);
			XSpreadsheetView xView = (XSpreadsheetView) UnoRuntime.queryInterface(XSpreadsheetView.class,
					xModel.getCurrentController());
			
			XSpreadsheet xSheet = xView.getActiveSheet();
			XNamed xName = UnoRuntime.queryInterface(XNamed.class, xSheet);
			XSheetCellCursor xCursor = UnoRuntime.queryInterface(XSheetCellCursor.class, xSheet);
			
			if (LOG.isTraceEnabled()) {
				LOG.trace("" + xSheet + ",xName:" + xName.getName());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (LOG.isTraceEnabled()) {
			StringBuffer sb = new StringBuffer();
			XObjectUtil.format(arg0, sb);
			LOG.trace(sb.toString());
		}
		return (int) 1;
	}

	@Override
	public int osSecond(XPropertySet arg0, int arg1) {
		return 2 + arg1;
	}

}
