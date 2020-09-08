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
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.impl.FileScanServerServiceImpl;
import com.huawei.sharedrive.app.exception.BaseRunException;
import com.huawei.sharedrive.app.files.dao.impl.INodeDAOImpl;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.RandomGUID;

@Service("distributeFileScanTask")
public class DistributeFileScanTask extends AbstractScanTask
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributeFileScanTask.class);
    
    @Autowired
    private ReallyDeleteService reallyDeleteService;
    
    private static final int MAX_USERDB_COUNT = 16;
    
    @Autowired
    private ConfigManager configManager;
    
    private int maxBeforeDay = Integer.parseInt(PropertiesUtils.getProperty("deletefile.reserve.day", "1"));
    
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
            // 发送消息
            configManager.setConfig(FileScanServerServiceImpl.class.getSimpleName(), pTaskID);
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
    
    private SystemTask createDistributeFileScanTask(Date createTime)
    {
        SystemTask task = new SystemTask();
        task.setCreateTime(createTime);
        task.setTaskId(new RandomGUID().getValueAfterMD5());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        task.setTaskKey(TaskKeyConstant.DISTRIBUTE_FILE_SCAN_TASK);
        return task;
    }
    
    private SystemTask createFileScanTableTask(SystemTask pTask, UserDBInfo db, String tableName,
        int tableNumber, Date lastModfied)
    {
        SystemTask task = new SystemTask();
        task.setCreateTime(pTask.getCreateTime());
        task.setTaskId(new RandomGUID().getValueAfterMD5() + '_' + tableName);
        task.setpTaskId(pTask.getTaskId());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        ScanTableInfo tableInfo = new ScanTableInfo();
        tableInfo.setDbName(db.getDbName());
        tableInfo.setDbNumber(db.getDbNumber());
        tableInfo.setTableName(tableName);
        tableInfo.setTableNumber(tableNumber);
        tableInfo.setLastModfied(lastModfied);
        task.setTaskKey(TaskKeyConstant.FILE_SCAN_TABLE_TASK);
        task.setTaskInfo(ScanTableInfo.toJsonStr(tableInfo));
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
            
            List<SystemTask> allTask = new ArrayList<SystemTask>(dbInfos.size() + 1);
            Date createTime = new Date();
            SystemTask pTask = createDistributeFileScanTask(createTime);
            allTask.add(pTask);
            
            // 多少天以前的数据才能删除
            Date lastModfied = DateUtils.getDateBefore(createTime, (maxBeforeDay));
            
            SystemTask task = null;
            for (UserDBInfo db : dbInfos)
            {
                for (int i = 0; i < INodeDAOImpl.TABLE_COUNT; i++)
                {
                    task = createFileScanTableTask(pTask, db, "inode_" + i, i, lastModfied);
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
            SystemTask task = reallyDeleteService.getFileDistributeScanTask();
            if (null != task)
            {
                List<SystemTask> fileScanTasks = reallyDeleteService.getScanTableTask(task.getTaskId());
                showLog(fileScanTasks);
            }
        }
        catch (Exception e)
        {
            LOGGER.warn("", e);
        }
        finally
        {
            reallyDeleteService.deleteAllFileScanTableTask();
        }
    }
    
    private void showLog(List<SystemTask> fileScanTasks) throws BaseRunException
    {
        if (null == fileScanTasks)
        {
            return;
        }
        int finishCount = 0;
        int allCount = 0;
        for (SystemTask fileTask : fileScanTasks)
        {
            if (StringUtils.isNotBlank(fileTask.getTaskInfo()))
            {
                ScanTableInfo info = ScanTableInfo.toObject(fileTask.getTaskInfo());
                if (fileTask.getState() == SystemTask.TASK_STATE_END)
                {
                    finishCount++;
                    LOGGER.info("The task createtime:" + fileTask.getClass().toString() + ',' + info.toStr()
                        + " finish.");
                    // 记录日志
                    // TODO
                }
                else
                {
                    LOGGER.warn("The task createtime:" + fileTask.getClass().toString() + ',' + info.toStr()
                        + " not finish" + ",state:" + fileTask.getState());
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
