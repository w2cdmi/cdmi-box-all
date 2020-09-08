package com.huawei.sharedrive.app.core.backtask;

import com.huawei.sharedrive.app.common.systemtask.domain.ScanTableInfo;
import com.huawei.sharedrive.app.common.systemtask.domain.SystemTask;
import com.huawei.sharedrive.app.common.systemtask.domain.UserDBInfo;

import pw.cdmi.common.job.quartz.QuartzJobTask;
import pw.cdmi.core.utils.RandomGUID;

public abstract class AbstractScanTask extends QuartzJobTask
{

    protected SystemTask createScanTableTask(SystemTask pTask, String taskKey, UserDBInfo db, 
        ScanTableInfo scanTableInfo)
    {
        SystemTask task = new SystemTask();
        task.setCreateTime(pTask.getCreateTime());
        task.setTaskId(new RandomGUID().getValueAfterMD5() + '_' + scanTableInfo.getTableName());
        task.setpTaskId(pTask.getTaskId());
        task.setState(SystemTask.TASK_STATE_BEGIN);
        ScanTableInfo tableInfo = new ScanTableInfo();
        tableInfo.setDbName(db.getDbName());
        tableInfo.setDbNumber(db.getDbNumber());
        tableInfo.setTableName(scanTableInfo.getTableName());
        tableInfo.setTableNumber(scanTableInfo.getTableNumber());
        if (null != scanTableInfo.getLastModfied())
        {
            tableInfo.setLastModfied(scanTableInfo.getLastModfied());
        }
        task.setTaskKey(taskKey);
        task.setTaskInfo(ScanTableInfo.toJsonStr(tableInfo));
        return task;
    }
    
}
