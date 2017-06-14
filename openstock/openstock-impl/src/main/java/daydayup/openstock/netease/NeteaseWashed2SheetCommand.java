package daydayup.openstock.netease;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;

public class NeteaseWashed2SheetCommand extends CommandBase<Void> {

	@Override
	public Void doExecute(CommandContext cc) {
		//XSpreadsheetDocument xDoc = DocUtil.getSpreadsheetDocument(cc.getComponentContext());
		//WashedFileLoadContext flc = new DocWashedFileLoadContext(cc.getComponentContext());
		//new WashedFileLoader(xDoc).load(NeteaseUtil.getDataWashedDir(), flc);;
		return null;
	}

}
