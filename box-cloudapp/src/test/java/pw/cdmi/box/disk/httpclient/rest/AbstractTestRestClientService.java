package pw.cdmi.box.disk.httpclient.rest;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.httpclient.exception.ClientException;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.common.HttpErrorCode;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.utils.JsonUtils;

public abstract class AbstractTestRestClientService {
	private static Logger logger = LoggerFactory.getLogger(AbstractRestClientService.class);

	protected static CloseableHttpClient httpClient;

	protected final static HttpClientConfiguration config;

	static {
		config = new HttpClientConfiguration();
		httpClient = RestHttpClient.getInstance(config);
	}

	protected <T> T executeGet(String apiPath, Object requestBody, Class<T> clazz) throws ClientException {
		String resultString = executeJson(Constants.HTTP_GET, apiPath, requestBody);
		return JsonUtils.stringToObject(resultString, clazz);
	}

	public <T> T executePost(String apiPath, Object requestBody, Class<T> clazz) throws ClientException {
		String resultString = executeJson(Constants.HTTP_POST, apiPath, requestBody);
		return JsonUtils.stringToObject(resultString, clazz);
	}

	public <T> T executePut(String apiPath, Object requestBody, Class<T> clazz) throws ClientException {
		String resultString = executeJson(Constants.HTTP_PUT, apiPath, requestBody);
		return JsonUtils.stringToObject(resultString, clazz);
	}

	public <T> T executeDelete(String apiPath, Object requestBody, Class<T> clazz) throws ClientException {
		String resultString = executeJson(Constants.HTTP_DELETE, apiPath, requestBody);
		return JsonUtils.stringToObject(resultString, clazz);
	}

	protected String executeGet(String apiPath, Object requestBody) throws ClientException {
		return executeJson(Constants.HTTP_GET, apiPath, requestBody);
	}

	public String executePost(String apiPath, Object requestBody) throws ClientException {
		return executeJson(Constants.HTTP_POST, apiPath, requestBody);
	}

	public String executePut(String apiPath, Object requestBody) throws ClientException {
		return executeJson(Constants.HTTP_PUT, apiPath, requestBody);
	}

	public String executeDelete(String apiPath, Object requestBody) throws ClientException {
		return executeJson(Constants.HTTP_DELETE, apiPath, requestBody);
	}

	protected UserToken getUserToken() {
		return new UserToken();
	}

	protected void executeObjStream() {
	}

	protected abstract String getServerUrl();

	// protected abstract Class<Object> getResultType();

	private String executeJson(int requestType, String apiPath, Object requestBody) throws ClientException {
		CloseableHttpResponse response = null;
		String requestUri = apiPath;
		if (!apiPath.startsWith("http")) {
			requestUri = getServerUrl() + apiPath;
		}

		try {
			HttpRequestBase httpRequest = this.createHttpRequest(requestType, requestUri, requestBody);
			httpRequest.setHeader(HTTP.CONTENT_TYPE, Constants.JSON_TYPE);
			httpRequest.setHeader(Constants.HTTP_ACCEPT, Constants.JSON_TYPE);
			httpRequest.setHeader(Constants.HTTP_AUTHOR, this.getUserToken().getToken());
			httpRequest.setProtocolVersion(HttpVersion.HTTP_1_1);

			response = httpClient.execute(httpRequest);
			String bodyAsString = EntityUtils.toString(response.getEntity());
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200 && statusCode != 201) {
				logger.error("http invoke error:" + statusCode, bodyAsString);
				throw new  BusinessException("http invoke error:" + statusCode);
			}
			return bodyAsString;
		} catch (Exception e) {
			logger.error("httpclient invoke rest api error:", e);
			throw new ClientException(HttpErrorCode.SERVER_ERROR, "Error when execute request with [" + apiPath + "]");
		} finally {
			IOUtils.closeQuietly(response);
		}
	}

	private HttpRequestBase createHttpRequest(int requestType, String apiPath, Object requestBody)
			throws ClientException {
		String body = JsonUtils.toJson(requestBody);
		StringEntity se = new StringEntity(body, Consts.UTF_8);
		se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, Constants.JSON_TYPE));

		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(config.getConnectionTimeout())
				.setConnectTimeout(config.getConnectionTimeout()).setSocketTimeout(config.getSocketTimeout()).build();

		switch (requestType) {
		case Constants.HTTP_GET:
			HttpGet httpGet = new HttpGet(apiPath);
			httpGet.setConfig(requestConfig);
			return httpGet;

		case Constants.HTTP_POST:
			HttpPost httpPost = new HttpPost(apiPath);
			httpPost.setEntity(se);
			httpPost.setConfig(requestConfig);
			return httpPost;

		case Constants.HTTP_PUT:
			HttpPut httpPut = new HttpPut(apiPath);
			httpPut.setEntity(se);
			httpPut.setConfig(requestConfig);
			return httpPut;

		case Constants.HTTP_DELETE:
			HttpDelete httpDelete = new HttpDelete(apiPath);
			httpDelete.setConfig(requestConfig);
			return httpDelete;
		}

		throw new ClientException(HttpErrorCode.ERROR_HTTP_REQUEST_TYPE, "UnSupport Http Type :" + requestType);
	}
}
