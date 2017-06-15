package daydayup.openstock.sina;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import daydayup.openstock.EnvUtil;
import daydayup.openstock.RtException;
import daydayup.openstock.wash.WashedFileLoader.WashedFileLoadContext;
import daydayup.openstock.wash.WashedFileProcessor;

public class AllQuotesFileProcessor implements WashedFileProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(AllQuotesFileProcessor.class);

	// private Map<String, String> header2columnMap = new HashMap<String,
	// String>();

	private String type;

	public AllQuotesFileProcessor(String type) {
		this.type = type;
		// header2columnMap.put("公司代码", "code");
		// header2columnMap.put("公司名称", "name");
	}

	@Override
	public void process(Reader fr, WashedFileLoadContext xContext) {

		CSVReader reader = new CSVReader(fr);
		Map<String, Integer> columnIndexMap = new HashMap<String, Integer>();
		try {

			// read header properties.
			Map<String, String> headerMap = new HashMap<>();
			while (true) {
				String[] line = reader.readNext();
				if (line == null) {
					break;
				}

				if (line[0].equals("Header")) {
					continue;
				}

				if (line[0].equals("Body")) {
					break;
				}
				headerMap.put(line[0], line[1]);
			}
			String dateF = headerMap.get("日期格式");
			String dateS = headerMap.get("报告日期");
			Date reportDate;
			try {
				reportDate = new SimpleDateFormat(dateF).parse(dateS);
			} catch (ParseException e) {
				throw RtException.toRtException(e);
			}
			reportDate = EnvUtil.floorHour(reportDate);
			// read columns line.
			String[] columns = reader.readNext();
			for (int i = 0; i < columns.length; i++) {
				columnIndexMap.put(columns[i], i);
			}

			// read data cells
			while (true) {
				String[] line = reader.readNext();
				if (line == null) {
					break;
				}
				String corpId = getColumn(columnIndexMap, "code", line);				
				String priceS = getColumn(columnIndexMap, "settlement", line);

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
