package com.huawei.sharedrive.app.core.backtask.reallydeletetask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;
import com.huawei.sharedrive.app.core.backtask.AbstractScanTask;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.ReallyDeleteService;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.impl.ObjectScanServerServiceImpl;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.dao.impl.INodeDAOImpl;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.core.utils.RandomGUID;

@Service("distributeObjectScanTask")
public class DistributeObjectScanTask extends AbstractScanTask
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeObjectScanTask.class);
    
    @Autowired
    private ReallyDeleteService reallyDeleteService;
    
    private static final int MAX_USERDB_COUNT = 16;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public void doTask(JobExecuteContext context, JobExecuteRecord record)  
    {
        LOGGER.info("DistributeFileScanTask begin.");
        
        // 检测上一个任务执行情况
        checkLastTask();
        
        // 分片任务
        String pTaskID = distributeScanTask();
        
        if (StringUtils.isNotBlank(pTaskID))
        {
            configManager.setConfig(ObjectScanServerServiceImpl.class.getSimpleName(), pTaskID);
            
        }
        else
        {
            String message = "distributeScanTask file scan task error.";
            LOGGER.warn(message);
            record.setSuccess(false);
            record.setOutput(message);
        }
        LOGGER.info("DistributeFileScanTask end.");
    }
    
    private SystemTask createDistributeObjectScanTask(Date createTime)
    {
        SystemTask task = new SystemTask();
        task.setCreateTime(createTime);
        task.setTaskId(new RandomGUID().getValueAfterMD5());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        task.setTaskKey(TaskKeyConstant.DISTRIBUTE_OBJECT_SCAN_TASK);
        return task;
    }
    
    /**
     * 分配任务
     * 
     * @return
     */
    private String distributeScanTask()
    {
        
        List<UserDBInfo> dbInfos = reallyDeleteService.listAllUserdbInfo();
        
        if (null != dbInfos && !dbInfos.isEmpty())
        {
            // 做数据库数量校验
            if (dbInfos.size() > MAX_USERDB_COUNT)
            {
                LOGGER.error("Data exception ,userdb count >" + MAX_USERDB_COUNT);
                return null;
            }
            
            List<SystemTask> allTask = new ArrayList<SystemTask>(INodeDAOImpl.TABLE_COUNT);
            Date createTime = new Date();
            SystemTask pTask = createDistributeObjectScanTask(createTime);
            allTask.add(pTask);
            
            SystemTask task = null;
            ScanTableInfo scanTableInfo = new ScanTableInfo();
            for (UserDBInfo db : dbInfos)
            {
                for (int i = 0; i < INodeDAOImpl.TABLE_COUNT; i++)
                {
                    
                    scanTableInfo.setTableName("object_reference_" + i);
                    scanTableInfo.setTableNumber(i);
                    scanTableInfo.setLastModfied(null);
                    
                    task = createScanTableTask(pTask,
                        TaskKeyConstant.OBJECT_SCAN_TABLE_TASK,
                        db,
                        scanTableInfo);
                    allTask.add(task);
                }
            }
            
            // 添加任务表
            reallyDeleteService.createSystemTask(allTask);
            return pTask.getTaskId();
        }
        
        return null;
    }
    
    /**
     * 检测上一次任务执行
     */
    private void checkLastTask()
    {
        try
        {
            SystemTask task = reallyDeleteService.getObjectDistributeScanTask();
            if (null != task)
            {
                List<SystemTask> objectScanTasks = reallyDeleteService.getScanTableTask(task.getTaskId());
                showLog(objectScanTasks);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("" + e);
        }
        finally
        {
            // 删除上一个周期的对象表
            reallyDeleteService.deleteAllObjectScanTableTask();
        }
    }
    
    private void showLog(List<SystemTask> objectScanTasks) throws BaseRunException
    {
        if (null == objectScanTasks)
        {
            return;
        }
        int finishCount = 0;
        int allCount = 0;
        for (SystemTask subTask : objectScanTasks)
        {
            if (StringUtils.isNotBlank(subTask.getTaskInfo()))
            {
                ScanTableInfo info = ScanTableInfo.toObject(subTask.getTaskInfo());
                if (subTask.getState() == SystemTask.TASK_STATE_END)
                {
                    finishCount++;
                    LOGGER.info("The task createtime:" + subTask.getClass().toString() + ',' + info.toStr()
                        + " finish.");
                    // 记录日志
                    // TODO
                }
                else
                {
                    LOGGER.warn("The task createtime:" + subTask.getClass().toString() + ',' + info.toStr()
                        + " not finish" + ",state:" + subTask.getState());
                    // 记录日志
                    // TODO
                }
            }
            allCount++;
            
        }
        
        LOGGER.warn("file scan task ,all task count:" + allCount + ", finish task count:" + finishCount);
        // 记录日志
        // TODO
    }
    
}
