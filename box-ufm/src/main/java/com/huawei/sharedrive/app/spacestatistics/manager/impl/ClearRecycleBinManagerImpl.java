package com.huawei.sharedrive.app.spacestatistics.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.spacestatistics.domain.ClearRecycleBinRecord;
import com.huawei.sharedrive.app.spacestatistics.manager.ClearRecycleBinManager;
import com.huawei.sharedrive.app.spacestatistics.service.ClearRecycleBinService;

@Component("clearRecycleBinManager")
public class ClearRecycleBinManagerImpl implements ClearRecycleBinManager
{
    
    @Autowired
    private ClearRecycleBinService clearRecycleBinService;
    
    @Override
    public void clearRecycleBin()
    {
        List<ClearRecycleBinRecord> tasks = clearRecycleBinService.getRecords();
        
        clearRecycleBinService.deleteRecycleBinRecord(tasks);
        
        clearRecycleBinService.updateUserSpace(tasks);
    }
    
}
