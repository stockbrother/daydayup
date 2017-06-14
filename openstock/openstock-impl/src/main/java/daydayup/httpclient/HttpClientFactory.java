package daydayup.httpclient;

import java.io.IOException;
import java.util.Iterator;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientFactory {

	private static final Logger LOG = LoggerFactory.getLogger(HttpClientFactory.class);

	private boolean proxyEnabled;

	private String httpProxyHost;

	private int httpProxyPort;

	private long pause = 10 * 1000;

	private HttpClientFactory() {

	}

	public static HttpClientFactory newInstance() {
		return new HttpClientFactory();
	}

	public HttpClientFactory setProxy(String host, int port) {
		this.proxyEnabled = true;
		this.httpProxyHost = host;
		this.httpProxyPort = port;
		return this;
	}

	public long getPause() {
		return pause;
	}

	public void setPauseInterval(long pause) {
		this.pause = pause;
	}

	public void get(String host, Iterator<String> uriIt, HttpResponseCallback hep) {
		try {
			this.doGet(host, uriIt, hep);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void doGet(String host, Iterator<String> uriIt, HttpResponseCallback hep) throws IOException {
		CloseableHttpClient httpclient = HttpClients.custom().build();
		try {

			HttpHost target = new HttpHost(host, 80, "http");
			RequestConfig.Builder cb = RequestConfig.custom();
			if (this.proxyEnabled) {

				HttpHost proxy = new HttpHost(httpProxyHost, httpProxyPort);
				cb.setProxy(proxy);
			}

			RequestConfig config = cb.build();
			long lastResponseTs = 0;
			while (uriIt.hasNext()) {

				String uri = uriIt.next();

				while (true) {
					long pass = System.currentTimeMillis() - lastResponseTs;
					long remain = pause - pass;
					if (remain <= 0) {
						break;
					}
					try {
						LOG.info("wait " + remain + "ms before next request");//
						Thread.sleep(remain);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}

				HttpGet httpget = new HttpGet(uri);
				httpget.setConfig(config);

				LOG.info("executing request url: " + target + httpget.getRequestLine()
						+ (this.proxyEnabled ? (" via proxy:" + this.httpProxyHost) : " via no proxy."));

				CloseableHttpResponse response = httpclient.execute(target, httpget);
				try {
					hep.onResponse(response);//
				} finally {
					response.close();
				}
				lastResponseTs = System.currentTimeMillis();

			}
		} finally {
			httpclient.close();
		}
	}

}
