package pw.cdmi.box.disk.share.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.client.api.ShareClient;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.files.service.FolderService;
import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.share.domain.INodeShare;
import pw.cdmi.box.disk.share.domain.INodeShareV2;
import pw.cdmi.box.disk.share.domain.SharePage;
import pw.cdmi.box.disk.share.domain.SharePageV2;
import pw.cdmi.box.disk.share.domain.UserType;
import pw.cdmi.box.disk.share.service.ShareToMeService;
import pw.cdmi.box.disk.user.domain.User;
import pw.cdmi.box.disk.user.service.UserTokenManager;
import pw.cdmi.box.disk.utils.BusinessConstants;
import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;
import pw.cdmi.box.domain.Pageable;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.exception.ForbiddenException;
import pw.cdmi.core.exception.NoSuchItemsException;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;

@Component
public class ShareToMeServiceImpl implements ShareToMeService
{
    private String apiPath = "/api/v2/";
    
    @Autowired
    private FolderService folderService;
    
    private ShareClient shareClient;
    
    @Resource
    private RestClient ufmClientService;
    
    @Autowired
    private UserTokenManager userTokenManager;
    
    @Override
    public void cancelShare(UserToken user, long ownerId, long iNodeId, byte userType) throws RestException
    {
        INode dbNode = folderService.getNodeInfo(user, ownerId, iNodeId);
        if (null == dbNode)
        {
            throw new NoSuchItemsException();
        }
        StringBuilder path = new StringBuilder();
        path.append(apiPath)
            .append("shareships/")
            .append(dbNode.getOwnedBy())
            .append('/')
            .append(iNodeId)
            .append("?userId=")
            .append(user.getCloudUserId())
            .append("&type=")
            .append(getUserType(userType));
        TextResponse response = ufmClientService.performDelete(path.toString(), assembleToken());
        if (response.getStatusCode() != HttpStatus.OK.value())
        {
            throw new BusinessException();
        }
        
    }
    
    private String getUserType(byte userType)
    {
        if (userType == User.USER_TYPE_USER)
        {
            return Constants.SPACE_TYPE_USER;
        }
        
        return Constants.SPACE_TYPE_GROUP;
    }
    
    @Override
    public Page<INodeShare> listShareToMe(UserToken user, String name, long sharedUserId, Pageable pageRequest)
        throws RestException
    {
        
        SharePageV2 rc = shareClient.getShareToMeResource(name, pageRequest);
        if (null != rc)
        {
            List<INodeShare> list = new ArrayList<INodeShare>(BusinessConstants.INITIAL_CAPACITIES);
            List<INodeShareV2> listV2 = rc.getContents();
            INodeShare nodeShare;
            for (INodeShareV2 isV2 : listV2)
            {
                nodeShare = new INodeShare();
                nodeShare.setCreatedBy(isV2.getCreatedBy());
                nodeShare.setModifiedAt(isV2.getModifiedAt());
                nodeShare.setModifiedBy(isV2.getModifiedBy());
                nodeShare.setName(isV2.getName());
                nodeShare.setOwnerId(isV2.getOwnerId());
                nodeShare.setOwnerLoginName(isV2.getOwnerLoginName());
                nodeShare.setOwnerName(isV2.getOwnerName());
                nodeShare.setRoleName(isV2.getRoleName());
                nodeShare.setSharedUserEmail(isV2.getSharedUserEmail());
                nodeShare.setSharedUserId(isV2.getSharedUserId());
                nodeShare.setSharedUserName(isV2.getSharedUserName());
                nodeShare.setSharedUserLoginName(isV2.getSharedUserLoginName());
                nodeShare.setSharedUserType(isV2.getSharedUserType());
                nodeShare.setSize(isV2.getSize());
                nodeShare.setStatus(isV2.getStatus());
                nodeShare.setiNodeId(isV2.getiNodeId());
                nodeShare.setSharedDepartment(isV2.getSharedUserDescrip());
                nodeShare.setThumbnailUrlList(isV2.getThumbnailUrlList());
                nodeShare.setType(isV2.getType());
                if (StringUtils.equals(isV2.getExtraType(), INode.TYPE_BACKUP_COMPUTER_STR))
                {
                    nodeShare.setType(INode.TYPE_BACKUP_COMPUTER);
                }
                else if (StringUtils.equals(isV2.getExtraType(), INode.TYPE_BACKUP_DISK_STR))
                {
                    nodeShare.setType(INode.TYPE_BACKUP_DISK);
                }
                nodeShare.setPreviewable(isV2.getPreviewable());
                list.add(nodeShare);
            }
            
            return new PageImpl<INodeShare>(list, pageRequest, rc.getTotalCount());
        }
        
        return null;
        
    }
    
    private byte getUserType(String userType)
    {
        if (Constants.SPACE_TYPE_GROUP.equals(userType))
        {
            return UserType.TYPE_GROUP;
        }
        
        return UserType.TYPE_USER;
    }
    
    @Override
    public SharePage listShareToMeForClient(UserToken user, long sharedUserId, Pageable pageRequest)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void validatePermission(UserToken user, INode node, String oper) throws ForbiddenException
    {
        // TODO Auto-generated method stub
        
    }
    
    @PostConstruct
    void init()
    {
        this.shareClient = new ShareClient(ufmClientService);
    }
    
    private Map<String, String> assembleToken()
    {
        Map<String, String> headers = new HashMap<String, String>(1);
        headers.put("Authorization", userTokenManager.getToken());
        return headers;
    }
}
