package pw.cdmi.box.disk.files.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.client.api.FolderClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.client.domain.node.RestFolderInfo;
import pw.cdmi.box.disk.client.domain.node.RestFolderLists;
import pw.cdmi.box.disk.client.domain.node.basic.BasicNodeListRequest;
import pw.cdmi.box.disk.files.service.FolderService;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BasicConstants;
import pw.cdmi.box.disk.utils.CommonTools;
import pw.cdmi.box.disk.utils.FilesCommonUtils;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.exception.BaseRunException;
import pw.cdmi.core.exception.NoSuchItemsException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

@Component
public class FolderServiceImpl implements FolderService
{
    
    private FolderClient folderClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @Override
    public INode getNodeInfo(UserToken user, long ownerId, long nodeID) throws RestException
    {
        RestFolderInfo restFolderInfo = folderClient.getNodeInfo(user, ownerId, nodeID);
        INode iNode = new INode(restFolderInfo);
        return iNode;
    }
    
    @Override
    public INode getNodeInfoCheckType(UserToken user, long ownerId, long nodeID, int type)
        throws RestException
    {
        INode fNode = getNodeInfo(user, ownerId, nodeID);
        if (fNode.getStatus() != INode.STATUS_NORMAL)
        {
            throw new NoSuchItemsException();
        }
        
        if (fNode.getType() == INode.TYPE_VERSION)
        {
            throw new NoSuchItemsException();
        }
        
        if (INode.TYPE_ALL != type)
        {
            if (INode.TYPE_FOLDER_ALL == type)
            {
                if (!FilesCommonUtils.isFolderType(fNode.getType()))
                {
                    throw new NoSuchItemsException();
                }
            }
            else
            {
                if (fNode.getType() != type)
                {
                    throw new NoSuchItemsException();
                }
            }
        }
        
        return fNode;
    }
    
    @Override
    public RestFolderLists listNodesbyFilter(BasicNodeListRequest listFolderRequest, long ownerId,
        UserToken user, Long folderId) throws BaseRunException, RestException
    {
        String uri = BasicConstants.RESOURCE_FOLDER + '/' + ownerId + '/' + folderId + "/items";
        Map<String, String> headers = new HashMap<String, String>(2);
        if (StringUtils.isNotBlank(user.getLinkCode()))
        {
            String acessCode = CommonTools.getAccessCode(user.getLinkCode());
            String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
            String authStr = "link," + user.getLinkCode() + ','
                + SignatureUtils.getSignature(acessCode, dateStr);
            headers.put("Authorization", authStr);
            headers.put("Date", dateStr);
        }
        else
        {
            headers = assembleToken();
        }
        
        TextResponse response = ufmClientService.performJsonPostTextResponse(uri, headers, listFolderRequest);
        
        if (response.getStatusCode() == HttpStatus.OK.value())
        {
            String content = response.getResponseBody();
            RestFolderLists result = JsonUtils.stringToObject(content, RestFolderLists.class);
            if (result != null)
            {
                result.transType();
            }
            return result;
        }
        RestException exception = JsonUtils.stringToObject(response.getResponseBody(), RestException.class);
        throw exception;
    }
    
    @PostConstruct
    void init()
    {
        this.folderClient = new FolderClient(ufmClientService);
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }
}
