package daydayup.openstock.sina;

import java.io.File;

import daydayup.openstock.BaseSheetCommand;
import daydayup.openstock.EnvUtil;
import daydayup.openstock.SheetCommandContext;
import daydayup.openstock.database.DataBaseService;
import daydayup.openstock.wash.WashedFileLoader;
import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;

public class AllQuotesUpdateSheetCommand extends BaseSheetCommand<Object> {

	protected void updateAndReload() {

	}

	@Override
	protected Object doExecute(SheetCommandContext cc) {
		File data = EnvUtil.getDataDir();
		File sinaData = new File(data, "sina");
		File outputParentDir = new File(sinaData, "raw");

		File output = new SinaQuotesCollector().pauseInterval(2000).outputParentDir(outputParentDir).start();

		File to = new File(data, "sina" + File.separator + "washed");

		new SinaAllQuotesPreprocessor(outputParentDir, to).process();
		File[] files = to.listFiles();
		File lastFile = null;
		for (File f : files) {
			if (f.isDirectory()) {
				continue;
			}
			if (lastFile == null || lastFile.lastModified() < f.lastModified()) {
				lastFile = f;
			}
		}
		// LOG.info("load the last file:" + lastFile.getAbsolutePath());//

		DataBaseService dbs = cc.getDataBaseService();
		WashedFileLoadContext flc = new WashedFileLoadContext(dbs);
		new WashedFileLoader().load(lastFile, flc);

		return null;
	}
}
