package bg.statealerts.scraper

import com.gargoylesoftware.htmlunit.HttpWebConnection
import org.apache.http.impl.client.AbstractHttpClient
import com.gargoylesoftware.htmlunit.WebClient
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.apache.http.impl.client.HttpClientBuilder

class RetryableWebConnection(webClient: WebClient) extends HttpWebConnection(webClient) {
  override def createHttpClient() : HttpClientBuilder  = {
	val builder= super.createHttpClient();
    builder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, false));
    builder
  }
}