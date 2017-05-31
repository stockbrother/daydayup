package ddu.test;

import com.sun.star.document.XEventBroadcaster;
import com.sun.star.document.XEventListener;
import com.sun.star.frame.TerminationVetoException;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XTerminateListener;
import com.sun.star.lang.EventObject;
import com.sun.star.uno.UnoRuntime;

import ooo.connector.BootstrapSocketConnector;

public class FirstUnoContact2 {

	public static void main(String[] args) {
		try {
			reInit();
			while (true) {
				Thread.sleep(1000);
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	public static void reInit() {
		try {

			// get the remote office component context
			// com.sun.star.uno.XComponentContext xContext =
			// com.sun.star.comp.helper.Bootstrap.bootstrap();
			String oooExeFolder = "C:/Program Files (x86)/OpenOffice 4/program";
			final com.sun.star.uno.XComponentContext xContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
			System.out.println("Connected to a running office ...");

			final com.sun.star.lang.XMultiComponentFactory xMCF = xContext.getServiceManager();

			resetGlobalEventListener(xMCF, xContext);

			String available = (xMCF != null ? "available" : "not available");
			System.out.println("remote ServiceManager is " + available);

			final Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);
			if (desktop == null) {
				throw new RuntimeException("nullf");
			}
			XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
			if (xDesktop == null) {
				throw new RuntimeException("nullf");
			}
			xDesktop.addTerminateListener(new XTerminateListener() {

				@Override
				public void disposing(EventObject eo) {
					System.out.println("disposing:" + eo.getClass() + "," + eo.Source);
				}

				@Override
				public void notifyTermination(EventObject eo) {
					System.out.println("notifyTermination:" + eo.getClass() + "," + eo.Source);
					System.exit(0);
				}

				@Override
				public void queryTermination(EventObject eo) throws TerminationVetoException {
					System.out.println("queryTermination:" + eo.getClass() + "," + eo.Source);
				}
			});
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public static void resetGlobalEventListener(com.sun.star.lang.XMultiComponentFactory xMCF,
			com.sun.star.uno.XComponentContext xContext) {
		Object globalEvents = null;
		try {
			globalEvents = xMCF.createInstanceWithContext("com.sun.star.frame.GlobalEventBroadcaster", xContext);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (globalEvents == null) {
			throw new RuntimeException("nullf");
		}
		addGlobalEventListener(globalEvents);

	}

	public static void addGlobalEventListener(Object globalEvents) {
		XEventBroadcaster events = UnoRuntime.queryInterface(XEventBroadcaster.class, globalEvents);
		if (events == null) {
			throw new RuntimeException("nullf");
		}
		events.addEventListener(new XEventListener() {

			@Override
			public void disposing(EventObject eo) {
				if (eo instanceof com.sun.star.document.EventObject) {
					System.out.println(":com.sun.star.document.EventObject" + eo.getClass() + "," + eo.Source);
				}
				System.out.println("modified:" + eo.getClass() + "," + eo.Source);
			}

			@Override
			public void notifyEvent(com.sun.star.document.EventObject eo) {

				System.out.println("Document event:" + eo.getClass() + "," + eo.Source + "," + eo.EventName);
			}
		});
	}
}
