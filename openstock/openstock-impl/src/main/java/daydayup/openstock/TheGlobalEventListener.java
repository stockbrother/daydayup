package daydayup.openstock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.beans.XPropertySet;
import com.sun.star.document.XEventListener;
import com.sun.star.lang.EventObject;
import com.sun.star.uno.UnoRuntime;

import daydayup.openstock.util.XObjectUtil;

public class TheGlobalEventListener implements XEventListener {
	private static Logger LOG = LoggerFactory.getLogger(TheGlobalEventListener.class);

	ProtocolHandlerImpl ph;

	public TheGlobalEventListener(ProtocolHandlerImpl ph) {
		this.ph = ph;
	}

	@Override
	public void disposing(EventObject arg0) {

	}

	@Override
	public void notifyEvent(com.sun.star.document.EventObject arg0) {
		if ("OnLoadFinished".equals(arg0.EventName)) {
			XPropertySet xSourceProps = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, arg0.Source);

			if (LOG.isTraceEnabled()) {
				LOG.trace(XObjectUtil.format(xSourceProps, new StringBuffer()).toString());
			}
			ph.execute(new CorpsApply2MemoryCommand());
			
		}
	}

}
