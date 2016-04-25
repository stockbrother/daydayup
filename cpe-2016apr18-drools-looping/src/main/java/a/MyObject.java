package a;

import java.util.ArrayList;
import java.util.List;

public class MyObject {

	private MyVar var = new MyVar();
	private List<String> stateChanges = new ArrayList<String>();

	public MyVar getVar() {
		return var;
	}

	public void setVar(MyVar var) {
		this.var = var;
	}

	private boolean justInsterted;

	public boolean isJustInsterted() {
		return justInsterted;
	}

	public void setJustInsterted(boolean justInsterted) {
		this.justInsterted = justInsterted;
	}

	private boolean aboutToBeRetracted;

	public boolean isHasStateChanged() {
		return !this.stateChanges.isEmpty();
	}

	public void addStateChange(String sc) {
		this.stateChanges.add(sc);
	}

	public void clearStateChanges() {
		this.stateChanges.clear();
	}

	public boolean isAboutToBeRetracted() {
		return aboutToBeRetracted;
	}

	public void setAboutToBeRetracted(boolean aboutToBeRetracted) {
		this.aboutToBeRetracted = aboutToBeRetracted;
	}

}
