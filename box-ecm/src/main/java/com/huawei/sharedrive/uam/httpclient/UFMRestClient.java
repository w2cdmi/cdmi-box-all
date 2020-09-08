package com.huawei.sharedrive.uam.httpclient;

import java.io.InputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.uam.system.service.AccessAddressService;

import pw.cdmi.common.domain.AccessAddressConfig;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.StreamResponse;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.restrpc.exception.ServiceException;

public class UFMRestClient extends RestClient
{
    @Autowired
    private AccessAddressService accessAddressService;
    
    private String getUFMServicePath()
    {
        AccessAddressConfig address = accessAddressService.getAccessAddress();
        return address.getUfmInnerAddress();
    }
    
    @Override
    public TextResponse performDelete(String apiPath, Map<String, String> headers) throws ServiceException
    {
        String requestUri = getUFMServicePath() + apiPath;
        return performDeleteByUri(requestUri, headers);
    }
    
    @Override
    public TextResponse performGetText(String apiPath, Map<String, String> headers) throws ServiceException
    {
        String requestUri = getUFMServicePath() + apiPath;
        return performGetTextByUri(requestUri, headers);
    }
    
    @Override
    public TextResponse performJsonPostTextResponse(String apiPath, Map<String, String> headers,
        Object requestBody) throws ServiceException
    {
        String requestUri = getUFMServicePath() + apiPath;
        return performJsonPostTextResponseByUri(requestUri, headers, requestBody);
    }
    
    @Override
    public TextResponse performJsonPutTextResponse(String apiPath, Map<String, String> headers,
        Object requestBody) throws ServiceException
    {
        String requestUri = getUFMServicePath() + apiPath;
        return performJsonPutTextResponseByUri(requestUri, headers, requestBody);
    }
    
    @Override
    public StreamResponse performGetStream(String apiPath, Map<String, String> headers)
        throws ServiceException
    {
        String requestUri = getUFMServicePath() + apiPath;
        return performGetStreamByUri(requestUri, headers);
    }
    
    @Override
    public TextResponse performStreamPutTextResponse(String apiPath, Map<String, String> headers,
        InputStream in, long length) throws ServiceException
    {
        String requestUri = getUFMServicePath() + apiPath;
        return performStreamPutTextResponseByUri(requestUri, headers, in, length);
    }
}
