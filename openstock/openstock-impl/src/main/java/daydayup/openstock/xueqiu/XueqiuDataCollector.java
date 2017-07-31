package daydayup.openstock.xueqiu;

import java.io.File;

import daydayup.openstock.collector.HttpDataCollector;

/**
 * http://api.xueqiu.com/stock/f10/balsheet.csv?symbol=SH601166&page=1&size=10000
 * http://api.xueqiu.com/stock/f10/incstatement.csv?symbol=SH601166&page=1&size=10000
 * http://api.xueqiu.com/stock/f10/cfstatement.csv?symbol=SH601166&page=1&size=10000
 * 
 * @author wuzhen
 *
 */
public class XueqiuDataCollector extends HttpDataCollector {

	public XueqiuDataCollector(File dir) {
		super(dir, "api.xueqiu.com");
	}

	@Override
	protected String getUrl(String corpCode, String type) {
		//
		// balsheet
		String market = getMarketCode(corpCode);

		return "/stock/f10/" + type + ".csv?symbol=" + market + corpCode + "&page=1&size=10000";
	}

	public static String getMarketCode(String corpCode) {
		if (corpCode.startsWith("60")) {
			return "SH";
		} else {
			return "SZ";
		}
	}

}
