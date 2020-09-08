package com.huawei.sharedrive.uam.accountuser.manager;

import com.huawei.sharedrive.uam.accountuser.domain.UpdateUserAccountList;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;

public interface UpdateUserAccountListManager
{
    void updateUserList(UpdateUserAccountList updateUserAccountList, String sessionId, String appId,
        Long authServerId, Long enterpriseId);
    
    void updateUserStatus(UserLdap userLdap, UserAccount userAccount, String appId, String filter,
        Integer selStatus);
    
    void updateRole(UserLdap userLdap, UserAccount userAccount, String appId, String ids, String filter);
}
