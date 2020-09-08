package pw.cdmi.box.disk.httpclient.rest;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.group.domain.GroupMembershipsInfo;
import pw.cdmi.box.disk.httpclient.rest.request.GroupMemberOrderRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestAddGroupRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestModifyMemberRequest;
import pw.cdmi.box.disk.httpclient.rest.response.GroupUserListResponse;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class GroupMembershipsHttpClient
{
    private static final String GROUP_URL = "/api/v2/groups";
    
    private static Logger logger = LoggerFactory.getLogger(GroupMembershipsHttpClient.class);
    
    private static UserTokenManager userTokenManager;
    
    public static UserTokenManager getUserTokenManager()
    {
        if (null == userTokenManager)
        {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }
    
    private RestClient ufmClientService;
    
    public GroupMembershipsHttpClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    /**
     * 
     * @param groupId
     * @param request
     * @return
     * @throws RestException
     */
    public String addMemberships(Long groupId, RestAddGroupRequest request)
    {
        Map<String, String> headers = assembleToken();
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append(groupId);
        url.append('/');
        url.append("memberships");
        TextResponse resp = ufmClientService.performJsonPostTextResponse(url.toString(), headers, request);
        String respStr = resp.getResponseBody();
        if (resp.getStatusCode() == HttpStatus.SC_CREATED)
        {
            return String.valueOf(resp.getStatusCode());
        }
        RestException ex = JsonUtils.stringToObject(respStr, RestException.class);
        logger.error("add member fail httpcode:" + resp.getStatusCode() + ", errorcode: " + ex.getCode());
        return getErrorCodeFromResponse(resp);
    }
    
    /**
     * 
     * @param groupId
     * @param id
     * @throws RestException
     */
    public TextResponse deleteMember(Long groupId, Long id)
    {
        Map<String, String> headers = assembleToken();
        
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append(groupId);
        url.append('/');
        url.append("memberships");
        url.append('/');
        url.append(id);
        
        TextResponse response = ufmClientService.performDelete(url.toString(), headers);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("delete member fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        return response;
    }
    
    /**
     * 
     * @param request
     * @param groupId
     * @return
     */
    public GroupUserListResponse listMember(GroupMemberOrderRequest request, Long groupId)
    {
        Map<String, String> headers = assembleToken();
        
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append(groupId);
        url.append('/');
        url.append("memberships");
        url.append('/');
        url.append("items");
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(url.toString(), headers, request);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("list members fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        GroupUserListResponse result = JsonUtils.stringToObject(responseStr, GroupUserListResponse.class);
        return result;
    }
    
    /**
     * 
     * @param id
     * @param request
     * @param groupId
     * @return
     * @throws RestException
     */
    public GroupMembershipsInfo updateMemberGroupRole(Long id, RestModifyMemberRequest request, Long groupId)
    {
        Map<String, String> headers = assembleToken();
        
        StringBuffer url = new StringBuffer();
        url.append(GROUP_URL);
        url.append('/');
        url.append(groupId);
        url.append('/');
        url.append("memberships");
        url.append('/');
        url.append(id);
        
        TextResponse response = ufmClientService.performJsonPutTextResponse(url.toString(), headers, request);
        
        String responseStr = response.getResponseBody();
        
        if (response.getStatusCode() != HttpStatus.SC_OK)
        {
            logger.error("modify member fail code:" + response.getStatusCode());
            RestException exception = JsonUtils.stringToObject(responseStr, RestException.class);
            throw exception;
        }
        
        GroupMembershipsInfo result = JsonUtils.stringToObject(responseStr, GroupMembershipsInfo.class);
        return result;
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", getUserTokenManager().getToken());
        return headers;
    }
    
    private String getErrorCodeFromResponse(TextResponse response)
    {
        Map<String, Object> temp = JsonUtils.stringToMap(response.getResponseBody());
        return temp != null ? temp.get("code").toString() : null;
    }
}
