package com.huawei.sharedrive.uam.accountuser.dao;

import java.util.List;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;

import com.huawei.sharedrive.uam.user.domain.User;
import pw.cdmi.box.domain.Limit;

public interface UserAccountDao
{
    long getMaxUserId();
    
    void create(UserAccount userAccount);
    
    void update(UserAccount userAccount);
    
    UserAccount get(long userId, long accountId);

    List<UserAccount> getByEnterpriseId(long enterpriseId,long accountId);

    void delByUserAccountId(UserAccount userAccount);
    
    UserAccount getBycloudUserAccountId(UserAccount userAccount);
    
    UserAccount getById(long id, long accountId);

    UserAccount getByImAccount(String imAccount, long accountId);

    int getFilterdCount(long accountId, long enterpriseId, long userSource, String filter, Integer status);
    
    List<UserAccount> getFilterd(UserAccount userAccount, long userSource, Limit limit,
        String filter);
    
    void updateStatus(UserAccount userAccount, String ids);
    
    void updateRole(UserAccount userAccount, String ids);
    
    void updateLoginTime(UserAccount userAccount);

    void updateSpaceQuota(UserAccount userAccount);
    
    void updateFirstLogin(UserAccount userAccount);
    
    void updateUserIdById(UserAccount userAccount);

    //查询accountId下账号数量
    int countByAccountId(long accountId);

    //查询accountId下所有已分配额度总和
    long sumSpaceQuotaByAccountId(long accountId);

    //统计accountId下配额为quota的数量
    int countByAccountIdAndSpaceQuota(long accountId, long quota);

    //将accountId下所有配额为oldValue的调整为newValue
    void compareAndSwapSpaceQuotaByAccountId(long accountId, long oldValue, long newValue);

    //查询accountId下特定用户的已分配额度总和
    long sumSpaceQuotaByAccountIdAndUserIds(long accountId, List<Long> userIds);

    //将accountId下所有配额为oldValue的调整为newValue
    void updateSpaceQuotaByAccountIdAndUserIds(long accountId, List<Long> userIds, long quota);
}
