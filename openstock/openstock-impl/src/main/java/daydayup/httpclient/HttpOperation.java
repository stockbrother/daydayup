package daydayup.httpclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;

public interface HttpOperation {
	public void execute(CloseableHttpClient client, RequestConfig config);
}
