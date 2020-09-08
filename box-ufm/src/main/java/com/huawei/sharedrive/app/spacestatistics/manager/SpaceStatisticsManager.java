package com.huawei.sharedrive.app.spacestatistics.manager;

import java.util.Map;

import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;

public interface SpaceStatisticsManager
{
    Map<Long, FilesAdd> getUserChangedInfo();
    
    Map<Long, AccountStatisticsInfo> getAccountChangedInfo();
    
    void updateUserInfo(Map<Long, FilesAdd> changedSpace);
    
    void updateAccountInfo(Map<Long, AccountStatisticsInfo> accountChangedSpace);
}
