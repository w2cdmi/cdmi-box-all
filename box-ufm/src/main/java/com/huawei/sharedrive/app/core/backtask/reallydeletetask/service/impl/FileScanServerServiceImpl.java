package com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.CommonScanExeThread;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.FileScanServerService;

import pw.cdmi.common.config.service.ConfigManager;

@Service("fileScanServerService")
@Lazy(false)
public class FileScanServerServiceImpl implements FileScanServerService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileScanServerServiceImpl.class);
    
    @Autowired
    private ConfigManager configManager;
    
    private CommonScanExeThread exeThread;
    
    private String taskInfo;
    
    @PostConstruct
    public void init()
    {
        configManager.registListener(this);
    }
    
    @Override
    public synchronized void configChanged(String key, Object value)
    {
        if (key.equals(FileScanServerServiceImpl.class.getSimpleName()))
        {
            LOGGER.info("Reload MailConfig By Cluster Notify.");
            taskInfo = (String) value;
            
            LOGGER.info("receive data!", taskInfo);
            if (exeThread == null || !exeThread.isAlive())
            {
                exeThread = new CommonScanExeThread(taskInfo, TaskKeyConstant.FILE_SCAN_TABLE_TASK);
                exeThread.start();
            }
            else
            {
                exeThread.notifyAll();
                exeThread.setPTaskAndReSet(taskInfo);
            }
        }
    }
    
}
