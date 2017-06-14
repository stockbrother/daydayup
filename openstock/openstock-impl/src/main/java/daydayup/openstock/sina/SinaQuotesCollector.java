package daydayup.openstock.sina;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import daydayup.httpclient.HttpClientFactory;
import daydayup.httpclient.HttpResponseCallback;
import daydayup.openstock.EnvUtil;

public class SinaQuotesCollector {
	private static final Logger LOG = LoggerFactory.getLogger(SinaQuotesCollector.class);

	private HttpClientFactory clients;

	private static SimpleDateFormat DF = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	private String host = "vip.stock.finance.sina.com.cn";

	private int nextPage = 1;

	private int responses = 0;

	private int pageSize = 500;

	private boolean stop = false;

	private File outputParentDir;

	private File output;

	public SinaQuotesCollector() {
		this.clients = HttpClientFactory.newInstance();
		if (EnvUtil.isProxyEnabled()) {
			this.clients.setProxy(EnvUtil.getProxyHome(), EnvUtil.getProxyPort());
		}
		this.clients.setPauseInterval(5 * 1000);//

	}

	public SinaQuotesCollector pauseInterval(long pause) {
		this.clients.setPauseInterval(pause);//
		return this;
	}

	public SinaQuotesCollector outputParentDir(File output) {
		this.outputParentDir = output;
		return this;
	}

	public static void main(String[] args) throws Exception {
		File data = EnvUtil.getDataDir();

		File outputParentDir = new File(data, "sina\\all-quotes");
		

		new SinaQuotesCollector().outputParentDir(outputParentDir).start();
	}

	public File start() {
		if (!this.outputParentDir.exists()) {
			if(!this.outputParentDir.mkdirs()){
				throw new RuntimeException("cannot mkdirs:" + this.outputParentDir.getAbsolutePath());				
			}
		}

		File output = null;
		while (true) {
			String name = DF.format(new Date());
			output = new File(this.outputParentDir, name);
			if (output.exists()) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				continue;
			}
			break;

		}
		output.mkdirs();
		this.output = output;

		LOG.info("download all-quotes from sina to folder:" + this.output.getAbsolutePath());

		HttpResponseCallback hep = new HttpResponseCallback() {

			@Override
			public void onResponse(CloseableHttpResponse response) throws IOException {
				SinaQuotesCollector.this.onResponse(response);
			}
		};

		Iterator<String> uriIt = new Iterator<String>() {

			@Override
			public boolean hasNext() {
				return SinaQuotesCollector.this.hasNext();
			}

			@Override
			public String next() {
				return SinaQuotesCollector.this.next();
			}
		};

		this.clients.get(host, uriIt, hep);

		return this.output;

	}

	protected void onResponse(CloseableHttpResponse response) throws IOException {
		this.responses++;
		StringBuffer sb = new StringBuffer();
		for (Header header : response.getAllHeaders()) {
			sb.append("(").append(header.getName() + "=" + header.getValue()).append("),");
		}
		LOG.info("onResponse,statusLine:" + response.getStatusLine() + sb.toString());

		int stateCode = response.getStatusLine().getStatusCode();
		if (stateCode != 200) {
			LOG.error("stop for statusCode:" + stateCode + " not expected.");
			this.stop = true;
			return;
		}

		Header headerContentType = response.getFirstHeader("Content-Type");
		String contentType = headerContentType.getValue();
		if (contentType.startsWith("text/html")) {
			LOG.error("stop contentType:" + contentType + " not expected.");
			this.stop = true;
			return;
		}

		File outputFile = new File(output, "Market_Center.getHQNodeData.p" + this.responses + ".json");
		Header h = response.getEntity().getContentEncoding();
		File workFile = null;
		int i = 0;
		while (true) {
			workFile = new File(outputFile.getAbsolutePath() + ".work" + (i++));
			if (!workFile.exists()) {
				break;
			}
		}
		FileOutputStream os = new FileOutputStream(workFile);
		response.getEntity().writeTo(os);
		os.close();
		boolean succ = workFile.renameTo(outputFile);
		if (succ) {
			LOG.info("got:" + outputFile.getAbsolutePath());//
		} else {
			LOG.error("cannot rename from:" + workFile.getAbsolutePath() + ",to:" + outputFile.getAbsolutePath());
		}
		Reader fr = new InputStreamReader(new FileInputStream(outputFile));
		char[] line = new char[4];
		int len = fr.read(line);
		if ("null".equals(new String(line, 0, len))) {
			this.stop = true;
		}

	}

	protected String next() {
		StringBuffer sb = new StringBuffer();
		sb.append("/quotes_service/api/json_v2.php/Market_Center.getHQNodeData")//
				.append("?num=").append(this.pageSize)//
				.append("&sort=symbol")//
				.append("&asc=0")//
				.append("&node=hs_a")//
				.append("&symbol=")//
				.append("&_s_r_a=page")//
				.append("&page=").append(this.nextPage)//
		;
		this.nextPage++;
		return sb.toString();
	}

	protected boolean hasNext() {
		return !this.stop;
	}
}
