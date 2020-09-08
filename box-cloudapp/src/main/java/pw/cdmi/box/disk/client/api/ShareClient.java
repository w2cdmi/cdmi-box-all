package pw.cdmi.box.disk.client.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.http.HttpStatus;

import pw.cdmi.box.disk.client.domain.node.Thumbnail;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.RequestListShareResourceRequest;
import pw.cdmi.box.disk.share.domain.SharePageV2;
import pw.cdmi.box.disk.share.service.impl.ShareOrderAdapter;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.domain.PageRequest;
import pw.cdmi.box.domain.Pageable;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

public class ShareClient
{
    private UserTokenManager userTokenManager;
    
    public UserTokenManager getUserTokenManager()
    {
        if (null == userTokenManager)
        {
            userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
        }
        return userTokenManager;
    }
    
    private final static String API_PATH = "/api/v2/";
    
    private final static String SHARE_TO_ME_PATH = "/api/v2/shares/received";
    
    private RestClient ufmClientService;
    
    public ShareClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    /**
     * cancel all share
     * 
     * @param user
     * @param ownerId
     * @param iNodeId
     * @throws BaseRunException
     */
    public void cancelAllShare(UserToken user, long ownerId, long iNodeId) throws BaseRunException
    {
        StringBuilder sb = new StringBuilder(API_PATH);
        sb.append("shareships/")
            .append(user.getCloudUserId())
            .append('/')
            .append(iNodeId)
            .append("?userId=")
            .append("&type=user");
        TextResponse response = ufmClientService.performDelete(sb.toString(), assembleToken());
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            throw new BusinessException();
        }
    }
    
    /**
     * delete share of relation
     * 
     * @param user
     * @param ownerId
     * @param sharedUserId
     * @param sharedUserType
     * @param iNodeId
     * @throws BaseRunException
     */
    public void deleteShare(UserToken user, long ownerId, long sharedUserId, String sharedUserType,
        long iNodeId) throws RestException
    {
        StringBuffer s = new StringBuffer();
        s.append(API_PATH + "shareships/")
            .append(user.getCloudUserId())
            .append('/')
            .append(iNodeId)
            .append("?userId=")
            .append(sharedUserId)
            .append("&type=")
            .append(sharedUserType);
        String path = s.toString();
        TextResponse response = ufmClientService.performDelete(path, assembleToken());
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
            throw exception;
        }
    }
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param iNodeId
     * @param pageRequest
     * @return
     * @throws RestException
     */
    public SharePageV2 getShareToMeResource(String name, Pageable pageRequest) throws RestException
    {
        int thumbNailSize = 2;
        RequestListShareResourceRequest rs = new RequestListShareResourceRequest();
        rs.setKeyword(name);
        rs.setOrder(ShareOrderAdapter.getOrderList(pageRequest.getOrder()));
        
        long pageNumber = pageRequest.getPageNumber();
        
        long offset = 0L;
        if (pageNumber > 0)
        {
            offset = (pageNumber - 1) * pageRequest.getPageSize();
        }
        rs.setLimit(pageRequest.getPageSize());
        rs.setOffset(offset);
        
        List<Thumbnail> thumbnailList = new ArrayList<Thumbnail>(thumbNailSize);
        Thumbnail smallThumb = new Thumbnail(Thumbnail.DEFAULT_SMALL_WIDTH, Thumbnail.DEFAULT_SMALL_HEIGHT);
        Thumbnail bigThumb = new Thumbnail(Thumbnail.DEFAULT_BIG_WIDTH, Thumbnail.DEFAULT_BIG_HEIGHT);
        thumbnailList.add(smallThumb);
        thumbnailList.add(bigThumb);
        rs.setThumbnail(thumbnailList);
        Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performJsonPostTextResponse(SHARE_TO_ME_PATH, headers, rs);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return JsonUtils.stringToObject(response.getResponseBody(), SharePageV2.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param iNodeId
     * @param pageRequest
     * @return
     * @throws BaseRunException
     */
    public SharePageV2 getShareUserList(UserToken user, long ownerId, long iNodeId, PageRequest pageRequest)
        throws RestException
    {
        
        long offset = 0L;
        long pageNumber = pageRequest.getPageNumber();
        if (pageRequest.getPageNumber() > 0)
        {
            offset = (pageNumber - 1) * pageRequest.getPageSize();
        }
        StringBuilder sb = new StringBuilder(API_PATH);
        sb.append("shareships/")
            .append(user.getCloudUserId())
            .append('/')
            .append(iNodeId)
            .append("?offset=")
            .append(offset)
            .append("&limit=")
            .append(100);
        Map<String, String> headers = assembleToken();
        TextResponse response = ufmClientService.performGetText(sb.toString(), headers);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            return JsonUtils.stringToObject(response.getResponseBody(), SharePageV2.class);
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    @PostConstruct
    void init()
    {
        userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", getUserTokenManager().getToken());
        return headers;
    }
}
