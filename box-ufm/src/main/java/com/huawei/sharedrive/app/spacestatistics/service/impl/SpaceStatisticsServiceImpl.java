package com.huawei.sharedrive.app.spacestatistics.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.account.dao.AccountDao;
import com.huawei.sharedrive.app.files.dao.INodeDAOSlaveDB;
import com.huawei.sharedrive.app.files.dao.INodeDAOV2;
import com.huawei.sharedrive.app.spacestatistics.dao.ClearRecycleBinDao;
import com.huawei.sharedrive.app.spacestatistics.dao.FilesAddDao;
import com.huawei.sharedrive.app.spacestatistics.dao.FilesDeleteDao;
import com.huawei.sharedrive.app.spacestatistics.dao.TemporaryUserInfoDao;
import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesAdd;
import com.huawei.sharedrive.app.spacestatistics.domain.FilesDelete;
import com.huawei.sharedrive.app.spacestatistics.domain.UserStatisticsInfo;
import com.huawei.sharedrive.app.spacestatistics.service.SpaceStatisticsService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.dao.UserReverseDAO;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Service("spaceStatisticsService")
public class SpaceStatisticsServiceImpl extends CacheableSqlMapClientDAO implements SpaceStatisticsService
{
    @Autowired
    private FilesAddDao filesAddDao;
    
    @Autowired
    private FilesDeleteDao filesDeleteDao;
    
    @Autowired
    private TemporaryUserInfoDao temporaryUserSpaceDao;
    
    @Autowired
    private INodeDAOV2 iNodeDAOV2;
    
    @Autowired
    private INodeDAOSlaveDB iNodeDAOSlaveDB;
    
    @Autowired
    private UserDAOV2 userDAOV2;
    
    @Autowired
    private UserReverseDAO userReverseDAO;
    
    @Autowired
    private AccountDao accountDao;
    
    @Autowired
    private ClearRecycleBinDao clearRecycleBinDao;
    
    @Override
    public List<FilesAdd> getFilesAdd()
    {
        return filesAddDao.getAllFilesAdd();
    }
    
    @Override
    public List<FilesDelete> getFilesDelete()
    {
        return filesDeleteDao.getAllFilesDelete();
    }
    
    @Override
    public Map<Long, FilesAdd> getChangedUserInfo(List<FilesAdd> addedSpaceList,
        List<FilesDelete> deletedSpaceList)
    {
        Map<Long, FilesAdd> changedUserInfo = new HashMap<Long, FilesAdd>(16);
        
        long userId;
        for (FilesAdd addedSpace : addedSpaceList)
        {
            userId = addedSpace.getOwnedBy();
            changedUserInfo.put(userId, addedSpace);
        }
        for (FilesDelete deletedSpace : deletedSpaceList)
        {
            userId = deletedSpace.getOwnedBy();
            if (changedUserInfo.get(userId) == null)
            {
                FilesAdd addedFiles = new FilesAdd();
                addedFiles.setAccountId(deletedSpace.getAccountId());
                addedFiles.setFileCount(-1 * deletedSpace.getFileCount());
                addedFiles.setOwnedBy(deletedSpace.getOwnedBy());
                addedFiles.setSize(-1 * deletedSpace.getSize());
                changedUserInfo.put(userId, addedFiles);
            }
            else
            {
                FilesAdd addedFiles = changedUserInfo.get(userId);
                addedFiles.setSize(addedFiles.getSize() - deletedSpace.getSize());
                addedFiles.setFileCount(addedFiles.getFileCount() - deletedSpace.getFileCount());
                changedUserInfo.put(userId, addedFiles);
            }
        }
        return changedUserInfo;
    }
    
    @Override
    public List<FilesAdd> getAddedAccountInfo()
    {
        return filesAddDao.getAddedAccountInfo();
    }
    
    @Override
    public List<FilesDelete> getDeletedAccountInfo()
    {
        return filesDeleteDao.getDeletedAccountInfo();
    }
    
    @Override
    public Map<Long, AccountStatisticsInfo> getChangedAccountInfo(List<FilesAdd> accountAddedSpaces,
        List<FilesDelete> accountDeletedSpaces)
    {
        Map<Long, AccountStatisticsInfo> accountChangedSpace = new HashMap<Long, AccountStatisticsInfo>(16);
        long accountId;
        AccountStatisticsInfo accountInfo;
        for (FilesAdd accountAddedSpace : accountAddedSpaces)
        {
            accountInfo = new AccountStatisticsInfo();
            accountInfo.setAccountId(accountAddedSpace.getAccountId());
            accountInfo.setCurrentSpace(accountAddedSpace.getSize());
            accountInfo.setCurrentFiles(accountAddedSpace.getFileCount());
            accountChangedSpace.put(accountAddedSpace.getAccountId(), accountInfo);
        }
        for (FilesDelete accountDeletedSpace : accountDeletedSpaces)
        {
            accountInfo = new AccountStatisticsInfo();
            accountId = accountDeletedSpace.getAccountId();
            if (accountChangedSpace.get(accountId) == null)
            {
                accountInfo.setAccountId(accountDeletedSpace.getAccountId());
                accountInfo.setCurrentSpace(-1 * accountDeletedSpace.getSize());
                accountInfo.setCurrentFiles(-1 * accountDeletedSpace.getFileCount());
                accountChangedSpace.put(accountId, accountInfo);
            }
            else
            {
                accountInfo = accountChangedSpace.get(accountId);
                accountInfo.setCurrentSpace(accountInfo.getCurrentSpace() - accountDeletedSpace.getSize());
                accountInfo.setCurrentFiles(accountInfo.getCurrentFiles()
                    - accountDeletedSpace.getFileCount());
                accountChangedSpace.put(accountId, accountInfo);
            }
        }
        return accountChangedSpace;
    }
    
    @Override
    public UserStatisticsInfo updateUserWithCacheInfo(UserStatisticsInfo userSpaceInfoInCache,
        FilesAdd changedInfo)
    {
        UserStatisticsInfo currentInfo = updateUserInfo(userSpaceInfoInCache, changedInfo);
        if (isCacheSupported())
        {
            getCacheClient().setCache(UserStatisticsInfo.CACHE_KEY_PRIFIX_USERSINFO
                + changedInfo.getOwnedBy(),
                currentInfo);
        }
        temporaryUserSpaceDao.insert(currentInfo, changedInfo);
        filesAddDao.deleteFilesAddByMaxNodeId(changedInfo);
        filesDeleteDao.deleteFilesDeleteByCache(changedInfo);
        return currentInfo;
    }
    
    private UserStatisticsInfo updateUserInfo(UserStatisticsInfo currentUserInfo, FilesAdd changedInfo)
    {
        UserStatisticsInfo currentInfo = new UserStatisticsInfo();
        currentInfo.setLastStatisNode(changedInfo.getNodeId());
        currentInfo.setFileCount(currentUserInfo.getFileCount() + changedInfo.getFileCount());
        currentInfo.setSpaceUsed(currentUserInfo.getSpaceUsed() + changedInfo.getSize());
        return currentInfo;
    }
    
    @Override
    public UserStatisticsInfo updateUserWithoutCacheInfo(long userId, long accountId)
    {
        int count = 0;
        UserStatisticsInfo userSpaceInfo = new UserStatisticsInfo();
        List<Long> lastNodeIds;
        List<Long> nodeIds;
        Date date = null;
        while (count < 10)
        {
            count++;
            lastNodeIds = filesDeleteDao.getNodeIdsByOwnedBy(userId);
            userSpaceInfo = iNodeDAOSlaveDB.getUserInfoById(userId);
            if(null == userSpaceInfo)
            {
                userSpaceInfo = iNodeDAOV2.getUserInfoById(userId);
            }

            date = new Date();
            nodeIds = filesDeleteDao.getNodeIdsByOwnedBy(userId);
            
            if (null == lastNodeIds && null == nodeIds)
            {
                break;
            }
            else if (null != lastNodeIds && null != nodeIds && lastNodeIds.equals(nodeIds))
            {
                break;
            }
        }
        filesDeleteDao.deleteByUserId(userId);
        if (isCacheSupported())
        {
            getCacheClient().setCache(UserStatisticsInfo.CACHE_KEY_PRIFIX_USERSINFO + userId, userSpaceInfo);
            deleteCacheAfterCommit(FilesDelete.CACHE_KEY_PREFIX_DELETEDNODES + userId);
        }
        filesAddDao.deleteFilesAddByMaxNodeId(new FilesAdd(userId, userSpaceInfo.getLastStatisNode()));
        temporaryUserSpaceDao.deleteByTime(userId, date);
        clearRecycleBinDao.deleteByTime(userId, date);
        userDAOV2.updateStatisticInfo(accountId,
            userId,
            userSpaceInfo.getSpaceUsed(),
            userSpaceInfo.getFileCount());
        userReverseDAO.updateStatisticInfo(accountId,
            userId,
            userSpaceInfo.getSpaceUsed(),
            userSpaceInfo.getFileCount());
        return userSpaceInfo;
    }
    
    @Override
    public AccountStatisticsInfo updateAccountWithoutCacheInfo(long accountId)
    {
        AccountStatisticsInfo accountInfo = getAccountInfoById(accountId);
        if (null == accountInfo)
        {
            return null;
        }
        AccountStatisticsInfo storeInfo = new AccountStatisticsInfo();
        storeInfo.setAccountId(accountId);
        storeInfo.setCurrentFiles(accountInfo.getCurrentFiles());
        storeInfo.setCurrentSpace(accountInfo.getCurrentSpace() / (1024 * 1024));
        accountDao.updateStatisticsInfo(accountId, storeInfo);
        if (isCacheSupported())
        {
            getCacheClient().setCache(AccountStatisticsInfo.CACHE_KEY_PRIFIX_ACCOUNTSPACE + accountId,
                accountInfo);
        }
        return accountInfo;
    }
    
    @Override
    public AccountStatisticsInfo getAccountInfoById(long accountId)
    {
        AccountStatisticsInfo currentInfo = userReverseDAO.getAccountInfoById(accountId);
        AccountStatisticsInfo changedInfo = temporaryUserSpaceDao.getAccountChangedInfoById(accountId);
        if (currentInfo == null)
        {
            return changedInfo;
        }
        if (changedInfo == null)
        {
            return currentInfo;
        }
        currentInfo.setCurrentFiles(currentInfo.getCurrentFiles() + changedInfo.getCurrentFiles());
        currentInfo.setCurrentSpace(currentInfo.getCurrentSpace() + changedInfo.getCurrentSpace());
        return currentInfo;
    }
    
    @Override
    public AccountStatisticsInfo updateAccountWithCacheInfo(AccountStatisticsInfo accountCurrentInfo,
        AccountStatisticsInfo accountChangedInfo)
    {
        AccountStatisticsInfo currentInfo = updateAccountInfo(accountCurrentInfo, accountChangedInfo);
        if (isCacheSupported())
        {
            getCacheClient().setCache(AccountStatisticsInfo.CACHE_KEY_PRIFIX_ACCOUNTSPACE
                + currentInfo.getAccountId(),
                currentInfo);
        }
        return currentInfo;
    }
    
    private AccountStatisticsInfo updateAccountInfo(AccountStatisticsInfo currentAccountInfo,
        AccountStatisticsInfo changedInfo)
    {
        AccountStatisticsInfo currentInfo = new AccountStatisticsInfo();
        currentInfo.setAccountId(changedInfo.getAccountId());
        currentInfo.setCurrentFiles(currentAccountInfo.getCurrentFiles() + changedInfo.getCurrentFiles());
        currentInfo.setCurrentSpace(currentAccountInfo.getCurrentSpace() + changedInfo.getCurrentSpace());
        return currentInfo;
    }
    
    @Override
    public AccountStatisticsInfo getAccountCurrentInfo(long accountId)
    {
        AccountStatisticsInfo currentAccountSpace;
        if (isCacheSupported()
            && getCacheClient().getCache(AccountStatisticsInfo.CACHE_KEY_PRIFIX_ACCOUNTSPACE + accountId) != null)
        {
            currentAccountSpace = (AccountStatisticsInfo) getCacheClient().getCache(AccountStatisticsInfo.CACHE_KEY_PRIFIX_ACCOUNTSPACE
                + accountId);
            
        }
        else
        {
            currentAccountSpace = updateAccountWithoutCacheInfo(accountId);
        }
        return currentAccountSpace;
    }
    
    @Override
    public UserStatisticsInfo getUserCurrentInfo(long userId, long accountId)
    {
        UserStatisticsInfo currentUserSpace = null;
        String key = UserStatisticsInfo.CACHE_KEY_PRIFIX_USERSINFO + userId;
        if (isCacheSupported()
            && getCacheClient().getCache(key) != null)
        {
            Object o = getCacheClient().getCache(key);
            try {
                currentUserSpace = (UserStatisticsInfo) getCacheClient().getCache(key);
            }catch (Exception e){
                currentUserSpace = updateUserWithoutCacheInfo(userId, accountId);
                getCacheClient().setCache(key,currentUserSpace);
            }
        }
        else
        {
            currentUserSpace = updateUserWithoutCacheInfo(userId, accountId);
        }
        return currentUserSpace;
    }
}
