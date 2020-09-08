package pw.cdmi.box.disk.client.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.node.RestFavoriteLists;
import pw.cdmi.box.disk.favorite.domain.FavoriteNode;
import pw.cdmi.box.disk.favorite.domain.FavoriteNodeCreateRequest;
import pw.cdmi.box.disk.favorite.domain.FavoriteNodeUFM;
import pw.cdmi.box.disk.favorite.domain.ListFavoriteRequest;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class FavoriteClient
{
    public static final Logger LOGGER = LoggerFactory.getLogger(FavoriteClient.class);
    
    private RestClient ufmClientService;
    
    private UserTokenManager userTokenManager;
    
    public FavoriteClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    @PostConstruct
    void init()
    {
        userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
    }
    
    public FavoriteNode createFavoriteNode(FavoriteNodeCreateRequest favoriteNode, Long userid)
        throws RestException
    {
        
        String uri = Constants.RESOURCE_FAVORITE;
        
        Map<String, String> headerMap = getHeaderToken();
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri,
            headerMap,
            new FavoriteNodeUFM(favoriteNode));
        
        LOGGER.debug("CREATE   " + response.getStatusCode());
        if (response.getStatusCode() != HttpStatus.CREATED.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
        return JsonUtils.stringToObject(response.getResponseBody(), FavoriteNode.class);
    }
    
    public List<FavoriteNode> listFavoriteTreeNode(Long id) throws RestException
    {
        String uri = Constants.RESOURCE_FAVORITE + "/" + id + "/items";
        Map<String, String> headerMap = getHeaderToken();
        ListFavoriteRequest requst = new ListFavoriteRequest(1000, 0L);
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headerMap, requst);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestFavoriteLists list = JsonUtils.stringToObject(content, RestFavoriteLists.class);
            return list.getContents();
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
        
    }
    
    public boolean deleteFavoriteTreeNode(Long id)
    {
        String uri = Constants.RESOURCE_FAVORITE + "/" + id;
        Map<String, String> headerMap = getHeaderToken();
        TextResponse response = ufmClientService.performDelete(uri, headerMap);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return true;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    private Map<String, String> getHeaderToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", getUserTokenManager().getToken());
        return headers;
    }
    
    public UserTokenManager getUserTokenManager()
    {
        if (null == userTokenManager)
        {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }
    
    protected UserToken getCurrentUser()
    {
        return (UserToken) SecurityUtils.getSubject().getPrincipal();
    }
}
