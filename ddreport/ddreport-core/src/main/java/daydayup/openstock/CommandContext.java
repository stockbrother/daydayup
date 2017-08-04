package daydayup.openstock;

import daydayup.openstock.database.DataBaseService;

public class CommandContext {
	DdrContext ddr;
	public CommandContext(DdrContext dc) {
		ddr = dc;
	}

	public DataBaseService getDataBaseService() {
		return ddr.getDataBaseService();
	}

}
