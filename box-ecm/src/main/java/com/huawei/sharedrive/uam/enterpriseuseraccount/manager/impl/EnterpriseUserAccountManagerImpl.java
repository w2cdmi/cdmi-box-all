package com.huawei.sharedrive.uam.enterpriseuseraccount.manager.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.enterpriseuseraccount.domain.EnterpriseUserAccount;
import com.huawei.sharedrive.uam.enterpriseuseraccount.manager.EnterpriseUserAccountManager;
import com.huawei.sharedrive.uam.enterpriseuseraccount.service.EnterpriseUserAccountService;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseSearchUser;
import com.huawei.sharedrive.uam.openapi.domain.user.ResponseUser;

import pw.cdmi.box.domain.Limit;
import pw.cdmi.core.utils.SqlUtils;

@Component
public class EnterpriseUserAccountManagerImpl implements EnterpriseUserAccountManager
{
    @Autowired
    private EnterpriseUserAccountService enterpriseUserAccountService;
    
    @Autowired
    private EnterpriseUserService enterpriseUserService;
    
    @Autowired
    private UserAccountManager userAccountManager;
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public ResponseSearchUser listUser(int limit, int offset, String filter, long accountid,
        long enterpriseId, String appId)
    {
        Limit objectLimit = new Limit();
        objectLimit.setOffset(Long.valueOf(offset));
        objectLimit.setLength(limit);
        String tempFilter = SqlUtils.stringToSqlLikeFields(filter);
        List<ResponseUser> responseUsers = enterpriseUserAccountService.listUser(appId,
            accountid,
            enterpriseId,
            tempFilter,
            objectLimit);
        int total = enterpriseUserAccountService.listUserCount(accountid, enterpriseId, tempFilter, objectLimit);
        ResponseSearchUser responseSearchUser = new ResponseSearchUser();
        responseSearchUser.setLimit(objectLimit.getLength());
        responseSearchUser.setOffset(objectLimit.getOffset());
        responseSearchUser.setTotalCount((long) total);
        responseSearchUser.setUsers(responseUsers);
        return responseSearchUser;
    }
    
    @Override
    public EnterpriseUserAccount get(Long userId, long accountId, long enterpriseId)
    {
        EnterpriseUser enterpriseUser = enterpriseUserService.get(userId, enterpriseId);
        if (null == enterpriseUser)
        {
            return null;
        }
        UserAccount userAccount = userAccountManager.get(userId, accountId);
        if (null == userAccount)
        {
            return null;
        }
        EnterpriseUserAccount enterpriseUserAccount = new EnterpriseUserAccount();
        EnterpriseUserAccount.copyEnterpriseUser(enterpriseUserAccount, enterpriseUser);
        EnterpriseUserAccount.copyAccountUser(enterpriseUserAccount, userAccount);
        return enterpriseUserAccount;
    }

    @Override
    public EnterpriseUserAccount getByImAccount(String imAccount, long accountId, long enterpriseId) {
        UserAccount userAccount = userAccountManager.getByImAccount(imAccount, accountId);
        EnterpriseUser enterpriseUser = enterpriseUserService.get(userAccount.getUserId(), enterpriseId);

        EnterpriseUserAccount enterpriseUserAccount = new EnterpriseUserAccount();
        EnterpriseUserAccount.copyEnterpriseUser(enterpriseUserAccount, enterpriseUser);
        EnterpriseUserAccount.copyAccountUser(enterpriseUserAccount, userAccount);
        return enterpriseUserAccount;
    }
}
