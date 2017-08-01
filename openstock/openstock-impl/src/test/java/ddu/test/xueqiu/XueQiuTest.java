package ddu.test.xueqiu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import daydayup.openstock.xueqiu.XueqiuDataCollector;
import daydayup.openstock.xueqiu.XueqiuDataWasher;
import daydayup.util.CsvUtil;
import junit.framework.TestCase;

public class XueQiuTest extends TestCase {
	private static String shCsv = "c:\\openstock\\sse\\sse.corplist.csv";
	private static String szCsv = "c:\\openstock\\szse\\szse.corplist.csv";
	File folder = new File("c:\\openstock\\xueqiu\\raw");
	File folder2 = new File("c:\\openstock\\xueqiu\\washed");
	
	public void test() throws IOException {
		//this.doWash();
		
	}
	private void doWash()throws IOException{
		
		XueqiuDataWasher w = new XueqiuDataWasher(folder,Charset.forName("UTF-8"),folder2);
		w.types("balsheet");
		w.types("incstatement");
		w.types("cfstatement");
		
		w.execute();
	}
	private void doCollect()throws IOException{
		
		folder.mkdirs();
		XueqiuDataCollector dc = new XueqiuDataCollector(folder);
		dc.types("balsheet", "incstatement", "cfstatement");
		dc.pauseInterval(1 * 1000);
		List<String> corpCodeL = getCorpCodeList();
		dc.corpCodes(corpCodeL);
		dc.execute();
	}

	public List<String> getCorpCodeList() throws IOException {
		List<String> rt = new ArrayList<String>();
		{

			Charset cs = Charset.forName("UTF-8");
			Reader fr = new InputStreamReader(new FileInputStream(szCsv), cs);
			List<String> corpL = CsvUtil.loadColumnFromCsvFile(fr, "A股代码");
			rt.addAll(corpL);
		}
		{

			Charset cs = Charset.forName("UTF-8");
			Reader fr = new InputStreamReader(new FileInputStream(shCsv), cs);
			List<String> corpL = CsvUtil.loadColumnFromCsvFile(fr, "A股代码");
			rt.addAll(corpL);
		}

		return rt;

	}
}
