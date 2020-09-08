package com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.impl;


import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.common.systemtask.domain.TaskKeyConstant;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.CommonScanExeThread;
import com.huawei.sharedrive.app.core.backtask.reallydeletetask.service.ObjectScanServerService;

import pw.cdmi.common.config.service.ConfigManager;



@Service("objectScanServerService")
@Lazy(false)
public class ObjectScanServerServiceImpl implements ObjectScanServerService
{
    private CommonScanExeThread exeThread;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectScanServerServiceImpl.class);
   
    @Autowired
    private ConfigManager configManager;
    
    private String taskInfo;
    
    @PostConstruct
    public void init()
    {
        configManager.registListener(this);
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(ObjectScanServerServiceImpl.class.getSimpleName()))
        {
            LOGGER.info("Reload MailConfig By Cluster Notify.");
            taskInfo = (String) value;
            
            LOGGER.info("receive data!"+taskInfo);
            if(exeThread == null || !exeThread.isAlive())  
            {
                exeThread = new CommonScanExeThread(taskInfo,TaskKeyConstant.OBJECT_SCAN_TABLE_TASK);
                exeThread.start();
            }
            else
            {
                LOGGER.info("reset receive data!"+taskInfo);
                exeThread.setPTaskAndReSet(taskInfo);
            }
            
        }
       
        
    }
    
   
    
}
