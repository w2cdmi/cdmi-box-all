package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.group.domain.GroupList;
import pw.cdmi.box.disk.group.domain.RestGroup;
import pw.cdmi.box.disk.group.domain.RestGroupOrderRequest;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class GroupClient
{
    
    private static final String BASE_PATH = "/api/v2/groups";
    
    private RestClient ufmClientService;
    
    private static UserTokenManager userTokenManager;
    
    public GroupClient()
    {
        
    }
    
    public GroupClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public GroupList getGroupList(String keyword, Long offset, Integer limit, String type, List<Order> order)
    {
        RestGroupOrderRequest groupOrder = new RestGroupOrderRequest(keyword, offset, limit, type, order);
        StringBuffer sb = new StringBuffer();
        sb.append(BASE_PATH).append("/all");
        Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performJsonPostTextResponse(sb.toString(),
            headers,
            groupOrder);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return JsonUtils.stringToObject(response.getResponseBody(), GroupList.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    public RestGroup getGroupInfo(Long groupId)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(BASE_PATH);
        sb.append('/');
        sb.append(groupId);
        Map<String, String> headers = assembleToken();
        TextResponse restResponse = ufmClientService.performGetText(sb.toString(), headers);
        if (restResponse.getStatusCode() == HttpStatus.OK.value())
        {
            return JsonUtils.stringToObject(restResponse.getResponseBody(), RestGroup.class);
        }
        RestException exception = JsonUtils.stringToObject(restResponse.getResponseBody(),
            RestException.class);
        throw exception;
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
