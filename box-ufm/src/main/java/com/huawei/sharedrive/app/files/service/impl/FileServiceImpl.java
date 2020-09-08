package com.huawei.sharedrive.app.files.service.impl;

import com.huawei.sharedrive.app.acl.service.INodeACLService;
import com.huawei.sharedrive.app.core.domain.OrderV1;
import com.huawei.sharedrive.app.core.job.ThreadPool;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.DCUrlManager;
import com.huawei.sharedrive.app.dataserver.url.URLReplaceTools;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.domain.PersistentEvent;
import com.huawei.sharedrive.app.event.manager.PersistentEventManager;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.*;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.files.service.FileService;
import com.huawei.sharedrive.app.files.service.NodeMessageService;
import com.huawei.sharedrive.app.files.service.NodeUpdateService;
import com.huawei.sharedrive.app.files.service.job.AyncDeleteObjectForVersionTask;
import com.huawei.sharedrive.app.files.service.job.DedupObjectTask;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;
import com.huawei.sharedrive.app.mirror.service.MirrorObjectService;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadRequest;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponseV1;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.security.service.SecurityMatrixService;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.service.RecordingAddedFilesService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pw.cdmi.box.app.convertservice.domain.ImgObject;
import pw.cdmi.box.app.convertservice.domain.TaskBean;
import pw.cdmi.box.app.convertservice.service.ConvertService;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.RandomGUID;

import java.util.*;

@Component("fileService")
public class FileServiceImpl implements FileService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
    
    private static final String URL_SPILT = "/";
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private DCUrlManager dcUrlManager;
    
    @Autowired
    private NodeUpdateService nodeUpdateService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
    @Autowired
    private MirrorObjectService mirrorObjectService;
    
    @Autowired
    private INodeACLService iNodeACLService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private NodeMessageService nodeMessageService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PersistentEventManager persistentEventManager;
    
    @Autowired
    private SecurityMatrixService securityMatrixService;
    
    @Autowired
    private UserDAOV2 userDao;
    @Autowired
    private ConvertService convertService;
    
    @Autowired(required = false)
    @Qualifier("urlReplaceTools")
    private URLReplaceTools urlReplaceTools;
    
    @Autowired
    private RecordingAddedFilesService recordingAddedFilesService;
    
    @Override
    public void abortUpload(String objectId, Long ownerId) throws BaseRunException
    {
        List<INode> fileNodeList = iNodeDAO.getINodeByObjectId(ownerId, objectId);
        
        if (null == fileNodeList || fileNodeList.isEmpty())
        {
            String msg = " Node not exist, owner: " + ownerId + " , object id:" + objectId;
            throw new NoSuchItemsException(msg);
        }
        
        INode fileNode = fileNodeList.get(0);
        
        // 取消分片的节点状态只能是creationg
        if (fileNode.getStatus() != INode.STATUS_CREATING)
        {
            String msg = " Node not in STATUS_CREATING, owner: " + ownerId + " , object id:" + objectId;
            throw new NoSuchItemsException(msg);
        }
        
        fileBaseService.deleteINode(fileNode);
        
        LOGGER.info("abortUpload success, owner: " + ownerId + " , object id:" + objectId);
    }
    
    /**
     * 对象级重删，需要更新INODE的objectID,和更新objectRef的引用计数
     * 
     * @param fileNode
     * @param objRef
     * @throws BaseRunException
     */
    @Transactional(propagation = Propagation.NEVER)
    @Override
    public boolean dedupObject(INode fileNode, ObjectReference objRef) throws BaseRunException
    {
        
        boolean isDedup = false;
        
        ObjectReference oldObjRef = fileBaseService.getObjectRefByMD5CheckRID(objRef.getResourceGroupId(),
            objRef.getSha1(),
            objRef.getBlockMD5(),
            objRef.getSize());
        
        // 重刪
        if (null != oldObjRef && !oldObjRef.getId().equals(objRef.getId()))
        {
            // 引用计数增加
            if (fileBaseService.increaseObjectRefCount(oldObjRef) <= 0)
            {
                throw new InternalServerErrorException("increaseObjectRefCount error,id:" + oldObjRef.getId());
            }
            
            try
            {
                // 更新未成功，则减少计数
                if (1 != nodeUpdateService.updateObjectForDedup(oldObjRef.getId(),
                    oldObjRef.getResourceGroupId(),
                    fileNode.getObjectId(),
                    fileNode.getOwnedBy()))
                {
                    // inode id change ,update failed.
                    fileBaseService.decreaseRefObjectCount(oldObjRef);
                    isDedup = false;
                }
                else
                {
                    // 对当前对象-1.
                    fileBaseService.decreaseRefObjectCount(objRef);
                    isDedup = true;
                    
                    // 保存新文件的srcObjectId
                    String srcObjectId = fileNode.getObjectId();
				    addTransformTask(oldObjRef.getId(), fileNode.getOwnedBy());
                    
                    // 发送一个内部事件
                    UserToken user = new UserToken();
                    user.setCloudUserId(User.SYSTEM_USER_ID);
                    fileNode.setObjectId(oldObjRef.getId());
                    INode destNode = new INode();
                    destNode.setOwnedBy(fileNode.getOwnedBy());
                    destNode.setId(fileNode.getId());
                    destNode.setObjectId(srcObjectId);
                    fileBaseService.sendINodeEvent(user,
                        EventType.INNER_DEDUP,
                        fileNode,
                        destNode,
                        null,
                        null,
                        null);
                    
                }
            }
            catch (Exception e)
            {
                // 异常情况下不做计数减一
                LOGGER.warn(e.getMessage(), e);
            }
            
        }
        
        if (!isDedup)
        {
            ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(objRef.getResourceGroupId());
            if (null == resourceGroup)
            {
                return isDedup;
            }
            int regionId = resourceGroup.getRegionId();
            
            try
            {
                fileBaseService.createObjectFrIndex(objRef, regionId);
                addTransformTask(objRef.getId(), fileNode.getOwnedBy());
            }
            catch (Exception e)
            {
                LOGGER.warn(e.getMessage(), e);
            }
        }
        
        return isDedup;
        
    }
    
    @Override
    @Transactional(propagation = Propagation.NEVER)
    public int deleteObjectReference(ObjectReference objectReference)
    {
        // 删除
        int ret = fileBaseService.deleteObjectRef(objectReference);
        
        // 删除
        fileBaseService.deleteObjectFrIndex(objectReference);
        
        return ret;
    }
    
    @Override
    public int getCurrentVersionNum(Long ownerId, Long nodeId)
    {
        INode node = new INode(ownerId, nodeId);
        return iNodeDAO.getINodeTotal(node);
    }
    
    @Override
    public int getCurrentVersionNumForUpdate(Long ownerId, Long nodeId)
    {
        INode node = new INode(ownerId, nodeId);
        return iNodeDAO.getINodeTotalForUpdate(node);
    }
    
    @Override
    public List<INode> getEarliestVersions(Long ownerId, Long nodeId, int limit)
    {
        return iNodeDAO.getEarliestVersions(ownerId, nodeId, limit);
    }
    
    @Override
    public String getFileDownloadUrl(UserToken user, INode node, HttpHeaders header) throws BaseRunException
    {
        long userId = iNodeACLService.vaildINodeOperACL(user, node, AuthorityMethod.GET_OBJECT.name());
        
        DataAccessURLInfo dataAccessUrlInfo = fileBaseService.getDownURLByNearAccess(user.getLoginRegion(),
            userId,
            node);
        
        sendLogEvent(user, node);
        header.add("objectId", node.getObjectId());
        return dataAccessUrlInfo.getDownloadUrl();
    }
    
    private void sendLogEvent(UserToken user, INode node)
    {
        String[] logMsgs = new String[]{String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        fileBaseService.sendINodeEvent(user,
            EventType.INODE_DOWNLOAD,
            node,
            null,
            UserLogType.DOWN_URL_FILE,
            logMsgs,
            keyword);
    }

    private void sendLogEvent(UserToken user, INode node, EventType eventType, UserLogType userLogType) {
        String[] logMsgs = new String[]{
                String.valueOf(node.getOwnedBy()),
                String.valueOf(node.getParentId())
        };
        String keyword = StringUtils.trimToEmpty(node.getName());
        fileBaseService.sendINodeEvent(user, eventType, node, null, userLogType, logMsgs, keyword);
    }

    @Override
    public String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId) throws BaseRunException
    {
        // 检查入参
        if (user == null || ownerId == null || fileId == null)
        {
            String msg = "user ownerId or fileId is null";
            throw new BadRequestException(msg);
        }
        // 判断 Inode是否存在<文件状态
        INode iNode = fileBaseService.getINodeInfoCheckStatus(ownerId, fileId, INode.STATUS_NORMAL);
        
        // 检测文件类型
        if (iNode.getType() != INode.TYPE_FILE && iNode.getType() != INode.TYPE_VERSION)
        {
            String msg = "inode not file,ownerId:" + ownerId + ", fileId:" + fileId + ",type:"
                + iNode.getType();
            throw new NoSuchItemsException(msg);
        }
        
        // 权限检测
        long userId = iNodeACLService.vaildINodeOperACL(user, iNode, AuthorityMethod.GET_OBJECT.name());
        
        DataAccessURLInfo dataAccessUrlInfo = fileBaseService.getDownURLByNearAccess(user.getLoginRegion(),
            userId,
            iNode);
        
        // 发送日志
        fileBaseService.sendINodeEvent(user, EventType.INODE_DOWNLOAD, iNode, null, null, null, null);
        
        return dataAccessUrlInfo.getDownloadUrl();
        
    }
    
    @Override
    public String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId, HttpHeaders header)
        throws BaseRunException
    {
        if (user == null || ownerId == null || fileId == null)
        {
            throw new InvalidParamException();
        }
        INode node = fileBaseService.getINodeInfo(ownerId, fileId);
        if (null == node)
        {
            LOGGER.error("File not exist, owner id:" + ownerId + ", file id:" + fileId);
            throw new NoSuchFileException();
        }
        
        if (INode.STATUS_NORMAL != node.getStatus())
        {
            LOGGER.error("File status abnormal, owner id:" + ownerId + ", file id:" + fileId + ", status:"
                + node.getStatus());
            throw new NoSuchFileException();
        }
        
        if (node.getType() != INode.TYPE_FILE && node.getType() != INode.TYPE_VERSION)
        {
            LOGGER.error("File type invalid, owner id:" + ownerId + ", file id:" + fileId + ",type:"
                + node.getType());
            throw new NoSuchFileException();
        }
        
        long userId = iNodeACLService.vaildINodeOperACL(user, node, AuthorityMethod.GET_OBJECT.name());
        DataAccessURLInfo dataAccessUrlInfo = fileBaseService.getDownURLByNearAccess(user.getLoginRegion(),
            userId,
            node);
        sendLogEvent(user, node);
        header.add("objectId", node.getObjectId());
        return dataAccessUrlInfo.getDownloadUrl();
    }
    
    @Override
    public String getFileDownloadUrl(UserToken user, Long ownerId, Long fileId, String objectId)
        throws BaseRunException
    {
        if (user == null || ownerId == null || fileId == null)
        {
            String msg = "user ownerId or fileId is null";
            throw new BadRequestException(msg);
        }
        INode iNode = fileBaseService.getINodeInfoCheckStatus(ownerId, fileId, INode.STATUS_NORMAL);
        if (iNode.getType() != INode.TYPE_FILE && iNode.getType() != INode.TYPE_VERSION)
        {
            String msg = "inode not file,ownerId:" + ownerId + ", fileId:" + fileId + ",type:"
                + iNode.getType();
            throw new NoSuchItemsException(msg);
        }
        long userId = iNodeACLService.vaildINodeOperACL(user, iNode, AuthorityMethod.GET_OBJECT.name());
        DataAccessURLInfo dataAccessUrlInfo = fileBaseService.getINodeInfoDownURL(userId, iNode, objectId);
        
        String[] logMsgs = new String[]{String.valueOf(ownerId), String.valueOf(iNode.getParentId())};
        String keyword = StringUtils.trimToEmpty(iNode.getName());
        
        fileBaseService.sendINodeEvent(user,
            EventType.INODE_DOWNLOAD,
            iNode,
            null,
            UserLogType.DOWN_URL_FILE,
            logMsgs,
            keyword);
        return dataAccessUrlInfo.getDownloadUrl();
        
    }
    
    @Override
    public String getFileDownloadUrlWithoutName(UserToken user, INode node, HttpHeaders header)
        throws BaseRunException
    {
        long userId = iNodeACLService.vaildINodeOperACL(user, node, AuthorityMethod.GET_OBJECT.name());
        DataAccessURLInfo dataAccessUrlInfo = fileBaseService.getINodeInfoDownURLWithoutName(userId, node);
        sendLogEvent(user, node);
        header.add("objectId", node.getObjectId());
        return dataAccessUrlInfo.getDownloadUrl();
    }
    
    @Override
    public FileINodesList getFileVersionLists(UserToken user, INode fileNode, Limit limit)
        throws BaseRunException
    {
        if (null == user || null == fileNode)
        {
            String message = "user or fileNode is null";
            throw new BadRequestException(message);
        }
        
        // 获取Inode信息,检测文件状态
        INode inode = fileBaseService.getINodeInfoCheckStatus(fileNode.getOwnedBy(),
            fileNode.getId(),
            INode.STATUS_NORMAL);
        
        // 检查文件类型
        if (inode.getType() != INode.TYPE_FILE)
        {
            String msg = "inode not file,ownerId:" + inode.getOwnedBy() + ", id:" + inode.getId() + ",type:"
                + inode.getType();
            throw new BadRequestException(msg);
        }
        
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, inode, AuthorityMethod.GET_INFO.name());
        
        // 设置节点过滤规则，按照ower 和parentid过滤
        INode filter = new INode();
        filter.setOwnedBy(fileNode.getOwnedBy());
        filter.setId(fileNode.getId());
        
        int total = iNodeDAO.getINodeTotal(filter);
        // 按时间倒序排列
        OrderV1 order = new OrderV1("modifiedAt", true);
        List<INode> versionsINode = iNodeDAO.getSubINodeAndSelf(filter, order, limit);
        
        List<INode> fileList = new ArrayList<INode>(versionsINode.size());
        List<INode> versionList = new ArrayList<INode>(versionsINode.size() - 1);
        for (INode tmpinode : versionsINode)
        {
            if (inode.getType() == INode.TYPE_FILE)
            {
                fileList.add(tmpinode);
            }
            else
            {
                versionList.add(tmpinode);
            }
            
        }
        
        // 按照版本号降序排列
        Collections.sort(versionList, new INodeIdComparator());
        
        fileList.addAll(versionList);
        
        FileINodesList versionLists = new FileINodesList();
        versionLists.setTotalCount(total);
        versionLists.setFiles(fileList);
        String[] logMsgs = new String[]{String.valueOf(inode.getOwnedBy()),
            String.valueOf(inode.getParentId())};
        String keyword = StringUtils.trimToEmpty(inode.getName());
        
        fileBaseService.sendINodeEvent(user,
            EventType.OTHERS,
            null,
            null,
            UserLogType.LIST_VERSION_FILE,
            logMsgs,
            keyword);
        return versionLists;
    }
    
    /**
     * 权限检测及token生成
     */
    @Override
    public UploadMultipartFileRSP getMultipartFilesToken(UserToken user,
        UploadMultipartFileRSP multipartFileRSP, Long ownerId, long fileId) throws BaseRunException
    {
        LOGGER.info("multipartFileRSP:" + ToStringBuilder.reflectionToString(multipartFileRSP));
        LOGGER.info("user:" + ToStringBuilder.reflectionToString(user));
        
        String uploadURL = multipartFileRSP.getUploadUrl();
        String[] uploadURLArg = uploadURL.split(URL_SPILT);
        // 文件检测
        if (uploadURLArg.length < 2)
        {
            throw new ForbiddenException();
        }
        String objectId = uploadURLArg[uploadURLArg.length - 1];
        List<INode> iNodeList = iNodeDAO.getINodeByObjectId(ownerId, objectId);
        if (null == iNodeList || iNodeList.size() != 1
            || iNodeList.get(0).getStatus() != INode.STATUS_CREATING)
        {
            throw new ForbiddenException();
        }
        INode iNode = iNodeList.get(0);
        // 权限检测
        iNodeACLService.vaildINodeOperACL(user, iNode, AuthorityMethod.UPLOAD_OBJECT.name());
        
        UserToken token = userTokenHelper.createTokenDataServer(user.getId(),
            iNode.getObjectId(),
            AuthorityMethod.UPLOAD_OBJECT,
            iNode.getOwnedBy());
        int objectIdPos = uploadURL.lastIndexOf(URL_SPILT);
        uploadURL = uploadURL.substring(0, objectIdPos);
        int tokenPos = uploadURL.lastIndexOf(URL_SPILT);
        uploadURL = uploadURL.substring(0, tokenPos);
        uploadURL = uploadURL + URL_SPILT + token.getToken() + URL_SPILT + objectId;
        multipartFileRSP.setUploadUrl(uploadURL);
        LOGGER.info(ToStringBuilder.reflectionToString(multipartFileRSP));
        return multipartFileRSP;
    }
    
    @Override
    public long getUserTotalFiles(Long ownerId)
    {
        return iNodeDAO.getUserTotalFiles(ownerId);
    }
    
    @Override
    public long getUserTotalSpace(long ownerId)
    {
        return iNodeDAO.getUserTotalSpace(ownerId);
    }
    
    @Override
    public List<INode> listFileAndVersions(int dbNum, int tableNum, long offset, int length)
    {
        Limit limit = new Limit(offset, length);
        return iNodeDAO.listFileAndVersions(dbNum, tableNum, limit);
    }
    
    @Override
    public FilePreUploadResponseV1 preUploadFile(UserToken userToken, INode node, FilePreUploadRequest request, boolean enableUploadNearest,Long ownerId)
        throws BaseRunException
	{

		// 权限检测
		iNodeACLService.vaildINodeOperACL(userToken, node, AuthorityMethod.UPLOAD_OBJECT.name());

		// 父目录状态校验
		INode parentNode = fileBaseService.getAndCheckParentNode(node);

		if (parentNode.getType() == INode.TYPE_BACKUP_COMPUTER) {
			throw new ForbiddenException("TYPE_BACKUP_COMPUTER folder is not allowed to preupload");
		}

		DataAccessURLInfo uploadURLInfo = dcUrlManager.getUploadURL(userToken, node, enableUploadNearest);
		// 检测文件夹下是否有重名文件(由于并发原因, 可能出现相同目录下存在多个同名文件的情况)
		List<INode> existNodes = iNodeDAO.getNodeByName(node.getOwnedBy(), node.getParentId(), node.getName());
		if (CollectionUtils.isNotEmpty(existNodes)) {
			INode fileNode = getFileNodeFromList(existNodes);
			// 如果存在同名文件, 作为版本处理, 默认选择集合的第一个元素作为版本文件
			if (fileNode != null) {
				fileBaseService.checkVersionFileSize(ownerId, fileNode.getSize());
				fileBaseService.checkVersionFileType(ownerId, fileNode.getName());
				parentNode = fileNode;
				node.setParentId(parentNode.getId());
				node.setType(INode.TYPE_VERSION);
			} else {
				// 有重名目录
				throw new FilesNameConflictException("Node name conflict: " + node.getName());
			}

		}

		Date date = new Date();
		node.setCreatedAt(date);
		node.setModifiedAt(date);
		if (request.getCreatedBy()!=null) {
			node.setCreatedBy(request.getCreatedBy());
			node.setModifiedBy(request.getCreatedBy());
		}else{
			node.setCreatedBy(userToken.getId());
			node.setModifiedBy(userToken.getId());
		}
		// 获取文件的安全级别
		if (securityMatrixService.isSecurityMatrixEnable() && StringUtils.isNotEmpty(userToken.getToken())) {
			byte securityId = securityMatrixService.getFileSecurityId(userToken);
			node.setSecurityId(securityId);
		}

		// 判断是否可以闪传
		INode retNode = null;
		if (canBeFlashUpload(node)) {
			fileBaseService.setNodeSyncStatusAndVersion(parentNode.getOwnedBy(), node, parentNode);
			retNode = tryFlashUpload(node, parentNode, uploadURLInfo.getResourceGroupId());
		}
		FilePreUploadResponseV1 rsp = new FilePreUploadResponseV1();

		// 非闪传
		if (null == retNode) {
			saveFileMetadataAndGetUploadURL(userToken, node, rsp, uploadURLInfo, request.getTokenTimeout());
			if (node.getType() == INode.TYPE_VERSION) {
				rsp.setFileId(node.getParentId());
			} else {
				rsp.setFileId(node.getId());
			}
			String[] logMsgs = new String[] { String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId()) };
			String keyword = StringUtils.trimToEmpty(node.getName());
			// 发送日志
			fileBaseService.sendINodeEvent(userToken, EventType.INODE_PRELOAD_BEGIN, node, null, UserLogType.UPLOADURL_PROVIDE, logMsgs, keyword);

		} else {
			FilesCommonUtils.setNodeVersionsForV2(retNode);
			rsp.setFile(new RestFileInfo(retNode, userToken.getDeviceType()));
			String[] logMsgs = new String[] { String.valueOf(node.getOwnedBy()), String.valueOf(node.getParentId()) };
			String keyword = StringUtils.trimToEmpty(node.getName());
			// 发送日志
			fileBaseService.sendINodeEvent(userToken, EventType.INODE_PRELOAD_END, retNode, null, UserLogType.UPLOAD_QUICK, logMsgs, keyword);

			// 闪传文件发送事件
			PersistentEvent event = generalUploadEvent(retNode);
			persistentEventManager.fireEvent(event);
		}

		if (null != urlReplaceTools) {
			urlReplaceTools.replaceUploadUrlForV1(userToken.getAppId(), rsp);
		}

		return rsp;
	}
    

	@Override
    public void updateObjectInfo(ObjectUpdateInfo objectUpdateInfo) throws BaseRunException
    {
        String objectId = objectUpdateInfo.getObjectId();
        // 闪传方案变更, 文件内容计算由sha1变更为整文件MD5 + 取样MD5;
        Map<String, String> md5Map = FilesCommonUtils.parseMD5(objectUpdateInfo.getSha1());
        String md5 = md5Map.get("MD5");
        md5 = md5 == null ? "" : md5;
        String blockMD5 = md5Map.get("BlockMD5");
        long userId = objectUpdateInfo.getOwnerId();
        long length = objectUpdateInfo.getLength();
        // 在异步更新sha1前，该节点可能执行了copy等操作，导致查询出的结果不唯一，故修改为list
        List<INode> fileNodeList = iNodeDAO.getINodeByObjectId(userId, objectId);
        if (null == fileNodeList || fileNodeList.isEmpty())
        {
            throw new NoSuchItemsException("Node not exist, owner: " + userId + " , object id:" + objectId);
        }
        // 如果是预上传第一次回调则fileNodeList length只能为1，且状态为creating
        // add by: guoz:上面一句話意思用戶在做文件上傳操作，當預上傳完成后，數據庫inode分表中只會對應一條記錄。所以這里取0。
        INode fileNode = fileNodeList.get(0);
        if (fileNode.getStatus() != INode.STATUS_NORMAL)
        {
            if (fileNode.getStatus() == INode.STATUS_DELETE)
            {
                String msg = " Node in delete status, owner: " + userId + " , object id:" + objectId;
                throw new NoSuchItemsException(msg);
            }
            // 数据库sha1字段名称不变, 用于保存MD5值
            fileNode.setSha1(md5);
            fileNode.setMd5(md5);
            fileNode.setBlockMD5(blockMD5);
            fileNode.setSize(length);
            fileNode.setModifiedAt(new Date());
            // 获取parentNode
            INode parentNode = getAndCheckParentNode(fileNode);
            ObjectReference objRef = null;
            if (fileNode.getType() == INode.TYPE_FILE)
            {
                // 设置同步状态, 更新同步版本号
                fileBaseService.setNodeSyncStatusAndVersion(fileNode.getOwnedBy(), fileNode, parentNode);
                objRef = updateFileInfo(fileNode);
            }
            else if (fileNode.getType() == INode.TYPE_VERSION)
            {
                ObjectReference parentObject = objectReferenceDAO.get(parentNode.getObjectId());
                if (parentObject != null && isContentEquals(fileNode, parentObject))
                {
                    // 内容相同，检查区域是否相同
                    if (isRegionEquals(fileNode, parentObject))
                    {
                        handleVersionSameParent(fileNode, parentNode);
                        return;
                    }
                    objRef = buildMirrorObject(fileNode, parentNode, parentObject);
                }
                else
                {
                    // 更新版本
                    objRef = updateVersionInfoNew(fileNode, parentNode, objectUpdateInfo);
                    // 通知共享变更
//                    nodeMessageService.notifyShareToUpdateMsg(fileNode);
                }
            }
            else
            {
                LOGGER.error("Unsupport file type, owner id: {}, node id: {}, type: {}", fileNode.getOwnedBy(), fileNode.getId(), fileNode.getType());
                throw new BadRequestException("Unsupport file type");
            }
            // 异步重删
            if (StringUtils.isNotBlank(objRef.getSha1()))
            {
                dedupTask(fileNode, objRef);
            }
            else
            {
            	//新增转换
                addTransformTask(fileNode.getObjectId(), fileNode.getOwnedBy());
            }
            
            // 发送日志
            User user = userService.get(null, objectUpdateInfo.getOwnerId());
            if (user != null)
            {
                // 发送消息的，确保状态正确状态
                fileNode.setStatus(INode.STATUS_NORMAL);
                UserToken userToken = new UserToken();
                userToken.setId(objectUpdateInfo.getOwnerId());
                userToken.setAppId(user.getAppId());
                userToken.setAccountId(user.getAccountId());
                fileBaseService.sendINodeEvent(userToken,
                    EventType.INODE_PRELOAD_END,
                    fileNode,
                    null,
                    null,
                    null,
                    null);
            }
            // 文件上传完成发送事件
            PersistentEvent event = generalUploadEvent(fileNode);
            persistentEventManager.fireEvent(event);
        }
        else
        {
            handleNormalStatus(md5, blockMD5, length, fileNodeList);
        }
        recordingAddedFilesService.put(new FilesAdd(userId, fileNode.getId(), userDao.get(userId).getAccountId(), length));
    }
    
    private void handleVersionSameParent(INode fileNode, INode parentNode)
    {
        LOGGER.info("Current node is same to the parent, do not create version file");
        fileBaseService.deleteINode(fileNode);
        try
        {
            nodeUpdateService.updateINodeModifiedAt(parentNode);
        }
        catch (Exception e)
        {
            LOGGER.warn("update last modify time faild, owner id: {}, node id: {}",
                parentNode.getOwnedBy(),
                parentNode.getId(),
                e.getMessage());
        }
        ThreadPool.execute(new AyncDeleteObjectForVersionTask(fileNode.getObjectId(),
            fileNode.getResourceGroupId()));
    }
    
    private ObjectReference buildMirrorObject(INode fileNode, INode parentNode, ObjectReference parentObject)
    {
        LOGGER.info("Current node's content is same to the parent but region, do not create version file and build mirror object");
        // 暂存预上传时产生版本节点，用于回滚
        INode tmpNode = fileBaseService.getINodeInfo(fileNode.getOwnedBy(), fileNode.getId());
        // 删除预上传时产生版本节点
        fileBaseService.deleteINode(fileNode);
        // 保存本区域实际对象
        ObjectReference currentObject = fileBaseService.createObjectRef(fileNode);
        // 保存本区域实际对象与父对象的副本关系
        MirrorObject mirrorObject = new MirrorObject();
        mirrorObject.setCreateAt(new Date());
        mirrorObject.setDestObjectId(currentObject.getId());
        mirrorObject.setDestResourceGroupId(currentObject.getResourceGroupId());
        mirrorObject.setOwnedBy(parentNode.getOwnedBy());
        mirrorObject.setPolicyId(MirrorCommonStatic.DEFAULT_POLICY_ID);
        mirrorObject.setSrcObjectId(parentObject.getId());
        mirrorObject.setSrcResourceGroupId(parentObject.getResourceGroupId());
        mirrorObject.setType(CopyType.COPY_TYPE_NEAR.getCopyType());
        try
        {
            mirrorObjectService.insert(mirrorObject);
        }
        catch (Exception e)
        {
            LOGGER.warn("can not build mirror relationship", e);
            // 异常回滚
            fileBaseService.deleteObjectRef(currentObject);
            fileBaseService.createNode(tmpNode);
            throw e;
        }
        try
        {
            nodeUpdateService.updateINodeModifiedAt(parentNode);
        }
        catch (Exception e)
        {
            LOGGER.warn("update last modify time faild, owner id: {}, node id: {}",
                parentNode.getOwnedBy(),
                parentNode.getId(),
                e.getMessage());
        }
        return currentObject;
    }
    
    @Override
    public void updateObjectRefDeleteTime(ObjectReference objectReference)
    {
        nodeUpdateService.updateObjectRefDeleteTime(objectReference);
    }
    
    // /**
    // * 更新文件版本信息
    // *
    // * @param fileNode
    // * @throws BaseRunException
    // */
    // @Transactional(propagation = Propagation.NEVER)
    // public ObjectReference updateVerionInfo(INode fileNode, INode parentNode) throws
    // BaseRunException
    // {
    // ObjectReference objRef = fileBaseService.createObjectRef(fileNode);
    //
    // // 交换版本和文件的ID和parent
    // long parentID = parentNode.getId();
    // long parentPID = parentNode.getParentId();
    // long parentOriginalId = parentID;
    //
    // // 版本的ID使用新的ID 避免出现唯一键冲突
    // parentNode.setId(fileBaseService.buildINodeID(fileNode.getOwnedBy()));
    // parentNode.setParentId(fileNode.getParentId());
    // parentNode.setType(INode.TYPE_VERSION);
    //
    // fileNode.setParentId(parentPID);
    // fileNode.setId(parentID);
    // fileNode.setType(INode.TYPE_FILE);
    // fileNode.setStatus(INode.STATUS_NORMAL);
    //
    // try
    // {
    // nodeUpdateService.updateINodeForUploadVersion(fileNode, parentNode,
    // parentOriginalId);
    // }
    // catch (Exception e)
    // {
    // // 异常回滚
    // fileBaseService.deleteObjectRef(objRef);
    // LOGGER.warn(e.getMessage(), e);
    // throw e;
    // }
    //
    // // 文件版本数大于用户设置版本数时清除历史版本
    // fileBaseService.cleanEarliestVersions(fileNode);
    //
    // return objRef;
    //
    // }
    
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
    
    /**
     * 异步重删
     * 
     * @param fileNode
     * @param objRef
     * @throws BaseRunException
     */
    private void dedupTask(INode fileNode, ObjectReference objRef) throws BaseRunException
    {
        ThreadPool.execute(new DedupObjectTask(this, fileNode, objRef));
    }
    
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
    
    private INode getAndCheckParentNode(INode fileNode) throws BaseRunException
    {
        INode parentNode = null;
        if (fileNode.getParentId() != INode.FILES_ROOT)
        {
            parentNode = fileBaseService.getINodeInfo(fileNode.getOwnedBy(), fileNode.getParentId());
            if (null == parentNode)
            {
                String msg = " Parent node not exist, ownerid :" + fileNode.getOwnedBy() + ",node id: "
                    + fileNode.getId();
                throw new NoSuchParentException(msg);
            }
            if (parentNode.getStatus() != INode.STATUS_NORMAL)
            {
                throw new NoSuchParentException("parentNode status is unnormal " + parentNode.getStatus());
            }
        }
        else
        {
            parentNode = new INode(fileNode.getOwnedBy(), INode.FILES_ROOT);
        }
        return parentNode;
    }
    
    /**
     * @param sha1
     * @param length
     * @param fileNodeList
     * @throws BaseRunException
     */
    private void handleNormalStatus(String md5, String blockMD5, long length, List<INode> fileNodeList)
        throws BaseRunException
    {
        if (StringUtils.isNotBlank(md5))
        {
            ObjectReference objRef = null;
            for (INode node : fileNodeList)
            {
                node.setSha1(md5);
                node.setSize(length);
                
                // 更新SHA1场景
                objRef = nodeUpdateService.updateObjectForMerge(length,
                    md5,
                    blockMD5,
                    node.getObjectId(),
                    node.getOwnedBy());
                // 再启动异步重删
                dedupTask(node, objRef);
            }
            
        }
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
    
    private boolean isContentEquals(INode currentNode, ObjectReference parentObject)
    {
        if (currentNode.getSize() != parentObject.getSize())
        {
            return false;
        }
        if (StringUtils.isBlank(currentNode.getMd5()))
        {
            // 分片上传第一次回调，指纹还未计算完成
            return false;
        }
        // 不大于256字节的文件只比较整文件MD5
        if (currentNode.getSize() <= Constants.SAMPLING_LENGTH_FOR_SMALLER_FILE)
        {
            // 数据库查询结果中sha1字段保存的MD5值
            return currentNode.getMd5().equals(parentObject.getSha1());
        }
        // 大于256字节的文件比较整文件MD5和取样MD5
        if (StringUtils.isNotBlank(currentNode.getBlockMD5()))
        {
            return currentNode.getMd5().equals(parentObject.getSha1())
                && currentNode.getBlockMD5().equals(parentObject.getBlockMD5());
        }
        return false;
        
    }
    
    private boolean isRegionEquals(INode currentNode, ObjectReference parentObject)
    {
        ResourceGroup currentResourceGroup = dcManager.getCacheResourceGroup(currentNode.getResourceGroupId());
        ResourceGroup parentResourceGroup = dcManager.getCacheResourceGroup(parentObject.getResourceGroupId());
        if (null == currentResourceGroup || null == parentResourceGroup)
        {
            return false;
        }
        return currentResourceGroup.getRegionId() == parentResourceGroup.getRegionId();
    }
    
    private void saveFileMetadataAndGetUploadURL(UserToken user, INode iNode, FilePreUploadResponseV1 rsp,
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
        rsp.setUrl(url);
    }
    
    /**
     * 闪传文件
     * 
     * @param fileNode
     * @param parentNode
     * @return
     * @throws BaseRunException
     */
    private INode tryFlashUpload(INode fileNode, INode parentNode, int nodeResourceGroupId) throws BaseRunException
    {
        ObjectReference objectRef = null;
        String md5 = fileNode.getMd5();
        String blockMD5 = fileNode.getBlockMD5();
        String sha1 = fileNode.getSha1();
        
        // MD5
        if (StringUtils.isNotBlank(md5))
        {
            objectRef = fileBaseService.getObjectRefByMD5CheckRID(nodeResourceGroupId,
                md5,
                blockMD5,
                fileNode.getSize());
        }
        // sha1
        else
        {
            objectRef = fileBaseService.getObjectRefBysha1CheckRID(nodeResourceGroupId,
                sha1,
                fileNode.getSize());
        }
        
        if (null == objectRef)
        {
            LOGGER.info("Matched digest not found. MD5: {}, block MD5: {} , Sha1: {}, resource gourp: {}",
                md5,
                blockMD5,
                sha1,
                nodeResourceGroupId);
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
    
    /**
     * 更新节点信息，非重删请情况下
     * 
     * @param fileNode
     * @throws BaseRunException
     */
    @Transactional(propagation = Propagation.NEVER)
    protected ObjectReference updateFileInfo(INode fileNode) throws BaseRunException
    {
        // 先创建对象引用数
        ObjectReference objRef = fileBaseService.createObjectRef(fileNode);
        try
        {
            nodeUpdateService.updateINodeForUploadFile(fileNode);
        }
        catch (Exception e)
        {
            // 异常回滚
            fileBaseService.deleteObjectRef(objRef);
            LOGGER.warn(e.getMessage(), e);
            throw e;
        }
        
        return objRef;
    }
    
    /**
     * 更新文件版本信息
     * 
     * @param fileNode
     * @throws BaseRunException
     */
    @Transactional(propagation = Propagation.NEVER)
    protected ObjectReference updateVersionInfoNew(INode fileNode, INode parentNode, ObjectUpdateInfo objectUpdateInfo) throws BaseRunException {
        ObjectReference objRef = fileBaseService.createObjectRef(fileNode);
        INode parent = null;
        try {
            parent = nodeUpdateService.updateINodeForUploadVersionById(fileNode, parentNode, objectUpdateInfo);
        } catch (Exception e) {
            // 异常回滚
            fileBaseService.deleteObjectRef(objRef);
            LOGGER.warn(e.getMessage(), e);
            throw e;
        }
        // 文件版本数大于用户设置版本数时清除历史版本
        fileBaseService.cleanEarliestVersions(parent);
        return objRef;
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

    @Override
    public ImgObject getImgObject(INode node) {
        ImgObject imgObject = convertService.getImage(node.getObjectId());

        if (null == imgObject) {
            TaskBean taskBean = new TaskBean();
            taskBean.setOwneId(String.valueOf(node.getOwnedBy()));
            taskBean.setObjectId(node.getObjectId());
            convertService.addTask(taskBean);
        }

        return imgObject;
    }

    @Override
    public TaskBean getTaskBean(INode node) {
        return this.convertService.getTaskInfo(node.getObjectId());
    }

    /**
     * 判断添加转换任务
     * @param fileObject
     * @param result
     */
    private void addTransformTask(String objectId, long ownerId) {
        LOGGER.info("Enter addTransformTask!objectId=" + objectId +
            ",ownerId=" + ownerId);

        TaskBean taskbean = new TaskBean();
        taskbean.setObjectId(objectId);
        taskbean.setOwneId(String.valueOf(ownerId));
        convertService.addTask(taskbean);

        LOGGER.info("Exit the addTransformTask");
    }
}
