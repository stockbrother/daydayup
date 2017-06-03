package daydayup.openstock.cninfo;

import java.io.File;

import com.sun.star.frame.XDesktop;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;

import daydayup.openstock.CommandBase;
import daydayup.openstock.CommandContext;
import daydayup.openstock.GlobalVars;

public class CorpInfoRefreshCommand extends CommandBase {

	@Override
	public void execute(CommandContext cc) {

		File csvFile = new File("C:\\D\\data\\cninfo\\20170602111822.csv");

		new CorpInfoLoader().loadCorpInfoIntoMemory(csvFile, GlobalVars.getInstance().getCorpNameService());
		Object desktop = null;
		try {
			desktop = cc.getComponentContext().getServiceManager()
					.createInstanceWithContext("com.sun.star.frame.Desktop", cc.getComponentContext());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		XDesktop xDesktop = UnoRuntime.queryInterface(XDesktop.class, desktop);
		new CorpInfoLoader().loadCorpInfoToSheet(GlobalVars.getInstance().getCorpNameService(), xDesktop);

	}

}
