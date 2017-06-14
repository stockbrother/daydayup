package daydayup.openstock.netease;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.wash.WashedFileLoader;
import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;

public class NeteaseWashed2DbCommand extends CommandBase<Void> {

	@Override
	public Void doExecute(CommandContext cc) {
		DataBaseService dbs = cc.getDataBaseService();		
		WashedFileLoadContext flc = new WashedFileLoadContext(dbs);
		new WashedFileLoader().load(NeteaseUtil.getDataWashedDir(), flc);;
		return null;
		
		
	}

}
