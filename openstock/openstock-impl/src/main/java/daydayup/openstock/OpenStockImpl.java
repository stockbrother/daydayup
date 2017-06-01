package daydayup.openstock;

import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.loader.FactoryHelper;
import com.sun.star.connection.ConnectionSetupException;
import com.sun.star.connection.NoConnectException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lang.XSingleServiceFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.registry.XRegistryKey;
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
public class OpenStockImpl extends com.sun.star.lib.uno.helper.WeakBase
		implements com.sun.star.lang.XServiceInfo, XOpenStock {
	public static final String __serviceName = "daydayup.openstock.OpenStock";

	private final XComponentContext xContext;

	// EntryPoint of the component.
	/**
	public static XSingleServiceFactory __getServiceFactory(String implName, XMultiServiceFactory multiFactory,
			com.sun.star.registry.XRegistryKey regKey) {

		com.sun.star.lang.XSingleServiceFactory xSingleServiceFactory = null;
		if (implName.equals(OpenStockImpl.class.getName())) {

			xSingleServiceFactory = FactoryHelper.getServiceFactory(OpenStockImpl.class, OpenStockImpl.__serviceName,
					multiFactory, regKey);
		}

		return xSingleServiceFactory;
	}
*/
	// ??
	public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
		XSingleComponentFactory xFactory = null;

		if (sImplementationName.equals(OpenStockImpl.class.getName())) {
			xFactory = Factory.createComponentFactory(OpenStockImpl.class, new String[] { __serviceName });
		}
		return xFactory;
	}

	// EntryPoint of Registry
	// Use tools such as regcomp to register a component. This tool takes the
	// path to the jar file containing the component as an argument.
	public static boolean __writeRegistryServiceInfo(XRegistryKey regKey) {

		return Factory.writeRegistryServiceInfo(OpenStockImpl.class.getName(), new String[] { __serviceName }, regKey);
	}

	// see:
	// https://wiki.openoffice.org/wiki/Documentation/DevGuide/WritingUNO/Create_Instance_with_Arguments

	public OpenStockImpl(XComponentContext xCompContext) {
		this.xContext = xCompContext;
	}

	@Override
	public String getImplementationName() {

		return getClass().getName();
	}

	@Override
	public String[] getSupportedServiceNames() {

		return new String[] { __serviceName };
	}

	@Override
	public boolean supportsService(String serviceName) {
		return serviceName.equals(__serviceName);
	}

	@Override
	public int getMyFirstValue(XPropertySet arg0) {
		return (int) 1;
	}

	@Override
	public int getMySecondValue(XPropertySet arg0, int arg1) {
		// TODO Auto-generated method stub
		return 2 + arg1;
	}

}
