package com.huawei.sharedrive.app.files.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.DCSimpleUrlManager;
import com.huawei.sharedrive.app.dataserver.url.URLReplaceTools;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.exception.ExceptionResponseEntity;
import com.huawei.sharedrive.app.exception.FilesNameConflictException;
import com.huawei.sharedrive.app.exception.ForbiddenException;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileServiceMulti;
import com.huawei.sharedrive.app.files.service.NodeUpdateService;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.FileMultiPreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadFailResponse;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponseV2;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.RandomGUID;
import pw.cdmi.uam.domain.AuthApp;

@Component("fileServiceMulti")
public class FileServiceMultiImpl extends FileServiceImpl implements FileServiceMulti
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceMultiImpl.class);
    
    private static final String URL_SPILT = "/";
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private DCSimpleUrlManager dcSimpleUrlManager;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private NodeUpdateService nodeUpdateService;
    
    @Autowired
    private PersistentEventManager persistentEventManager;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired(required = false)
    @Qualifier("urlReplaceTools")
    private URLReplaceTools urlReplaceTools;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Override
    public FileMultiPreUploadResponse preUploadFile(UserToken userToken, List<INode> fileNodeList,
        Long tokenTimeout) throws BaseRunException
    {
        INode parentNode = validateParameter(userToken, fileNodeList);
        Byte securityId = null;
        if (securityMatrixService.isSecurityMatrixEnable() && StringUtils.isNotEmpty(userToken.getToken()))
        {
            securityId = securityMatrixService.getFileSecurityId(userToken);
        }
        AuthApp authApp = authAppService.getByAuthAppID(userToken.getAppId());
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;
        ResourceGroup resourceGroup = dcManager.selectBestGroupRwNormal(userToken.getRegionId());
        if (null == resourceGroup)
        {
            String message = "No Support ResourceGroup For User [ " + userToken.getId() + ',' + userToken.getRegionId() + " ]";
            throw new InnerException(message);
        }
        String domain = resourceGroup.getDomainName();
        if (StringUtils.isBlank(resourceGroup.getDomainName()))
        {
            domain = dssDomainService.getOuterDomainByDssId(resourceGroup);
        }
        
        List<FilePreUploadResponseV2> uploadUrlList = new ArrayList<FilePreUploadResponseV2>(10);
        List<RestFileInfo> uploadedList = new ArrayList<RestFileInfo>(10);
        List<FilePreUploadFailResponse> failedList = new ArrayList<FilePreUploadFailResponse>(10);
        Set<String> currentNameSet = new HashSet<String>(10);
        List<INode> currentNodeList = iNodeDAO.getINodeByParentAndStatus(parentNode, null, null);
        for(INode tempNode: currentNodeList)
        {
            currentNameSet.add(tempNode.getName());
        }
        FilePreUploadFailResponse failResp;
        ExceptionResponseEntity exceptionRespEntity;
        for (INode file : fileNodeList)
        {
            try
            {
                preUploadOneFile(userToken, file, parentNode, uploadUrlList, uploadedList, tokenTimeout, securityId, qosPort, resourceGroup, domain, currentNameSet);
            }
            catch (BaseRunException e)
            {
                String message = "preupload failed For file [ " + file.getName() + ',' + file.getParentId()
                    + " ]";
                LOGGER.warn(message);
                failResp = new FilePreUploadFailResponse();
                exceptionRespEntity = new ExceptionResponseEntity(LoggerUtil.getCurrentLogID(), e);
                failResp.setException(exceptionRespEntity);
                failResp.setName(file.getName());
                failedList.add(failResp);
            }
        }
        return packResult(uploadUrlList, uploadedList, failedList);
    }

    private String buildObjectID(Long userId)
    {
        LOGGER.info("userId:" + userId);
        return new RandomGUID().getValueAfterMD5();
    }

    private boolean canBeFlashUpload(INode node)
    {
        // 兼容老客户端和老数据sha1值的闪传
        if (StringUtils.isNotBlank(node.getSha1()))
        {
            return true;
        }
        // 不大于256字节的文件只比较MD5值
        if (node.getSize() <= Constants.SAMPLING_LENGTH_FOR_SMALLER_FILE)
        {
            return StringUtils.isNotBlank(node.getMd5());
        }
        // 大于256字节的文件比较整文件MD5和取样MD5
        return StringUtils.isNotBlank(node.getMd5()) && StringUtils.isNotBlank(node.getBlockMD5());
    }
    
    // 同以往实现
    private PersistentEvent generalUploadEvent(INode fileNode)
    {
        PersistentEvent event = new PersistentEvent();
        event.setEventType(EventType.INODE_PRELOAD_END);
        event.setNodeId(fileNode.getId());
        event.setNodeName(fileNode.getName());
        event.setOwnedBy(fileNode.getOwnedBy());
        event.setParentId(fileNode.getParentId());
        event.setPriority(PersistentEvent.PRIORITY_NORMAL);
        event.addParameter("objectId", fileNode.getObjectId());
        event.setCreatedBy(fileNode.getCreatedBy());
        return event;
    }
    
    private INode getFileNodeFromList(List<INode> existNodes)
    {
        for (INode item : existNodes)
        {
            if (INode.TYPE_FILE == item.getType())
            {
                return item;
            }
        }
        return null;
    }
    
    private void handleFlashUpload(UserToken userToken, INode node, List<RestFileInfo> uploadedList,
        INode retNode)
    {
        FilesCommonUtils.setNodeVersionsForV2(retNode);
        RestFileInfo restFileInfo = new RestFileInfo(retNode, userToken.getDeviceType());
        transThumbnailUrlList(restFileInfo);
        uploadedList.add(restFileInfo);
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()),
            String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        // 发送日志
        fileBaseService.sendINodeEvent(userToken,
            EventType.INODE_PRELOAD_END,
            retNode,
            null,
            UserLogType.UPLOAD_QUICK,
            logMsgs,
            keyword);
        LOGGER.info("preUploadFile flash result [{}, {}]", restFileInfo.getId(), "");
        // 闪传文件发送事件
        PersistentEvent event = generalUploadEvent(retNode);
        persistentEventManager.fireEvent(event);
    }
    
    private INode handleSameNameFile(INode node, INode parentNode, Set<String> fileNameSet)
    {
        if(CollectionUtils.isEmpty(fileNameSet))
        {
            return parentNode;
        }
        // 检测文件夹下是否有重名文件(由于并发原因, 可能出现相同目录下存在多个同名文件的情况)
        
        if (fileNameSet.contains(node.getName()))
        {
            List<INode> existNodes = iNodeDAO.getNodeByName(node.getOwnedBy(), node.getParentId(), node.getName());
            INode fileNode = getFileNodeFromList(existNodes);
            // 如果存在同名文件, 作为版本处理, 默认选择集合的第一个元素作为版本文件
            if (fileNode != null)
            {
                parentNode = fileNode;
                node.setParentId(parentNode.getId());
                node.setType(INode.TYPE_VERSION);
            }
            else
            {
                throw new FilesNameConflictException("Node name conflict: " + node.getName());
            }
        }
        return parentNode;
    }
    
    private void handleUpload(UserToken userToken, INode node, List<FilePreUploadResponseV2> uploadUrlList,
        Long tokenTimeout, DataAccessURLInfo uploadURLInfo)
    {
        FilePreUploadResponseV2 rsp = new FilePreUploadResponseV2();
        saveFileMetadataAndGetUploadURL(userToken, node, rsp, uploadURLInfo, tokenTimeout);
        if (node.getType() == INode.TYPE_VERSION)
        {
            rsp.setFileId(node.getParentId());
        }
        else
        {
            rsp.setFileId(node.getId());
        }
        rsp.setName(node.getName());
        
        if(null != urlReplaceTools)
        {
            urlReplaceTools.replaceUploadUrlForV2(userToken.getAppId(), rsp);
        }

        uploadUrlList.add(rsp);
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()),
            String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        // 发送日志
        fileBaseService.sendINodeEvent(userToken,
            EventType.INODE_PRELOAD_BEGIN,
            node,
            null,
            UserLogType.UPLOADURL_PROVIDE,
            logMsgs,
            keyword);
        LOGGER.info("preUploadFile not flash result [{}, {}]", rsp.getFileId(), rsp.getUploadUrl());
    }

    private boolean isSameToParent(INode child, INode parent)
    {
        if (child.getSize() != parent.getSize())
        {
            return false;
        }
        
        // MD5校验
        if (StringUtils.isNotBlank(child.getMd5()))
        {
            // 不大于256字节的文件只比较整文件MD5
            if (child.getSize() <= Constants.SAMPLING_LENGTH_FOR_SMALLER_FILE)
            {
                // 数据库查询结果中sha1字段保存的MD5值
                return child.getMd5().equals(parent.getSha1());
            }
            // 大于256字节的文件比较整文件MD5和取样MD5
            else if (StringUtils.isNotBlank(child.getBlockMD5()))
            {
                ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(parent.getResourceGroupId());
                ObjectReference parentReference = fileBaseService.getObjectRefByObjectIDCheckRID(resourceGroup.getRegionId(),
                    parent.getObjectId(),
                    parent.getSize());
                return parentReference == null ? false : child.getMd5().equals(parentReference.getSha1())
                    && child.getBlockMD5().equals(parentReference.getBlockMD5());
            }
            
        }
        // sha1值校验
        else if (StringUtils.isNotBlank(child.getSha1()))
        {
            return child.getSha1().equals(parent.getSha1());
        }
        return false;
    }

    private FileMultiPreUploadResponse packResult(List<FilePreUploadResponseV2> uploadUrlList,
        List<RestFileInfo> uploadedList, List<FilePreUploadFailResponse> failedList)
    {
        FileMultiPreUploadResponse result = new FileMultiPreUploadResponse();
        if (!uploadUrlList.isEmpty())
        {
            result.setUploadUrlList(uploadUrlList);
        }
        if (!uploadedList.isEmpty())
        {
            result.setUploadedList(uploadedList);
        }
        if (!failedList.isEmpty())
        {
            result.setFailedList(failedList);
        }
        return result;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private void preUploadOneFile(UserToken userToken, INode node, INode parentNode,
        List<FilePreUploadResponseV2> uploadUrlList, List<RestFileInfo> uploadedList, Long tokenTimeout,
        Byte securityId, Integer qosPort, ResourceGroup resourceGroup, String domain, Set<String> currentNameSet)
    {
        DataAccessURLInfo uploadURLInfo = dcSimpleUrlManager.getUploadURL(userToken,node, qosPort, resourceGroup, domain);
        parentNode.setResourceGroupId(uploadURLInfo.getResourceGroupId());
        parentNode = handleSameNameFile(node, parentNode, currentNameSet);
        Date date = new Date();
        node.setCreatedAt(date);
        node.setModifiedAt(date);
        node.setCreatedBy(userToken.getId());
        node.setModifiedBy(userToken.getId());
        if(null != securityId)
        {
            node.setSecurityId(securityId);
        }
        // 判断是否可以闪传
        INode retNode = null;
        if (canBeFlashUpload(node))
        {
            fileBaseService.setNodeSyncStatusAndVersion(parentNode.getOwnedBy(), node, parentNode);
            retNode = tryFlashUpload(node, parentNode);
        }
        // 非闪传
        if (null == retNode)
        {
            handleUpload(userToken, node, uploadUrlList, tokenTimeout, uploadURLInfo);
        }
        else
        {
            handleFlashUpload(userToken, node, uploadedList, retNode);
        }
        
    }
    
    // 同以往实现
    private void saveFileMetadataAndGetUploadURL(UserToken user, INode iNode, FilePreUploadResponseV2 rsp,
        DataAccessURLInfo uploadURLInfo, Long tokenTimeout) throws BaseRunException
    {
        // 设置对象ID
        String objectID = buildObjectID(user.getId());
        iNode.setObjectId(objectID);
        
        iNode.setShareStatus(INode.SHARE_STATUS_UNSHARED);
        iNode.setStatus(INode.STATUS_CREATING);
        iNode.setId(fileBaseService.buildINodeID(iNode.getOwnedBy()));
        iNode.setResourceGroupId(uploadURLInfo.getResourceGroupId());
        
        // 预上传时写入size 0,为避免用户空间统计将上传失败的记录一并统计
        iNode.setSize(0L);
        
        fileBaseService.createINode(iNode);
        
        UserToken token = userTokenHelper.createTokenDataServer(iNode.getOwnedBy(),
            iNode.getObjectId(),
            AuthorityMethod.UPLOAD_OBJECT,
            iNode.getOwnedBy(),
            null,
            tokenTimeout);
        
        String url = uploadURLInfo.getUploadUrl();
        if (!url.endsWith(URL_SPILT))
        {
            url = url + URL_SPILT;
        }
        // 组装URL，token在URL中
        url = url + token.getToken() + URL_SPILT + iNode.getObjectId();
        rsp.setUploadUrl(url);
    }
    
    private void transThumbnailUrlList(RestFileInfo restFileInfo)
    {
        if (restFileInfo == null || restFileInfo.getThumbnailUrlList() == null)
        {
            return;
        }
        if (restFileInfo.getThumbnailUrlList().isEmpty())
        {
            restFileInfo.setThumbnailUrlList(null);
        }
    }
    
    /**
     * 闪传文件
     * 
     * @param fileNode
     * @param parentNode
     * @return
     * @throws BaseRunException
     */
    private INode tryFlashUpload(INode fileNode, INode parentNode) throws BaseRunException
    {
        ObjectReference objectRef = null;
        String md5 = fileNode.getMd5();
        String blockMD5 = fileNode.getBlockMD5();
        String sha1 = fileNode.getSha1();
        
        // MD5
        if (StringUtils.isNotBlank(md5))
        {
            objectRef = fileBaseService.getObjectRefByMD5CheckRID(parentNode.getResourceGroupId(),
                md5,
                blockMD5,
                fileNode.getSize());
        }
        // sha1
        else
        {
            objectRef = fileBaseService.getObjectRefBysha1CheckRID(parentNode.getResourceGroupId(),
                sha1,
                fileNode.getSize());
        }
        
        if (null == objectRef)
        {
            LOGGER.info("Matched digest not found. MD5: {}, block MD5: {} , Sha1: {}, resource gourp: {}",
                md5,
                blockMD5,
                sha1,
                parentNode.getResourceGroupId());
            return null;
        }
        
        INode node = INode.valueOf(fileNode);
        node.setObjectId(objectRef.getId());
        node.setSize(objectRef.getSize());
        node.setStatus(INode.STATUS_NORMAL);
        node.setResourceGroupId(objectRef.getResourceGroupId());
        node.setShareStatus(INode.SHARE_STATUS_UNSHARED);
        Date date = new Date();
        node.setCreatedAt(date);
        node.setModifiedAt(date);
        node.setVersion(INode.FIRST_VER_NUM);
        
        // 文件
        if (fileNode.getType() == INode.TYPE_FILE)
        {
            node.setId(fileBaseService.buildINodeID(node.getOwnedBy()));
            
            // 增加引用计数, 如果增加失败, 可能原文件已删除, 走正常上传流程
            if (fileBaseService.increaseObjectRefCount(objectRef) <= 0)
            {
                LOGGER.info("increase object ref failed, object id: " + objectRef.getId() + ", sha1:  "
                    + objectRef.getSha1());
                return null;
            }
            
            try
            {
                node.setSha1(objectRef.getSha1());
                fileBaseService.createINode(node);
            }
            catch (Exception e)
            {
                // 注意，不做垃圾数据清理，允许引用计数大于普通计数
                throw new InternalServerErrorException(e);
            }
        }
        else if (fileNode.getType() == INode.TYPE_VERSION)
        {
            // 如果新版本和原文件内容一致, 不生成版本
            if (isSameToParent(node, parentNode))
            {
                LOGGER.info("The same sha1 was found, no version created, sha1 :" + parentNode.getSha1());
                return parentNode;
            }
            
            long parentId = parentNode.getId();
            long parentPId = parentNode.getParentId();
            node.setType(parentNode.getType());
            node.setParentId(parentPId);
            node.setId(parentId);
            node.setLinkCode(parentNode.getLinkCode());
            node.setShareStatus(parentNode.getShareStatus());
            node.setSyncStatus(parentNode.getSyncStatus());
            
            if (StringUtils.isNotBlank(parentNode.getVersion()))
            {
                // 获取当前版本数量
                int verNum = iNodeDAO.getINodeTotal(parentNode);
                node.setVersion(String.valueOf(++verNum));
            }
            
            // 版本的ID使用新的ID 避免出现唯一键冲突
            parentNode.setId(fileBaseService.buildINodeID(fileNode.getOwnedBy()));
            parentNode.setParentId(fileNode.getParentId());
            parentNode.setType(INode.TYPE_VERSION);
            
            // 增加引用计数, 如果增加失败, 可能原文件已删除, 走正常上传流程
            if (fileBaseService.increaseObjectRefCount(objectRef) <= 0)
            {
                LOGGER.warn("increase object reference failed, object id: {}, digest: {}, sampling md5: {}",
                    objectRef.getId(),
                    objectRef.getSha1(),
                    objectRef.getBlockMD5());
                return null;
            }
            
            try
            {
                node.setSha1(objectRef.getSha1());
                nodeUpdateService.updateINodeForFlashUploadFile(node, parentNode, parentId);
            }
            catch (Exception e)
            {
                fileBaseService.decreaseRefObjectCount(objectRef);
                throw new InternalServerErrorException(e);
            }
            
            // 文件版本数大于用户设置版本数时清除历史版本
            fileBaseService.cleanEarliestVersions(node);
        }
        
        return node;
    }
    
    private INode validateParameter(UserToken userToken, List<INode> fileNodeList)
    {
        if (fileNodeList == null)
        {
            throw new InvalidParamException("files can not be null");
        }
        if (fileNodeList.isEmpty())
        {
            throw new InvalidParamException("files.size() == 0");
        }
        // 权限检测
        iNodeACLService.vaildINodeOperACL(userToken,
            fileNodeList.get(0),
            AuthorityMethod.UPLOAD_OBJECT.name());
        
        // 父目录状态校验
        INode parentNode = fileBaseService.getAndCheckParentNode(fileNodeList.get(0));
        
        if (parentNode.getType() == INode.TYPE_BACKUP_COMPUTER)
        {
            throw new ForbiddenException("TYPE_BACKUP_COMPUTER folder is not allowed to preupload");
        }
        return parentNode;
    }
}
