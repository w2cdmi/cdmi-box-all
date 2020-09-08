package com.huawei.sharedrive.app.spacestatistics.manager.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.manager.SpaceStatisticsManager;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Component("spaceStatisticsManager")
public class SpaceStatisticsManagerImpl extends CacheableSqlMapClientDAO implements SpaceStatisticsManager
{
    @Autowired
    private SpaceStatisticsService spaceStatisticsService;
    
    @Override
    public Map<Long, FilesAdd> getUserChangedInfo()
    {
        List<FilesAdd> addedSpace = spaceStatisticsService.getFilesAdd();
        List<FilesDelete> deletedSpace = spaceStatisticsService.getFilesDelete();
        if (deletedSpace == null)
        {
            return null;
        }
        return spaceStatisticsService.getChangedUserInfo(addedSpace, deletedSpace);
    }
    
    @Override
    public Map<Long, AccountStatisticsInfo> getAccountChangedInfo()
    {
        List<FilesAdd> accountAdded = spaceStatisticsService.getAddedAccountInfo();
        List<FilesDelete> accountDeleted = spaceStatisticsService.getDeletedAccountInfo();
        return spaceStatisticsService.getChangedAccountInfo(accountAdded, accountDeleted);
    }
    
    @Override
    public void updateUserInfo(Map<Long, FilesAdd> userChangedInfo)
    {
        if (userChangedInfo == null)
        {
            return;
        }
        Object userSpaceInfo;
        long userId;
        for (Map.Entry<Long, FilesAdd> entry : userChangedInfo.entrySet())
        {
            userId = entry.getKey();
            userSpaceInfo = getCacheClient().getCache(UserStatisticsInfo.CACHE_KEY_PRIFIX_USERSINFO + userId);
            if (null == userSpaceInfo)
            {
                spaceStatisticsService.updateUserWithoutCacheInfo(userId, userChangedInfo.get(userId)
                    .getAccountId());
            }
            else
            {
                spaceStatisticsService.updateUserWithCacheInfo((UserStatisticsInfo) userSpaceInfo,
                    entry.getValue());
            }
        }
    }
    
    @Override
    public void updateAccountInfo(Map<Long, AccountStatisticsInfo> accountChangedInfo)
    {
        if (accountChangedInfo == null)
        {
            return;
        }
        long accountId;
        
        for (Map.Entry<Long, AccountStatisticsInfo> entry : accountChangedInfo.entrySet())
        {
            accountId = entry.getKey();
            spaceStatisticsService.updateAccountWithoutCacheInfo(accountId);
        }
    }
}
