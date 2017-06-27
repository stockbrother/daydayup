package daydayup.openstock;

import java.util.ArrayList;
import java.util.List;

import daydayup.openstock.document.StatusIndicator;

public class SheetCommandContext extends CommandContext {

	private List<String> argumentList = new ArrayList<>();

	private Attributes attributes = new Attributes();

	public List<String> getArgumentList() {
		return argumentList;
	}

	public SheetCommandContext(CommandContext parent, List<String> argList) {
		super(parent.cc);
		this.argumentList.addAll(argList);
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public StatusIndicator getStatusIndicator() {
		
		return null;
	}

}
