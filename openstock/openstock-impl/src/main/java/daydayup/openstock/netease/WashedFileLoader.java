package daydayup.openstock.netease;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.star.sheet.XSpreadsheetDocument;

public class WashedFileLoader {
	private static final Logger LOG = LoggerFactory.getLogger(WashedFileLoader.class);

	private Map<String, WashedFileProcessor> processMap = new HashMap<>();

	private int maxSize = 20;

	private int processed;

	private boolean interrupted;

	public WashedFileLoader(XSpreadsheetDocument xDoc) {

		processMap.put("zcfzb", new WashedFileProcessor("ZCFZB"));
		processMap.put("lrb", new WashedFileProcessor("LRB"));

	}

	public void load(File dir, WashedFileLoadContext xContext) {

		if (dir.isFile()) {
			File f = dir;
			String fname = f.getName();
			String[] fnames = fname.split("\\.");

			if (fnames[fnames.length - 1].equals("csv")) {
				String ftype = fnames[fnames.length - 2];
				WashedFileProcessor fp = processMap.get(ftype);
				if (fp == null) {
					LOG.warn("no processor found for file:" + f.getAbsolutePath() + ",type:" + ftype);
				} else {

					if (LOG.isTraceEnabled()) {
						LOG.trace("process file:" + f.getAbsolutePath());
					}
					fp.process(f, xContext);//
					if (this.processed++ > this.maxSize) {
						this.interrupted = true;
					}
					;

				}
			}
			return;
		}
		// is directory
		if (this.interrupted) {
			LOG.warn("interrupted.");
			return;
		}
		for (File f : dir.listFiles()) {
			this.load(f, xContext);
		}

	}

}
