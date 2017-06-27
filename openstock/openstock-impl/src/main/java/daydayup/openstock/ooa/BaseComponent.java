package daydayup.openstock.ooa;

import com.sun.star.uno.XComponentContext;

public class BaseComponent extends com.sun.star.lib.uno.helper.WeakBase {
	protected XComponentContext xContext;
	public OoaComponentContext componentContext;

	public BaseComponent(XComponentContext xCompContext) {
		this.xContext = xCompContext;
		this.componentContext = new OoaComponentContext(this.xContext);
	}
}
