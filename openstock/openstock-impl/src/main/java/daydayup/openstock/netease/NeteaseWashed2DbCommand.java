package daydayup.openstock.netease;

import com.sun.star.sheet.XSpreadsheetDocument;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.netease.WashedFileLoader.DbWashedFileLoadContext;
import daydayup.openstock.netease.WashedFileLoader.WashedFileLoadContext;
import daydayup.openstock.util.DocUtil;

public class NeteaseWashed2DbCommand extends CommandBase<Void> {

	@Override
	public Void execute(CommandContext cc) {
		DataBaseService dbs = cc.getDataBaseService();
		XSpreadsheetDocument xDoc = DocUtil.getSpreadsheetDocument(cc.getComponentContext());
		WashedFileLoadContext flc = new DbWashedFileLoadContext(dbs);
		new WashedFileLoader(xDoc).load(NeteaseUtil.getDataWashedDir(), flc);;
		return null;
		
		
	}

}
