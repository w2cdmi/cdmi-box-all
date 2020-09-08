package com.huawei.sharedrive.app.spacestatistics.manager.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.spacestatistics.domain.TemporaryUserInfo;
import com.huawei.sharedrive.app.spacestatistics.manager.ModifySpaceDBTaskManager;
import com.huawei.sharedrive.app.spacestatistics.service.ModifySpaceDBTaskService;

@Component("modifySpaceDBTaskManager")
public class ModifySpaceDBTaskManagerImpl implements ModifySpaceDBTaskManager
{
    
    @Autowired
    private ModifySpaceDBTaskService modifySpaceDBTaskService;
    
    @Override
    public void modifySpaceDB()
    {
        List<TemporaryUserInfo> currentUserInfo = modifySpaceDBTaskService.getCurrentUserInfo();
        
        Date date = new Date();
        
        List<Long> accountIds = modifySpaceDBTaskService.getAccountIds();
        
        modifySpaceDBTaskService.deleteTemporaryUserInfo(currentUserInfo, date);
        
        modifySpaceDBTaskService.updateUserDB(currentUserInfo);
        
        modifySpaceDBTaskService.updateAccountDBANDCache(accountIds);
        
    }
    
}
