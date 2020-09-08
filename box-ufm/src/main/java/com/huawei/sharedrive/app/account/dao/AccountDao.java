package com.huawei.sharedrive.app.account.dao;

import java.util.List;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.spacestatistics.domain.AccountStatisticsInfo;

public interface AccountDao
{
    
    void create(Account account);
    
    void delete(long accountId);
    
    long getMaxAccountId();
    
    Account getByDomain(String domain);
    
    Account getById(long accountId);
    
    int update(Account account);
    
    /**
     * 查询指定的AppId的Account,按升序方式查询
     * 
     * @param appid
     * @return
     */
    Account getOneAccountOrderByACS(String appId, long id);
    
    List<Account> getListByAppId(String appId);
    
    void updateStatisticsInfo(long accountId, AccountStatisticsInfo accountInfo);
    
    long getMaxSpace(long accountId);
    
    long getMaxSpaceForDB(long accountId);
}
