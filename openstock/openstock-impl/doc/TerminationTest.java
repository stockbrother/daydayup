import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.PropertyValue;

import com.sun.star.frame.XDesktop;
import com.sun.star.frame.TerminationVetoException;
import com.sun.star.frame.XTerminateListener;
import com.sun.star.frame.TerminationVetoException;
import com.sun.star.frame.XTerminateListener;

/**
 * https://wiki.openoffice.org/wiki/Documentation/DevGuide/OfficeDev/
 * Using_the_Desktop
 * 
 * @author wu
 *
 */

public class TerminationTest extends java.lang.Object {
	public static class TerminateListener implements XTerminateListener {

		public void notifyTermination(com.sun.star.lang.EventObject eventObject) {
			System.out.println("about to terminate...");
		}

		public void queryTermination(com.sun.star.lang.EventObject eventObject) throws TerminationVetoException {

			// test if we can terminate now
			if (TerminationTest.isAtWork()) {
				System.out.println("Terminate while we are at work? No way!");
				throw new TerminationVetoException(); // this will veto the
														// termination,
														// a call to terminate()
														// returns false
			}
		}

		public void disposing(com.sun.star.lang.EventObject eventObject) {
		}
	}

	private static boolean atWork = false;

	public static void main(String[] args) {

		XComponentContext xRemoteContext = null;
		XMultiComponentFactory xRemoteServiceManager = null;
		XDesktop xDesktop = null;

		try {
			// connect and retrieve a remote service manager and component
			// context
			XComponentContext xLocalContext = com.sun.star.comp.helper.Bootstrap.createInitialComponentContext(null);
			XMultiComponentFactory xLocalServiceManager = xLocalContext.getServiceManager();
			Object urlResolver = xLocalServiceManager.createInstanceWithContext("com.sun.star.bridge.UnoUrlResolver",
					xLocalContext);
			XUnoUrlResolver xUnoUrlResolver = (XUnoUrlResolver) UnoRuntime.queryInterface(XUnoUrlResolver.class,
					urlResolver);
			Object initialObject = xUnoUrlResolver
					.resolve("uno:socket,host=localhost,port=2083;urp;StarOffice.ServiceManager");
			XPropertySet xPropertySet = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, initialObject);
			Object context = xPropertySet.getPropertyValue("DefaultContext");
			xRemoteContext = (XComponentContext) UnoRuntime.queryInterface(XComponentContext.class, context);
			xRemoteServiceManager = xRemoteContext.getServiceManager();

			// get Desktop instance
			Object desktop = xRemoteServiceManager.createInstanceWithContext("com.sun.star.frame.Desktop",
					xRemoteContext);
			xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, desktop);

			TerminateListener terminateListener = new TerminateListener();
			xDesktop.addTerminateListener(terminateListener);

			// try to terminate while we are at work
			atWork = true;
			boolean terminated = xDesktop.terminate();
			System.out
					.println("The Office " + (terminated ? "has been terminated" : "is still running, we are at work"));

			// no longer at work
			atWork = false;
			// once more: try to terminate
			terminated = xDesktop.terminate();
			System.out.println("The Office " + (terminated ? "has been terminated"
					: "is still running. Someone else prevents termination, e.g. the quickstarter"));
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}

	}

	public static boolean isAtWork() {
		return atWork;
	}

}