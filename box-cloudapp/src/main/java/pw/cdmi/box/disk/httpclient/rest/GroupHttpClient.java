package pw.cdmi.box.disk.httpclient.rest;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.httpclient.rest.request.GroupOrderRequest;
import pw.cdmi.box.disk.httpclient.rest.request.GroupOrderUserRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestGroupRequest;
import pw.cdmi.box.disk.httpclient.rest.response.GroupListResponse;
import pw.cdmi.box.disk.httpclient.rest.response.GroupUserListResponse;
import pw.cdmi.box.disk.httpclient.rest.response.RestGroupResponse;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

@Component
public class GroupHttpClient
{
    private static final String GROUP_URL = "/api/v2/groups";
    
    private static Logger logger = LoggerFactory.getLogger(GroupHttpClient.class);
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    public static String getGroupUrl()
    {
        return GROUP_URL;
    }
    
    public static Logger getLogger()
    {
        return logger;
    }
    
    public UserTokenManager getUserTokenManager()
    {
        if (null == userTokenManager)
        {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }
    
    public static void setLogger(Logger logger)
    {
        GroupHttpClient.logger = logger;
    }
    
    public void setUserTokenManager(UserTokenManager userTokenManager)
    {
        this.userTokenManager = userTokenManager;
    }
    
    @Autowired
    private RestClient ufmClientService;
    
    public GroupHttpClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    /**
     * 
     * @param request
     * @return
     */
    public RestGroupResponse createGroup(RestGroupRequest request)
    {
        Map<String, String> headers = assembleToken();
        
        String url = GROUP_URL;
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(url, headers, request);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_CREATED)
        {
            logger.error("create group fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        RestGroupResponse result = JsonUtils.stringToObject(responseStr, RestGroupResponse.class);
        return result;
        
    }
    
    /**
     * 
     * @param groupId
     * @return
     */
    public TextResponse deleteGroup(Long groupId)
    {
        Map<String, String> headers = assembleToken();
        
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append(groupId);
        
        TextResponse response = ufmClientService.performDelete(url.toString(), headers);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("delete group fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        return response;
    }
    
    /**
     * 
     * @param groupId
     * @return
     */
    public RestGroupResponse getGroupInfo(Long groupId)
    {
        Map<String, String> headers = assembleToken();
        
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append(groupId);
        TextResponse response = ufmClientService.performGetText(url.toString(), headers);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("get group information fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        RestGroupResponse result = JsonUtils.stringToObject(responseStr, RestGroupResponse.class);
        return result;
    }
    
    public RestClient getUfmClientService()
    {
        return ufmClientService;
    }
    
    /**
     * 
     * @param request
     * @return
     */
    public GroupListResponse listAllGroups(GroupOrderRequest orderRequest)
    {
        Map<String, String> headers = assembleToken();
        
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append("all");
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(url.toString(),
            headers,
            orderRequest);
        
        String responseStr = response.getResponseBody();
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("list all groups fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        GroupListResponse result = JsonUtils.stringToObject(responseStr, GroupListResponse.class);
        return result;
    }
    
    /**
     * 
     * @param orderRequest
     * @return
     */
    public GroupUserListResponse listUserGroups(GroupOrderUserRequest orderUserRequest)
    {
        Map<String, String> headers = assembleToken();
        
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append("items");
        TextResponse response = ufmClientService.performJsonPostTextResponse(url.toString(),
            headers,
            orderUserRequest);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("list user's groups fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        GroupUserListResponse result = JsonUtils.stringToObject(responseStr, GroupUserListResponse.class);
        return result;
    }
    
    /**
     * 
     * @param request
     * @param groupId
     * @return
     */
    public RestGroupResponse modifyGroup(RestGroupRequest request, Long groupId)
    {
        Map<String, String> headers = assembleToken();
        
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append(groupId);
        
        TextResponse response = ufmClientService.performJsonPutTextResponse(url.toString(), headers, request);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("modify group fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        RestGroupResponse result = JsonUtils.stringToObject(responseStr, RestGroupResponse.class);
        return result;
    }
    
    public void setUfmClientService(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", getUserTokenManager().getToken());
        return headers;
    }
    
    @PostConstruct
    void init()
    {
        userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
    }

	public boolean isGroupNameExist(RestGroupRequest request) 
	{
		Map<String, String> headers = assembleToken();
        
		StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append("checkname");
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(url.toString(), headers, request);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() == HttpStatus.SC_OK)
        {
            RestGroupResponse result = JsonUtils.stringToObject(responseStr, RestGroupResponse.class);
            return result != null;
        }
		return false;
	}
    
}
