package daydayup.httpclient;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;

public interface HttpResponseCallback {
	public void onResponse(CloseableHttpResponse response) throws IOException;
}
