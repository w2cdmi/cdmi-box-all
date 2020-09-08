package com.huawei.sharedrive.app.spacestatistics.service;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.spacestatistics.domain.TemporaryUserInfo;

public interface ModifySpaceDBTaskService
{
    List<TemporaryUserInfo> getCurrentUserInfo();
    
    List<Long> getAccountIds();
    
    void updateUserDB(List<TemporaryUserInfo> currentSpaces);
    
    void updateAccountDBANDCache(List<Long> accountIds);
    
    void deleteTemporaryUserInfo(List<TemporaryUserInfo> currentInfos, Date date);
}
