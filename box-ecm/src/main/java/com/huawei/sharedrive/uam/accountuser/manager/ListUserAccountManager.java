package com.huawei.sharedrive.uam.accountuser.manager;

import java.util.List;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccountExtend;
import com.huawei.sharedrive.uam.enterpriseuser.domain.UserLdap;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageRequest;

public interface ListUserAccountManager
{
    Page<UserAccountExtend> getPagedUserAccount(UserLdap userLdap, String appId, String filter,
        Integer status, PageRequest pageRequest);
    
    List<UserAccount> getUserAccountList(UserLdap userLdap, UserAccount userAccount, String appId,
        String filter);
}
