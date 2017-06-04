package daydayup.openstock;

import com.sun.star.sheet.XSpreadsheetDocument;

import daydayup.openstock.netease.NeteaseUtil;
import daydayup.openstock.netease.WashedFileLoadContext;
import daydayup.openstock.netease.WashedFileLoader;
import daydayup.openstock.util.DocUtil;

public class NeteaseWashed2SheetCommand extends CommandBase {

	@Override
	public void execute(CommandContext cc) {
		XSpreadsheetDocument xDoc = DocUtil.getSpreadsheetDocument(cc.getComponentContext());
		WashedFileLoadContext flc = new WashedFileLoadContext(cc.getComponentContext());
		new WashedFileLoader(xDoc).load(NeteaseUtil.getDataWashedDir(), flc);;
	}

}
