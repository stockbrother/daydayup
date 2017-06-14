package daydayup.openstock.sina;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;
import daydayup.openstock.EnvUtil;
import daydayup.openstock.util.FileUtil;

public class SinaAllQuotesPreprocessor extends FilesPreprocessor {

	private static Logger LOG = LoggerFactory.getLogger(SinaAllQuotesPreprocessor.class);

	private static String[] HEADERS = new String[] { "symbol", "code", "name", "trade", "pricechange", "changepercent",
			"buy", "sell", "settlement", "open", "high", "low", "volume", "amount", "ticktime", "per", "pb", "mktcap",
			"nmc", "turnoverratio" };
	private static Charset charSet = Charset.forName("gb2312");

	public SinaAllQuotesPreprocessor(File sourceDir, File targetDir) {
		super(sourceDir, targetDir);
	}

	public static void main(String[] args) {
		File data = EnvUtil.getDataDir();
		File from = new File(data, "sina\\raw");

		File to = new File(data, "sina\\washed");
		
		new SinaAllQuotesPreprocessor(from, to).process();

	}

	@Override
	public void process() {
		try {
			this.doProcess();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} //
	}

	private void doProcess() throws IOException {
		if (!this.targetDir.exists()) {
			this.targetDir.mkdirs();
		}
		for (File f : this.sourceDir.listFiles()) {
			if (f.isDirectory()) {
				this.processFileGroup(f);
			}
		}
	}

	private void processFileGroup(File dir) throws IOException {
		String name = dir.getName();

		File output = new File(this.targetDir, name + ".quotes" + ".csv");
		if (output.exists()) {
			// ignore.
			LOG.warn("exists" + output.getAbsolutePath());
			return;//
		}

		File outputWork = new File(this.targetDir, name + ".quotes" + ".csv.work");

		CSVWriter cw = new CSVWriter(new OutputStreamWriter(new FileOutputStream(outputWork), Charset.forName("UTF-8")),
				',', CSVWriter.NO_QUOTE_CHARACTER);
		cw.writeNext(HEADERS);
		int total = 0;
		for (File f : dir.listFiles()) {
			String fname = f.getName();
			if (!fname.endsWith(".json")) {
				LOG.warn("ignore:" + f.getAbsolutePath());
				// ignore
				continue;
			}
			Reader r = FileUtil.newReader(f, charSet);
			// Object jso = JSONValue.parse(r);
			JSONTokener jt = new JSONTokener(r);
			Object o = jt.nextValue();
			if (JSONObject.NULL.equals(o)) {
				LOG.info("found 'null',ignore:" + f.getAbsolutePath());
			} else if (o instanceof JSONArray) {
				JSONArray jsa = (JSONArray) o;
				LOG.info("process:" + f.getAbsolutePath() + ",array length:" + jsa.length());
				for (int i = 0; i < jsa.length(); i++) {
					JSONObject jo = (JSONObject) jsa.get(i);
					String[] line = new String[HEADERS.length];
					for (int j = 0; j < HEADERS.length; j++) {
						String key = HEADERS[j];
						line[j] = String.valueOf(jo.get(key));
					}
					cw.writeNext(line);
					total++;
				}
			} else {
				LOG.error("unkown json object:" + o);
			}

		}
		cw.close();
		if (total == 0) {
			outputWork.delete();
		} else {
			outputWork.renameTo(output);
		}
	}

}
