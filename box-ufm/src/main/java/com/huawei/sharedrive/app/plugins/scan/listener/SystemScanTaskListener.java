package com.huawei.sharedrive.app.plugins.scan.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.CommonScanExeThread;

import pw.cdmi.common.config.service.ConfigListener;

/**
 * 安全扫描任务监听类。监听安全扫描任务zookeeper通知
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-4-24
 * @see
 * @since
 */
@Component("systemScanTaskListener")
@DependsOn("springContextUtil")
public class SystemScanTaskListener implements ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemScanTaskListener.class);
    
    private CommonScanExeThread exeThread;
    
    private static final String SYSTEM_SCAN_TASK_START_KEY = "system_scan_task_start";
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (SYSTEM_SCAN_TASK_START_KEY.equals(key))
        {
            String taskInfo = (String) value;
            LOGGER.info("Receive data: {}", taskInfo);
            
            if (exeThread == null || !exeThread.isAlive())
            {
                exeThread = new CommonScanExeThread(taskInfo, TaskKeyConstant.SYSTEM_SCAN_TASK);
                exeThread.start();
            }
            else
            {
                LOGGER.info("Reset task: {}" + taskInfo);
                exeThread.setPTaskAndReSet(taskInfo);
            }
        }
    }
    
}
