package daydayup.openstock.cninfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.openstock.CorpNameService;

public class CorpInfoLoader {

	public void loadCorpInfoIntoMemory(File csvFile, CorpNameService cns) {

		try {
			Charset cs = Charset.forName("GBK");
			Reader fr = new InputStreamReader(new FileInputStream(csvFile), cs);
			CSVReader reader = new CSVReader(fr);

			// skip header1
			String[] next = reader.readNext();
			// skip header2
			next = reader.readNext();
			while (true) {
				next = reader.readNext();
				if (next == null) {
					break;
				}
				String code = next[0].trim();
				String name = next[1].trim();
				cns.addCorpName(code, name);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
