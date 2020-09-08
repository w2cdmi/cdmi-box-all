package com.huawei.sharedrive.app.spacestatistics.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.spacestatistics.dao.TemporaryUserInfoDao;
import com.huawei.sharedrive.app.spacestatistics.domain.TemporaryUserInfo;
import com.huawei.sharedrive.app.spacestatistics.service.ModifySpaceDBTaskService;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service("modifySpaceDBTaskService")
public class ModifySpaceDBTaskServiceImpl extends CacheableSqlMapClientDAO implements
    ModifySpaceDBTaskService
{
    
    @Autowired
    private TemporaryUserInfoDao temporaryUserInfoDao;
    
    @Autowired
    private UserDAOV2 userDAOV2;
    
    @Autowired
    private UserReverseDAO userReverseDAO;
    
    @Autowired
    private SpaceStatisticsService spaceStatisticsService;
    
    @Override
    public List<TemporaryUserInfo> getCurrentUserInfo()
    {
        return temporaryUserInfoDao.getCurrentUserInfo();
    }
    
    @Override
    public List<Long> getAccountIds()
    {
        return temporaryUserInfoDao.getAccountIds();
    }
    
    @Override
    public void updateUserDB(List<TemporaryUserInfo> currentInfos)
    {
        // 更新用户数据，删除其对应二级映射表
        for (TemporaryUserInfo currentInfo : currentInfos)
        {
            userDAOV2.updateStatisticInfo(currentInfo.getAccountId(),
                currentInfo.getOwnedBy(),
                currentInfo.getSpaceUsed(),
                currentInfo.getCurrentFileCount());
            userReverseDAO.updateStatisticInfo(currentInfo.getAccountId(),
                currentInfo.getOwnedBy(),
                currentInfo.getSpaceUsed(),
                currentInfo.getCurrentFileCount());
        }
        
    }
    
    @Override
    public void updateAccountDBANDCache(List<Long> accountIds)
    {
        for (long accountId : accountIds)
        {
            spaceStatisticsService.updateAccountWithoutCacheInfo(accountId);
        }
    }
    
    @Override
    public void deleteTemporaryUserInfo(List<TemporaryUserInfo> currentInfos, Date date)
    {
        for (TemporaryUserInfo currentInfo : currentInfos)
        {
            temporaryUserInfoDao.deleteByTime(currentInfo.getOwnedBy(), date);
        }
    }
}
