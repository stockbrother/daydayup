package daydayup.openstock;

import java.util.ArrayList;
import java.util.List;

public class SheetCommandContext extends CommandContext {

	private List<String> argumentList = new ArrayList<>();

	public List<String> getArgumentList() {
		return argumentList;
	}

	public SheetCommandContext(CommandContext parent, List<String> argList) {
		super(parent.componentContext, parent.statusIndicator);
		this.argumentList.addAll(argList);
	}

}
