package com.huawei.sharedrive.isystem.httpclient;

import java.util.Map;

import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.StreamResponse;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;

public class BMSRestClient extends RestClient
{
    private String bmsInnerAddress;
	
    public String getBmsInnerAddress() {
		return bmsInnerAddress;
	}

	public void setBmsInnerAddress(String bmsInnerAddress) {
		this.bmsInnerAddress = bmsInnerAddress;
	}

	@Override
    public TextResponse performDelete(String apiPath, Map<String, String> headers) throws ServiceException
    {
        String requestUri = bmsInnerAddress + apiPath;
        return performDeleteByUri(requestUri, headers);
    }
    
    @Override
    public TextResponse performGetText(String apiPath, Map<String, String> headers) throws ServiceException
    {
        String requestUri = bmsInnerAddress + apiPath;
        return performGetTextByUri(requestUri, headers);
    }
    
    @Override
    public TextResponse performJsonPostTextResponse(String apiPath, Map<String, String> headers,
        Object requestBody) throws ServiceException
    {
        String requestUri = bmsInnerAddress + apiPath;
        return performJsonPostTextResponseByUri(requestUri, headers, requestBody);
    }
    
    @Override
    public StreamResponse performJsonPostStreamResponse(String apiPath, Map<String, String> headers,
        Object requestBody) throws ServiceException
    {
        String requestUri = bmsInnerAddress + apiPath;
        return performJsonPostStreamResponseByUri(requestUri, headers, requestBody);
    }
    
    @Override
    public TextResponse performJsonPutTextResponse(String apiPath, Map<String, String> headers,
        Object requestBody) throws ServiceException
    {
        String requestUri = bmsInnerAddress + apiPath;
        return performJsonPutTextResponseByUri(requestUri, headers, requestBody);
    }
    
    @Override
    public StreamResponse performGetStream(String apiPath, Map<String, String> headers)
        throws ServiceException
    {
        String requestUri = bmsInnerAddress + apiPath;
        return performGetStreamByUri(requestUri, headers);
    }
    
}
