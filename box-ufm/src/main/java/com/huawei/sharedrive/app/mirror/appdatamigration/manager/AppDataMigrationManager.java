package com.huawei.sharedrive.app.mirror.appdatamigration.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.app.common.systemtask.service.UserDBInfoService;
import com.huawei.sharedrive.app.files.dao.impl.INodeDAOImpl;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.files.manager.FilesInnerManager;
import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationEverydayProcess;
import com.huawei.sharedrive.app.mirror.appdatamigration.domain.MigrationProcessInfo;
import com.huawei.sharedrive.app.mirror.appdatamigration.service.MigrationEverydayProcessService;
import com.huawei.sharedrive.app.mirror.appdatamigration.service.MigrationProcessInfoService;
import com.huawei.sharedrive.app.mirror.appdatamigration.service.NeverCopyContentService;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.app.mirror.domain.CopyPolicySiteInfo;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyType;
import com.huawei.sharedrive.app.mirror.manager.BaseHandleCopyTask;
import com.huawei.sharedrive.app.mirror.manager.CopyTaskManager;
import com.huawei.sharedrive.app.mirror.service.CopyPolicyService;

import pw.cdmi.core.utils.RandomGUID;

@Service("appDataMigrationManager")
public class AppDataMigrationManager
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppDataMigrationManager.class);
    
    private static final int MAX_USERDB_COUNT = 16;
    
    @Autowired
    private UserDBInfoService userDBInfoService;
    
    @Autowired
    private FilesInnerManager filesInnerManager;
    
    @Autowired
    private CopyPolicyService copyPolicyService;
    
    @Autowired
    private MigrationProcessInfoService migrationProcessInfoService;
    
    @Autowired
    private MigrationEverydayProcessService migrationEverydayProcessService;
    
    @Autowired
    private CopyTaskManager copyTaskManager;
    
    @Autowired
    private NeverCopyContentService neverCopyContentService;
    
    /**
     * 统计所有userdb_*数据库中inode_*表里面属于原地址的dc的文件数量和大小，并新建一条记录些到数据库中
     * 
     */
    public void statisticTotalFileNumAndSize()
    {
        //先获取数据迁移策略，没有就返回
        List<CopyPolicy> lstMigrationPolicy = lstMigrationPolicy();
        if(lstMigrationPolicy.isEmpty())
        {
            LOGGER.info("get MirgrationPolicy null");
            return;
        }
        
        List<UserDBInfo> dbInfos = userDBInfoService.listAll();
        if (null == dbInfos || dbInfos.isEmpty())
        {
            LOGGER.error("Data exception ,userdb is null");
            return;
        }
        
        // 做数据库数量校验
        if (dbInfos.size() > MAX_USERDB_COUNT)
        {
            LOGGER.error("Data exception ,userdb count >" + MAX_USERDB_COUNT);
            return;
        }
        
        for(CopyPolicy copyPolicy : lstMigrationPolicy)
        {
            //首先关闭上一次扫描记录
            endLastMigrationProcess(copyPolicy);
            
            /**
             * 拿到每条策略需要复制的总文件数和大小
             * map.key 文件总数
             * map.value 文件总大小
             */
            statisticFilesAndSizesForSinglePolicy(dbInfos,copyPolicy);
            
        }
    }
    
    private void createNewMigrationProcess(CopyPolicy copyPolicy,long files ,long sizes)
    {
        MigrationProcessInfo migrationProcessInfo = new MigrationProcessInfo();
        migrationProcessInfo.setId(new RandomGUID().getValueAfterMD5());
        migrationProcessInfo.setPolicyId(copyPolicy.getId());
        migrationProcessInfo.setStatus(MigrationProcessInfo.STATUS_RUNNING);
        migrationProcessInfo.setCreatedAt(new Date());
        migrationProcessInfo.setModifiedAt(new Date());
        migrationProcessInfo.setTotalFiles(files);
        migrationProcessInfo.setTotalSizes(sizes);
        migrationProcessInfo.setCurFiles(0L);
        migrationProcessInfo.setCurSizes(0L);
        migrationProcessInfo.setFailedFiles(0L);
        migrationProcessInfo.setFailedSizes(0L);
        
        migrationProcessInfoService.createNewMigrationProcess(migrationProcessInfo);
        
        //创建一条记录后，便开始新一天的记录
        migrationEverydayProcessService.create(migrationProcessInfo);
    }
    
    /**
     * 完成上一次扫描
     * 在表数据中更新状态
     */
    public void endLastMigrationProcess(CopyPolicy copyPolicy)
    {
        MigrationProcessInfo migrationProcessInfo = migrationProcessInfoService.getLastUnDoneMigrationProcess(copyPolicy.getId());
        if(null == migrationProcessInfo)
        {
            LOGGER.info("get migrationProcessInfo by policy id:"+copyPolicy.getId()+" is null");
            return;
        }
        LOGGER.info("endLastMigrationProcess ,id is:"+migrationProcessInfo.getId());
        migrationProcessInfoService.endMigrationProcess(migrationProcessInfo);
    }
    
    /**
     * 判断在copytask表中是否还有数据迁移的任务遗留
     * 还有任务遗留则不能开始新一轮扫描，避免影响统计结果
     * @return
     */
    public boolean checkNoAppMigrationTaskIsLeft()
    {
        List<Integer> lstPolicyIds = copyTaskManager.selectAllPolicyId();
        if(lstPolicyIds == null || lstPolicyIds.isEmpty())
        {
            return true;
        }
        List<CopyPolicy> lstMigrationPolicy = lstMigrationPolicy();
        for(CopyPolicy copyPolicy : lstMigrationPolicy)
        {
            for(Integer integer : lstPolicyIds)
            {
                if(copyPolicy.getId() == integer)
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void handleMirrorExists(INode iNode,CopyTask copyTask)
    {
        BaseHandleCopyTask instance = copyTaskManager.getHandleCopyTaskInstance(copyTask);
        if(instance == null)
        {
            LOGGER.error("copytask type error");
            return;
        }
        instance.handleDataIsExistingBeforeCopy(copyTask);
    }
    
    private Map<Long, Long> statisticFilesAndSizesForSinglePolicy(List<UserDBInfo> dbInfos ,CopyPolicy copyPolicy)
    {
        long totalFiles = 0L;
        long totalSizes = 0L;
        Map<Long, Long> map = null;
        Map<Long, Long> resultMap = new HashMap<Long,Long>(1);
        
        List<CopyPolicySiteInfo> lstCopyPolicySiteInfo = copyPolicy.getLstCopyPolicyDataSiteInfo();
        if(lstCopyPolicySiteInfo==null || lstCopyPolicySiteInfo.isEmpty())
        {
            LOGGER.error("lstCopyPolicySiteInfo is null,policyid is:"+copyPolicy.getId());
            return null;
        }
        
        for(CopyPolicySiteInfo copyPolicySiteInfo : lstCopyPolicySiteInfo)
        {
            for (UserDBInfo db : dbInfos)
            {
                for (int i = 0; i < INodeDAOImpl.TABLE_COUNT; i++)
                {
                    map=filesInnerManager.lstFilesNumAndSizesByResourceGroup(db.getDbNumber(), i,copyPolicySiteInfo.getSrcResourceGroupId() );
                    totalFiles=totalFiles+getFilesFromMap(map);
                    totalSizes=totalSizes+getSizesFromMap(map);
                }
            }
        }
        LOGGER.info("policy["+copyPolicy.getName()+"],id is:"+copyPolicy.getId()+" has "+totalFiles+"files to copy, total size is:"+totalSizes);
        resultMap.put(totalFiles, totalSizes);
        
        // 在数据库中插入新数据
        createNewMigrationProcess(copyPolicy,totalFiles,totalSizes);
        return resultMap;
    }
    
    private long getFilesFromMap(Map<Long, Long> map)
    {
        long files = 0L;
        if(map==null)
        {
            LOGGER.info("lstFilesNumAndSizesByResourceGroup is null");
            return 0;
        }
        for (Map.Entry<Long, Long> entry : map.entrySet())
        {
            files = entry.getKey();
        }
        return files;
    }
    
    private long getSizesFromMap(Map<Long, Long> map)
    {
        long sizes = 0L;
        if(map==null)
        {
            LOGGER.info("lstFilesNumAndSizesByResourceGroup is null");
            return 0;
        }
        for (Map.Entry<Long, Long> entry : map.entrySet())
        {
            sizes = entry.getValue() == null ? 0 : entry.getValue();
        }
        return sizes;
    }
    
    public List<CopyPolicy> lstMigrationPolicy()
    {
        
        List<CopyPolicy> lstMigrationPolicy = new ArrayList<CopyPolicy>(3);
        List<CopyPolicy> lstCopyPolicy = copyPolicyService.listCopyPolicy();
        
        if(lstCopyPolicy==null || lstCopyPolicy.isEmpty())
        {
            LOGGER.info("get policy null");
            return lstMigrationPolicy;
        }
        for(CopyPolicy copyPolicy:lstCopyPolicy)
        {
            if(copyPolicy.getCopyType()==CopyType.COPY_TYPE_APP_MIGRATION_CLEAR_OLD_DATA.getCopyType()
                ||copyPolicy.getCopyType()==CopyType.COPY_TYPE_APP_MIGRATION_PERSISTED_OLD_DATA.getCopyType())
            {
                lstMigrationPolicy.add(copyPolicy);
            }
        }
        return lstMigrationPolicy;
    }
    
    public void hasFailedFile(CopyTask copyTask,String reason)
    {
        CopyPolicy copyPolicy = copyPolicyService.getCopyPolicy(copyTask.getPolicyId());
        if(copyPolicy==null)
        {
            LOGGER.error("get copyPolicy by ["+copyTask.getPolicyId()+"] is null");
            return;
        }
        MigrationProcessInfo migrationProcessInfo = migrationProcessInfoService.getLastUnDoneMigrationProcess(copyPolicy.getId());
        if(migrationProcessInfo == null)
        {
            LOGGER.error("getLastUnDoneMigrationProcess by policyid:"+copyPolicy.getId()+" is null");
            return;
        }
        
        MigrationEverydayProcess migrationEverydayProcess = migrationEverydayProcessService.getUnCompleteDayProcess(migrationProcessInfo.getId());
        if(migrationEverydayProcess == null)
        {
            LOGGER.error("getUnCompleteDayProcess by migrationProcessInfo:"+copyPolicy.getId()+" is null");
            return;
        }
        //插入一条失败记录
        neverCopyContentService.insert(copyPolicy,migrationEverydayProcess, copyTask, reason);
        
        //更新总的情况
        migrationProcessInfo.setFailedFiles(migrationProcessInfo.getFailedFiles()+1);
        migrationProcessInfo.setFailedSizes(migrationProcessInfo.getFailedSizes()+copyTask.getSize());
        migrationProcessInfoService.updateMigrationProcessForCompleteFile(migrationProcessInfo);
    }
    
    public void updateDataMigrationProcess(CopyTask copyTask)
    {
        CopyPolicy copyPolicy = copyPolicyService.getCopyPolicy(copyTask.getPolicyId());
        if(copyPolicy==null)
        {
            LOGGER.error("get copyPolicy by ["+copyTask.getPolicyId()+"] is null");
            return;
        }
        MigrationProcessInfo migrationProcessInfo = migrationProcessInfoService.getLastUnDoneMigrationProcess(copyPolicy.getId());
        if(migrationProcessInfo == null)
        {
            LOGGER.error("getLastUnDoneMigrationProcess by policyid:"+copyPolicy.getId()+" is null");
            return;
        }
        
        MigrationEverydayProcess migrationEverydayProcess = migrationEverydayProcessService.getUnCompleteDayProcess(migrationProcessInfo.getId());
        if(migrationEverydayProcess == null)
        {
            LOGGER.error("getUnCompleteDayProcess by migrationProcessInfo:"+copyPolicy.getId()+" is null");
            return;
        }
        
        //更新每天的迁移情况
        migrationEverydayProcess.setNewAddFiles(migrationEverydayProcess.getNewAddFiles()+1);
        migrationEverydayProcess.setNewAddSizes(migrationEverydayProcess.getNewAddSizes()+copyTask.getSize());
        migrationEverydayProcessService.updateCompleteDayProcess(migrationEverydayProcess);
        
        //更新总的情况
        migrationProcessInfo.setCurFiles(migrationProcessInfo.getCurFiles()+1);
        migrationProcessInfo.setCurSizes(migrationProcessInfo.getCurSizes()+copyTask.getSize());
        migrationProcessInfoService.updateMigrationProcessForCompleteFile(migrationProcessInfo);
    }
}
