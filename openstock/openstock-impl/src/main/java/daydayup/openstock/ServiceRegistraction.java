package daydayup.openstock;

import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.registry.XRegistryKey;

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
public class ServiceRegistraction {

	public static final String[] SERVICE_NAMES = new String[] { OpenStockFunctionServiceImpl.SERVICE_NAME,
			ProtocolHandlerImpl.SERVICE_NAME };
	public static final Class[] SERVICE_CLASSES = new Class[] { OpenStockFunctionServiceImpl.class, ProtocolHandlerImpl.class };

	// EntryPoint of the component.
	/**
	 * public static XSingleServiceFactory __getServiceFactory(String implName,
	 * XMultiServiceFactory multiFactory, com.sun.star.registry.XRegistryKey
	 * regKey) {
	 * 
	 * com.sun.star.lang.XSingleServiceFactory xSingleServiceFactory = null; if
	 * (implName.equals(OpenStockImpl.class.getName())) {
	 * 
	 * xSingleServiceFactory =
	 * FactoryHelper.getServiceFactory(OpenStockImpl.class,
	 * OpenStockImpl.__serviceName, multiFactory, regKey); }
	 * 
	 * return xSingleServiceFactory; }
	 */
	// ??
	public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
		XSingleComponentFactory xFactory = null;
		for (int i = 0; i < SERVICE_CLASSES.length; i++) {
			if (sImplementationName.equals(SERVICE_CLASSES[i].getName())) {
				xFactory = Factory.createComponentFactory(SERVICE_CLASSES[i], new String[] { SERVICE_NAMES[i] });
				break;
			}
		}
		return xFactory;
	}

	// EntryPoint of Registry
	// Use tools such as regcomp to register a component. This tool takes the
	// path to the jar file containing the component as an argument.
	public static boolean __writeRegistryServiceInfo(XRegistryKey regKey) {
		boolean rt = true;
		for (int i = 0; i < SERVICE_CLASSES.length; i++) {
			boolean rtI = Factory.writeRegistryServiceInfo(SERVICE_CLASSES[i].getName(),
					new String[] { SERVICE_NAMES[i] }, regKey);
			rt = rt && rtI;
		}
		return rt;
	}

}
