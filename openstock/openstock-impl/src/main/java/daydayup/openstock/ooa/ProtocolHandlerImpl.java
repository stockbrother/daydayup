package daydayup.openstock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.document.XEventBroadcaster;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import daydayup.openstock.cninfo.CorpInfoRefreshCommand;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.executor.TaskConflictException;
import daydayup.openstock.executor.TaskExecutor;
import daydayup.openstock.netease.NeteaseDataDownloadCommand;
import daydayup.openstock.netease.NeteaseWashed2DbCommand;
import daydayup.openstock.netease.NeteaseDataWashingCommand;
import daydayup.openstock.netease.NeteaseWashed2SheetCommand;
import daydayup.openstock.util.MessageBoxUtil;

public final class ProtocolHandlerImpl extends WeakBase implements com.sun.star.frame.XDispatchProvider,
		com.sun.star.frame.XDispatch, com.sun.star.lang.XInitialization, XServiceInfo {

	public final XComponentContext xContext;
	private com.sun.star.frame.XFrame xFrame;

	public static final String SERVICE_NAME = "com.sun.star.frame.ProtocolHandler";
	private static final String PROTOCOL = "daydayup.openstock.command:";
	private static final Logger LOG = LoggerFactory.getLogger(ProtocolHandlerImpl.class);

	public ProtocolHandlerImpl(XComponentContext context) {
		xContext = context;
	};

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

	// com.sun.star.frame.XDispatchProvider:
	@Override
	public com.sun.star.frame.XDispatch queryDispatch(com.sun.star.util.URL aURL, String sTargetFrameName,
			int iSearchFlags) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("queryDispatch({},{},{})", aURL, sTargetFrameName, iSearchFlags);
		}
		if (aURL.Protocol.compareTo(PROTOCOL) == 0) {
			if (aURL.Path.compareTo("ShowAboutCommand") == 0) {
				return this;
			}
			if (aURL.Path.compareTo("MySecondCommand") == 0) {
				return this;
			}
			if (aURL.Path.compareTo("CorpInfoRefreshCommand") == 0) {
				return this;
			}
			if (aURL.Path.compareTo("NeteaseDataDownloadCommand") == 0) {
				return this;
			}

			if (aURL.Path.compareTo("NeteaseDataWashingCommand") == 0) {
				return this;
			}
			if (aURL.Path.compareTo("NeteaseWashed2DbCommand") == 0) {
				return this;
			}
			if (aURL.Path.compareTo("NeteaseWashed2SheetCommand") == 0) {
				return this;
			}
			if (aURL.Path.compareTo("InterruptAllTaskCommand") == 0) {
				return this;
			}
			if (aURL.Path.compareTo("CorpsApply2MemoryCommand") == 0) {
				return this;
			}

		}
		return null;
	}

	// com.sun.star.frame.XDispatchProvider:
	@Override
	public com.sun.star.frame.XDispatch[] queryDispatches(com.sun.star.frame.DispatchDescriptor[] seqDescriptors) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("queryDispatches({})", (Object) seqDescriptors);
		}
		int nCount = seqDescriptors.length;
		com.sun.star.frame.XDispatch[] seqDispatcher = new com.sun.star.frame.XDispatch[seqDescriptors.length];

		for (int i = 0; i < nCount; ++i) {
			seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL, seqDescriptors[i].FrameName,
					seqDescriptors[i].SearchFlags);
		}
		return seqDispatcher;
	}

	// com.sun.star.frame.XDispatch:
	@Override
	public void dispatch(com.sun.star.util.URL aURL, com.sun.star.beans.PropertyValue[] aArguments) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("dispatch({},{})", aURL, (Object) aArguments);
		}

		if (aURL.Protocol.compareTo(PROTOCOL) == 0) {
			if (aURL.Path.compareTo("ShowAboutCommand") == 0) {
				// add your own code here
				MessageBoxUtil.showMessageBox(this.xContext, this.xFrame, "A Message", "aURL:" + aURL.Complete);
				return;
			}
			if (aURL.Path.compareTo("MySecondCommand") == 0) {
				// add your own code here
				MessageBoxUtil.showMessageBox(this.xContext, this.xFrame, "A Message", "aURL:" + aURL.Complete);
				return;
			}
			String command = aURL.Path;
			OpenStock.getInstance().execute(command, this.xContext);
		}
	}

	@Override
	public void addStatusListener(com.sun.star.frame.XStatusListener xControl, com.sun.star.util.URL aURL) {
		// add your own code here
	}

	@Override
	public void removeStatusListener(com.sun.star.frame.XStatusListener xControl, com.sun.star.util.URL aURL) {
		// add your own code here
	}

	// com.sun.star.lang.XInitialization:
	@Override
	public void initialize(Object[] object) throws com.sun.star.uno.Exception {
		if (LOG.isTraceEnabled()) {
			LOG.trace("initialize({})", (Object) object);
		}
		xFrame = (com.sun.star.frame.XFrame) UnoRuntime.queryInterface(com.sun.star.frame.XFrame.class, object[0]);
		/*
		 * Object officeDoc =
		 * this.xContext.getServiceManager().createInstanceWithContext(
		 * "com.sun.star.document.OfficeDocument", this.xContext);
		 * XDocumentEventBroadcaster deb =
		 * UnoRuntime.queryInterface(XDocumentEventBroadcaster.class,
		 * officeDoc); deb.addDocumentEventListener(new
		 * TheDocumentEventListener(this));
		 */
		Object geb = this.xContext.getServiceManager()
				.createInstanceWithContext("com.sun.star.frame.GlobalEventBroadcaster", this.xContext);
		XEventBroadcaster xeb = UnoRuntime.queryInterface(XEventBroadcaster.class, geb);
		xeb.addEventListener(new TheGlobalEventListener(this));

	}

}
