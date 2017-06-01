package daydayup.openstock;

import com.sun.star.lang.XServiceInfo;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public final class ProtocolHandlerImpl extends WeakBase implements com.sun.star.frame.XDispatchProvider,
		com.sun.star.frame.XDispatch, com.sun.star.lang.XInitialization, XServiceInfo {
	private final XComponentContext m_xContext;
	private com.sun.star.frame.XFrame m_xFrame;

	public static final String SERVICE_NAME = "com.sun.star.frame.ProtocolHandler";
	private static final String PROTOCOL = "daydayup.openstock.command:";
	
	
	
	public ProtocolHandlerImpl(XComponentContext context) {
		m_xContext = context;
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
		if (aURL.Protocol.compareTo(PROTOCOL) == 0) {
			if (aURL.Path.compareTo("ShowAboutCommand") == 0)
				return this;
			if (aURL.Path.compareTo("MySecondCommand") == 0)
				return this;
		}
		return null;
	}

	// com.sun.star.frame.XDispatchProvider:
	@Override
	public com.sun.star.frame.XDispatch[] queryDispatches(com.sun.star.frame.DispatchDescriptor[] seqDescriptors) {
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
		if (aURL.Protocol.compareTo(PROTOCOL) == 0) {
			if (aURL.Path.compareTo("ShowAboutCommand") == 0) {
				// add your own code here
				return;
			}
			if (aURL.Path.compareTo("MySecondCommand") == 0) {
				// add your own code here
				return;
			}
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
		if (object.length > 0) {
			m_xFrame = (com.sun.star.frame.XFrame) UnoRuntime.queryInterface(com.sun.star.frame.XFrame.class,
					object[0]);			
		}
	}

}
