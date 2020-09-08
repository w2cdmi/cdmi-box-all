package com.huawei.sharedrive.app.files.manager;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.dataserver.domain.DataAccessURLInfo;
import com.huawei.sharedrive.app.dataserver.domain.DataCenter;
import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.DCUrlManager;
import com.huawei.sharedrive.app.dataserver.thrift.client.FileObjectThriftServiceClient;
import com.huawei.sharedrive.app.dataserver.url.URLReplaceTools;
import com.huawei.sharedrive.app.dns.service.DssDomainService;
import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.exception.NoSuchUserException;
import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.dao.INodeDAOSlaveDB;
import com.huawei.sharedrive.app.files.dao.ObjectFingerprintIndexDAO;
import com.huawei.sharedrive.app.files.dao.ObjectReferenceDAO;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.domain.ObjectFingerprintIndex;
import com.huawei.sharedrive.app.files.domain.ObjectReference;
import com.huawei.sharedrive.app.files.service.FileBaseService;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.MirrorObject;
import com.huawei.sharedrive.app.mirror.manager.CopyConfigLocalCache;
import com.huawei.sharedrive.app.mirror.manager.MirrorObjectManager;
import com.huawei.sharedrive.app.mirror.manager.NearAccessManager;
import com.huawei.sharedrive.app.mirror.manager.ObjectMirrorManager;
import com.huawei.sharedrive.app.oauth2.domain.UserToken;
import com.huawei.sharedrive.app.oauth2.service.Authorize.AuthorityMethod;
import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;
import com.huawei.sharedrive.app.system.service.SystemConfigService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pw.cdmi.box.app.convertservice.domain.ImgObject;
import pw.cdmi.box.app.convertservice.service.ConvertService;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.RandomGUID;
import pw.cdmi.uam.domain.AuthApp;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 文件管理，供内部模块使用
 * 
 * @author c00287749
 * 
 */
@Service("filesInnerManager")
public class FilesInnerManager implements ConfigListener
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesInnerManager.class);
    
    @Autowired
    private AuthAppService authAppService;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private DCUrlManager dcUrlManager;
    
    @Autowired
    private DssDomainService dssDomainService;
    
    @Autowired
    private FileBaseService fileBaseService;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
    @Autowired
    private INodeDAOSlaveDB iNodeDAOSlaveDB;
    
    @Autowired
    private NearAccessManager nearAccessManager;
    
    @Autowired
    private ObjectFingerprintIndexDAO objectFrIndexDAO;
    
    @Autowired
    private ObjectReferenceDAO objectReferenceDAO;
    
    @Autowired
    private UserDAOV2 userDAO;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Autowired
    private ObjectMirrorManager objectMirrorManager;
    
    @Autowired
    private MirrorObjectManager mirrorObjectManager;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired(required = false)
    @Qualifier("urlReplaceTools")
    private URLReplaceTools urlReplaceTools;
    
    @Autowired
    private CopyConfigLocalCache copyPolicyLocalCache;
    
    @Autowired
    private ConvertService convertService;
    
    private static final String SLAVEDB_CONFIG = "mirror.use.slavedb";
    
    private boolean useSlave;
    
    private static final String CONFIG_ZOOKEEPER_KEY_USE_SLAVEDB_CHANGE = "config.zookeeper.key.use.slavedb.change";
    
    public String buildObjectID()
    {
        return new RandomGUID().getValueAfterMD5();
    }
    
    @PostConstruct
    public void init()
    {
        SystemConfig systemConfig = systemConfigService.getConfig(SLAVEDB_CONFIG);
        if (systemConfig == null)
        {
            LOGGER.info("get systemConfig is null by:" + SLAVEDB_CONFIG);
            useSlave = false;
            return;
        }
        
        useSlave = Boolean.parseBoolean(systemConfig.getValue());
        LOGGER.info("useSlave value is:" + useSlave);
    }
    
    /**
     * 根据FileNode创建对象
     * 
     * @param fileNode
     */
    @MethodLogAble
    public ObjectReference createObject(INode fileNode)
    {
        if (null == fileNode)
        {
            LOGGER.error("fileNode is null ");
            throw new BusinessException();
        }
        ResourceGroup resourceGroup = dcManager.getCacheResourceGroup(fileNode.getResourceGroupId());
        
        if (null == resourceGroup)
        {
            LOGGER.error("resourceGroup is null ,resourceGroup id:" + fileNode.getResourceGroupId());
            throw new BusinessException();
        }
        
        ObjectReference objRef = fileBaseService.createObjectRef(fileNode);
        
        fileBaseService.createObjectFrIndex(objRef, resourceGroup.getRegionId());
        
        return objRef;
        
    }
    
    public boolean updateKiaLabel(ObjectReference obj)
    {
        return fileBaseService.updateKiaLabel(obj) == 1;
    }
    
    public boolean decreaseRefObjectCount(ObjectReference objRf)
    {
        return fileBaseService.decreaseRefObjectCount(objRf) > 0;
    }
    
    public boolean checkObjectExistingForINode(long ownerId, String objectId)
    {
        if (!fileBaseService.getNodeByObjectId(ownerId, objectId).isEmpty())
        {
            return true;
        }
        return false;
    }
    
    /**
     * 删除DSS对象，不做其他事情
     * 
     * @param resourceGroupId
     * @param objectId
     */
    public void deleteDSSObject(int resourceGroupId, String objectId)
    {
        // 删除对象
        ResourceGroup group = dcManager.getCacheResourceGroup(resourceGroupId);
        String domain = dssDomainService.getDomainByDssId(group);
        FileObjectThriftServiceClient client = null;
        try
        {
            client = new FileObjectThriftServiceClient(domain, group.getManagePort());
            client.deleteFileObject(objectId);
        }
        catch (Exception e)
        {
            LOGGER.warn(e.getMessage(), e);
        }
        finally
        {
            if (null != client)
            {
                client.close();
            }
        }
    }
    
    /**
     * 获取最佳的镜像
     * 
     * @param authApp
     * @param accessRegionId
     * @param resourceGroupId
     * @param inode
     * @return
     */
    public MirrorObject getBestObjectMirrorShip(AuthApp authApp, Integer accessRegionId, int resourceGroupId,
        INode inode)
    {
        
        // 检查资源组是否存活，不存活直接访问备
        ResourceGroup resourceGroup = dcManager.checkResourceGroupIsActive(resourceGroupId);
        if (null != resourceGroup)
        {
            // 源节点存活状态判断就近上传是否开启，
            if (authApp == null || null == accessRegionId)
            {
                LOGGER.info("authApp or accessRegionId is null");
                return null;
            }
            
            if (!isOpenNear(authApp))
            {
                LOGGER.info("The app no enable near access");
                return null;
            }
            
            if (accessRegionId.intValue() == resourceGroup.getRegionId())
            {
                LOGGER.info("access region == src region");
                return null;
            }
            
        }
        
        // 查看是否有镜像 对象
        List<MirrorObject> lstMirrorObject = mirrorObjectManager.getMirrorObjectByOwnedByAndSrcObjectId(inode.getOwnedBy(),
            inode.getObjectId());
        Map<Integer, MirrorObject> map = new HashMap<Integer, MirrorObject>(10);
        
        if (null == lstMirrorObject || lstMirrorObject.isEmpty())
        {
            LOGGER.info("not mirror object");
            return null;
        }
        
        List<Integer> lstSrcResourceGroup = getResourceGroupList(resourceGroupId, lstMirrorObject, map);
        
        // 只有不为空的时候，进行优选
        if (lstSrcResourceGroup.isEmpty())
        {
            LOGGER.info("lstSrcResourceGroup is isEmpty");
            return null;
        }
        
        resourceGroupId = nearAccessManager.getNearResourceGroup(accessRegionId, lstSrcResourceGroup);
        
        LOGGER.info("getNearResourceGroup,resourceGroupId:" + resourceGroupId);
        
        return map.get(Integer.valueOf(resourceGroupId));
        
    }
    
    /**
     * 下载地址
     * 
     * @param inode ID
     * @param userId 用户
     * @param accessRegionId 访问的存储区域
     * @param objectId 对象本身
     * @return
     */
    public DataAccessURLInfo getDownloadURL(INode inode, long userId, Integer accessRegionId)
    {
        
        String downloadObject = inode.getObjectId();
        
        User user = userDAO.get(inode.getOwnedBy());
        if (null == user)
        {
            LOGGER.error("user no exist,userid :" + inode.getOwnedBy() + ",inode id:" + inode.getId());
            throw new NoSuchUserException("CloudUserId:" + inode.getOwnedBy());
        }
        AuthApp authApp = authAppService.getByAuthAppID(user.getAppId());
        Integer qosPort = authApp != null ? authApp.getQosPort() : null;
        
        int resourceGroupId = inode.getResourceGroupId();
        
        MirrorObject mirrorObject = getBestObjectMirrorShip(authApp, accessRegionId, resourceGroupId, inode);
        
        if (null != mirrorObject)
        {
            resourceGroupId = mirrorObject.getDestResourceGroupId();
            downloadObject = mirrorObject.getDestObjectId();
        }
        
        MirrorObject priorityMirrorObject = getResourceGroupByPriority(resourceGroupId, inode);
        
        if (null != priorityMirrorObject)
        {
            resourceGroupId = priorityMirrorObject.getDestResourceGroupId();
            downloadObject = priorityMirrorObject.getDestObjectId();
        }
        
        DataAccessURLInfo urlinfo = dcUrlManager.getDownloadURL(resourceGroupId, qosPort);
        
        String nodeLastModified = DateUtils.dateToString(DateUtils.RFC822_DATE_FORMAT, new Date());
        
        UserToken token = userTokenHelper.createTokenDataServer(userId,
            downloadObject,
            AuthorityMethod.GET_OBJECT,
            inode.getOwnedBy(),
            nodeLastModified);
        
        String url = urlinfo.getDownloadUrl() + token.getToken() + '/' + downloadObject + '/';
        
        if (StringUtils.isNotBlank(inode.getName()))
        {
            url = url + FilesCommonUtils.encodeUft8Value(inode.getName());
        }
        else
        {
            // 没有名字反馈objectid代替名字
            url = url + inode.getObjectId();
        }
        
        urlinfo.setDownloadUrl(url);
        
        if (null != urlReplaceTools)
        {
            urlReplaceTools.replaceDownloadUrl(authApp, urlinfo, userTokenHelper.getCurrentToken());
        }
        
        return urlinfo;
        
    }
    
    /**
     * 列举文件的子文件
     * 
     * @param filter
     * @param limit
     * @return
     */
    public List<INode> getINodeByParent(INode filter, Limit limit)
    {
        List<INode> lstNode = iNodeDAO.getINodeByParent(filter, null, limit);
        
        return lstNode;
    }
    
    @MethodLogAble
    public List<INode> getINodeVersion(INode filter)
    {
        List<INode> versionsINode = iNodeDAO.getSubINodeAndSelf(filter, null, null);
        return versionsINode;
    }
    
    /**
     * 获取对象ID
     * 
     * @param ownerId
     * @param objectId
     * @return
     */
    public List<INode> getObject(long ownerId, String objectId)
    {
        return iNodeDAO.getINodeByObjectId(ownerId, objectId);
    }
    
    public ObjectReference getObjectReference(String objectId)
    {
        return objectReferenceDAO.get(objectId);
    }
    
    /**
     * 获取同一数据区域的对象，但是不包对象本身
     * 
     * @param resourceGroupID
     * @param md5
     * @param blockMD5
     * @param size
     * @return
     */
    public List<ObjectReference> getSameContentObject(String srcObjectId, int regionId)
    {
        
        ObjectReference srcObjRef = objectReferenceDAO.get(srcObjectId);
        if (null == srcObjRef || StringUtils.isBlank(srcObjRef.getSha1()))
        {
            return null;
        }
        
        // 文件内容计算算法由sha1变更为md5, 数据库sha1字段名称不变, 保存MD5值
        List<ObjectFingerprintIndex> lstObjFp = objectFrIndexDAO.getBySha1AndRegionID(srcObjRef.getSha1(),
            regionId);
        if (null == lstObjFp)
        {
            return null;
        }
        List<ObjectReference> objects = new ArrayList<ObjectReference>(10);
        ObjectReference objRef = null;
        for (ObjectFingerprintIndex tmpObjIndex : lstObjFp)
        {
            objRef = objectReferenceDAO.get(tmpObjIndex.getId());
            if (objRef == null)
            {
                continue;
            }
            // 不包含对象本身
            if (objRef.hasSameContents(srcObjRef))
            {
                objects.add(objRef);
            }
        }
        
        return objects;
    }
    
    /**
     * 为INODE节点仅仅替换object内容
     * 
     * @param inode
     * @param objRf
     */
    private boolean replaceObject(INode inode, ObjectReference objRf)
    {
        // 更新是否失败，等于1
        if (!fileBaseService.replaceObjectForINode(inode, objRf))
        {
            // 可能是发现ID变化或者对象不存在
            List<INode> lstNode = getObject(inode.getOwnedBy(), inode.getObjectId());
            if (null == lstNode || lstNode.isEmpty())
            {
                // 原对象不存在，认为迁移成功
                LOGGER.error("It had happen major error, inode not existing" + ",it's owner:"
                    + inode.getOwnedBy() + ",id:" + inode.getId() + ",objectid:" + inode.getObjectId());
                
                return false;
            }
            
            for (INode tmpNode : lstNode)
            {
                // 多版本场景，原始的ID是父ID,新上传的对象使用了父ID,
                if (tmpNode.getParentId() == inode.getId())
                {
                    // 替换
                    return fileBaseService.replaceObjectForINode(tmpNode, objRf);
                    
                }
                
                continue;
            }
            
            return false;
        }
        return true;
    }
    
    /**
     * 清理老数据
     * 
     * @param inode
     * @param objRf
     * @param isNewObject
     * @return
     */
    public ReplaceObjectStatus changeNodeObject(CopyTask task, INode inode, ObjectReference objRf,
        boolean isNewObject)
    {
        return changeNodeObject(task, inode, objRf, isNewObject, true);
    }
    
    /**
     * 为节点替换Object,包含引用计算
     * 
     * @param inode
     * @param objRf
     */
    public ReplaceObjectStatus changeNodeObject(CopyTask task, INode inode, ObjectReference objRf,
        boolean isNewObject, boolean isClearOldObject)
    {
        ReplaceObjectStatus bRet = ReplaceObjectStatus.FINISH_STATUS;
        
        // 查找INODE的对象是否存在
        List<INode> lstNode = getObject(inode.getOwnedBy(), inode.getObjectId());
        if (null == lstNode || lstNode.isEmpty())
        {
            // 原对象不存在，认为迁移成功
            LOGGER.error("It had happen major error, inode not existing" + ",it's owner:"
                + inode.getOwnedBy() + ",id:" + inode.getId() + ",objectid:" + inode.getObjectId());
            
            return ReplaceObjectStatus.SRC_NOT_EXIST_STATUS;
        }
        
        // 查询出老的old_objectRef
        ObjectReference oldObjRef = getObjectReference(inode.getObjectId());
        
        if (null == oldObjRef || oldObjRef.getRefCount() <= 0)
        {
            // 对象不存在，程序不应该走到这里，为了后续流程走通，不报抛出异常
            LOGGER.error("It had happen major error, inode' object not existing");
            
            return ReplaceObjectStatus.SRC_NOT_EXIST_STATUS;
        }
        
        // 如果是老对象，需要增加引用计算；如果是新对象，创建对象的时已经默认有引起计算为1了，只是没有雨INODE建立关系
        if (!isNewObject)
        {
            // 对需要目标对象objRf新对象+1
            if (!increaseObjectRefCount(objRf))
            {
                LOGGER.error("increaseObjectRefCount failed." + objRf.getId());
                return ReplaceObjectStatus.INNER_OPER_FAILED_STATUS;
            }
        }
        
        try
        {
            // 更新INODE对象
            if (!replaceObject(inode, objRf))
            {
                bRet = ReplaceObjectStatus.INNER_OPER_FAILED_STATUS;
            }
        }
        catch (Exception e)
        {
            bRet = ReplaceObjectStatus.INNER_OPER_FAILED_STATUS;
            // objRf 需要-1
            decreaseRefObjectCount(objRf);
            
            // 抛出异常
            throw new InternalServerErrorException(e);
        }
        
        try
        {
            if (null != oldObjRef.getSecurityLabel()
                && StringUtils.isNotBlank(oldObjRef.getSecurityVersion()))
            {
                objRf.setSecurityLabel(oldObjRef.getSecurityLabel());
                objRf.setSecurityVersion(oldObjRef.getSecurityVersion());
                
                if (!updateKiaLabel(objRf))
                {
                    LOGGER.error("update objectRef KiaLabel error, objectid is:" + objRf.getId());
                }
                
            }
            
            /**
             * 判断是否要做老数据删除，不删除则做新对象和老对象的mirror关系
             */
            if (isClearOldObject)
            {
                // 清楚老对象，及老对象的对象关系
                clearOldObject(oldObjRef);
            }
            else
            {
                reCreateMirrorForObjectMigration(task, inode, objRf, oldObjRef);
            }
            
        }
        catch (Exception e)
        {
            // 不做异常处理，可能存在数据多余，但是不存在数据丢失
            LOGGER.error("decrease old object refcount failed.", e);
        }
        
        // 不做异常处理
        return bRet;
    }
    
    private void clearOldObject(ObjectReference oldObjRef)
    {
        // oldObjRef 做计计数-1
        decreaseRefObjectCount(oldObjRef);
        
    }
    
    /**
     * 为对象数据迁移做构建新的镜像关系 1:删除老对象的镜像关系 2:老对象和迁移对象形成新的镜像关系
     * 
     * @param objRef
     * @param oldObjRef
     */
    private void reCreateMirrorForObjectMigration(CopyTask task, INode inode, ObjectReference objRef,
        ObjectReference oldObjRef)
    {
        // 首先鄢删除老对象的镜像关系
        objectMirrorManager.clearMirrorObject(oldObjRef.getId());
        
        // 新对象和老对象建立镜像关系
        objectMirrorManager.createMirrorObject(task, inode, objRef, oldObjRef);
        
    }
    
    public boolean increaseObjectRefCount(ObjectReference objRf)
    {
        return (fileBaseService.increaseObjectRefCount(objRf) > 0);
    }
    
    /**
     * 是否开启就近下载
     * 
     * @return
     */
    public boolean isOpenNear(AuthApp authApp)
    {
        if (!NearAccessManager.isSystemNearAccessEnable())
        {
            LOGGER.info("isSystemNearAccessEnable fasle");
            return false;
        }
        
        return nearAccessManager.getNearAccessEnable(authApp.getAuthAppId());
    }
    
    /**
     * 获取节点表
     * 
     * @param userdbNumber
     * @param tableNumber
     * @param limit
     * @return
     */
    public List<INode> lstContentNode(int userdbNumber, int tableNumber, Limit limit)
    {
        if (useSlave)
        {
            return iNodeDAOSlaveDB.lstContentNode(userdbNumber, tableNumber, limit);
        }
        
        return iNodeDAO.lstContentNode(userdbNumber, tableNumber, limit);
    }
    
    /**
     * 备库获取节点表
     * 
     * @param userdbNumber
     * @param tableNumber
     * @param limit
     * @return
     */
    public List<INode> lstContentNodeBySlaveDB(int userdbNumber, int tableNumber, Limit limit)
    {
        return iNodeDAOSlaveDB.lstContentNode(userdbNumber, tableNumber, limit);
    }
    
    private List<Integer> getResourceGroupList(int resourceGroupId, List<MirrorObject> lstMirrorObject,
        Map<Integer, MirrorObject> map)
    {
        List<Integer> lstSrcResourceGroup = new ArrayList<Integer>(10);
        // 检查存活状态
        if (null != dcManager.checkResourceGroupIsActive(resourceGroupId))
        {
            // 如果原对象也存在的时候，也添加到src中
            lstSrcResourceGroup.add(Integer.valueOf(resourceGroupId));
            // 不添加到MAP中
        }
        else
        {
            LOGGER.info("the " + resourceGroupId + " not Active");
        }
        
        for (MirrorObject mirrorObject : lstMirrorObject)
        {
            // 检查存活状态
            if (null != dcManager.checkResourceGroupIsActive(mirrorObject.getDestResourceGroupId()))
            {
                map.put(mirrorObject.getDestResourceGroupId(), mirrorObject);
                
                lstSrcResourceGroup.add(Integer.valueOf(mirrorObject.getDestResourceGroupId()));
            }
            else
            {
                LOGGER.info("the " + mirrorObject.getDestResourceGroupId() + " not Active");
            }
            
        }
        return lstSrcResourceGroup;
    }
    
    /**
     * 列举不在这些资源组的对象
     * 
     * @param userId
     * @param limit
     * @param lstResourceGroups，不在这些资源组的对象
     * @return
     */
    public List<INode> lstContentsNodeFilterRGs(long userId, Limit limit,
        List<ResourceGroup> lstResourceGroups)
    {
        return fileBaseService.lstContentsNodeFilterRGs(userId, limit, lstResourceGroups);
    }
    
    /**
     * 列举每张表中同一个资源组的文件的总数和总大小
     * 
     * @return
     */
    public Map<Long, Long> lstFilesNumAndSizesByResourceGroup(int userdbNumber, int tableNumber,
        int resourceGroupId)
    {
        if (useSlave)
        {
            return iNodeDAOSlaveDB.lstFilesNumAndSizesByResourceGroup(userdbNumber,
                tableNumber,
                resourceGroupId);
        }
        return iNodeDAO.lstFilesNumAndSizesByResourceGroup(userdbNumber, tableNumber, resourceGroupId);
    }
    
    public Map<Long, Long> lstFilesNumAndSizesByResourceGroupBySlaveDB(int userdbNumber, int tableNumber,
        int resourceGroupId)
    {
        return iNodeDAOSlaveDB.lstFilesNumAndSizesByResourceGroup(userdbNumber, tableNumber, resourceGroupId);
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        LoggerUtil.regiestThreadLocalLog();
        if (!CONFIG_ZOOKEEPER_KEY_USE_SLAVEDB_CHANGE.equals(key))
        {
            return;
        }
        LOGGER.info("useSlave value modified ,reload it");
        init();
    }
    
    public MirrorObject getResourceGroupByPriority(int resourceGroupId, INode inode)
    {
        ResourceGroup resourseGroup = dcManager.getCacheResourceGroup(resourceGroupId);
        int regionId = resourseGroup.getRegionId();
        
        MirrorObject reMirrorObject = null;
        if (isHaveMoreDataCenter(regionId))
        {
            reMirrorObject = getAliveDatacenterByPriorty(regionId, inode);
        }
        
        return reMirrorObject;
    }
    
    private MirrorObject getAliveDatacenterByPriorty(int regionId, INode inode)
    {
        
        DataCenter dc = copyPolicyLocalCache.getPriortyDCbyRegionid(regionId);
        List<MirrorObject> lstMirror = mirrorObjectManager.getMirrorObjectByOwnedByAndSrcObjectId(inode.getOwnedBy(),
            inode.getObjectId());
        MirrorObject reMirrorObject = null;
        
        if (CollectionUtils.isEmpty(lstMirror) || null == dc)
        {
            return reMirrorObject;
        }
        
        return getBestObject(lstMirror, dc);
    }
    
    private MirrorObject getBestObject(List<MirrorObject> lstMirror, DataCenter dc)
    {
        MirrorObject reMirrorObject = null;
        for (MirrorObject mirrorObject : lstMirror)
        {
            if (null == dcManager.checkResourceGroupIsActive(mirrorObject.getDestResourceGroupId()))
            {
                break;
            }
            
            if (mirrorObject.getDestResourceGroupId() == dc.getResourceGroup().getDcId())
            {
                reMirrorObject = mirrorObject;
            }
        }
        return reMirrorObject;
    }
    
    private boolean isHaveMoreDataCenter(int regionId)
    {
        Map<Integer, ResourceGroup> resourceGroupMap = dcManager.getAllResourceGroup();
        Set<Integer> kSet = resourceGroupMap.keySet();
        Iterator<Integer> it = kSet.iterator();
        List<ResourceGroup> lstResourceGroup = new ArrayList<ResourceGroup>(
            BusinessConstants.INITIAL_CAPACITIES);
        
        ResourceGroup temp = null;
        while (it.hasNext())
        {
            temp = resourceGroupMap.get(it.next());
            if (temp.getRegionId() == regionId)
            {
                lstResourceGroup.add(temp);
            }
        }
        
        if (lstResourceGroup.size() > 1)
        {
            return true;
        }
        
        return false;
    }
}
