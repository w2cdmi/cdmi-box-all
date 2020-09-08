package com.huawei.sharedrive.cloudapp.client.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import pw.cdmi.box.disk.client.domain.user.RestAccountConfigList;
import pw.cdmi.box.disk.client.domain.user.RestUserConfigList;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

public class UserConfigClient
{
    
	
    private RestClient ufmClientService;
    
    public UserConfigClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public RestUserConfigList getConfig(long userId, String name, String token) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_USER);
        uri.append('/');
        uri.append(userId);
        uri.append("/attributes?name="); 
        uri.append(name);
        
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        
        TextResponse response = ufmClientService.performGetText(uri.toString(), headers);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), RestUserConfigList.class);
    }
    
    public RestAccountConfigList getAccountConfig(long userId, String tag, String token,RestClient uamClientService) throws RestException
    {
        StringBuffer uri = new StringBuffer();
        uri.append(Constants.RESOURCE_ACCOUNTS);
        uri.append('/');
        uri.append(userId);
        uri.append("/config?tag="); 
        uri.append(tag);
        
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", token);
        
        TextResponse response = uamClientService.performGetText(uri.toString(), headers);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), RestAccountConfigList.class);
    }
    
}
