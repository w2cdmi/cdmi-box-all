package com.huawei.sharedrive.isystem.mirror.service.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.mirror.dao.CopyTaskDAO;
import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.domain.MirrorCommonStatic;
import com.huawei.sharedrive.isystem.mirror.service.CopyTaskService;
import com.huawei.sharedrive.isystem.mirror.service.CopyTaskStatistic;

@Service("copyTaskService")
public class CopyTaskServiceImpl implements CopyTaskService
{
    @Autowired
    private CopyTaskDAO copyTaskDAO;
    
    private final static int TASK_STATE_FAILED = 3;
    
    // 执行中
    private final static int TASK_STATE_EXEING = 1;
    
    @Override
    public CopyTaskStatistic statisticCurrentTaskInfo()
    {
        CopyTaskStatistic statistic = new CopyTaskStatistic();
        
        statistic.setStatTime(new Date());
        
        Map<Long, Long> noactivateMap = copyTaskDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_NOACTIVATE);
        for (Map.Entry<Long, Long> entry : noactivateMap.entrySet())
        {
            statistic.setNoactivateTaskNum(entry.getKey());
            statistic.setNoactivateTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        
        Map<Long, Long> waitingNumMap = copyTaskDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_WAITTING);
        for (Map.Entry<Long, Long> entry : waitingNumMap.entrySet())
        {
            statistic.setWaitingTaskNum(entry.getKey());
            statistic.setWaitingTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        
        Map<Long, Long> exeingNumMap = copyTaskDAO.statisticCopyTask(TASK_STATE_EXEING);
        for (Map.Entry<Long, Long> entry : exeingNumMap.entrySet())
        {
            statistic.setExeingTaskNum(entry.getKey());
            statistic.setExeingTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        Map<Long, Long> failedNumMap = copyTaskDAO.statisticCopyTask(TASK_STATE_FAILED);
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
    public CopyTaskStatistic statisticCurrentTaskInfo(CopyPolicy copyPolicy)
    {
        CopyTaskStatistic statistic = new CopyTaskStatistic();
        
        statistic.setStatTime(new Date());
        
        Map<Long, Long> noactivateMap = copyTaskDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_NOACTIVATE,
            copyPolicy);
        for (Map.Entry<Long, Long> entry : noactivateMap.entrySet())
        {
            statistic.setNoactivateTaskNum(entry.getKey());
            statistic.setNoactivateTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        
        Map<Long, Long> waitingNumMap = copyTaskDAO.statisticCopyTask(MirrorCommonStatic.TASK_STATE_WAITTING,
            copyPolicy);
        for (Map.Entry<Long, Long> entry : waitingNumMap.entrySet())
        {
            statistic.setWaitingTaskNum(entry.getKey());
            statistic.setWaitingTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        
        Map<Long, Long> exeingNumMap = copyTaskDAO.statisticCopyTask(TASK_STATE_EXEING,
            copyPolicy);
        for (Map.Entry<Long, Long> entry : exeingNumMap.entrySet())
        {
            statistic.setExeingTaskNum(entry.getKey());
            statistic.setExeingTaskSize(entry.getValue() == null ? 0 : entry.getValue());
        }
        Map<Long, Long> failedNumMap = copyTaskDAO.statisticCopyTask(TASK_STATE_FAILED,
            copyPolicy);
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
    public long pauseOrGoTask(int state)
    {
        return copyTaskDAO.pauseOrGoTask(state);
    }
    
}
