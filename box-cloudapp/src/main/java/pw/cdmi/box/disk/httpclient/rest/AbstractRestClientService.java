package pw.cdmi.box.disk.httpclient.rest;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.httpclient.exception.ClientException;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.httpclient.rest.common.HttpErrorCode;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.utils.JsonUtils;

public abstract class AbstractRestClientService
{
    protected final static HttpClientConfiguration CONFIG = new HttpClientConfiguration();
    
    protected final static CloseableHttpClient HTTP_CLIENT = RestHttpClient.getInstance(CONFIG);
    
    private static Logger logger = LoggerFactory.getLogger(AbstractRestClientService.class);
    
    public String executeDelete(String apiPath, Object requestBody) throws ClientException
    {
        return executeJson(BasicConstants.HTTP_DELETE, apiPath, requestBody);
    }
    
    public <T> T executeDelete(String apiPath, Object requestBody, Class<T> clazz) throws ClientException
    {
        String resultString = executeJson(BasicConstants.HTTP_DELETE, apiPath, requestBody);
        return JsonUtils.stringToObject(resultString, clazz);
    }
    
    public String executePost(String apiPath, Object requestBody) throws ClientException
    {
        return executeJson(Constants.HTTP_POST, apiPath, requestBody);
    }
    
    public <T> T executePost(String apiPath, Object requestBody, Class<T> clazz) throws ClientException
    {
        String resultString = executeJson(Constants.HTTP_POST, apiPath, requestBody);
        return JsonUtils.stringToObject(resultString, clazz);
    }
    
    public String executePut(String apiPath, Object requestBody) throws ClientException
    {
        return executeJson(Constants.HTTP_PUT, apiPath, requestBody);
    }
    
    public <T> T executePut(String apiPath, Object requestBody, Class<T> clazz) throws ClientException
    {
        String resultString = executeJson(Constants.HTTP_PUT, apiPath, requestBody);
        return JsonUtils.stringToObject(resultString, clazz);
    }
    
    protected String executeGet(String apiPath, Object requestBody) throws ClientException
    {
        return executeJson(Constants.HTTP_GET, apiPath, requestBody);
    }
    
    protected <T> T executeGet(String apiPath, Object requestBody, Class<T> clazz) throws ClientException
    {
        String resultString = executeJson(Constants.HTTP_GET, apiPath, requestBody);
        return JsonUtils.stringToObject(resultString, clazz);
    }
    
    protected void executeObjStream()
    {
    }
    
    protected abstract String getServerUrl();
    
    protected UserToken getUserToken()
    {
        Subject subject = SecurityUtils.getSubject();
        Object obj = subject.getPrincipal();
        return (UserToken) obj;
    }
    
    private HttpRequestBase createHttpRequest(int requestType, String apiPath, Object requestBody)
        throws ClientException
    {
        String body = JsonUtils.toJson(requestBody);
        StringEntity se = new StringEntity(body, Consts.UTF_8);
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, Constants.JSON_TYPE));
        
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(CONFIG.getConnectionTimeout())
            .setConnectTimeout(CONFIG.getConnectionTimeout())
            .setSocketTimeout(CONFIG.getSocketTimeout())
            .build();
        
        switch (requestType)
        {
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
            default:
                throw new ClientException(HttpErrorCode.ERROR_HTTP_REQUEST_TYPE, "UnSupport Http Type :"
                    + requestType);
        }
    }
    
    @SuppressWarnings("PMD.PreserveStackTrace")
    private String executeJson(int requestType, String apiPath, Object requestBody) throws ClientException
    {
        CloseableHttpResponse response = null;
        String requestUri = apiPath;
        if (!apiPath.startsWith("http"))
        {
            requestUri = getServerUrl() + apiPath;
        }
        
        try
        {
            UserToken userToken = getUserToken();
            
            HttpRequestBase httpRequest = this.createHttpRequest(requestType, requestUri, requestBody);
            httpRequest.setHeader(HTTP.CONTENT_TYPE, Constants.JSON_TYPE);
            httpRequest.setHeader(Constants.HTTP_ACCEPT, Constants.JSON_TYPE);
            httpRequest.setHeader(Constants.HTTP_X_REAL_IP, userToken.getDeviceAddress());
            httpRequest.setHeader(Constants.HTTP_X_FORWARDED_FOR, userToken.getProxyAddress());
            httpRequest.setHeader(Constants.HTTP_AUTHOR, userToken.getToken());
            httpRequest.setProtocolVersion(HttpVersion.HTTP_1_1);
            
            response = HTTP_CLIENT.execute(httpRequest);
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200 && statusCode != 201)
            {
                logger.error("http invoke error:" + statusCode, bodyAsString);
                throw new BusinessException("http invoke error:" + statusCode);
            }
            return bodyAsString;
        }
        catch (ClientProtocolException e)
        {
            logger.error("Error when execute request with [" + apiPath + ']', e);
            throw new ClientException(HttpErrorCode.SERVER_ERROR);
        }
        catch (ParseException e)
        {
            logger.error("Error when execute request with [" + apiPath + ']', e);
            throw new ClientException(HttpErrorCode.SERVER_ERROR);
        }
        catch (Exception e)
        {
            logger.error("Error when execute request with [" + apiPath + ']', e);
            throw new ClientException(HttpErrorCode.SERVER_ERROR, e);
        }
        finally
        {
            IOUtils.closeQuietly(response);
        }
    }
}
