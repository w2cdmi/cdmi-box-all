package com.huawei.sharedrive.app.spacestatistics.dao;

import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.domain.TemporaryUserInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;

public interface TemporaryUserInfoDao
{
    void insert(UserStatisticsInfo currentInfo, FilesAdd changedInfo);
    
    void deleteByTime(long userId, Date date);
    
    AccountStatisticsInfo getAccountChangedInfoById(long accountId);
    
    List<TemporaryUserInfo> getCurrentUserInfo();
    
    List<Long> getAccountIds();
}
