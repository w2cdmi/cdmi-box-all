
/**
 * 
 */

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Test;
import pw.cdmi.core.restrpc.RestClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author
 * 
 */

public class ExampleTest 
{
	@Test
	public void testJunit() 
	{
		System.out.println("简单测试LLT");
		Assert.assertEquals(4, 4);

		String url = "http://www.jmapi.cn/html/vi.docx/jmapi-token";
		try {
			System.out.println(URLEncoder.encode(url, "utf-8"));

			System.out.println(URLDecoder.decode("https%3A%2F%2Fobs.storbox.cn%2Fapi%2FB671647C7DC7D7F44981E3C1F04AA57006508DD9C930C4941927FCE3%2F9796ba5025b18455ceaf900924a83aa4%2F%25E3%2580%258A%25E8%2590%25A5%25E9%2594%2580%25E5%2588%2586%25E6%259E%2590%25E4%25B8%258E%25E5%2586%25B3%25E7%25AD%2596%25E3%2580%258B%25E2%2580%2594%25E2%2580%2594%25E7%25AC%25AC%25E5%259B%259B%25E7%25AB%25A0%2520%25E9%2580%25A0%25E5%258A%25BF.ppt", "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testFilePreview() {
		RestClient client = new RestClient();
		try {
			HttpGet httpRequest = new HttpGet("http://view.filepro.cn/view/AQGHPPwe");
			CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = httpClient.execute(httpRequest);
			System.out.println("Status Code:" + response.getStatusLine().getStatusCode());

			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = null;
			while ((line =reader.readLine()) != null) {
				System.out.println(line);
			}
			/*SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(initKeyManagers(), initTrustManagers(), null);
			ConnectionSocketFactory httpsFactory = new SSLConnectionSocketFactory(sslContext, enabledProtocols,
					cipherSuites, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.INSTANCE)
					.register("https", httpsFactory)
					.build();
			PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
			connectionManager.setDefaultSocketConfig(socketConfig);
			connectionManager.setMaxTotal(maxTotal);
			connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
			httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

			httpClient*/
/*
			System.out.println(response.getResponseBody());
*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
