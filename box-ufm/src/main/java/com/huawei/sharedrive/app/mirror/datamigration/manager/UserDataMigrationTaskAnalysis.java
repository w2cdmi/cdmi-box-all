package com.huawei.sharedrive.app.mirror.datamigration.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.app.dataserver.domain.ResourceGroup;
import com.huawei.sharedrive.app.dataserver.service.ResourceGroupService;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.datamigration.domain.UserDataMigrationTask;
import com.huawei.sharedrive.app.mirror.datamigration.service.UserDataMigrationTaskService;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.job.Task;
import pw.cdmi.core.utils.RandomGUID;
import pw.cdmi.core.utils.SpringContextUtil;

/**
 * 数据迁移任务分解，列举用户数据，生成单文件的迁移任务
 * 
 * @author c00287749
 * 
 */
public class UserDataMigrationTaskAnalysis extends Task
{
    
    private UserDataMigrationTask task;
    
    private FilesInnerManager filesInnerManager;
    
    private ResourceGroupService resourceGroupService;
    
    private UserDataMigrationTaskService userDataMigrationTaskService;
    
    private HandleDataMigration handleDataMigration;
    
    private final static int MAX_LENGTHS = 10000;
    
    private long totalFiles = 0L;
    
    private long totalSizes = 0L;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataMigrationTaskAnalysis.class);
    
    private CopyTaskService copyTaskService;
    
    public UserDataMigrationTaskAnalysis(UserDataMigrationTask task)
    {
        this.task = task;
        this.filesInnerManager = (FilesInnerManager) SpringContextUtil.getBean("filesInnerManager");
        this.resourceGroupService = (ResourceGroupService) SpringContextUtil.getBean("resourceGroupService");
        this.copyTaskService = (CopyTaskService) SpringContextUtil.getBean("copyTaskService");
        this.userDataMigrationTaskService = (UserDataMigrationTaskService) SpringContextUtil.getBean("userDataMigrationTaskService");
        this.handleDataMigration = (HandleDataMigration) SpringContextUtil.getBean("handleDataMigration");
        
    }
    
    /**
     * 执行用户扫描任务
     */
    @Override
    public void execute()
    {
        
        try
        {
            jobExecute();
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
        }
        
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
        ResourceGroup group = resourceGroupService.getResourceGroup(iNode.getResourceGroupId());
        if (null == group)
        {
            return -1;
        }
        
        return group.getRegionId();
    }
    
    /**
     * 创建复制任务
     * 
     * @param iNode
     * @return
     */
    private CopyTask buildTaskForContentNode(INode iNode, UserDataMigrationTask datatask)
    {
        CopyTask task = new CopyTask();
        
        task.setTaskId(new RandomGUID(true).getValueAfterMD5());
        task.setCopyType(CopyType.COPY_TYPE_USER_DATA_MIGRATION.getCopyType());
        
        task.setDestRegionId(datatask.getDestRegionId());
        task.setDestResourceGroupId(datatask.getDestResourceGroupId());
        
        // 选择合适的resorucegroup,如果没有合适的，先填写默认值，等后续执行任务的时候在选择一个合适的。
        
        task.setSize(iNode.getSize());
        task.setSrcINodeId(iNode.getId());
        task.setSrcObjectId(iNode.getObjectId());
        
        task.setSrcRegionId(getINodeRegionId(iNode));
        task.setSrcResourceGroupId(iNode.getResourceGroupId());
        
        task.setDestINodeId(iNode.getId());
        task.setDestObjectId(filesInnerManager.buildObjectID());
        task.setExeType(MirrorCommonStatic.EXE_TYPE_NOW);
        task.setDestOwnedBy(iNode.getOwnedBy());
        task.setSrcOwnedBy(iNode.getOwnedBy());
        
        // 定时执行任务，状态位未激活
        task.setState(MirrorCommonStatic.TASK_STATE_WAITTING);
        
        task.setPolicyId(MirrorCommonStatic.DEFAULT_POLICY_ID);
        task.setPriority(MirrorCommonStatic.PRIORITY_TYPE_COMMON);
        
        task.setCreatedAt(new Date());
        
        return task;
    }
    
    private void cleanMirrorCopyTask(long userId)
    {
        long ret = copyTaskService.cleanMirrorCopyTask(userId);
        
        LOGGER.info("The userid:" + userId + ",clean task number:" + ret);
    }
    
    /**
     * 列举任务，并添加到数据库中
     */
    private void jobExecute()
    {
        
        List<ResourceGroup> groups = resourceGroupService.listGroupsByRegion(task.getDestRegionId());
        Limit limit = new Limit();
        
        limit.setOffset(0L);
        limit.setLength(MAX_LENGTHS);
        List<CopyTask> lstCopyTask = new ArrayList<CopyTask>(MAX_LENGTHS);
        
        /**
         * 清楚重复的copytask
         */
        cleanMirrorCopyTask(task.getCloudUserId());
        
        task.setStatus(UserDataMigrationTask.EXECUTE_SCAN_STATUS);
        userDataMigrationTaskService.updateStatus(task);
        
        try
        {
            List<INode> lstNodes = null;
            while (true)
            {
                lstNodes = filesInnerManager.lstContentsNodeFilterRGs(task.getCloudUserId(), limit, groups);
                if (null == lstNodes || lstNodes.isEmpty())
                {
                    break;
                }
                
                // 清理内存
                lstCopyTask.clear();
                
                for (INode node : lstNodes)
                {
                    if (node.getResourceGroupId() == task.getDestResourceGroupId())
                    {
                        LOGGER.info("The node " +node.getId()+" objectid "+node.getObjectId()+" is existing dest resouce group " + node.getResourceGroupId());
                        continue;
                    }
                    
                    // 查找相同内容的object已经在目标区域了
                    if (handleDataMigration.replaceObject(node, task.getDestRegionId()))
                    {
                        LOGGER.info("dest resouce group had other same content object");
                        continue;
                    }
                    
                    totalFiles++;
                    
                    totalSizes = totalSizes + node.getSize();
                    
                    LOGGER.info("bulid task the node " +node.getId()+" objectid "+node.getObjectId());
                    
                    // 生成任务
                    lstCopyTask.add(buildTaskForContentNode(node, this.task));
                }
                
                // 写入DB
                copyTaskService.saveCopyTask(lstCopyTask);
                
                // 退出循环
                if (lstNodes.isEmpty() || lstNodes.size() < MAX_LENGTHS)
                {
                    break;
                }
                
                //设置偏移量
                limit.setOffset(limit.getOffset()+MAX_LENGTHS);
            }
        }
        catch(RuntimeException re)
        {
            LOGGER.error(re.getMessage(),re);
            // 异常处理
            // 删除已经插入的任务
            copyTaskService.deleteTaskForDataMigration(task.getCloudUserId());
            // 重置迁移任务
            task.setStatus(UserDataMigrationTask.INIT_STATUS);
            task.setExeAgent(null);
            userDataMigrationTaskService.update(task);
            return;
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(), e);
            // 异常处理
            // 删除已经插入的任务
            copyTaskService.deleteTaskForDataMigration(task.getCloudUserId());
            // 重置迁移任务
            task.setStatus(UserDataMigrationTask.INIT_STATUS);
            task.setExeAgent(null);
            userDataMigrationTaskService.update(task);
            return;
        }
        
        if (totalFiles == 0 && totalSizes == 0)
        {
            // 更新任务状态
            task.setStatus(UserDataMigrationTask.COMPELETE_STATUS);
        }
        else
        {
            // 更新任务状态
            task.setStatus(UserDataMigrationTask.EXECUTE_MIGRATION_STATUS);
        }
        
        task.setTotalFiles(totalFiles);
        task.setTotalSizes(totalSizes);
        userDataMigrationTaskService.update(task);
        
    }
    
    @Override
    public String getName()
    {
        return this.getClass().getCanonicalName();
    }
    
}
