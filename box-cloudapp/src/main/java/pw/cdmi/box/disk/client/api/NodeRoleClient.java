package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.teamspace.domain.RestNodeRoleInfo;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class NodeRoleClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeRoleClient.class);
    
    private RestClient ufmClientService;
    
    private static UserTokenManager userTokenManager;
    
    public static UserTokenManager getUserTokenManager()
    {
        if (null == userTokenManager)
        {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }
    
    public NodeRoleClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    @SuppressWarnings("unchecked")
    public List<RestNodeRoleInfo> getNodeRoles() throws RestException
    {
        Map<String, String> headerMap = assembleToken();
        TextResponse response = ufmClientService.performGetText(Constants.NODES_API_ROLES, headerMap);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            List<RestNodeRoleInfo> result = (List<RestNodeRoleInfo>) JsonUtils.stringToList(response.getResponseBody(),
                List.class,
                RestNodeRoleInfo.class);
            return result;
        }
        
        LOGGER.error("getNodeRoleInfo failure, response:" + response.getResponseBody());
        return null;
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put(RestConstants.HEADER_AUTHORIZATION, getUserTokenManager().getToken());
        return headers;
    }
}
