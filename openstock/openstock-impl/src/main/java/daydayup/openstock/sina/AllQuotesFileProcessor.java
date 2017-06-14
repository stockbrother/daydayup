package daydayup.openstock.sina;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;
import daydayup.openstock.wash.WashedFileProcessor;

public class AllQuotesFileProcessor implements WashedFileProcessor {
	private static final Logger LOG = LoggerFactory.getLogger(AllQuotesFileProcessor.class);
	private Map<String, String> header2columnMap = new HashMap<String, String>();
	private String type;

	public AllQuotesFileProcessor(String type) {
		this.type = type;
		header2columnMap.put("公司代码", "code");
		header2columnMap.put("公司名称", "name");
	}

	@Override
	public void process(Reader fr, WashedFileLoadContext xContext) {

		CSVReader reader = new CSVReader(fr);
		Map<String, Integer> columnIndexMap = new HashMap<String, Integer>();
		try {

			String[] header = reader.readNext();
			for (int i = 0; i < header.length; i++) {
				columnIndexMap.put(header[i], i);

			}
			String[] line = null;
			// skip header
			while (true) {
				line = reader.readNext();
				if (line == null) {
					break;
				}
				String corpId = getColumn(columnIndexMap, "code", line);
				String name = getColumn(columnIndexMap, "name", line);
				String priceS = getColumn(columnIndexMap, "settlement", line);

				// QuotesEntity ce = new QuotesEntity();
				// ce.setId(code);
				// ce.setCode(code);
				// ce.setName(name);

				// ce.setSettlement(new BigDecimal(getColumn(columnIndexMap,
				// "settlement", line)));//
				// es.save(ce);
				Date reportDate = new Date();// TODO
				
				xContext.getOrCreateTypeContext(type).writeRow(corpId, reportDate, "PRICE", new BigDecimal(priceS));
			}
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);

		}
	}

	private String getColumn(Map<String, Integer> columnIndexMap, String key, String[] line) {
		Integer idxO = columnIndexMap.get(key);
		if (idxO == null) {
			throw new RuntimeException("no this key:" + key);
		}
		return line[idxO];

	}

}
