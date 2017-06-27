package daydayup.openstock;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	/* THIS MUST BE FIRST!!! */
	static {//file:/C:/Users/wu/AppData/Roaming/OpenOffice/4/user/uno_packages/cache/uno_packages/sv4dhhr.tmp_/openstock.oxt.zip/openstock-impl-0.0.1-SNAPSHOT.jar!/log4j2.xml
		final String configFile = ServiceRegistraction.class.getResource("/log4j2.xml").getFile();
		final LoggerContext ctx = Configurator.initialize("", ServiceRegistraction.class.getClassLoader(),
				"jar:" + configFile);
		ctx.start();
	}

	private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistraction.class);

	public static final String[] SERVICE_NAMES = new String[] { OpenStockFunctionServiceImpl.SERVICE_NAME,
			ProtocolHandlerImpl.SERVICE_NAME };
	public static final Class[] SERVICE_CLASSES = new Class[] { OpenStockFunctionServiceImpl.class,
			ProtocolHandlerImpl.class };

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
		if (LOG.isTraceEnabled()) {
			LOG.trace("__getComponentFactory({})", sImplementationName);
		}
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
		if (LOG.isTraceEnabled()) {
			LOG.trace("__writeRegistryServiceInfo({})", regKey);
		}
		boolean rt = true;
		for (int i = 0; i < SERVICE_CLASSES.length; i++) {
			boolean rtI = Factory.writeRegistryServiceInfo(SERVICE_CLASSES[i].getName(),
					new String[] { SERVICE_NAMES[i] }, regKey);
			rt = rt && rtI;
		}
		return rt;
	}

}
