package daydayup.openstock.netease;

import com.sun.star.sheet.XSpreadsheetDocument;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;
import daydayup.openstock.netease.WashedFileLoader.DocWashedFileLoadContext;
import daydayup.openstock.netease.WashedFileLoader.WashedFileLoadContext;
import daydayup.openstock.util.DocUtil;

public class NeteaseWashed2SheetCommand extends CommandBase {

	@Override
	public void execute(CommandContext cc) {
		XSpreadsheetDocument xDoc = DocUtil.getSpreadsheetDocument(cc.getComponentContext());
		WashedFileLoadContext flc = new DocWashedFileLoadContext(cc.getComponentContext());
		new WashedFileLoader(xDoc).load(NeteaseUtil.getDataWashedDir(), flc);;
	}

}
