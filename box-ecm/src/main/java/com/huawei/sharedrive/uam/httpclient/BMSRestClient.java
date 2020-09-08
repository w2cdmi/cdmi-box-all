package com.huawei.sharedrive.uam.httpclient;

import java.io.InputStream;
import java.util.Map;

import com.huawei.sharedrive.uam.util.PropertiesUtils;

import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.StreamResponse;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;

public class BMSRestClient extends RestClient{
		private static String BMSSERVICE_URL = PropertiesUtils.getProperty("bms.client.url");
		
	    @Override
	    public TextResponse performDelete(String apiPath, Map<String, String> headers) throws ServiceException
	    {
	        String requestUri = BMSSERVICE_URL + apiPath;
	        return performDeleteByUri(requestUri, headers);
	    }
	    
	    @Override
	    public TextResponse performGetText(String apiPath, Map<String, String> headers) throws ServiceException
	    {
	        String requestUri = BMSSERVICE_URL + apiPath;
	        return performGetTextByUri(requestUri, headers);
	    }
	    
	    @Override
	    public TextResponse performJsonPostTextResponse(String apiPath, Map<String, String> headers,
	        Object requestBody) throws ServiceException
	    {
	        String requestUri = BMSSERVICE_URL + apiPath;
	        return performJsonPostTextResponseByUri(requestUri, headers, requestBody);
	    }
	    
	    @Override
	    public TextResponse performJsonPutTextResponse(String apiPath, Map<String, String> headers,
	        Object requestBody) throws ServiceException
	    {
	        String requestUri = BMSSERVICE_URL + apiPath;
	        return performJsonPutTextResponseByUri(requestUri, headers, requestBody);
	    }
	    
	    @Override
	    public StreamResponse performGetStream(String apiPath, Map<String, String> headers)
	        throws ServiceException
	    {
	        String requestUri = BMSSERVICE_URL + apiPath;
	        return performGetStreamByUri(requestUri, headers);
	    }
	    
	    @Override
	    public TextResponse performStreamPutTextResponse(String apiPath, Map<String, String> headers,
	        InputStream in, long length) throws ServiceException
	    {
	        String requestUri = BMSSERVICE_URL + apiPath;
	        return performStreamPutTextResponseByUri(requestUri, headers, in, length);
	    }
}
