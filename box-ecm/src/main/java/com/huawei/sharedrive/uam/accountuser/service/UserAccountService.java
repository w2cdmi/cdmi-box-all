package com.huawei.sharedrive.uam.accountuser.service;

import java.util.List;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;

import com.huawei.sharedrive.uam.user.domain.User;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.common.domain.AppBasicConfig;

public interface UserAccountService
{
    /**
     * create user account
     * 
     * @param userAccount
     * @return
     */
    void create(UserAccount userAccount);
    
    void update(UserAccount userAccount);
    
    UserAccount get(long userId, long accountId);

    List<UserAccount> getByEnterpriseId(long enterpriseId,long accountId);
    
    void delByUserAccountId(UserAccount userAccount);
    
    UserAccount getById(long id, long accountId);
    
    UserAccount getByImAccount(String imAccount, long accountId);

    void bulidUserAccount(UserAccount userAccount, AppBasicConfig appBasicConfig);
    
    void bulidUserAccountParam(UserAccount userAccount, AppBasicConfig appBasicConfig);
    
    int getFilterdCount(long accountId, long enterpriseId, long userSource, String filter, Integer status);
    
    List<UserAccount> getFilterd(UserAccount userAccount, long userSource, Limit limit, String filter);
    
    void updateStatus(UserAccount userAccount, String ids);
    
    void updateRole(UserAccount userAccount, String ids);
    
    void updateLoginTime(UserAccount userAccount);

    void updateSpaceQuota(UserAccount userAccount);
    
    UserAccount getBycloudUserAccountId(UserAccount userAccount);
    
    boolean isLocalAndFirstLogin(long accountId, long userId);
    
    void setNoneFirstLogin(long accountId, long userId);
    
    void updateUserIdById(UserAccount userAccount);

    //计算accountId下已经分配的容量
    long sumSpaceQuotaByAccountId(long accountId);

    //计算accountId下账号数量
    int countByAccountId(long accountId);

    //统一调整企业内的用户空间配额，将容量为from的用户批量改为to。容量不为from的不修改。
    public void updateAccountQuota(long accountId, long from, long to);

    //调整企业内的指定用户的空间配额，将容量设置为quota。
    public void updateUserAccountSpaceQuota(long accountId, List<Long> userIds, long quota);
}
