package ddu.test;

import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.XModifyBroadcaster;
import com.sun.star.util.XModifyListener;

import ooo.connector.BootstrapSocketConnector;

public class FirstUnoContact {

	public static void main(String[] args) {
		try {
			// get the remote office component context
			// com.sun.star.uno.XComponentContext xContext =
			// com.sun.star.comp.helper.Bootstrap.bootstrap();
			String oooExeFolder = "C:/Program Files (x86)/OpenOffice 4/program";
			com.sun.star.uno.XComponentContext xContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
			System.out.println("Connected to a running office ...");

			com.sun.star.lang.XMultiComponentFactory xMCF = xContext.getServiceManager();

			String available = (xMCF != null ? "available" : "not available");
			System.out.println("remote ServiceManager is " + available);

			Object desktop = xMCF.createInstanceWithContext("com.sun.star.frame.Desktop", xContext);
			
			
			XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class,
					desktop);
			XComponent xComponent = xComponentLoader.loadComponentFromURL("private:factory/scalc", "_blank", 0, null);
			XSpreadsheetDocument xSpreadsheetDocument = (XSpreadsheetDocument) UnoRuntime
					.queryInterface(XSpreadsheetDocument.class, xComponent);

			// Cast to interface XModifyBroadcaster to attach listener
			final XModifyBroadcaster messageHost = (XModifyBroadcaster) UnoRuntime
					.queryInterface(XModifyBroadcaster.class, xSpreadsheetDocument);
			messageHost.addModifyListener(new XModifyListener() {
				public void modified(EventObject eo) {

					System.out.println("modified:" + eo.getClass() + "," + eo.Source);
				}

				public void disposing(EventObject eo) {
					System.out.println("disposing:" + eo);
				}
			});
			while (true) {
				Thread.sleep(1000);
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
}
