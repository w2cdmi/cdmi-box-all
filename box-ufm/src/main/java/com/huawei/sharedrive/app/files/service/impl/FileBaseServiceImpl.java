package com.huawei.sharedrive.app.files.service.impl;

import com.huawei.sharedrive.app.account.dao.AccountDao;
import com.huawei.sharedrive.app.account.domain.AccountConstants;
import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.core.domain.Thumbnail;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.DCUrlManager;
import com.huawei.sharedrive.app.dataserver.url.URLReplaceTools;
import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.event.service.EventService;
import com.huawei.sharedrive.app.exception.*;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.files.dao.ObjectFingerprintIndexDAO;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.FileBasicConfig;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.files.service.*;
import com.huawei.sharedrive.app.files.service.lock.Locks;
import com.huawei.sharedrive.app.log.domain.UserLogType;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.plugins.preview.manager.FilePreviewManager;
import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.user.service.UserSyncVersionService;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.file.service.CloudFilesService;
import pw.cdmi.uam.domain.AuthApp;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Service("fileBaseService")
public class FileBaseServiceImpl implements FileBaseService
{
    public static final String THUMBNAIL_PREFIX_BIG = "/thumbnail?minHeight=96&minWidth=96";
    
    public static final String THUMBNAIL_PREFIX_HUGE = "/thumbnail?minHeight=200&minWidth=200";
    
    public static final String THUMBNAIL_PREFIX_SMALL = "/thumbnail?minHeight=32&minWidth=32";
    
    private static final long DEFAULT_MAX_FILE_COUNT = 1000000;
    
    private static final int LIMIT = 1000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileBaseServiceImpl.class);
    
    private static Long userMaxFileNum;
    
    private static final String GETURLINFO = "getUrlInfo";
    
    private static final String GETURLINFOWITHOUTNAME = "getUrlInfoWithoutName";
    
    @Autowired
    private AuthAppService authAppService;
    
    // 注入上下文
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private DCUrlManager dcUrlManager;
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private INodeIdGenerateService iNodeIdGenerateService;
    
    @Autowired
    private NodeUpdateService nodeUpdateService;
    
    @Autowired
    private ObjectFingerprintIndexDAO objectFrIndexDAO;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
    private FileBaseServiceImpl proxySelf; // ② 表示代理对象，不是目标对象
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private UserDAOV2 userDAO;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private UserSyncVersionService userSyncVersionService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private FilePreviewManager filePreviewManager;

    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired(required = false)
    @Qualifier("urlReplaceTools")
    private URLReplaceTools urlReplaceTools;
    
    @Autowired
    private SpaceStatisticsService spaceStatisticsService;
    
    @Autowired
    private AccountDao accountDao;
    
    @Autowired
    private INodeDAOV2 iNodeDAOV2;
    
    /**
     * 获取缩略图后缀
     * 
     * @param thumbSize
     * @return
     */
    public static String getThumbnaliSuffix(Thumbnail thumbnail)
    {
        return "/thumbnail?minHeight=" + thumbnail.getHeight() + "&minWidth=" + thumbnail.getWidth();
    }
    
    /**
     * 产生一个新的节点ID
     * 
     * @param owner_id
     * @return
     */
    @Override
    public long buildINodeID(long ownerId)
    {
        return iNodeIdGenerateService.getNextUserNodeId(ownerId);
    }
    
    @Override
    public void checkMaxVersions(Long ownerId, Long nodeId) throws BaseRunException
    {
        // 用户设置的最大版本数
        int maxVersions = userService.getMaxVersions(ownerId);
        
        // 无限制
        if (User.VERSION_NUM_UNLIMITED == maxVersions || 0 == maxVersions)
        {
            return;
        }
        
        // 当前版本数
        int currentVerions = fileService.getCurrentVersionNum(ownerId, nodeId);
        if (currentVerions >= maxVersions)
        {
            throw new ExceedUserMaxVersionNumException();
        }
    }
    
    /**
     * 判断目标节点是否为原节点的子节点
     * 
     * @param srcFolder
     * @param destFolder
     * @return
     * @throws BaseRunException
     */
    @Override
    public boolean checkNodeIsSubNode(INode srcFolder, INode dstNode) throws BaseRunException
    {
        if (srcFolder.getOwnedBy() != dstNode.getOwnedBy())
        {
            return false;
        }
        
        if (srcFolder.getId() == dstNode.getId())
        {
            return true;
        }
        
        INode tmpNode = INode.valueOf(dstNode);
        while (true)
        {
            if (tmpNode.getParentId() == srcFolder.getId())
            {
                return true;
            }
            
            if (tmpNode.getParentId() == INode.FILES_ROOT)
            {
                break;
            }
            
            tmpNode = getINodeInfo(tmpNode.getOwnedBy(), tmpNode.getParentId());
            if (null == tmpNode)
            {
                return false;
            }
            
        }
        return false;
    }
    
    @Override
    public boolean checkObjectInUserStorRegion(long userId, ObjectReference objRef)
    {
        if (objRef == null)
        {
            return false;
        }
        User user = userDAO.get(userId);
        
        ResourceGroup destResourceGroup = dcManager.getCacheResourceGroup(objRef.getResourceGroupId());
        if (null == destResourceGroup || null == user
            || destResourceGroup.getRegionId() != user.getRegionId())
        {
            return false;
        }
        
        return true;
        
    }
    
    @Override
    public void checkVersionFileType(long userId,String type) throws BaseRunException
    {
        String versionFileSize = userDAO.get(userId).getVersionFileType();
      
        if (versionFileSize!=null&&!versionFileSize.equals(""))
        {
           	if(type.indexOf(versionFileSize)==-1){
           	 throw new ExceedSpaceQuotaException("Account versionType not allow");
           	}
        }
       
    }
    
    @Override
    public void checkVersionFileSize(long userId,long size) throws BaseRunException
    {
        long MaxVersionFileSize = userDAO.get(userId).getVersionFileSize();
      
        if (MaxVersionFileSize != AccountConstants.UNLIMIT_NUM)
        {
            if(size>MaxVersionFileSize){
            	 throw new ExceedSpaceQuotaException("Account space quota exceeded");
            }
           
        }
       
    }
    
    @Override
    public void checkSpaceAndFileCount(Long userId, long accountId) throws BaseRunException
    {
        long userSpaceQuota = userService.getSpaceQuota(userId);
        long accountSpaceQuota = accountDao.getMaxSpace(accountId);
        UserStatisticsInfo userInfo = spaceStatisticsService.getUserCurrentInfo(userId, accountId);
        AccountStatisticsInfo accountInfo = spaceStatisticsService.getAccountCurrentInfo(accountId);
      
        if (userSpaceQuota != User.SPACE_QUOTA_UNLIMITED && userSpaceQuota <= userInfo.getSpaceUsed())
        {
            LOGGER.error("User space quota exceeded. Space Quota: {}, Space used: {}",
                userSpaceQuota,
                userInfo.getSpaceUsed());
            throw new ExceedSpaceQuotaException("User space quota exceeded");
        }
        if (accountSpaceQuota != AccountConstants.UNLIMIT_NUM && accountSpaceQuota <= accountInfo.getCurrentSpace() / (1024 * 1024))
        {
            LOGGER.error("Account space quota exceeded. Space quota: {}, Space used: {}",
                accountSpaceQuota,
                accountInfo.getCurrentSpace());
            throw new ExceedSpaceQuotaException("Account space quota exceeded");
        }
        if (getUserMaxFileNum() <= userInfo.getFileCount())
        {
            LOGGER.error("User nodes count exceeded. Max nodes num: {}, current nodes num: {}",
                getUserMaxFileNum(),
                userInfo.getFileCount());
            throw new ExceedUserMaxNodeNumException("User node count exceeded");
        }
    }
    
    
    @Override
	public void checkSpaceAndFileCount(Long userId, long accountId, long fileSize) throws BaseRunException,ExceedSpaceQuotaException {
        long userSpaceQuota = userService.getSpaceQuota(userId);
        long accountSpaceQuota = accountDao.getMaxSpace(accountId);
        
        UserStatisticsInfo userInfo = spaceStatisticsService.getUserCurrentInfo(userId, accountId);
        AccountStatisticsInfo accountInfo = spaceStatisticsService.getAccountCurrentInfo(accountId); 
        
        long userAvailableSpace = userSpaceQuota - userInfo.getSpaceUsed();
        if (userSpaceQuota != User.SPACE_QUOTA_UNLIMITED && userAvailableSpace <= fileSize){
            LOGGER.error("file size exceeded available space. userAvailableSpace: {}, file size: {}",
                userAvailableSpace,
                fileSize);
            throw new ExceedUserAvailabelSpaceException("file size exceeded userAvailableSpace");
        }
        long enterpriseAvailableSpace = accountSpaceQuota * 1024 * 1024 - accountInfo.getCurrentSpace();
        if (accountSpaceQuota != AccountConstants.UNLIMIT_NUM && enterpriseAvailableSpace <= fileSize){
            LOGGER.error("file size exceeded available space. enterpriseAvailableSpace: {}, file size: {}",
                enterpriseAvailableSpace,
                fileSize);
            throw new ExceedEnterpriseAvailabelSpaceException("file size exceeded enterpriseAvailableSpace");
        }
		if (getUserMaxFileNum() <= userInfo.getFileCount())
        {
            LOGGER.error("User nodes count exceeded. Max nodes num: {}, current nodes num: {}",
                getUserMaxFileNum(),
                userInfo.getFileCount());
            throw new ExceedUserMaxNodeNumException("User node count exceeded");
        }
	}

	@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void cleanEarliestVersions(INode node) throws BaseRunException
    {
        // 用户设置的最大版本数
        int maxVersions = userService.getMaxVersions(node.getOwnedBy());
        
        // 无限制
        if (User.VERSION_NUM_UNLIMITED == maxVersions || 0 == maxVersions)
        {
            return;
        }
        
        // 当前版本数
        int currentVerions = fileService.getCurrentVersionNumForUpdate(node.getOwnedBy(), node.getId());
        if (currentVerions <= maxVersions)
        {
            return;
        }
        
        // 待删除的版本数
        int limit = currentVerions - maxVersions;
        List<INode> nodeList = fileService.getEarliestVersions(node.getOwnedBy(), node.getId(), limit);
        
        UserToken userToken = new UserToken();
        userToken.setId(node.getOwnedBy());
        User user = userService.get(node.getOwnedBy());
        if (user == null)
        {
            throw new NoSuchUserException("can not found user " + node.getOwnedBy());
        }
        userToken.setAppId(user.getAppId());
        String[] logMsgs = null;
        String keyword = null;
        
        for (INode temp : nodeList)
        {
            LOGGER.info("Delete version file. Owner id: {}, node id: {}", node.getOwnedBy(), node.getId());
            nodeUpdateService.updateAtmoNodeStatusToDelete(temp, INode.STATUS_DELETE);
            
            logMsgs = new String[]{StringUtils.trimToEmpty(temp.getName()),
                String.valueOf(temp.getParentId())};
            keyword = StringUtils.trimToEmpty(temp.getName());
            sendINodeEvent(userToken,
                EventType.VERSION_DELETE,
                temp,
                null,
                UserLogType.DELETE_FILE,
                logMsgs,
                keyword);
        }
        
        // 更新当前节点的总版本数
        node.setVersion(String.valueOf(maxVersions));
        iNodeDAO.updateVersionNum(node);
    }
    
    /**
     * 创建NODE节点
     * 
     * @param node
     * @return
     * @throws BaseRunException
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode createINode(INode node) throws BaseRunException
    {
        if (null != node)
        {
            iNodeDAO.create(node);
        }
        return node;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createNode(INode node)
    {
        iNodeDAO.create(node);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public INode createNode(long userId, INode srcFile, INode destFolder)
    {
        INode inode = new INode();
        inode.copyFrom(srcFile);
        inode.copyDest(userId, buildINodeID(destFolder.getOwnedBy()), destFolder);
        iNodeDAO.create(inode);
        return inode;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void createObjectFrIndex(ObjectReference objRef, int regionId)
    {
        ObjectFingerprintIndex objFpIndex = new ObjectFingerprintIndex(objRef, regionId);
        objectFrIndexDAO.create(objFpIndex);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ObjectReference createObjectRef(INode fileNode)
    {
        ObjectReference objectReference = new ObjectReference(fileNode);
        objectReferenceDAO.create(objectReference);
        return objectReference;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int decreaseRefObjectCount(ObjectReference objRf)
    {
        return objectReferenceDAO.decreaseRefCount(objRf);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCreatingNode(long ownerID, String objectID) throws BaseRunException
    {
        INode iNode = new INode();
        iNode.setObjectId(objectID);
        iNode.setStatus(INode.STATUS_CREATING);
        iNode.setOwnedBy(ownerID);
        iNodeDAO.deleteNodeByObjectAndCheckStatus(iNode);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteINode(INode node)
    {
        iNodeDAO.delete(node);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteObjectFrIndex(ObjectReference objectReference)
    {
        ObjectFingerprintIndex objFpIndex = new ObjectFingerprintIndex();
        objFpIndex.setId(objectReference.getId());
        objFpIndex.setSha1(objectReference.getSha1());
        objectFrIndexDAO.delete(objFpIndex);
        
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteObjectRef(ObjectReference objRef) {
        //先删除文件
        int count = objectReferenceDAO.deleteCheckRef(objRef);

        //TODO: 通知预览服务器删除文件
        filePreviewManager.deleteByObjectId(objRef.getId());

        return count;
    }
    
    @Override
    public INode getAndCheckNode(Long ownerId, Long nodeId, byte type) throws BaseRunException
    {
        
        BaseRunException exception = null;
        switch (type)
        {
            case INode.TYPE_FILE:
                exception = new NoSuchFileException("File not exist");
                break;
            case INode.TYPE_FOLDER_ALL:
            case INode.TYPE_FOLDER:
            case INode.TYPE_BACKUP_COMPUTER:
            case INode.TYPE_BACKUP_DISK:
            case INode.TYPE_WECHAT:
            case INode.TYPE_MIGRATION:
            case INode.TYPE_BACKUP_EMAIL:
            case INode.TYPE_INBOX:
            case INode.TYPE_IMAGE:
            case INode.TYPE_VIDEO:
            case INode.TYPE_DOCUMENT:
             	exception = new NoSuchFileException("folder not exist");
                break;
            case INode.TYPE_VERSION:
                exception = new NoSuchFileException("version not exist");
                break;
            case INode.TYPE_ALL:
                exception = new NoSuchItemsException("Node not exist");
                break;
            default:
                throw new InvalidParamException("Invalid parameter");
        }
        
        INode node = getINodeInfo(ownerId, nodeId);
        
        if (null == node)
        {
            LOGGER.error("Node not exist, owner id: {}, id: {}", ownerId, nodeId);
            throw exception;
        }
        
        if (INode.STATUS_NORMAL != node.getStatus())
        {
            LOGGER.error("Node status abnormal, owner id: {}, id: {}, status: {}",
                ownerId,
                nodeId,
                node.getStatus());
            throw exception;
        }
        
        doCheckNodeType(node, type, exception);
        return node;
    }
    
    private void doCheckNodeType(INode node, byte type, BaseRunException exception)
    {
        if (INode.TYPE_ALL != type)
        {
            if (INode.TYPE_FOLDER_ALL == type)
            {
                if (INode.TYPE_FOLDER != node.getType() && INode.TYPE_BACKUP_COMPUTER != node.getType()
                    && INode.TYPE_BACKUP_DISK != node.getType() && INode.TYPE_BACKUP_EMAIL != node.getType()
                    && INode.TYPE_MIGRATION != node.getType() && INode.TYPE_WECHAT!= node.getType()
                	&& INode.TYPE_INBOX!= node.getType()&& INode.TYPE_VIDEO!= node.getType()
                    && INode.TYPE_IMAGE!= node.getType()&& INode.TYPE_DOCUMENT!= node.getType())
                {
                    LOGGER.error("Invalid node type, owner id: {}, id: {}, type: {}",
                        node.getOwnedBy(),
                        node.getId(),
                        node.getType());
                    throw exception;
                }
            }
            else
            {
                if (node.getType() != type)
                {
                    LOGGER.error("Invalid node type, owner id: {}, id: {}, type: {}",
                        node.getOwnedBy(),
                        node.getId(),
                        node.getType());
                    throw exception;
                }
            }
        }
        
    }
    
    @Override
    public INode getAndCheckNode(UserToken token, Long ownerId, Long nodeId, byte type)
        throws BaseRunException
    {
        
        BaseRunException exception = null;
        switch (type)
        {
            case INode.TYPE_FILE:
                exception = new NoSuchFileException("File not exist");
                break;
            case INode.TYPE_FOLDER_ALL:
            case INode.TYPE_FOLDER:
            case INode.TYPE_BACKUP_COMPUTER:
            case INode.TYPE_BACKUP_DISK:
                exception = new NoSuchFolderException("Folder not exist");
                break;
            case INode.TYPE_BACKUP_EMAIL:
            case INode.TYPE_ALL:
                exception = new NoSuchItemsException("Node not exist");
                break;
            default:
                throw new InvalidParamException("Invalid parameter");
        }
        
        INode node = getINodeInfo(ownerId, nodeId);
        
        if (null == node)
        {
            LOGGER.error("Node not exist, owner id: {}, id: {}", ownerId, nodeId);
            throw exception;
        }
        
        if (INode.STATUS_NORMAL != node.getStatus())
        {
            LOGGER.error("Node status abnormal, owner id: {}, id: {}, status: {}",
                ownerId,
                nodeId,
                node.getStatus());
            throw exception;
        }
        
        doCheckNodeType(node, type, exception);
        String[] logMsgs = new String[]{String.valueOf(ownerId), String.valueOf(node.getParentId())};
        String keyword = StringUtils.trimToEmpty(node.getName());
        
        sendINodeEvent(token, EventType.OTHERS, node, null, UserLogType.GET_NODEINFO, logMsgs, keyword);
        return node;
    }
    
    @Override
    public INode getAndCheckNodeForDeleteFile(Long ownerId, Long nodeId) throws BaseRunException
    {
        
        INode node = getINodeInfo(ownerId, nodeId);
        
        if (null == node)
        {
            LOGGER.error("Node not exist, owner id: {}, id: {}", ownerId, nodeId);
            throw new NoSuchFileException("File not exist");
        }
        
        if (INode.STATUS_NORMAL != node.getStatus())
        {
            LOGGER.error("Node status abnormal, owner id: {}, id: {}, status: {}",
                ownerId,
                nodeId,
                node.getStatus());
            throw new NoSuchFileException("File not exist");
        }
        
        if (INode.TYPE_FILE != node.getType() && INode.TYPE_VERSION != node.getType())
        {
            LOGGER.error("Invalid node type, owner id: {}, id: {}, type: {}", ownerId, nodeId, node.getType());
            throw new NoSuchFileException("File not exist");
        }
        return node;
    }
    
    /**
     * 获取节点信息, 并校验状态和类型(用于获取节点路径接口)
     * 
     * @param ownerId 节点所有者ID
     * @param nodeId 节点ID
     * @return 节点信息
     * @throws BaseRunException 当节点不存在、节点状态不为正常/回收站/回收站子节点状态、类型为文件版本类型时抛出异常
     */
    @Override
    public INode getAndCheckNodeForGetNodePath(Long ownerId, Long nodeId) throws BaseRunException
    {
        INode node = getINodeInfo(ownerId, nodeId);
        
        // 节点已删除
        if (null == node || INode.STATUS_DELETE == node.getStatus())
        {
            LOGGER.warn("Node not exist, owner id: {}, id: {}", ownerId, nodeId);
            return null;
        }
        
        if (INode.STATUS_NORMAL != node.getStatus() && INode.STATUS_TRASH != node.getStatus()
            && INode.STATUS_TRASH_DELETE != node.getStatus())
        {
            throw new NoSuchItemsException("Node status abnormal: " + node.getStatus());
        }
        
        if (node.getType() == INode.TYPE_VERSION)
        {
            throw new NoSuchItemsException("Invalid node type: " + node.getType());
        }
        return node;
    }
    
    /**
     * 父节点状态校验
     * 
     * @param user
     * @param node
     * @return
     * @throws BaseRunException
     * @throws NoSuchParentException
     * @throws NoSuchItemsException
     */
    @Override
    public INode getAndCheckParentNode(INode node) throws BaseRunException
    {
        INode parentNode = null;
        
        // 非根目录
        if (INode.FILES_ROOT != node.getParentId())
        {
            // 父节点检测
            parentNode = getINodeInfo(node.getOwnedBy(), node.getParentId());
            
            if (null == parentNode)
            {
                LOGGER.error("Parent not found, ownerId:" + node.getOwnedBy() + ", id:" + node.getParentId());
                throw new NoSuchParentException();
            }
            
            if (parentNode.getType()>0)
            {
                LOGGER.error("illeagl parent node , ownerId:" + node.getOwnedBy() + ", id:"
                    + node.getParentId() + ",type: " + parentNode.getType());
                throw new NoSuchParentException();
            }
            
            if (parentNode.getStatus() != INode.STATUS_NORMAL)
            {
                LOGGER.error("inode status is abnormal , ownerId:" + node.getOwnedBy() + ", id:"
                    + node.getParentId() + ", Status:" + parentNode.getStatus());
                throw new NoSuchParentException();
            }
        }
        else
        {
            parentNode = new INode();
            parentNode.setId(INode.FILES_ROOT);
            parentNode.setOwnedBy(node.getOwnedBy());
            parentNode.setType(INode.TYPE_FOLDER);
        }
        return parentNode;
    }
    
    @Override
    public FileBasicConfig getFileBaiscConfig(UserToken userToken)
    {
        FileBasicConfig basicConfig = null;
        SystemConfig config = systemConfigDAO.get(FileBasicConfig.FILE_BASIC_CONFIG);
        if (config != null)
        {
            basicConfig = FileBasicConfig.buildConfigFromJson(config.getValue());
        }
        if (config == null)
        {
            basicConfig = new FileBasicConfig();
            basicConfig.setOverwriteAcl(FileBasicConfig.DEFAULT_OVERWRITE_ACL);
            basicConfig.setAllowBatch(FileBasicConfig.DEFAULT_ALLOW_PATCH);
        }
        return basicConfig;
    }
    
    /**
     * 获取节点信息
     * 
     * @param owner_id
     * @param inodeid
     * @return
     * @throws BaseRunException
     */
    @Override
    public INode getINodeInfo(long ownerId, long inodeid) throws BaseRunException
    {
        INode iNode = new INode();
        iNode.setOwnedBy(ownerId);
        iNode.setId(inodeid);
        return iNodeDAO.get(iNode);
    }
    
    /**
     * 获取节点信息
     * 
     * @param owner_id
     * @param inodeid
     * @return
     * @throws BaseRunException
     */
    @Override
    public INode getINodeInfo(long ownerId, long inodeid,byte type) throws BaseRunException
    {
        INode iNode = new INode();
        iNode.setOwnedBy(ownerId);
        iNode.setId(inodeid);
        iNode.setType(type);
        return iNodeDAO.get(iNode);
    }
    
    
    @Override
    public INode getINodeInfo(long ownerId, long parentNodeId, String nodeName) throws BaseRunException
    {
        return iNodeDAO.getINodeByName(ownerId, parentNodeId, nodeName);
    }
    
    @Override
    public INode getINodeInfoCheckStatus(long ownerId, long inodeId, byte status) throws BaseRunException
    {
        // 获取Inode信息,检测文件状态
        INode inode = getINodeInfo(ownerId, inodeId);
        
        if (null == inode)
        {
            String msg = "inode not exist, ownerid:" + ownerId + ",id:" + inodeId;
            throw new NoSuchFileException(msg);
        }
        
        if (inode.getStatus() != status)
        {
            String msg = "inode is abnormal, ownerid:" + inode.getOwnedBy() + ",id:" + inode.getId()
                + ",node status:" + inode.getStatus() + ",need status:" + status;
            throw new NoSuchItemsException(msg);
        }
        return inode;
    }
    
    /**
     * 获取读写的下载地址
     * 
     * @param node
     * @return
     * @throws BaseRunException
     */
    @Override
    public DataAccessURLInfo getINodeInfoDownURL(long userId, INode node) throws BaseRunException
    {
        return getDownLoadUrlInfo(userId, node, GETURLINFO);
    }
    
    /**
     * 获取读写的下载地址
     * 
     * @param node
     * @return
     * @throws BaseRunException
     */
    @Override
    public DataAccessURLInfo getINodeInfoDownURL(long userId, INode node, String objectId)
        throws BaseRunException
    {
        // 获取QOS端口
        User user = userDAO.get(userId);
        AuthApp authApp = authAppService.getByAuthAppID(user.getAppId());
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;
        
        String downloadObject = node.getObjectId();
        int resourceGroupId = node.getResourceGroupId();
        
        MirrorObject mirrorObject = filesInnerManager.getBestObjectMirrorShip(authApp,
            null,
            resourceGroupId,
            node);
        
        if (null != mirrorObject)
        {
            resourceGroupId = mirrorObject.getDestResourceGroupId();
            downloadObject = mirrorObject.getDestObjectId();
        }
        
        MirrorObject priorityMirrorObject = filesInnerManager.getResourceGroupByPriority(resourceGroupId, node);
        
        if(null!= priorityMirrorObject)
        {
            resourceGroupId = priorityMirrorObject.getDestResourceGroupId();
            downloadObject = priorityMirrorObject.getDestObjectId();
        }
        
        // 获取资源组信息
        DataAccessURLInfo urlinfo = dcUrlManager.getDownloadURL(resourceGroupId, qosPort);
        
        String nodeLastModified = DateUtils.dateToString(DateUtils.RFC822_DATE_FORMAT, node.getModifiedAt());
        
        UserToken token = userTokenHelper.createTokenDataServer(userId,
            objectId,
            AuthorityMethod.GET_OBJECT,
            node.getOwnedBy(),
            nodeLastModified);
        
        urlinfo.setDownloadUrl(urlinfo.getDownloadUrl() + token.getToken() + '/' + downloadObject + '/'
            + FilesCommonUtils.encodeUft8Value(node.getName()));
        
        if(null != urlReplaceTools)
        {
            urlReplaceTools.replaceDownloadUrl(authApp, urlinfo, userTokenHelper.getCurrentToken());
        }

        return urlinfo;
        
    }
    
    @Override
    public DataAccessURLInfo getINodeInfoDownURLWithoutName(long userId, INode node) throws BaseRunException
    {
        return getDownLoadUrlInfo(userId, node, GETURLINFOWITHOUTNAME);
        
    }
    
    private DataAccessURLInfo getDownLoadUrlInfo(long userId, INode node, String path)
    {
        // 获取QOS端口
        User user = userDAO.get(userId);
        AuthApp authApp = authAppService.getByAuthAppID(user.getAppId());
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;
        
        String downloadObject = node.getObjectId();
        int resourceGroupId = node.getResourceGroupId();
        
        MirrorObject mirrorObject = filesInnerManager.getBestObjectMirrorShip(authApp,
            null,
            resourceGroupId,
            node);
        
        if (null != mirrorObject)
        {
            resourceGroupId = mirrorObject.getDestResourceGroupId();
            downloadObject = mirrorObject.getDestObjectId();
        }
        
        MirrorObject priorityMirrorObject = filesInnerManager.getResourceGroupByPriority(resourceGroupId, node);
        
        if(null != priorityMirrorObject)
        {
            resourceGroupId = priorityMirrorObject.getDestResourceGroupId();
            downloadObject = priorityMirrorObject.getDestObjectId();
        }
        
        // 获取资源组信息
        DataAccessURLInfo urlinfo = dcUrlManager.getDownloadURL(resourceGroupId, qosPort);
        
        String nodeLastModified = DateUtils.dateToString(DateUtils.RFC822_DATE_FORMAT, node.getModifiedAt());
        
        UserToken token = userTokenHelper.createTokenDataServer(userId,
            downloadObject,
            AuthorityMethod.GET_OBJECT,
            node.getOwnedBy(),
            nodeLastModified);
        if (GETURLINFO.equals(path))
        {
            urlinfo.setDownloadUrl(urlinfo.getDownloadUrl() + token.getToken() + '/' + downloadObject + '/'
                + FilesCommonUtils.encodeUft8Value(node.getName()));
        }
        else if (GETURLINFOWITHOUTNAME.equals(path))
        {
            urlinfo.setDownloadUrl(urlinfo.getDownloadUrl() + token.getToken() + '/' + downloadObject
                + "/preview");
        }
        
        if(null != urlReplaceTools)
        {
            urlReplaceTools.replaceDownloadUrl(authApp, urlinfo, userTokenHelper.getCurrentToken());
        }
        
        return urlinfo;
    }
    
    @Override
    public List<INode> getNodeByObjectId(long ownerId, String objectId)
    {
        List<INode> nodeList = iNodeDAO.getINodeByObjectId(ownerId, objectId);
        return nodeList;
    }
    
    @Override
    public List<ObjectFingerprintIndex> getObjectPf(String md5,int resourceGroupID){
    	ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(resourceGroupID);
    	List<ObjectFingerprintIndex> lstObjFp = objectFrIndexDAO.getBySha1AndRegionID(md5, resourceGroup.getRegionId());
        if (null == lstObjFp|| lstObjFp.size()==0)
        {
            return null;
        }
        return lstObjFp;
    }
    
    
    @Override
    public ObjectReference getObjectRefByMD5CheckRID(int resourceGroupID, String md5, String blockMD5,
        long size) throws BaseRunException
    {
        ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(resourceGroupID);
        if (null == resourceGroup)
        {
            return null;
        }
        int regionId = resourceGroup.getRegionId();
        
        // 文件内容计算算法由sha1变更为md5, 数据库sha1字段名称不变, 保存MD5值
        List<ObjectFingerprintIndex> lstObjFp = objectFrIndexDAO.getBySha1AndRegionID(md5, regionId);
        if (null == lstObjFp)
        {
            return null;
        }
        
        ObjectReference objRef = null;
        for (ObjectFingerprintIndex tmpObjIndex : lstObjFp)
        {
            objRef = objectReferenceDAO.get(tmpObjIndex.getId());
            if (objRef == null || objRef.getSize() != size || objRef.getLastDeleteTime() != null)
            {
                continue;
            }
            // 不大于256字节的文件只比较整文件MD5
            if (objRef.getSize() <= Constants.SAMPLING_LENGTH_FOR_SMALLER_FILE)
            {
                return objRef;
            }
            // 大于256字节的文件比较整文件MD5和抽样MD5
            else if (blockMD5.equals(objRef.getBlockMD5()))
            {
                return objRef;
            }
        }
        return null;
    }
    
    /**
     * 获取引用对象，判断对象和Size和区域
     * 
     * @param regionID
     * @param objectId
     * @param size
     * @return
     * @throws BaseRunException
     */
    @Override
    public ObjectReference getObjectRefByObjectIDCheckRID(int regionID, String objectId, long size)
        throws BaseRunException
    {
        ObjectReference objRef = objectReferenceDAO.get(objectId);
        
        // 不存在，或者大小不一致则返回null
        if (null == objRef || objRef.getSize() != size)
        {
            return null;
        }
        
        ResourceGroup destResourceGroup = dcManager.getCacheResourceGroup(objRef.getResourceGroupId());
        if (null == destResourceGroup)
        {
            return null;
        }
        
        // 如果不是同一区域,不闪传
        if (regionID == destResourceGroup.getRegionId())
        {
            return objRef;
        }
        
        return null;
    }
    
    /**
     * 获取引用对象，判断对象SHA1和Size和区域
     * 
     * @param resourceGroupID
     * @param sha1
     * @param size
     * @return
     * @throws BaseRunException
     */
    @Override
    public ObjectReference getObjectRefBysha1CheckRID(int resourceGroupID, String sha1, long size)
        throws BaseRunException
    {
        ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(resourceGroupID);
        if (null == resourceGroup)
        {
            return null;
        }
        int regionId = resourceGroup.getRegionId();
        
        // 只选择同一区域的对象
        List<ObjectFingerprintIndex> lstObjFp = objectFrIndexDAO.getBySha1AndRegionID(sha1, regionId);
        if (null == lstObjFp)
        {
            return null;
        }
        
        ObjectReference objRef = null;
        for (ObjectFingerprintIndex tmpObjIndex : lstObjFp)
        {
            objRef = objectReferenceDAO.get(tmpObjIndex.getId());
            if (null != objRef && objRef.getSize() == size && objRef.getLastDeleteTime() == null)
            {
                return objRef;
            }
        }
        return null;
    }
    
    @Override
    public INode getParentINodeInfoCheckStatus(long ownerId, long inodeId, byte status)
        throws BaseRunException
    {
        // 获取Inode信息,检测文件状态
        INode inode = getINodeInfo(ownerId, inodeId);
        
        if (null == inode)
        {
            String msg = "parentinode not exist, ownerid:" + ownerId + ",id:" + inodeId;
            throw new NoSuchParentException(msg);
        }
        
        if (inode.getStatus() != status)
        {
            String msg = "parentinode is abnormal, ownerid:" + inode.getOwnedBy() + ",id:" + inode.getId()
                + ",node status:" + inode.getStatus() + ",need status:" + status;
            throw new NoSuchParentException(msg);
        }
        return inode;
    }
    
    /**
     * 获取同步版本号，如果出现垃圾数据异常，则创建新对象。
     * 
     * @param userId
     * @return
     */
    
    @Override
    public long getUserSyncVersion(long userId)
    {
        return userSyncVersionService.getNextUserSyncVersion(userId);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int increaseObjectRefCount(ObjectReference objRf)
    {
        return objectReferenceDAO.increaseRefCount(objRf);
    }
    
    @Override
    public boolean isSameNameNodeExist(Long ownerId, Long parentId, String name) throws BaseRunException
    {
        List<INode> list = iNodeDAO.getNodeByName(ownerId, parentId, name);
        return CollectionUtils.isNotEmpty(list);
    }
    
    @Override
    public boolean isSameNameNodeExistNoSelf(Long ownerId, Long parentId, Long nodeId, String name)
        throws BaseRunException
    {
        List<INode> list = iNodeDAO.getNodeByName(ownerId, parentId, name);
        for (INode item : list)
        {
            if (item.getId() != nodeId)
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public INode moveBaseNodeToFolder(long userId, INode inode, INode folderNode)
    {
        // 同用户移动，只需要修改parenetid.DTS2014012500138
        // 但是需要递归更新子文件及文件夹的同步版本号
        // 移动不更新修改时间，同windows一致
        inode.setParentId(folderNode.getId());
        inode.setModifiedBy(userId);
        inode.setOwnedBy(folderNode.getOwnedBy());
        iNodeDAO.updateForMove(inode);
        
        if (FilesCommonUtils.isFolderType(inode.getType()))
        {
            try
            {
                Locks.MOVE_LOCK.tryLock();
                proxySelf.setSubNodeSyncVersionByRecursive(inode);
            }
            finally
            {
                Locks.MOVE_LOCK.unlock();
            }
        }
        
        return inode;
    }
    
    /**
     * 发送INode日志
     * 
     * @param user
     * @param type
     * @param srcNode
     * @param destNode
     */
    @Override
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public void sendINodeEvent(UserToken user, EventType type, INode srcNode, INode destNode,
        UserLogType userLogType, String[] params, String keyword)
    {
        try
        {
            if (null == user)
            {
                return;
            }
            if (notNeedSendLogEvent(type) && notNeedSendLogEvent(userLogType))
            {
                return;
            }
            
            Event event = new Event(user, srcNode, type, userLogType, params, keyword);
            
            // 对重删的特殊处理
            if (EventType.INNER_DEDUP == event.getType())
            {
                event.setDest(destNode);
            }
            
            eventService.fireEvent(event);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        
    }
    
    private boolean notNeedSendLogEvent(EventType type)
    {
        if (type == null || type == EventType.OTHERS)
        {
            return true;
        }
        return false;
    }
    
    private boolean notNeedSendLogEvent(UserLogType userLogType)
    {
        if (userLogType == null || !userLogType.isEnable())
        {
            LOGGER.warn("user log not send:" + userLogType);
            return true;
        }
        return false;
    }
    
    /**
     * 设置节点同步状态, 新建节点(含复制)时默认设置根目录下的节点为同步状态
     * 
     * @param inode
     * @param parent
     */
    @Override
    public void setNodeSyncStatus(INode node, INode parent)
    {
        if (null == node)
        {
            return;
        }
        // 备份文件夹设置成不同的状态
        if (FilesCommonUtils.isBackupFolderType(node.getType()))
        {
            node.setSyncStatus(INode.SYNC_STATUS_BACKUP);
            return;
        }
        if (FilesCommonUtils.isEmailBackupFolderType(node.getType()))
        {
            node.setSyncStatus(INode.SYNC_STATUS_EMAIL);
            return;
        }
        // 根目录自动为同步状态
        if (parent != null)
        {
            if (parent.getId() == INode.FILES_ROOT)
            {
                node.setSyncStatus(INode.SYNC_STATUS_SETTED);
            }
            else if (parent.getSyncStatus() == INode.SYNC_STATUS_BACKUP)
            {
                node.setSyncStatus(INode.SYNC_STATUS_BACKUP);
            }
            else if (parent.getSyncStatus() == INode.SYNC_STATUS_EMAIL)
            {
                node.setSyncStatus(INode.SYNC_STATUS_EMAIL);
            }
            else
            {
                node.setSyncStatus(INode.SYNC_STATUS_UNSET);
            }
        }
        else
        {
            node.setSyncStatus(INode.SYNC_STATUS_UNSET);
        }
        
    }
    
    /**
     * 设置节点同步状态和同步版本号
     * 
     * @param inode
     * @param folderNode
     */
    @Override
    public void setNodeSyncStatusAndVersion(long userId, INode node, INode folderNode)
    {
        // 设置同步状态
        setNodeSyncStatus(node, folderNode);
        
        // 设置同步版本号
        setNodeSyncVersion(userId, node);
        
    }
    
    /**
     * 设置节点同步版本号
     * 
     * @param inode
     */
    @Override
    public void setNodeSyncVersion(long userId, INode node)
    {
        if (null == node)
        {
            return;
        }
        
        // 全盘备份的文件，不需要设定同步版本号
        if (node.getSyncStatus() == INode.SYNC_STATUS_BACKUP)
        {
            return;
        }
        
        // 邮件归档的文件，不需要设定同步版本号
        if (node.getSyncStatus() == 4)
        {
            return;
        }
        long syncVersion = getUserSyncVersion(userId);
        node.setSyncVersion(syncVersion);
    }
    
    @PostConstruct
    // ③ 初始化方法
    public void setSelf()
    {
        // 从上下文获取代理对象（如果通过proxtSelf=this是不对的，this是目标对象）
        // 此种方法不适合于prototype Bean，因为每次getBean返回一个新的Bean
        proxySelf = context.getBean(FileBaseServiceImpl.class);
    }
    
    /**
     * 递归复制
     * 
     * @param userId
     * @param srcNode
     * @param destFolder
     * @throws BaseRunException
     */
    @Override
    public void setSubNodeSyncVersionByRecursive(INode parentNode)
    {
        Limit limit;
        long offset = 0;
        List<INode> subNodeList = null;
        while (true)
        {
            // 批量更新节点的状态
            limit = new Limit(offset, LIMIT);
            subNodeList = iNodeDAO.getINodeByParent(parentNode, null, limit);
            if (CollectionUtils.isEmpty(subNodeList))
            {
                return;
            }
            for (INode node : subNodeList)
            {
                if (node.getStatus() != INode.STATUS_NORMAL)
                {
                    LOGGER.warn("INode status is not normal,status:" + node.getStatus() + ",ownerid:"
                        + node.getOwnedBy() + ",id:" + node.getId() + ",name:" + node.getName());
                    continue;
                }
                node.setSyncStatusFromParent(parentNode);
                if (FilesCommonUtils.isFolderType(node.getType()))
                {
                    // 本节点
                    iNodeDAO.updateINodeSyncVersion(node);
                    // 递归
                    try
                    {
                        Locks.MOVE_LOCK.tryLock();
                        setSubNodeSyncVersionByRecursive(node);
                    }
                    finally
                    {
                        Locks.MOVE_LOCK.unlock();
                    }
                }
                else if (INode.TYPE_FILE == node.getType())
                {
                    // 本节点
                    iNodeDAO.updateINodeSyncVersion(node);
                }
                else
                {
                    continue;
                }
            }
            offset += LIMIT;
        }
    }
    
    private long getUserMaxFileNum()
    {
        if (userMaxFileNum == null)
        {
            SystemConfig config = systemConfigDAO.get("user.max.file.num");
            userMaxFileNum = config == null || config.getValue() == null ? DEFAULT_MAX_FILE_COUNT
                : Long.parseLong(config.getValue());
        }
        return userMaxFileNum;
    }
    
    /**
     * 就近访问部分
     */
    @Override
    public DataAccessURLInfo getDownURLByNearAccess(Integer accessRegionId, long userId, INode inode) throws BaseRunException {
        LOGGER.info("accessRegionId is {}", accessRegionId);

        if (accessRegionId == null) {
            return getINodeInfoDownURL(userId, inode);
        }
        return filesInnerManager.getDownloadURL(inode, userId, accessRegionId);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public int updateKiaLabel(ObjectReference objRef)
    {
        return objectReferenceDAO.updateSecurityLabel(objRef.getSecurityLabel(),
            objRef.getSecurityVersion(),
            objRef.getId());
    }
    
    @Override
    public List<INode> lstContentsNodeFilterRGs(long userId, Limit limit,
        List<ResourceGroup> lstResourceGroups)
    {
        
        StringBuffer buffer = new StringBuffer();
        for (ResourceGroup tmp : lstResourceGroups)
        {
            buffer.append(tmp.getId());
            buffer.append(',');
        }
        
        String strRGS = buffer.substring(0, buffer.length() - 1);
        
        INode filter = new INode();
        filter.setOwnedBy(userId);
        LOGGER.info("not in resource group :" + strRGS);
        return iNodeDAOV2.lstContentsNodeFilterRGs(filter, limit, strRGS);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean replaceObjectForINode(INode node, ObjectReference objRf)
    {
        int ret = iNodeDAO.replaceObjectForINode(node, objRf);
        if (1 == ret)
        {
            return true;
        }
        LOGGER.info("replaceObjectForINode ret :" + ret);
        return false;
    }

	@Override
	public List<INode> getChildrensInods(Long ownerId, long parentId, byte type) {
		// TODO Auto-generated method stub
	    
        BaseRunException exception = null;
        switch (type)
        {
            case INode.TYPE_FILE:
                exception = new NoSuchFileException("File not exist");
                break;
            case INode.TYPE_FOLDER_ALL:
            case INode.TYPE_FOLDER:
            case INode.TYPE_BACKUP_COMPUTER:
            case INode.TYPE_BACKUP_DISK:
                exception = new NoSuchFolderException("Folder not exist");
                break;
            case INode.TYPE_BACKUP_EMAIL:
            case INode.TYPE_VERSION:
                exception = new NoSuchFileException("version not exist");
                break;
            case INode.TYPE_ALL:
                exception = new NoSuchItemsException("Node not exist");
                break;
            default:
                throw new InvalidParamException("Invalid parameter");
        }
        List<INode> nodes = getIChildrenNode(ownerId, parentId);
		        
		        if (0 == nodes.size())
		        {
		            LOGGER.info("Node no child, owner id: {}, id: {}", ownerId, parentId);
//		            throw exception;
		        }
//		        
//		        if (INode.STATUS_NORMAL != node.getStatus())
//		        {
//		            LOGGER.error("Node status abnormal, owner id: {}, id: {}, status: {}",
//		                ownerId,
//		                parentId,
//		                node.getStatus());
//		            throw exception;
//		        }
		        
	    return nodes;
	}

	private List<INode> getIChildrenNode(Long ownerId, long parentId) {
		// TODO Auto-generated method stub
		    INode iNode = new INode();
	        iNode.setOwnedBy(ownerId);
	        iNode.setId(parentId);
	        return iNodeDAO.getChiledrenNodes(iNode);
	}
}
