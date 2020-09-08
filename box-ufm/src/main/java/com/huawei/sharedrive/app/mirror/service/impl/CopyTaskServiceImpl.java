package com.huawei.sharedrive.app.mirror.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.app.mirror.dao.CopyTaskDAO;
import com.huawei.sharedrive.app.mirror.dao.CopyTaskSlaveDBDAO;
import com.huawei.sharedrive.app.mirror.domain.CopyTask;
import com.huawei.sharedrive.app.mirror.domain.CopyTaskStatistic;
import com.huawei.sharedrive.app.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.app.mirror.service.CopyTaskService;
import com.huawei.sharedrive.app.system.service.SystemConfigService;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.LoggerUtil;

@Service("copyTaskService")
public class CopyTaskServiceImpl implements CopyTaskService,ConfigListener
{
    
    @Autowired
    private CopyTaskDAO copyTaskDAO;
    
    @Autowired
    private CopyTaskSlaveDBDAO copyTaskSlaveDBDAO;
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    private static final String SLAVEDB_CONFIG="mirror.use.slavedb";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyTaskServiceImpl.class);
    
    private boolean useSlave;
    
    private static final String CONFIG_ZOOKEEPER_KEY_USE_SLAVEDB_CHANGE="config.zookeeper.key.use.slavedb.change";
    
    @PostConstruct
    public void init()
    {
        SystemConfig systemConfig = systemConfigService.getConfig(SLAVEDB_CONFIG);
        if(systemConfig == null)
        {
            LOGGER.info("get systemConfig is null by:"+SLAVEDB_CONFIG);
            useSlave = false;
            return;
        }
        
        useSlave = Boolean.parseBoolean(systemConfig.getValue());
        LOGGER.info("useSlave value is:"+useSlave);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveCopyTask(List<CopyTask> lstCopyTask)
    {
        for (CopyTask task : lstCopyTask)
        {
            if (useSlave)
            {
                copyTaskSlaveDBDAO.create(task);
            }
            else
            {
                copyTaskDAO.create(task);
            }
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveSigleCopyTask(CopyTask copyTask)
    {
        if (useSlave)
        {
            copyTaskSlaveDBDAO.create(copyTask);
        }
        else
        {
            copyTaskDAO.create(copyTask);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateCopyTask(CopyTask copyTask)
    {
        if (useSlave)
        {
            copyTaskSlaveDBDAO.update(copyTask);
        }
        else
        {
            copyTaskDAO.update(copyTask);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<CopyTask> getWaittingTaskAndSetPop(Limit limit)
    {
        
        List<CopyTask> lstTask = null;
        
        if (useSlave)
        {
            lstTask = copyTaskSlaveDBDAO.getBystatusAndExeType(MirrorCommonStatic.EXE_TYPE_NOW,
                MirrorCommonStatic.TASK_STATE_WAITTING,
                false,
                limit);
        }
        else
        {
            lstTask = copyTaskDAO.getBystatusAndExeType(MirrorCommonStatic.EXE_TYPE_NOW,
                MirrorCommonStatic.TASK_STATE_WAITTING,
                false,
                limit);
        }
        
        if (null != lstTask && !lstTask.isEmpty())
        {
            
            if (useSlave)
            {
                copyTaskSlaveDBDAO.updateForPopForList(lstTask, true);
            }
            else
            {
                copyTaskDAO.updateForPop(lstTask, true);
            }
        }
        
        return lstTask;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void activateTimeTask(String curTime)
    {
        if (useSlave)
        {
            copyTaskSlaveDBDAO.activateTimeTask(curTime);
        }
        else
        {
            copyTaskDAO.activateTimeTask(curTime);
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public long recoveryNoExeTaskForPopState(Date outTime)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.recoveryNoExeTaskForPopState(outTime);
        }
        return copyTaskDAO.recoveryNoExeTaskForPopState(outTime);
    }
    
    @Override
    public CopyTask getCopyTask(String taskId)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.get(taskId);
        }
        return copyTaskDAO.get(taskId);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteCopyTask(CopyTask task)
    {
        if (useSlave)
        {
            copyTaskSlaveDBDAO.delete(task.getTaskId());
        }
        else
        {
            copyTaskDAO.delete(task.getTaskId());
        }
    }
    
    @Override
    public CopyTaskStatistic statisticCopyTask()
    {
        
        CopyTaskStatistic statistic = new CopyTaskStatistic();
        
        statistic.setStatTime(new Date());
        
        Map<Long, Long> noactivateMap = null;
        if (useSlave)
        {
            noactivateMap = copyTaskSlaveDBDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_NOACTIVATE);
        }
        else
        {
            noactivateMap = copyTaskDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_NOACTIVATE);
        }
        for (Map.Entry<Long, Long> entry : noactivateMap.entrySet())
        {
            statistic.setNoactivateTaskNum(entry.getKey());
            statistic.setNoactivateTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        
        Map<Long, Long> waitingNumMap = null;
        if (useSlave)
        {
            waitingNumMap = copyTaskSlaveDBDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_WAITTING);
        }
        else
        {
            waitingNumMap = copyTaskDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_WAITTING);
        }
        for (Map.Entry<Long, Long> entry : waitingNumMap.entrySet())
        {
            statistic.setWaitingTaskNum(entry.getKey());
            statistic.setWaitingTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        
        Map<Long, Long> exeingNumMap = null;
        if (useSlave)
        {
            exeingNumMap = copyTaskSlaveDBDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_EXEING);
        }
        else
        {
            exeingNumMap = copyTaskDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_EXEING);
        }
        for (Map.Entry<Long, Long> entry : exeingNumMap.entrySet())
        {
            statistic.setExeingTaskNum(entry.getKey());
            statistic.setExeingTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        
        Map<Long, Long> failedNumMap = null;
        if (useSlave)
        {
            failedNumMap = copyTaskSlaveDBDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_FAILED);
        }
        else
        {
            failedNumMap = copyTaskDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_FAILED);
        }
        for (Map.Entry<Long, Long> entry : failedNumMap.entrySet())
        {
            statistic.setFailedTaskNum(entry.getKey());
            statistic.setFailedTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        
        statistic.setAllTaskNum(statistic.getExeingTaskNum() + statistic.getFailedTaskNum()
            + statistic.getNoactivateTaskNum() + statistic.getWaitingTaskNum());
        statistic.setAllSize(statistic.getExeingTaskSize() + statistic.getFailedTaskSize()
            + statistic.getNoactivateTaskSize() + statistic.getWaitingTaskSize());
        
        return statistic;
    }
    
    @Override
    public void deactivatOverdueTimeTask(String curTime)
    {
        if (useSlave)
        {
            copyTaskSlaveDBDAO.deactivatOverdueTimeTask(curTime);
        }
        else
        {
            copyTaskDAO.deactivatOverdueTimeTask(curTime);
        }
    }
    
    @Override
    public CopyTask checkSameMirrorTask(CopyTask task)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.getTaskBySrcObjectAndResourceGroupID(task);
        }
        return copyTaskDAO.getTaskBySrcObjectAndResourceGroupID(task);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public long deleteTaskByErrorCode(int errCode)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.deleteTaskByErrorCode(errCode);
        }
        return copyTaskDAO.deleteTaskByErrorCode(errCode);
    }
    
    @Override
    public List<CopyTask> lstTaskByErrorCode(int errCode)
    {
        if(useSlave)
        {
            return copyTaskSlaveDBDAO.lstTaskByErrorCode(errCode);
        }
        return copyTaskDAO.lstTaskByErrorCode(errCode);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public long recoveryExeTimeOutTask(Date outTime)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.recoveryExeTimeOutTask(outTime);
        }
        return copyTaskDAO.recoveryExeTimeOutTask(outTime);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public long recoveryFailedTask()
    {
        long total = 0;
        if (useSlave)
        {
            total = copyTaskSlaveDBDAO.recoveryFailedTaskForByExeType(MirrorCommonStatic.EXE_TYPE_NOW);
            return total
                + copyTaskSlaveDBDAO.recoveryFailedTaskForByExeType(MirrorCommonStatic.EXE_TYPE_TIME);
        }
        total = copyTaskDAO.recoveryFailedTaskForByExeType(MirrorCommonStatic.EXE_TYPE_NOW);
        return total + copyTaskDAO.recoveryFailedTaskForByExeType(MirrorCommonStatic.EXE_TYPE_TIME);
    }
    
    @Override
    public List<CopyTask> getCopyTaskBySrcObjectId(String srcObjectId)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.getCopyTaskBySrcObjectId(srcObjectId);
        }
        return copyTaskDAO.getCopyTaskBySrcObjectId(srcObjectId);
    }
    
    @Override
    public long deleteTaskForDataMigration(long userId)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.deleteTaskForDataMigration(userId);
        }
        return copyTaskDAO.deleteTaskForDataMigration(userId);
    }
    
    @Override
    public long getNotCompleteTaskForDataMigration(long userId)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.getNotCompleteTaskForDataMigration(userId);
        }
        return copyTaskDAO.getNotCompleteTaskForDataMigration(userId);
    }

    @Override
    public long clearNotExeTaskForDataMigration(long userId)
    { 
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.clearNotExeTaskForDataMigration(userId);
        }
        return copyTaskDAO.clearNotExeTaskForDataMigration(userId);
    }

    @Override
    public long cleanMirrorCopyTask(long userId)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.cleanMirrorCopyTaskBySrcOwnedby(userId);
        }
        return copyTaskDAO.cleanMirrorCopyTaskBySrcOwnedby(userId);
    }

    @Override
    public long updateTaskStateSameWithSystemConfigState(int newState, int oldState)
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.updateTaskStateSameWithSystemConfigState(newState,oldState);
        }
        return copyTaskDAO.updateTaskStateSameWithSystemConfigState(newState,oldState);
    }

    @Override
    public List<Integer> selectAllPolicyId()
    {
        if (useSlave)
        {
            return copyTaskSlaveDBDAO.selectAllPolicyId();
        }
        return copyTaskDAO.selectAllPolicyId();
    }

    @Override
    public void deleteCopyTaskByPolicy(Integer policyId)
    {
        if (useSlave)
        {
            copyTaskSlaveDBDAO.deleteCopyTaskByPolicy(policyId);
        }
        else
        {
            copyTaskDAO.deleteCopyTaskByPolicy(policyId);
        }
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
 

}
