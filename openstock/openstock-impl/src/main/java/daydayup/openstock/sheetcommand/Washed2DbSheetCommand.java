package daydayup.openstock.sheetcommand;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.EnvUtil;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.document.Spreadsheet;
import daydayup.openstock.wash.WashedFileLoader;
import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;

public class Washed2DbSheetCommand extends BaseSheetCommand<Object> {

	public static final DateFormat DF = new SimpleDateFormat("yyyy/MM/dd");

	@Override
	protected Object doExecute(SheetCommandContext scc) {

		Spreadsheet sheet = scc.getSheet();

		String folderS = null;


		for (int i = 0; i < 100; i++) {
			String key = sheet.getText(0, i);
			if (key.equals("Folder")) {
				folderS = sheet.getText(1, i);
				break;
			}
		}
		
		if (folderS == null) {
			return "no folder provided!";
		}
		DataBaseService dbs = scc.getDataBaseService();
		WashedFileLoadContext flc = new WashedFileLoadContext(dbs);
		File folder = new File(EnvUtil.getDataDir().getAbsolutePath() + File.separator +folderS);
		new WashedFileLoader().load(folder, flc);
		
		return "done";
	}

	protected String getTargetSheet(SheetCommandContext scc, String tableName) {
		return "" + tableName;
	}

}
