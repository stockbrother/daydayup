package daydayup.openstock.netease;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import daydayup.openstock.executor.Interruptable;

/**
 * Convert original file format to the target format acceptable.
 * 
 * @author wu
 *
 */
public class NeteaseDataWashingProcessor implements Interruptable {
	private static final Logger LOG = LoggerFactory.getLogger(NeteaseDataWashingProcessor.class);

	private File sourceDir;
	private File targetDir;

	List<String> types = new ArrayList<>();

	private boolean interrupted;

	public NeteaseDataWashingProcessor(File sourceDir, File targetDir) {
		this.sourceDir = sourceDir;
		this.targetDir = targetDir;
	}

	public NeteaseDataWashingProcessor types(String... types) {
		for (String type : types) {
			this.types.add(type);
		}
		return this;
	}

	public NeteaseDataWashingProcessor xjllb() {
		this.types.add("xjllb");
		return this;
	}

	public void execute() {
		try {
			this.process(this.sourceDir);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} //
	}

	private void process(File file) throws IOException {
		if (this.interrupted) {
			LOG.warn("interrupted.");
			return;
		}
		if (file.isFile()) {
			String name = file.getName();
			if (!name.endsWith(".csv")) {
				// ignore
				if (LOG.isInfoEnabled()) {
					LOG.info("ignore file:" + file.getAbsolutePath());
				}
				return;
			}
			String type = null;

			for (String typeI : types) {
				if (name.startsWith(typeI)) {
					type = typeI;
				}
			}

			if (type == null) {
				// ignore
				if (LOG.isInfoEnabled()) {
					LOG.info("ignore file:" + file.getAbsolutePath());
				}
				return;
			}
			String code = name.substring(type.length(), name.length() - ".csv".length());
			this.doProcess(file, type, code);

			return;
		}
		// isDirectory
		for (File f : file.listFiles()) {
			this.process(f);
		}

	}

	/**
	 * <code>
	    Header,		
		报告日期,2015-12-31,2014-12-31,2013-12-31,2012-12-31,2011-12-31,2010-12-31,2009-12-31,2008-12-31,
		日期格式,yyyy-MM-dd
		公司代码,300201		
		单位,10000
		备注,zcfzb
		Body,
		... ... 
		</code>
	 */
	private void doProcess(File file, String type, String code) throws IOException {

		File typeDir = new File(this.targetDir.getAbsolutePath(), type);
		File areaDir = new File(typeDir, code.substring(0, 4));

		File output = new File(areaDir, code + "." + type + ".csv");
		if (output.exists()) {
			LOG.info("skip of file for it's already exists:" + output.getAbsolutePath());
			return;
		}
		if (!areaDir.exists()) {
			areaDir.mkdirs();
		}
		LOG.info("generating output file:" + output.getAbsolutePath());
		Charset cs = Charset.forName("GBK");
		Reader fr = new InputStreamReader(new FileInputStream(file), cs);

		CSVReader r = new CSVReader(fr);

		CSVWriter w = new CSVWriter(new OutputStreamWriter(new FileOutputStream(output), Charset.forName("UTF-8")), ',',
				CSVWriter.NO_QUOTE_CHARACTER);
		w.writeNext(new String[] { "Header", "" });
		w.writeNext(new String[] { "日期格式", "yyyy-MM-dd" });
		w.writeNext(new String[] { "公司代码", code });
		w.writeNext(new String[] { "单位", "10000" });
		w.writeNext(new String[] { "备注", type });
		String[] reportDate = r.readNext();
		w.writeNext(reportDate);
		w.writeNext(new String[] { "Body", "" });
		while (true) {
			String[] line = r.readNext();
			if (line == null) {
				break;
			}

			String title = line[0];
			title = title.trim();
			if (title.length() == 0) {
				continue;// ignore
			}
			title = title.replace("(万元)", "");
			title = title.replace("(或股本)", "");
			title = title.replace("(或股东权益)", "");
			line[0] = title;
			for (int i = 1; i < line.length; i++) {
				line[i] = line[i].trim();
				if (line[i].equals("--")) {
					line[i] = "";
				}
			}
			w.writeNext(line);//
		}
		w.close();
	}

	@Override
	public void interrupt() {
		this.interrupted = true;
	}

}
