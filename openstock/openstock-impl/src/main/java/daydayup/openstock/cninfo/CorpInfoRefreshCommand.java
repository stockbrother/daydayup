package daydayup.openstock.cninfo;

import java.io.File;

import daydayup.openstock.CommandBase;
import daydayup.openstock.GlobalVars;

public class CorpInfoRefreshCommand extends CommandBase {

	@Override
	public void execute() {

		File csvFile = new File("C:\\D\\data\\cninfo\\20170602111822.csv");

		new CorpInfoLoader().loadCorpInfoIntoMemory(csvFile, GlobalVars.getInstance().getCorpNameService());

	}

}
