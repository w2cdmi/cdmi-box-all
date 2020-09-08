package pw.cdmi.box.disk.wxrobot.util;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import blade.kit.json.JSONObject;
import pw.cdmi.box.disk.client.domain.node.FilePreUploadRequest;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.httpclient.rest.request.RestLoginResponse;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @web http://www.mobctrl.net
 * @author Zheng Haibo
 * @Description: 文件下载 POST GET
 */
public class HttpClientUtils {

	private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);
	private static final String DEFAULT_CHARSET = "UTF-8";// 默认请求编码
	private static final int DEFAULT_SOCKET_TIMEOUT = 5000;// 默认等待响应时间(毫秒)
	private static final int DEFAULT_RETRY_TIMES = 0;// 默认执行重试的次数
	private static final String BOUNDARYSTR = "XMKSSS***********";
	private static final String BOUNDARY = "--" + BOUNDARYSTR + "\r\n";

	/**
	 * 创建一个默认的可关闭的HttpClient
	 *
	 * @return
	 */
	public static CloseableHttpClient createHttpClient() {
		return createHttpClient(DEFAULT_RETRY_TIMES, DEFAULT_SOCKET_TIMEOUT);
	}

	/**
	 * 创建一个可关闭的HttpClient
	 *
	 * @param socketTimeout
	 *            请求获取数据的超时时间
	 * @return
	 */
	public static CloseableHttpClient createHttpClient(int socketTimeout) {
		return createHttpClient(DEFAULT_RETRY_TIMES, socketTimeout);
	}

	/**
	 * 创建一个可关闭的HttpClient
	 *
	 * @param socketTimeout
	 *            请求获取数据的超时时间
	 * @param retryTimes
	 *            重试次数，小于等于0表示不重试
	 * @return
	 */
	public static CloseableHttpClient createHttpClient(int retryTimes, int socketTimeout) {
		Builder builder = RequestConfig.custom();
		builder.setConnectTimeout(5000);// 设置连接超时时间，单位毫秒
		builder.setConnectionRequestTimeout(1000);// 设置从connect
													// Manager获取Connection
													// 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
		if (socketTimeout >= 0) {
			builder.setSocketTimeout(socketTimeout);// 请求获取数据的超时时间，单位毫秒。
													// 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
		}
		RequestConfig defaultRequestConfig = builder.setCookieSpec(CookieSpecs.STANDARD_STRICT)
				.setExpectContinueEnabled(true)
				.setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
				.setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
		// 开启HTTPS支持
		enableSSL();
		// 创建可用Scheme
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
		// 创建ConnectionManager，添加Connection配置信息
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				socketFactoryRegistry);
		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		if (retryTimes > 0) {
			setRetryHandler(httpClientBuilder, retryTimes);
		}
		CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(defaultRequestConfig).build();
		return httpClient;
	}

	/**
	 * 执行GET请求
	 *
	 * @param url
	 *            远程URL地址
	 * @param charset
	 *            请求的编码，默认UTF-8
	 * @param socketTimeout
	 *            超时时间（毫秒）
	 * @return HttpResult
	 * @throws IOException
	 */
	public static HttpResult executeGet(String url, String charset, int socketTimeout) throws IOException {
		CloseableHttpClient httpClient = createHttpClient(socketTimeout);
		return executeGet(httpClient, url, null, null, charset, true);
	}

	/**
	 * 执行GET请求
	 *
	 * @param url
	 *            远程URL地址
	 * @param charset
	 *            请求的编码，默认UTF-8
	 * @param socketTimeout
	 *            超时时间（毫秒）
	 * @return String
	 * @throws IOException
	 */
	public static String executeGetString(String url, String charset, int socketTimeout) throws IOException {
		CloseableHttpClient httpClient = createHttpClient(socketTimeout);
		return executeGetString(httpClient, url, null, null, charset, true);
	}

	/**
	 * 执行HttpGet请求
	 *
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求的远程地址
	 * @param referer
	 *            referer信息，可传null
	 * @param cookie
	 *            cookies信息，可传null
	 * @param charset
	 *            请求编码，默认UTF8
	 * @param closeHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 * @return HttpResult
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResult executeGet(CloseableHttpClient httpClient, String url, String referer, String cookie,
			String charset, boolean closeHttpClient) throws IOException {
		CloseableHttpResponse httpResponse = null;
		try {
			charset = getCharset(charset);
			httpResponse = executeGetResponse(httpClient, url, referer, cookie);
			// Http请求状态码
			Integer statusCode = httpResponse.getStatusLine().getStatusCode();
			String content = getResult(httpResponse, charset);
			return new HttpResult(statusCode, content);
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (closeHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param httpClient
	 *            httpclient对象
	 * @param url
	 *            执行GET的URL地址
	 * @param referer
	 *            referer地址
	 * @param cookie
	 *            cookie信息
	 * @return CloseableHttpResponse
	 * @throws IOException
	 */
	public static CloseableHttpResponse executeGetResponse(CloseableHttpClient httpClient, String url, String referer,
			String cookie) throws IOException {
		if (httpClient == null) {
			httpClient = createHttpClient();
		}
		HttpGet get = new HttpGet(url);
		if (cookie != null && !"".equals(cookie)) {
			get.setHeader("Cookie", cookie);
		}
		if (referer != null && !"".equals(referer)) {
			get.setHeader("referer", referer);
		}
		return httpClient.execute(get);
	}

	/**
	 * 执行HttpGet请求
	 *
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求的远程地址
	 * @param referer
	 *            referer信息，可传null
	 * @param cookie
	 *            cookies信息，可传null
	 * @param charset
	 *            请求编码，默认UTF8
	 * @param closeHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 * @return String
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String executeGetString(CloseableHttpClient httpClient, String url, String referer, String cookie,
			String charset, boolean closeHttpClient) throws IOException {
		CloseableHttpResponse httpResponse = null;
		try {
			charset = getCharset(charset);
			httpResponse = executeGetResponse(httpClient, url, referer, cookie);
			return getResult(httpResponse, charset);
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (closeHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 简单方式执行POST请求
	 *
	 * @param url
	 *            远程URL地址
	 * @param paramsObj
	 *            post的参数，支持map<String,String>,JSON,XML
	 * @param charset
	 *            请求的编码，默认UTF-8
	 * @param socketTimeout
	 *            超时时间(毫秒)
	 * @return HttpResult
	 * @throws IOException
	 */
	public static HttpResult executePost(String url, Object paramsObj, String charset, int socketTimeout)
			throws IOException {
		CloseableHttpClient httpClient = createHttpClient(socketTimeout);
		return executePost(httpClient, url, paramsObj, null, null, charset, true);
	}

	/**
	 * 简单方式执行POST请求
	 *
	 * @param url
	 *            远程URL地址
	 * @param paramsObj
	 *            post的参数，支持map<String,String>,JSON,XML
	 * @param charset
	 *            请求的编码，默认UTF-8
	 * @param socketTimeout
	 *            超时时间(毫秒)
	 * @return HttpResult
	 * @throws IOException
	 */
	public static String executePostString(String url, Object paramsObj, String charset, int socketTimeout)
			throws IOException {
		CloseableHttpClient httpClient = createHttpClient(socketTimeout);
		return executePostString(httpClient, url, paramsObj, null, null, charset, true);
	}

	/**
	 * 执行HttpPost请求
	 *
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求的远程地址
	 * @param paramsObj
	 *            提交的参数信息，目前支持Map,和String(JSON\xml)
	 * @param referer
	 *            referer信息，可传null
	 * @param cookie
	 *            cookies信息，可传null
	 * @param charset
	 *            请求编码，默认UTF8
	 * @param closeHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static HttpResult executePost(CloseableHttpClient httpClient, String url, Object paramsObj, String referer,
			String cookie, String charset, boolean closeHttpClient) throws IOException {
		CloseableHttpResponse httpResponse = null;
		try {
			charset = getCharset(charset);
			httpResponse = executePostResponse(httpClient, url, paramsObj, referer, cookie, charset);
			// Http请求状态码
			Integer statusCode = httpResponse.getStatusLine().getStatusCode();
			String content = getResult(httpResponse, charset);
			return new HttpResult(statusCode, content);
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if (closeHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 执行HttpPost请求
	 *
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param url
	 *            请求的远程地址
	 * @param paramsObj
	 *            提交的参数信息，目前支持Map,和String(JSON\xml)
	 * @param referer
	 *            referer信息，可传null
	 * @param cookie
	 *            cookies信息，可传null
	 * @param charset
	 *            请求编码，默认UTF8
	 * @param closeHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 * @return String
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String executePostString(CloseableHttpClient httpClient, String url, Object paramsObj, String referer,
			String cookie, String charset, boolean closeHttpClient) throws IOException {
		CloseableHttpResponse httpResponse = null;
		try {
			charset = getCharset(charset);
			httpResponse = executePostResponse(httpClient, url, paramsObj, referer, cookie, charset);
			return getResult(httpResponse, charset);
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if (closeHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param httpClient
	 *            HttpClient对象
	 * @param url
	 *            请求的网络地址
	 * @param paramsObj
	 *            参数信息
	 * @param referer
	 *            来源地址
	 * @param cookie
	 *            cookie信息
	 * @param charset
	 *            通信编码
	 * @return CloseableHttpResponse
	 * @throws IOException
	 */
	private static CloseableHttpResponse executePostResponse(CloseableHttpClient httpClient, String url,
			Object paramsObj, String referer, String cookie, String charset) throws IOException {
		if (httpClient == null) {
			httpClient = createHttpClient();
		}
		HttpPost post = new HttpPost(url);
		if (cookie != null && !"".equals(cookie)) {
			post.setHeader("Cookie", cookie);
		}
		if (referer != null && !"".equals(referer)) {
			post.setHeader("referer", referer);
		}
		// 设置参数
		HttpEntity httpEntity = getEntity(paramsObj, charset);
		if (httpEntity != null) {
			post.setEntity(httpEntity);
		}
		return httpClient.execute(post);
	}

	/**
	 * @param httpClient
	 *            HttpClient对象
	 * @param url
	 *            请求的网络地址
	 * @param paramsObj
	 *            参数信息
	 * @param referer
	 *            来源地址
	 * @param cookie
	 *            cookie信息
	 * @param charset
	 *            通信编码
	 * @return CloseableHttpResponse
	 * @throws IOException
	 */
	private static CloseableHttpResponse executePostResponse(CloseableHttpClient httpClient, String url,
			Map<String, Object> paramsObj, Map<String, Object> header, String charset) throws IOException {
		if (httpClient == null) {
			httpClient = createHttpClient();
		}
		HttpPost post = new HttpPost(url);
		for (Entry<String, Object> Entry : header.entrySet()) {
			post.setHeader(Entry.getKey(), Entry.getValue().toString());
		}

		// 设置参数
		HttpEntity httpEntity = getEntity(paramsObj, charset);
		if (httpEntity != null) {
			post.setEntity(httpEntity);
		}
		return httpClient.execute(post);
	}

	/**
	 * 执行文件上传
	 *
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param remoteFileUrl
	 *            远程接收文件的地址
	 * @param localFilePath
	 *            本地文件地址
	 * @param charset
	 *            请求编码，默认UTF-8
	 * @param closeHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResult executeUploadFile(CloseableHttpClient httpClient, String remoteFileUrl,
			String localFilePath, String charset) throws IOException {
		CloseableHttpResponse httpResponse = null;
		try {
			// 把文件转换成流对象FileBody
			File localFile = new File(localFilePath);
			FileBody fileBody = new FileBody(localFile);
			// 以浏览器兼容模式运行，防止文件名乱码。
			HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
					.addPart("uploadFile", fileBody).setCharset(CharsetUtils.get(charset)).build();
			HttpPost httpPost = new HttpPost(remoteFileUrl);
			httpPost.setEntity(reqEntity);

			RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(6000).setConnectionRequestTimeout(1000).setSocketTimeout(60000 * 30).build();
			httpPost.setConfig(requestConfig);
			httpResponse = httpClient.execute(httpPost);
			Integer statusCode = httpResponse.getStatusLine().getStatusCode();
			String content = getResult(httpResponse, charset);
			return new HttpResult(statusCode, content);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		} finally {
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 执行文件上传(以二进制流方式)
	 *
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param remoteFileUrl
	 *            远程接收文件的地址
	 * @param localFilePath
	 *            本地文件地址
	 * @param charset
	 *            请求编码，默认UTF-8
	 * @param closeHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static HttpResult executeUploadFileStream(CloseableHttpClient httpClient, String remoteFileUrl,
			String localFilePath, String charset, boolean closeHttpClient) throws ClientProtocolException, IOException {
		CloseableHttpResponse httpResponse = null;
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		try {
			if (httpClient == null) {
				httpClient = createHttpClient();
			}
			// 把文件转换成流对象FileBody
			File localFile = new File(localFilePath);
			fis = new FileInputStream(localFile);
			byte[] tmpBytes = new byte[1024];
			byte[] resultBytes = null;
			baos = new ByteArrayOutputStream();
			int len;
			while ((len = fis.read(tmpBytes, 0, 1024)) != -1) {
				baos.write(tmpBytes, 0, len);
			}
			resultBytes = baos.toByteArray();
			ByteArrayEntity byteArrayEntity = new ByteArrayEntity(resultBytes, ContentType.APPLICATION_OCTET_STREAM);
			HttpPost httpPost = new HttpPost(remoteFileUrl);
			httpPost.setEntity(byteArrayEntity);
			httpResponse = httpClient.execute(httpPost);
			Integer statusCode = httpResponse.getStatusLine().getStatusCode();
			String content = getResult(httpResponse, charset);
			return new HttpResult(statusCode, content);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
			if (httpResponse != null) {
				try {
					httpResponse.close();
				} catch (Exception e) {
				}
			}
			if (closeHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 执行文件下载
	 *
	 * @param httpClient
	 *            HttpClient客户端实例，传入null会自动创建一个
	 * @param remoteFileUrl
	 *            远程下载文件地址
	 * @param localFilePath
	 *            本地存储文件地址
	 * @param charset
	 *            请求编码，默认UTF-8
	 * @param closeHttpClient
	 *            执行请求结束后是否关闭HttpClient客户端实例
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static boolean executeDownloadFile(CloseableHttpClient httpClient, String remoteFileUrl,
			String localFilePath, boolean closeHttpClient) throws ClientProtocolException, IOException {
		CloseableHttpResponse response = null;
		InputStream in = null;
		FileOutputStream fout = null;
		try {
			HttpGet httpget = new HttpGet(remoteFileUrl);
			response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return false;
			}
			in = entity.getContent();
			File file = new File(localFilePath);
			fout = new FileOutputStream(file);
			int l;
			byte[] tmp = new byte[1024];
			while ((l = in.read(tmp)) != -1) {
				fout.write(tmp, 0, l);
			}
			// 将文件输出到本地
			fout.flush();
			EntityUtils.consume(entity);
			System.out.println(file.getAbsolutePath());
			return true;
		} finally {
			// 关闭低层流。
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception e) {
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
			if (closeHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 根据参数获取请求的Entity
	 *
	 * @param paramsObj
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static HttpEntity getEntity(Object paramsObj, String charset) throws UnsupportedEncodingException {
		if (paramsObj == null) {
			logger.info("当前未传入参数信息，无法生成HttpEntity");
			return null;
		}
		if (Map.class.isInstance(paramsObj)) {// 当前是map数据
			@SuppressWarnings("unchecked")
			Map<String, String> paramsMap = (Map<String, String>) paramsObj;
			List<NameValuePair> list = getNameValuePairs(paramsMap);
			UrlEncodedFormEntity httpEntity = new UrlEncodedFormEntity(list, charset);
			httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
			return httpEntity;
		} else if (String.class.isInstance(paramsObj)) {// 当前是string对象，可能是
			String paramsStr = (String) paramsObj;
			StringEntity httpEntity = new StringEntity(paramsStr, charset);
			if (paramsStr.startsWith("{")) {
				httpEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
			} else if (paramsStr.startsWith("<")) {
				httpEntity.setContentType(ContentType.APPLICATION_XML.getMimeType());
			} else {
				httpEntity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
			}
			return httpEntity;
		} else {
			logger.info("当前传入参数不能识别类型，无法生成HttpEntity");
		}
		return null;
	}

	/**
	 * 从结果中获取出String数据
	 *
	 * @param httpResponse
	 *            http结果对象
	 * @param charset
	 *            编码信息
	 * @return String
	 * @throws ParseException
	 * @throws IOException
	 */
	private static String getResult(CloseableHttpResponse httpResponse, String charset)
			throws ParseException, IOException {
		String result = null;
		if (httpResponse == null) {
			return result;
		}
		HttpEntity entity = httpResponse.getEntity();
		if (entity == null) {
			return result;
		}
		result = EntityUtils.toString(entity, charset);
		EntityUtils.consume(entity);// 关闭应该关闭的资源，适当的释放资源 ;也可以把底层的流给关闭了
		return result;
	}

	/**
	 * 转化请求编码
	 *
	 * @param charset
	 *            编码信息
	 * @return String
	 */
	private static String getCharset(String charset) {
		return charset == null ? DEFAULT_CHARSET : charset;
	}

	/**
	 * 将map类型参数转化为NameValuePair集合方式
	 *
	 * @param paramsMap
	 * @return
	 */
	private static List<NameValuePair> getNameValuePairs(Map<String, String> paramsMap) {
		List<NameValuePair> list = new ArrayList<>();
		if (paramsMap == null || paramsMap.isEmpty()) {
			return list;
		}
		for (Entry<String, String> entry : paramsMap.entrySet()) {
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return list;
	}

	/**
	 * 开启SSL支持
	 */
	private static void enableSSL() {
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] { manager }, null);
			socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SSLConnectionSocketFactory socketFactory;

	// HTTPS网站一般情况下使用了安全系数较低的SHA-1签名，因此首先我们在调用SSL之前需要重写验证方法，取消检测SSL。
	private static TrustManager manager = new X509TrustManager() {

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			//

		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			//

		}
	};

	/**
	 * 为httpclient设置重试信息
	 *
	 * @param httpClientBuilder
	 * @param retryTimes
	 */
	private static void setRetryHandler(HttpClientBuilder httpClientBuilder, final int retryTimes) {
		HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				if (executionCount >= retryTimes) {
					// Do not retry if over max retry count
					return false;
				}
				if (exception instanceof InterruptedIOException) {
					// Timeout
					return false;
				}
				if (exception instanceof UnknownHostException) {
					// Unknown host
					return false;
				}
				if (exception instanceof ConnectTimeoutException) {
					// Connection refused
					return false;
				}
				if (exception instanceof SSLException) {
					// SSL handshake exception
					return false;
				}
				HttpClientContext clientContext = HttpClientContext.adapt(context);
				HttpRequest request = clientContext.getRequest();
				boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
				if (idempotent) {
					// 如果请求被认为是幂等的，那么就重试
					// Retry if the request is considered idempotent
					return true;
				}
				return false;
			}
		};
		httpClientBuilder.setRetryHandler(myRetryHandler);
	}

	public static void dumpFile(String remoteFileUrl, String localTempFilePath, RestLoginResponse loginResp,
			String filename,String fileType, long fileSize, RestClient ufmClientService,String nikeName,String language) throws ClientProtocolException, IOException {
		try {
			if(fileSize==0L){
				return;
			}
			// 获取微信文件上传目录
			RestFolderInfo wxParentNode = getWxFolder(loginResp,fileType,nikeName,language, ufmClientService);
			// 获取预上传地址
			String remoteUploadUrl = getPreUploadUrl(loginResp, filename, fileSize, wxParentNode.getId(),
					ufmClientService);
			// 上传文件到服务器
			CloseableHttpClient uploadHttpClient = HttpClientUtils.createHttpClient(0, 5000);
			HttpResult httpResult = HttpClientUtils.executeUploadFile(uploadHttpClient, remoteUploadUrl,localTempFilePath, "utf-8");
//			HttpClientUtils.uploadFile(remoteUploadUrl, localTempFilePath);
			if (httpResult.getStatusCode() == 200) {
				new File(localTempFilePath).delete();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static boolean executeDownloadFile(CloseableHttpClient httpClient, String remoteFileUrl,
			String localFilePath, String charset, boolean closeHttpClient) throws ClientProtocolException, IOException {
		CloseableHttpResponse response = null;
		InputStream in = null;
		FileOutputStream fout = null;
		try {
			HttpGet httpget = new HttpGet(remoteFileUrl);
			response = httpClient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return false;
			}
			in = entity.getContent();
			File file = new File(localFilePath);
			fout = new FileOutputStream(file);
			int l;
			byte[] tmp = new byte[1024];
			while ((l = in.read(tmp)) != -1) {
				fout.write(tmp, 0, l);
				// 注意这里如果用OutputStream.write(buff)的话，图片会失真
			}
			// 将文件输出到本地
			fout.flush();
			EntityUtils.consume(entity);
			return true;
		} finally {
			// 关闭低层流。
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception e) {
				}
			}
			if (response != null) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
			if (closeHttpClient && httpClient != null) {
				try {
					httpClient.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static CloseableHttpResponse executePost(String url, Map<String, Object> parameter,
			Map<String, Object> header, String charset, int socketTimeout) throws IOException {
		CloseableHttpClient httpClient = createHttpClient(socketTimeout);
		return executePostResponse(httpClient, url, parameter, header, charset);
	}

	public static RestFolderInfo getWxFolder(RestLoginResponse loginResp,String fileType,String wxName,String language, RestClient ufmClientService) {

		// 获取预上传地址
		String uri = BasicConstants.RESOURCE_FOLDER + '/' + loginResp.getCloudUserId() + 
				"/getWxFolder?fileType="+fileType+"&wxName="+wxName+"&language="+language;
		Map<String, String> headerMap = new HashMap<>(1);
		headerMap.put("Authorization", loginResp.getToken());
		TextResponse response;
		int status;
		response = ufmClientService.performGetText(uri, headerMap);
		status = response.getStatusCode();
		if (status == HttpStatus.OK.value() || status == HttpStatus.CREATED.value()) {

			String content = response.getResponseBody();
			RestFolderInfo iNode = JsonUtils.stringToObject(content, RestFolderInfo.class);
			return iNode;
		}
		return null;
	}

	public static String getPreUploadUrl(RestLoginResponse loginResp, String filename, long fileSize, long parentId,
			RestClient ufmClientService) {
		// 获取预上传地址
		String uri = BasicConstants.RESOURCE_FILE + '/' + loginResp.getCloudUserId();
		Map<String, String> headerMap = new HashMap<>(1);
		headerMap.put("Authorization", loginResp.getToken());
		FilePreUploadRequest request = new FilePreUploadRequest(filename, parentId, fileSize);
		TextResponse response;
		int status;
		response = ufmClientService.performJsonPutTextResponse(uri, headerMap, request);
		status = response.getStatusCode();
		if (status == HttpStatus.OK.value()) {
			String content = response.getResponseBody();
			HashMap<String, Object> map = (HashMap<String, Object>) JsonUtils.stringToMap(content);
			String remoteUploadUrl = map.get("uploadUrl").toString() + "?objectLength=" + fileSize;
			return remoteUploadUrl;
		}
		return null;
	}

	public static void storeTempFile(String filePathName, InputStream inStream) throws Exception {
		File storeFile = new File(filePathName);
		FileOutputStream output = new FileOutputStream(storeFile);
		byte b[] = new byte[1024];
		int length = 0;
		while ((length = inStream.read(b)) != -1) {
			output.write(b, 0, length);
		}
		output.flush();
		output.close();

	}
	
	
	public static void downFile(JSONObject loginUser, String url, String filePathName) {
		try {
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(60 * 1000);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			/*conn.setRequestProperty("Content-Type", "image/jpeg");*/
			conn.setRequestProperty("Cookie", loginUser.getString("cookie"));
			InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
			storeTempFile(filePathName, inStream);// 得到图片的二进制数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void downImage(JSONObject loginUser, String url, String filePathName) {
		try {
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(60 * 1000);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			conn.setRequestProperty("Content-Type", "image/jpeg");
			conn.setRequestProperty("Cookie", loginUser.getString("cookie"));
			InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
			storeTempFile(filePathName, inStream);// 得到图片的二进制数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void downVedio(JSONObject loginUser, String url, String filePathName) {
		try {
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(60 * 1000);
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
			conn.setRequestProperty("Range", "bytes=0-");
			conn.setRequestProperty("Cookie", loginUser.getString("cookie"));
			InputStream inStream = conn.getInputStream();// 通过输入流获取图片数据
			storeTempFile(filePathName, inStream);// 得到图片的二进制数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	 @SuppressWarnings("finally")
	    public static String uploadFile(String actionUrl, String uploadFilePath) {
	        String end = "\r\n";
	        String twoHyphens = "--";
	        String boundary = "*****";

	        DataOutputStream ds = null;
	        InputStream inputStream = null;
	        InputStreamReader inputStreamReader = null;
	        BufferedReader reader = null;
	        StringBuffer resultBuffer = new StringBuffer();
	        String tempLine = null;

	        try {
	            // 统一资源
	            URL url = new URL(actionUrl);
	            // 连接类的父类，抽象类
	            URLConnection urlConnection = url.openConnection();
	            // http的连接类
	            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

	            // 设置是否从httpUrlConnection读入，默认情况下是true;
	            httpURLConnection.setDoInput(true);
	            // 设置是否向httpUrlConnection输出
	            httpURLConnection.setDoOutput(true);
	            // Post 请求不能使用缓存
	            httpURLConnection.setUseCaches(false);
	            // 设定请求的方法，默认是GET
	            httpURLConnection.setRequestMethod("POST");
	            // 设置字符编码连接参数
	            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
	            // 设置字符编码
	            httpURLConnection.setRequestProperty("Charset", "UTF-8");
	            // 设置请求内容类型
	            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

	            // 设置DataOutputStream
	            ds = new DataOutputStream(httpURLConnection.getOutputStream());
                String uploadFile =uploadFilePath;
                String filename = uploadFile.substring(uploadFile.lastIndexOf("//") + 1);
                ds.writeBytes(twoHyphens + boundary + end);
                ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + 0 + "\";filename=\"" + filename
                        + "\"" + end);
                ds.writeBytes(end);
                FileInputStream fStream = new FileInputStream(uploadFile);
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                while ((length = fStream.read(buffer)) != -1) {
                    ds.write(buffer, 0, length);
                }
                ds.writeBytes(end);
                /* close streams */
                fStream.close();
	            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
	            /* close streams */
	            ds.flush();
	            if (httpURLConnection.getResponseCode() >= 300) {
	                throw new Exception(
	                        "HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
	            }

	            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                inputStream = httpURLConnection.getInputStream();
	                inputStreamReader = new InputStreamReader(inputStream);
	                reader = new BufferedReader(inputStreamReader);
	                tempLine = null;
	                resultBuffer = new StringBuffer();
	                while ((tempLine = reader.readLine()) != null) {
	                    resultBuffer.append(tempLine);
	                    resultBuffer.append("\n");
	                }
	            }

	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        } finally {
	            if (ds != null) {
	                try {
	                    ds.close();
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	            if (inputStreamReader != null) {
	                try {
	                    inputStreamReader.close();
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	            if (inputStream != null) {
	                try {
	                    inputStream.close();
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }

	            return resultBuffer.toString();
	        }
	    }
}