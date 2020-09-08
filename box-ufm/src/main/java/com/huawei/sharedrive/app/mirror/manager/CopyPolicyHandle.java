package com.huawei.sharedrive.app.mirror.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.DCManager;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.appdatamigration.manager.AppDataMigrationManager;
import com.huawei.sharedrive.app.mirror.datamigration.service.UserDataMigrationTaskService;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.service.CopyPolicyService;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.user.domain.User;
import com.huawei.sharedrive.app.user.service.UserService;
import com.huawei.sharedrive.app.utils.BusinessConstants;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.MethodLogAble;
import pw.cdmi.core.utils.RandomGUID;

/**
 * 复制策略handle,根据策略生成复制任务
 * 
 * @author c00287749
 * 
 */
@Service("copyPolicyHandle")
public class CopyPolicyHandle
{
    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private CopyTaskService copyTaskService;
    
    @Autowired
    private ResourceGroupService resourceGroupService;
    
    @Autowired
    private CopyPolicyService copyPolicyService;
    
    @Autowired
    private CopyTaskManager copyTaskManager;
    
    @Autowired
    private CopyConfigLocalCache copyConfigLocalCache;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private HandleMirrorCopy handleMirrorCopy;
    
    @Autowired
    private DCManager dcManager;
    
    @Autowired
    private UserDataMigrationTaskService userDataMigrationTaskService;
    
    @Autowired
    private MirrorObjectManager mirrorObjectManager;
    
    @Autowired
    private AppDataMigrationManager appDataMigrationManager;
    
    private static final int STATIC_LENGTH = 1000;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyPolicyHandle.class);
    
    /**
     * 复制文件夹消息处理
     * 
     * @param inode
     * @return
     */
    public void buildCopyTaskForCopyFolderNode(INode inode)
    {
        // 列举节点的版本
        Limit limit = new Limit();
        long start = 0L;
        limit.setOffset(start);
        limit.setLength(STATIC_LENGTH);
        List<INode> lstNode = null;
        
        while (true)
        {
            lstNode = filesInnerManager.getINodeByParent(inode, limit);
            if (null == lstNode || lstNode.isEmpty())
            {
                return;
            }
            
            for (INode iNode : lstNode)
            {
                if (FilesCommonUtils.isFolderType(iNode.getType()))
                {
                    buildCopyTaskForCopyFolderNode(iNode);
                }
                else if (INode.TYPE_FILE == iNode.getType())
                {
                    buildCopyTaskForCreateFileNode(iNode);
                }
                else
                {
                    continue;
                }
            }
            
            if (lstNode.size() < STATIC_LENGTH)
            {
                break;
            }
            limit.setOffset(limit.getOffset() + STATIC_LENGTH);
        }
        
    }
    
    /**
     * 根据策略生成复制任务
     * 
     * @param inode
     * @return
     */
    @MethodLogAble
    public void buildCopyTaskForCreateFileNode(INode iNode)
    {
        if (null == iNode)
        {
            LOGGER.info("inode is null");
            return;
        }
        
        // 检查当前是否有数据迁移任务，如果有，则不处理，等等后续的数据扫描任务触发
        if (userDataMigrationTaskService.checkUserDataMigrationIsRunning(iNode.getOwnedBy()))
        {
            LOGGER.info("The user " + iNode.getOwnedBy()
                + " executing data migraion,not can build mirro task ,the node id: ," + iNode.getId()
                + ",objectid:" + iNode.getObjectId());
            return;
        }
        
     
        // 形成任务
        List<CopyTask> lstCopyTask = buildCopyTaskForSameOwnedBy(iNode);
        
        if (null == lstCopyTask || lstCopyTask.isEmpty())
        {
            LOGGER.info("lstCopyTask is null,not task build");
            return;
        }
        
        lstCopyTask = copyTaskManager.filterSameTask(lstCopyTask);
        
        copyTaskService.saveCopyTask(lstCopyTask);
    }
    
    /**
     * 单一Node对象
     * 
     * @param iNode
     * @return
     */
    public List<CopyTask> buildCopyTaskForSingleNode(INode iNode)
    {
        if (null == iNode)
        {
            LOGGER.info("inode is null");
            return null;
        }
        
        // 检查当前是否有数据迁移任务，如果有，则不处理，等等后续的数据扫描任务触发
        if (userDataMigrationTaskService.checkUserDataMigrationIsRunning(iNode.getOwnedBy()))
        {
            LOGGER.info("The user " + iNode.getOwnedBy()
                + " executing data migraion,not can build mirro task ,the node id: ," + iNode.getId()
                + ",objectid:" + iNode.getObjectId());
            return null;
        }
        
        List<CopyPolicy> lstCopyPolicy = getCopyPolicyByINode(iNode);
        if (null == lstCopyPolicy || lstCopyPolicy.isEmpty())
        {
            LOGGER.info("get policy null, inode id is:" + iNode.getId());
            return null;
        }
        LOGGER.debug("get policy success, get count is:" + lstCopyPolicy.size());
        List<CopyTask> lstCopyTask = new ArrayList<CopyTask>(10);
        List<CopyTask> lst = null;
        for (CopyPolicy policy : lstCopyPolicy)
        {
            // 检查作用域是不是用户
            if (policy.getType() != MirrorCommonStatic.POLICY_APP
                && policy.getType() != MirrorCommonStatic.POLICY_APP_USER)
            {
                LOGGER.info("policy type is not valid " + policy.getType());
                continue;
            }
            if (policy.getType() == MirrorCommonStatic.POLICY_APP_USER)
            {
                long ownerId = iNode.getOwnedBy();
                // 检查该用户是否用复制策略
                if (!copyPolicyService.checkUserCopyPolicy(policy.getId(), ownerId))
                {
                    LOGGER.info("checkUserCopyPolicy dont pass,just ignore , ownerid is:" + ownerId);
                    continue;
                }
            }
            // 循环构建任务
            lst = buildCopyTask(iNode, policy);
            if (null != lst && !lst.isEmpty())
            {
                lstCopyTask.addAll(lst);
            }
            
        }
        return lstCopyTask;
    }
    
    public void handleDedupMsg(INode srcNode, INode destNode)
    {
        copyTaskManager.handleDedupMsg(srcNode, destNode);
    }
    
    /**
     * 对每一个节点，形成一个策略
     * 
     * @param iNode
     * @param policy
     * @return
     */
    @MethodLogAble
    private List<CopyTask> buildCopyTask(INode iNode, CopyPolicy policy)
    {
        if (null == iNode || null == policy)
        {
            LOGGER.info("iNode is null or policy is null ");
            return null;
        }
        
        if (INode.STATUS_DELETE == iNode.getStatus() || INode.STATUS_CREATING == iNode.getStatus())
        {
            LOGGER.info("iNode state error ,state:" + iNode.getStatus());
            return null;
        }
        
        List<CopyPolicySiteInfo> lstSiteInfo = policy.getLstCopyPolicyDataSiteInfo();
        if (null == lstSiteInfo || lstSiteInfo.isEmpty())
        {
            LOGGER.info("lstSiteInfo is null ");
            return null;
        }
        
        List<CopyTask> lstCopyTask = new ArrayList<CopyTask>(BusinessConstants.INITIAL_CAPACITIES);
        int srcRegionId;
        CopyTask task = null;
        String randomGuid = null;
        Date createdAt = null;
        RandomGUID randomGen = new RandomGUID(true);
        for (CopyPolicySiteInfo siteInfo : lstSiteInfo)
        {
            srcRegionId = getINodeRegionId(iNode);
            if (siteInfo.getSrcResourceGroupId() != iNode.getResourceGroupId())
            {
                LOGGER.info("this file'ResourceGroup is suit the policy,inode resourceGroup"
                    + iNode.getResourceGroupId() + ",policy srcResourceGroup:"
                    + siteInfo.getSrcResourceGroupId());
                continue;
            }
            task = new CopyTask();
            randomGuid = randomGen.getValueAfterMD5();
            task.setTaskId(randomGuid);
            task.setCopyType(policy.getCopyType());
            task.setDestRegionId(siteInfo.getDestRegionId());
            task.setDestResourceGroupId(siteInfo.getDestResourceGroupId());
            // 选择合适的resorucegroup,如果没有合适的，先填写默认值，等后续执行任务的时候在选择一个合适的。
            
            task.setSize(iNode.getSize());
            task.setSrcINodeId(iNode.getId());
            task.setSrcObjectId(iNode.getObjectId());
            task.setSrcRegionId(srcRegionId);
            task.setSrcResourceGroupId(iNode.getResourceGroupId());
            task.setDestINodeId(iNode.getId());
            task.setDestObjectId(filesInnerManager.buildObjectID());
            task.setExeType(policy.getExeType());
            task.setDestOwnedBy(iNode.getOwnedBy());
            task.setSrcOwnedBy(iNode.getOwnedBy());
            
            // 定时执行任务，状态位未激活
            if (policy.getExeType() == MirrorCommonStatic.EXE_TYPE_TIME)
            {
                task.setState(MirrorCommonStatic.TASK_STATE_NOACTIVATE);
                task.setExeStartAt(policy.getExeStartAt());
                task.setExeEndAt(policy.getExeEndAt());
            }
            else
            {
                task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
            }
            
            // 全局控制任務狀態
            if (MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE == copyConfigLocalCache.getSystemMirrorTaskState())
            {
                task.setState(MirrorCommonStatic.TASK_STATE_SYSTEM_PAUSE);
            }
            
            task.setPolicyId(policy.getId());
            task.setPriority(MirrorCommonStatic.PRIORITY_TYPE_COMMON);
            
            createdAt = new Date();
            task.setCreatedAt(createdAt);
            
            /**
             * 检查需要复制的内容是否已经存在的了
             */
            //对数据迁移特殊处理
            if(task.getCopyType()==CopyType.COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA.getCopyType()
                ||task.getCopyType()==CopyType.COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA.getCopyType())
            {
                if(mirrorObjectManager.isCopyTaskAlreadyExist(task) != null)
                {
                    appDataMigrationManager.handleMirrorExists(iNode, task);
                    continue;
                }
            }
            else
            {
                if (handleMirrorCopy.handleDataIsExistingBeforeCopy(task))
                {
                    LOGGER.debug("this task has already exist, srcobjectid is:" + task.getSrcObjectId());
                    continue;
                }
            }
            lstCopyTask.add(task);
        }
        return lstCopyTask;
    }
    
    /**
     * 
     * @param iNode
     * @return
     */
    @MethodLogAble
    public List<CopyTask> buildCopyTaskForSameOwnedBy(INode iNode)
    {
        if (null == iNode)
        {
            LOGGER.info("iNode is null");
            return null;
        }
        
        List<CopyPolicy> lstCopyPolicy = getCopyPolicyByINode(iNode);
        
        if (null == lstCopyPolicy || lstCopyPolicy.isEmpty())
        {
            LOGGER.info("lstCopyPolicy is null");
            return null;
        }
        
        List<CopyTask> lstCopyTask = new ArrayList<CopyTask>(10);
        List<CopyTask> lst = null;
        
        for (CopyPolicy policy : lstCopyPolicy)
        {
            // 检查作用域是不是用户
            if (policy.getType() != MirrorCommonStatic.POLICY_APP
                && policy.getType() != MirrorCommonStatic.POLICY_APP_USER)
            {
                // 不做任何 事情
                LOGGER.info("policy type is error");
                continue;
            }
            
            if (policy.getType() == MirrorCommonStatic.POLICY_APP_USER)
            {
                long ownerId = iNode.getOwnedBy();
                // 检查该用户是否用复制策略
                if (!copyPolicyService.checkUserCopyPolicy(policy.getId(), ownerId))
                {
                    continue;
                }
            }
            
            // 循环构建任务
            lst = buildCopyTask(iNode, policy);
            if (null != lst && !lst.isEmpty())
            {
                lstCopyTask.addAll(lst);
            }
        }
        return lstCopyTask;
    }
    
    /**
     * /** 根据节点返回存储策略
     * 
     * @return
     */
    private List<CopyPolicy> getCopyPolicyByINode(INode iNode)
    {
        
        User user = userService.get(iNode.getOwnedBy());
        if (null == user)
        {
            LOGGER.error("cannot find user using ownedby num:" + iNode.getOwnedBy());
            return null;
        }
        List<CopyPolicy> tmPolicies = copyConfigLocalCache.getLstCopyPolicy(user.getAppId());
        if (null == tmPolicies)
        {
            LOGGER.error("cannot get policy from copyConfigLocalCache");
            return null;
        }
        
        return tmPolicies;
    }
    
    /**
     * 
     * 获取节点的区域ID
     * 
     * @param iNode
     * @return
     */
    private int getINodeRegionId(INode iNode)
    {
        // 优先查询缓存
        ResourceGroup group = dcManager.getResourceGroup(iNode.getResourceGroupId());
        if (null != group)
        {
            return group.getRegionId();
        }
        
        group = resourceGroupService.getResourceGroup(iNode.getResourceGroupId());
        if (null == group)
        {
            return -1;
        }
        
        return group.getRegionId();
    }
    
}
