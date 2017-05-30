package daydayup.openstock;

import com.sun.star.comp.loader.FactoryHelper;
import com.sun.star.connection.ConnectionSetupException;
import com.sun.star.connection.NoConnectException;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.lang.XSingleServiceFactory;
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

	// EntryPoint of the component.
	public static XSingleServiceFactory __getServiceFactory(String implName, XMultiServiceFactory multiFactory,
			com.sun.star.registry.XRegistryKey regKey) {

		com.sun.star.lang.XSingleServiceFactory xSingleServiceFactory = null;
		if (implName.equals(OpenStockImpl.class.getName())) {

			xSingleServiceFactory = FactoryHelper.getServiceFactory(OpenStockImpl.class, OpenStockImpl.__serviceName,
					multiFactory, regKey);
		}

		return xSingleServiceFactory;
	}

	// EntryPoint of Registry
	// Use tools such as regcomp to register a component. This tool takes the
	// path to the jar file containing the component as an argument.
	public static boolean __writeRegistryServiceInfo(XRegistryKey regKey) {

		return FactoryHelper.writeRegistryServiceInfo(OpenStockImpl.class.getName(), __serviceName, regKey);
	}

	// see:
	// https://wiki.openoffice.org/wiki/Documentation/DevGuide/WritingUNO/Create_Instance_with_Arguments

	public OpenStockImpl(XComponentContext xCompContext, XRegistryKey xRegKey, Object[] args) {

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
	public String hello(String arg0) throws NoConnectException, ConnectionSetupException, IllegalArgumentException {
		return "Hello " + arg0;
	}

}
