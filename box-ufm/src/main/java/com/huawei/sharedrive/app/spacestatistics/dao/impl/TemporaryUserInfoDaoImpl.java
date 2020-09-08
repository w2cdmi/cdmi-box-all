package com.huawei.sharedrive.app.spacestatistics.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.spacestatistics.dao.TemporaryUserInfoDao;
import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.domain.TemporaryUserInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;

@Service("temporaryUserSpaceDao")
@SuppressWarnings("deprecation")
public class TemporaryUserInfoDaoImpl implements TemporaryUserInfoDao
{
    @Autowired
    protected SqlMapClientTemplate sqlMapClientTemplate;
    
    @Override
    public void insert(UserStatisticsInfo currentInfo, FilesAdd changedInfo)
    {
        TemporaryUserInfo info = new TemporaryUserInfo();
        info.setOwnedBy(changedInfo.getOwnedBy());
        info.setAccountId(changedInfo.getAccountId());
        info.setSpaceUsed(currentInfo.getSpaceUsed());
        info.setSpaceChanged(changedInfo.getSize());
        info.setCurrentFileCount(currentInfo.getFileCount());
        info.setChangedFileCount(changedInfo.getFileCount());
        info.setCreatedAt(new Date());
        sqlMapClientTemplate.insert("TemporaryUserInfo.insert", info);
    }
    
    @Override
    public void deleteByTime(long userId, Date date)
    {
        TemporaryUserInfo info = new TemporaryUserInfo();
        info.setOwnedBy(userId);
        info.setCreatedAt(date);
        sqlMapClientTemplate.delete("TemporaryUserInfo.deleteByTime", info);
    }
    
    @Override
    public AccountStatisticsInfo getAccountChangedInfoById(long accountId)
    {
        Object accountStatisticsInfo = sqlMapClientTemplate.queryForObject("TemporaryUserInfo.getAccountChangedInfoById",
            accountId);
        if(null == accountStatisticsInfo)
        {
            return null;
        }
        AccountStatisticsInfo accountInfo = (AccountStatisticsInfo) accountStatisticsInfo;
        if (null == accountInfo.getAccountId())
        {
            return null;
        }
        if (null == accountInfo.getCurrentFiles())
        {
            accountInfo.setCurrentFiles(0L);
        }
        if (null == accountInfo.getCurrentSpace())
        {
            accountInfo.setCurrentSpace(0L);
        }
        return accountInfo;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<TemporaryUserInfo> getCurrentUserInfo()
    {
        return (List<TemporaryUserInfo>) sqlMapClientTemplate.queryForList("TemporaryUserInfo.getCurrentUserInfo");
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> getAccountIds()
    {
        return (List<Long>) sqlMapClientTemplate.queryForList("TemporaryUserInfo.getAccountIds");
    }
    
}
