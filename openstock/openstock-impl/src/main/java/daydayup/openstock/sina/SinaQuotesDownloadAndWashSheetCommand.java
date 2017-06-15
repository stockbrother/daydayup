package daydayup.openstock.sina;

import java.io.File;

import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.EnvUtil;
import daydayup.openstock.SheetCommandContext;

public class SinaQuotesDownloadAndWashSheetCommand extends BaseSheetCommand<Object> {

	protected void updateAndReload() {

	}

	@Override
	protected Object doExecute(SheetCommandContext cc) {
		File data = EnvUtil.getDataDir();
		File sinaData = new File(data, "sina");
		File outputParentDir = new File(sinaData, "raw");

		new SinaQuotesCollector().pauseInterval(2000).outputParentDir(outputParentDir).start();

		File to = new File(data, "sina" + File.separator + "washed");

		new SinaAllQuotesPreprocessor(outputParentDir, to).process();
		
		return null;
	}
}
