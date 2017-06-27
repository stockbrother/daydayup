package daydayup.openstock.ooa;

import com.sun.star.task.XStatusIndicator;

import daydayup.openstock.document.StatusIndicator;

public class OoaStatusIndicator implements StatusIndicator {

	XStatusIndicator si;

	public OoaStatusIndicator(XStatusIndicator si){
		this.si = si;
	}
	@Override
	public void start(String string, int i) {
		this.si.start(string, i);
	}

	@Override
	public void end() {
		this.si.end();
	}

	@Override
	public void setText(String string) {
		this.si.setText(string);
	}

	@Override
	public void setValue(int i) {
		this.si.setValue(i);
	}

}
