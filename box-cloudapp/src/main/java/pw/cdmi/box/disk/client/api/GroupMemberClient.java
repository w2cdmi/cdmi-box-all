package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.group.domain.GroupMembershipsList;
import pw.cdmi.box.disk.group.domain.RestGroupMemberOrderRequest;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class GroupMemberClient
{
    private static final String BASE_PATH = "/api/v2/groups/";
    
    private RestClient ufmClientService;
    
    private static UserTokenManager userTokenManager;
    
    public GroupMemberClient()
    {
        
    }
    
    public GroupMemberClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public GroupMembershipsList getUserList(Long groupId, RestGroupMemberOrderRequest groupOrder)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(BASE_PATH);
        sb.append(groupId);
        sb.append("/memberships/items");
        Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performJsonPostTextResponse(sb.toString(),
            headers,
            groupOrder);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return JsonUtils.stringToObject(response.getResponseBody(), GroupMembershipsList.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    public void deleteMemberships(Long groupId, Long userId)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(BASE_PATH);
        sb.append(groupId);
        sb.append("/memberships/");
        sb.append(userId);
        Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performDelete(sb.toString(), headers);
        
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
    }
    
    public static UserTokenManager getUserTokenManager()
    {
        if (null == userTokenManager)
        {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", getUserTokenManager().getToken());
        return headers;
    }
}
