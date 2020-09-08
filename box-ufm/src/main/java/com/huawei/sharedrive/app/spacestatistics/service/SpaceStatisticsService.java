package com.huawei.sharedrive.app.spacestatistics.service;

import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;

public interface SpaceStatisticsService
{
    List<FilesAdd> getFilesAdd();
    
    List<FilesDelete> getFilesDelete();
    
    Map<Long, FilesAdd> getChangedUserInfo(List<FilesAdd> addedSpaceList, List<FilesDelete> deletedSpaceList);
    
    UserStatisticsInfo updateUserWithCacheInfo(UserStatisticsInfo userSpaceInfoInCache, FilesAdd changedInfo);
    
    UserStatisticsInfo updateUserWithoutCacheInfo(long userId, long accountId);
    
    List<FilesAdd> getAddedAccountInfo();
    
    List<FilesDelete> getDeletedAccountInfo();
    
    Map<Long, AccountStatisticsInfo> getChangedAccountInfo(List<FilesAdd> accountAddedSpace,
        List<FilesDelete> accountDeletedSpace);
    
    AccountStatisticsInfo updateAccountWithoutCacheInfo(long accountId);
    
    AccountStatisticsInfo getAccountCurrentInfo(long accountId);
    
    AccountStatisticsInfo getAccountInfoById(long accountId);
    
    UserStatisticsInfo getUserCurrentInfo(long userId, long accountId);
    
    AccountStatisticsInfo updateAccountWithCacheInfo(AccountStatisticsInfo accountCurrentInfo,
        AccountStatisticsInfo accountChangedInfo);
}
