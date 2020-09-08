package pw.cdmi.box.disk.files.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.client.api.FileClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFileInfo;
import pw.cdmi.box.disk.client.domain.node.ThumbnailUrl;
import pw.cdmi.box.disk.files.domain.ObjectSecretLevel;
import pw.cdmi.box.disk.files.service.FileService;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

@Component
public class FileServiceImpl implements FileService
{
    private FileClient fileClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    /**
     * 
     * @param user
     * @param ownerId
     * @param fileId
     * @param linkCode
     * @return
     * @throws BaseRunException
     */
    @Override
    public String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId, String linkCode)
        throws BaseRunException, RestException
    {
        return fileClient.getFileDownloadUrl(user, ownerId, fileId, linkCode);
    }
    
    @Override
    public String getFileThumbUrls(UserToken user, Long ownerId, Long fileId) throws RestException
    {
        return fileClient.getFileThumbUrls(ownerId, fileId);
    }
    
    @Override
    public RestFileInfo getFileInfo(UserToken user, Long ownerId, Long fileId) throws RestException
    {
        return fileClient.getFileInfo(user, ownerId, fileId);
    }
    
    @Override
    public String getFileThumbUrls(UserToken user, INode node, String linkCode, String thumbNailUrl)
        throws BaseRunException
    {
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        if (StringUtils.isNotBlank(user.getLinkCode()))
        {
            String accessCode = CommonTools.getAccessCode(linkCode);
            
            String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
            String authStr = "link," + user.getLinkCode() + ','
                + SignatureUtils.getSignature(accessCode, dateStr);
            headers.put("Authorization", authStr);
            headers.put("Date", dateStr);
        }
        else
        {
            headers = assembleToken();
        }
        
        String path = Constants.FILES_API_PATH + node.getOwnedBy() + '/' + node.getId() + thumbNailUrl;
        TextResponse response = ufmClientService.performGetText(path, headers);
        if (response.getStatusCode() == 200)
        {
            
            return JsonUtils.stringToObject(response.getResponseBody(), ThumbnailUrl.class).getThumbnailUrl();
        }
        return null;
    }
    
    @PostConstruct
    void init()
    {
        this.fileClient = new FileClient(ufmClientService);
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(BusinessConstants.INITIAL_CAPACITIES);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }

	@Override
	public ObjectSecretLevel getNodeSecretLevel(String token, long ownerId, long iNodeId) {
		// TODO Auto-generated method stub
		ObjectSecretLevel objectSecretLevel=fileClient.getNodeSecretLevel(token,ownerId,iNodeId);
		return objectSecretLevel;
	}
}
