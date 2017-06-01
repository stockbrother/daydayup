package daydayup.openstock;

import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.uno.XComponentContext;

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
public class OpenStockFunctionServiceImpl extends com.sun.star.lib.uno.helper.WeakBase implements XFunctions, XServiceInfo {
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
		return (int) 1;
	}

	@Override
	public int osSecond(XPropertySet arg0, int arg1) {
		return 2 + arg1;
	}

}
