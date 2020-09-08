package pw.cdmi.box.disk.client.api;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import pw.cdmi.box.disk.client.domain.node.*;
import pw.cdmi.box.disk.client.utils.RestConstants;
import pw.cdmi.box.disk.files.domain.ObjectSecretLevel;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.NoThumbNailException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.SpringContextUtil;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
public class FileClient
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileClient.class);
    private RestClient ufmClientService;
    
    private UserTokenManager userTokenManager;
    
    @PostConstruct
    void init()
    {
        userTokenManager = (UserTokenManager) SpringContextUtil.getBean("userTokenManager");
    }
    
    public FileClient(RestClient ufmClientService)
    {
        this.ufmClientService = ufmClientService;
    }
    
    public DownloadResponse getDownloadUrl(String token, Long ownerId, long fileId) throws RestException
    {
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(fileId).append("/url");
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);
        
        TextResponse response = ufmClientService.performGetText(sb.toString(), headerMap);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            DownloadResponse downloadUrl = JsonUtils.stringToObject(content, DownloadResponse.class);
            return downloadUrl;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    
    public DownloadResponse getWXDownloadUrl(String token, Long ownerId, long fileId) throws RestException
    {
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(fileId).append("/urlWx");
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);
        
        TextResponse response = ufmClientService.performGetText(sb.toString(), headerMap);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            DownloadResponse downloadUrl = JsonUtils.stringToObject(content, DownloadResponse.class);
            return downloadUrl;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    
    public DownloadResponse getDownloadUrlForHistory(String token, Long ownerId, long fileId) throws RestException
    {
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE_HISTORY);
        sb.append('/').append(ownerId).append('/').append(fileId).append("/url");
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);
        
        TextResponse response = ufmClientService.performGetText(sb.toString(), headerMap);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            DownloadResponse downloadUrl = JsonUtils.stringToObject(content, DownloadResponse.class);
            return downloadUrl;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    public DownloadResponse getUrlAndBrowse(String token, Long ownerId, long fileId) throws RestException
    {
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(fileId).append("/UrlAndBrowse");
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);
        
        TextResponse response = ufmClientService.performGetText(sb.toString(), headerMap);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            DownloadResponse downloadUrl = JsonUtils.stringToObject(content, DownloadResponse.class);
            return downloadUrl;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * 
     * @param userToken
     * @param ownerId
     * @param fileId
     * @param linkCode
     * @return
     * @throws RestException
     */
    public PreviewDownloadResponse getPreviewFileUrl(UserToken userToken, long ownerId, long fileId,
        String linkCode) throws RestException
    {
        Map<String, String> headers;
        if (StringUtils.isNotBlank(userToken.getLinkCode()))
        {
            headers = assembleLink(userToken.getLinkCode());
        }
        else
        {
            headers = assembleToken();
        }
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(fileId).append("/swfUrl");
        String path = sb.toString();
        
        TextResponse response = ufmClientService.performGetText(path, headers);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            PreviewDownloadResponse previewDownloadResponse = JsonUtils.stringToObject(response.getResponseBody(),
                PreviewDownloadResponse.class);
            return previewDownloadResponse;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param fileId
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    public String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId, String linkCode)
        throws BaseRunException, RestException
    {
        Map<String, String> headers;
        if (StringUtils.isNotBlank(user.getLinkCode()))
        {
            headers = assembleLink(user.getLinkCode());
        }
        else
        {
            headers = assembleToken();
        }
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(fileId).append("/url");
        String path = sb.toString();
        
        headers.put("x-usertoken", user.getToken());
        
        TextResponse response = ufmClientService.performGetText(path, headers);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            DownloadResponse downloadUrl = JsonUtils.stringToObject(response.getResponseBody(),
                DownloadResponse.class);
            String url = downloadUrl.getDownloadUrl();
            if (StringUtils.isBlank(url))
            {
                throw new NoThumbNailException();
            }
            return url;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    public RestFileInfo getFileInfo(UserToken user, Long ownerId, long fileId) throws RestException
    {
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(fileId);
        
        Map<String, String> headers;
        if (StringUtils.isNotBlank(user.getLinkCode()))
        {
            headers = assembleLink(user.getLinkCode());
        }
        else
        {
            headers = assembleToken();
        }
        TextResponse response = ufmClientService.performGetText(sb.toString(), headers);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestFileInfo fileInfo = JsonUtils.stringToObject(content, RestFileInfo.class);
            return fileInfo;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * @param token
     * @param ownerId
     * @param versionId
     * @return
     * @throws RestException
     */
    public RestFileInfo restoreVersion(String token, Long ownerId, long versionId) throws RestException
    {
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(versionId);
        sb.append("/restore");
        
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);
        TextResponse response = ufmClientService.performJsonPutTextResponse(sb.toString(), headerMap, null);
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestFileInfo fileInfo = JsonUtils.stringToObject(content, RestFileInfo.class);
            return fileInfo;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param fileId
     * @return
     * @throws BaseRunException
     */
    public String getFileThumbUrls(Long ownerId, Long fileId) throws RestException
    {
        Map<String, String> headers = assembleToken();
        String path = Constants.FILES_API_PATH + ownerId + '/' + fileId + Constants.DEFAULT_THUMB_SIZE_URL;
        TextResponse response = ufmClientService.performGetText(path, headers);
        ThumbnailUrl url = null;
        if (response.getStatusCode() == 200)
        {
            url = JsonUtils.stringToObject(response.getResponseBody(), ThumbnailUrl.class);
        }
        if (null == url)
        {
            return null;
        }
        return url.getThumbnailUrl();
    }
    
    public RestVersionLists listVersion(String token, Long ownerId, long fileId, Long offset, Integer limit)
        throws RestException
    {
        StringBuilder sb = new StringBuilder(BasicConstants.RESOURCE_FILE);
        sb.append('/')
            .append(ownerId)
            .append('/')
            .append(fileId)
            .append("/versions?offset=")
            .append(offset)
            .append("&limit=")
            .append(limit);
        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);
        
        TextResponse response = ufmClientService.performGetText(sb.toString(), headerMap);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestVersionLists versionList = JsonUtils.stringToObject(content, RestVersionLists.class);
            return versionList;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put(RestConstants.HEADER_AUTHORIZATION, getUserTokenManager().getToken());
        return headers;
    }
    
    protected Map<String, String> assembleLink(String linkCode)
    {
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        String accessCode = CommonTools.getAccessCode(linkCode);
        
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
        String authStr = "link," + linkCode + ',' + SignatureUtils.getSignature(accessCode, dateStr);
        headers.put(RestConstants.HEADER_AUTHORIZATION, authStr);
        headers.put(RestConstants.HEADER_DATE, dateStr);
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

    public PreviewMetaResponse getPreviewMeta(String token, Long ownerId,
        long fileId) throws RestException {
        FileClient.LOGGER.info("PreviewMeta token = {}.", token);

        StringBuilder sb = new StringBuilder(Constants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(fileId)
          .append("/previewMeta");

        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);

        FileClient.LOGGER.info("Send PreviewMeta to ufm.");

        TextResponse response = ufmClientService.performGetText(sb.toString(),
                headerMap);

        if (response.getStatusCode() == HttpStatus.OK.value()) {
            String content = response.getResponseBody();
            PreviewMetaResponse previewMeta = JsonUtils.stringToObject(content,
                    PreviewMetaResponse.class);

            return previewMeta;
        }

        RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
        throw exception;
    }

    //通过ownerId + nodeId来查询预览地址
    public PreviewUrlResponse getPreviewUrl(String token, long ownerId, long fileId) {
        FileClient.LOGGER.info("PreviewUrl token = {}.", token);

        StringBuilder sb = new StringBuilder(Constants.RESOURCE_FILE);
        sb.append('/').append(ownerId).append('/').append(fileId).append("/preview");

        Map<String, String> headerMap = new HashMap<String, String>(1);
        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);

        FileClient.LOGGER.info("Send PreviewUrl to ufm.");

        TextResponse response = ufmClientService.performGetText(sb.toString(), headerMap);

        if (response.getStatusCode() == HttpStatus.OK.value()) {
            String content = response.getResponseBody();
            return JsonUtils.stringToObject(content, PreviewUrlResponse.class);
        }

        throw JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
    }

    public ObjectSecretLevel getNodeSecretLevel(String token, long ownerId, long iNodeId) {
		// TODO Auto-generated method stub

        FileClient.LOGGER.info("getNodeSecretLevel token = {}.", token);

        StringBuilder sb = new StringBuilder(Constants.NODES_API_PATH);
        sb.append('/').append(ownerId).append('/').append(iNodeId)
          .append("/secretLevel");

//        Map<String, String> headerMap = new HashMap<String, String>(1);
        Map<String, String> headers = assembleToken();
//        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);

        FileClient.LOGGER.info("get secretLevel from ufm.");

        TextResponse response = ufmClientService.performGetText(sb.toString(),
        		headers);

        if (response.getStatusCode() == HttpStatus.OK.value()) {
            String content = response.getResponseBody();
            if("".equals(content)){
            	return null;
            }
            ObjectSecretLevel objectSecretLevel = JsonUtils.stringToObject(content,
            		ObjectSecretLevel.class);

            return objectSecretLevel;
        }

        RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
                RestException.class);
        throw exception;
    
	}

	public void updateFileSecretLevel(Long ownerId, Long iNodeId, String secretLevel,String token) {
		// TODO Auto-generated method stub
		 FileClient.LOGGER.info("getNodeSecretLevel token = {}.", token);

	        StringBuilder sb = new StringBuilder(Constants.NODES_API_PATH);
	        sb.append(ownerId).append('/').append(iNodeId)
	          .append("/secretLevel");

	        Map<String, String> headerMap = new HashMap<String, String>(2);
	        headerMap.put(RestConstants.HEADER_AUTHORIZATION, token);
	        headerMap.put("secretLevel", secretLevel);
	        FileClient.LOGGER.info("get secretLevel from ufm.");
	        TextResponse response = ufmClientService.performJsonPostTextResponse(sb.toString(), headerMap,null);
	        if (response.getStatusCode() == HttpStatus.OK.value()) {
	            String content = response.getResponseBody();
	            return ;
	        }

	        RestException exception = JsonUtils.stringToObject(response.getResponseBody(),
	                RestException.class);
	        throw exception;
	}
}
