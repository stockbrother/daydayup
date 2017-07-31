package daydayup.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.openstock.sse.SseCorpInfo2DbSheetCommand;

public class CsvUtil {
	public static interface CsvRowHandler<T> {

		public void onRow(Map<String, Integer> colIndexMap, String[] row);

	}

	public static List<String> loadColumnFromCsvFile(Reader csvFile, String columnName) {
		List<String> rt = new ArrayList<String>();
		CsvRowHandler<String> crh = new CsvRowHandler<String>() {

			@Override
			public void onRow(Map<String, Integer> colIndexMap, String[] row) {
				String code = SseCorpInfo2DbSheetCommand.getValueByColumn(row, colIndexMap, columnName);
				rt.add(code);
			}
		};

		parseCsvFileWithHeader(csvFile, crh);

		return rt;
	}

	public static <T> void parseCsvFileWithHeader(Reader fr, CsvRowHandler<T> crh) {
		try {
			
			CSVReader reader = new CSVReader(fr);

			// skip header1
			String[] next = reader.readNext();
			Map<String, Integer> colIndexMap = new HashMap<>();
			for (int i = 0; i < next.length; i++) {
				String key = next[i];
				colIndexMap.put(key, i);
			}

			while (true) {
				next = reader.readNext();
				if (next == null) {
					break;
				}
				crh.onRow(colIndexMap, next);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
