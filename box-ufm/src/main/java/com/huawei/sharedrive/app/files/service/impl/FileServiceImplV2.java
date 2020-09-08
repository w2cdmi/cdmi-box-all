/**
 * 文件夹service
 */
package com.huawei.sharedrive.app.files.service.impl;

import java.security.InvalidParameterException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.FilesNameConflictException;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.exception.NoSuchFileException;
import com.huawei.sharedrive.app.filelabel.service.IFileLabelService;
import com.huawei.sharedrive.app.filelabel.util.FilelabelUtils;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileServiceV2;
import com.huawei.sharedrive.app.files.service.NodeMessageService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;

import pw.cdmi.core.exception.InnerException;

/**
 * 文件相关操作业务实现类(V2)
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-5-12
 * @see
 * @since
 */
@Component
public class FileServiceImplV2 implements FileServiceV2
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImplV2.class);
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private NodeMessageService nodeMessageService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private IFileLabelService fileLabelService;
    
    @Override
    public INode getFileInfo(UserToken user, long ownerId, long fileId) throws BaseRunException
    {
        INode file = fileBaseService.getINodeInfo(ownerId, fileId);
        
        if (null == file)
        {
            LOGGER.error("Node not exist, owner id: {}, id: {}", ownerId, fileId);
            throw new NoSuchFileException("File not exist");
        }
        
        if (INode.STATUS_NORMAL != file.getStatus())
        {
            LOGGER.error("Node status abnormal, owner id: {}, id: {}, status: {}",
                ownerId,
                fileId,
                file.getStatus());
            throw new NoSuchFileException("File not exist");
        }
        
        if (INode.TYPE_FILE != file.getType() && INode.TYPE_VERSION != file.getType())
        {
            LOGGER.error("Invalid node type, owner id: {}, id: {}, type: {}", ownerId, fileId, file.getType());
            throw new NoSuchFileException("File not exist");
        }
        
        // 填充文件標簽信息
        FilelabelUtils.fillFilelabelForNode(user.getAccountId(), file, fileLabelService);
        iNodeACLService.vaildINodeOperACL(user, file, AuthorityMethod.GET_INFO.name());
        
        String[] logMsgs = new String[]{String.valueOf(ownerId), String.valueOf(file.getParentId())};
        String keyword = StringUtils.trimToEmpty(file.getName());
        
        // EventType无获取文件详细的枚举
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            file,
            null,
            UserLogType.GET_FILE,
            logMsgs,
            keyword);
        return file;
    }
    
    @Override
    public ThumbnailUrl getThumbnailUrl(UserToken userToken, INode node, Thumbnail thumbnail)
        throws BaseRunException
    {
        DataAccessURLInfo urlInfo = fileBaseService.getINodeInfoDownURL(node.getOwnedBy(), node);
        
        ThumbnailUrl thumbnailUrl = new ThumbnailUrl(urlInfo.getDownloadUrl()
            + FileBaseServiceImpl.getThumbnaliSuffix(thumbnail));
        
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()),
            String.valueOf(node.getParentId()),};
        String keyword = StringUtils.trimToEmpty(node.getName());
        
        fileBaseService.sendINodeEvent(userToken,
            EventType.OTHERS,
            node,
            null,
            UserLogType.GET_THUMBNAIL_URL_FILE,
            logMsgs,
            keyword);
        return thumbnailUrl;
    }
    
    @Override
    public String refreshUploadUrl(UserToken userToken, INode node, String uploadUrl) throws BaseRunException
    {
        // 暂不对url格式做校验
        String[] array = uploadUrl.split("/");
        if (array.length < 1)
        {
            throw new InvalidParameterException();
        }
        String objectId = array[array.length - 1];
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(userToken, node, AuthorityMethod.UPLOAD_OBJECT.name());
        
        UserToken token = userTokenHelper.createTokenDataServer(userToken.getId(),
            node.getObjectId(),
            AuthorityMethod.UPLOAD_OBJECT,
            node.getOwnedBy());
        int objectIdPos = uploadUrl.lastIndexOf("/");
        uploadUrl = uploadUrl.substring(0, objectIdPos);
        int tokenPos = uploadUrl.lastIndexOf("/");
        uploadUrl = uploadUrl.substring(0, tokenPos);
        
        StringBuffer newUrl = new StringBuffer(uploadUrl).append("/")
            .append(token.getToken())
            .append("/")
            .append(objectId);
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        
        fileBaseService.sendINodeEvent(token,
            EventType.OTHERS,
            node,
            null,
            UserLogType.REFRESH_UPLOAD_URL,
            logMsgs,
            keyword);
        return newUrl.toString();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode restoreFileVersion(UserToken userToken, INode versionNode) throws BaseRunException
    {
        // 获取文件当前节点
        INode parentNode = fileBaseService.getAndCheckNode(versionNode.getOwnedBy(),
            versionNode.getParentId(),
            INode.TYPE_FILE);
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(userToken, parentNode, AuthorityMethod.PUT_COPY.name());
        
        iNodeACLService.vaildINodeOperACL(userToken, versionNode, AuthorityMethod.GET_ALL.name());
        
        // 提前校验版本数
        fileBaseService.checkMaxVersions(parentNode.getOwnedBy(), parentNode.getId());
        
        // 重名检测
        if (fileBaseService.isSameNameNodeExistNoSelf(parentNode.getOwnedBy(),
            parentNode.getParentId(),
            parentNode.getId(),
            versionNode.getName()))
        {
            throw new FilesNameConflictException();
        }
        
        ObjectReference objectRef = new ObjectReference();
        objectRef.setId(versionNode.getObjectId());
        if (fileBaseService.increaseObjectRefCount(objectRef) <= 0)
        {
            throw new InternalServerErrorException("increaseObjectRefCount error,id:" + objectRef.getId());
        }
        
        long newVersionId = fileBaseService.buildINodeID(versionNode.getOwnedBy());
        int newVerNum = iNodeDAO.getINodeTotal(parentNode) + 1;
        
        // 将当前文件变成历史版本
        INode newNode = INode.valueOf(parentNode);
        newNode.setId(newVersionId);
        newNode.setParentId(parentNode.getId());
        newNode.setType(INode.TYPE_VERSION);
        
        // 当前待恢复版本的内容复制到当前节点
        long time = new Date().getTime() / 1000 * 1000;
        Date date = new Date(time);
        versionNode.setId(parentNode.getId());
        versionNode.setParentId(parentNode.getParentId());
        // 更新当前的版本数
        versionNode.setVersion(String.valueOf(newVerNum));
        versionNode.setType(INode.TYPE_FILE);
        versionNode.setCreatedAt(date);
        versionNode.setModifiedAt(date);
        // 更新共享外链及同步状态
        versionNode.setShareStatus(parentNode.getShareStatus());
        versionNode.setLinkCode(parentNode.getLinkCode());
        versionNode.setSyncStatus(parentNode.getSyncStatus());
        fileBaseService.setNodeSyncVersion(versionNode.getOwnedBy(), versionNode);
        // 通过交换Id达到交换内容目的
        iNodeDAO.create(newNode);
        
        // 如果更新失败，抛出异常，触发事务回滚
        if (iNodeDAO.update(versionNode) != 1)
        {
            LOGGER.error("update versionNode failed");
            throw new InnerException("update versionNode failed");
        }
        
        // 更新共享关系中的节点名称
        nodeMessageService.notifyShareToUpdateMsg(versionNode,userToken.getId());
        
        String keyword = StringUtils.trimToEmpty(versionNode.getName());
        String[] logMsgs = new String[]{String.valueOf(versionNode.getOwnedBy()),
            String.valueOf(versionNode.getId())};
        
        fileBaseService.sendINodeEvent(userToken,
            EventType.VERSION_RESTORE,
            versionNode,
            null,
            UserLogType.RESTORE_FILE_VERSION,
            logMsgs,
            keyword);
        
        return versionNode;
    }
    
}
