package com.huawei.sharedrive.app.message.task;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.CommonScanExeThread;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.impl.FileScanServerServiceImpl;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;

@Component("expiredMsgScanTaskMonitor")
@Lazy(false)
public class ExpiredMsgScanTaskMonitor implements ConfigListener
{

    private static final Logger LOGGER = LoggerFactory.getLogger(FileScanServerServiceImpl.class);
   
    private CommonScanExeThread exeThread;
    
    @Autowired
    private ConfigManager configManager;
    
    @PostConstruct
    public void init()
    {
        configManager.registListener(this);
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(ExpiredMsgScanTask.class.getSimpleName()))
        {
            String taskInfo = (String) value;
            LOGGER.info("Receive data: {}", taskInfo);
            
            if(exeThread == null || !exeThread.isAlive())  
            {
                exeThread = new CommonScanExeThread(taskInfo,TaskKeyConstant.EXPIRED_MSG_SCAN_TASK);
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
