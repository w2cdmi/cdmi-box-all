package com.huawei.sharedrive.app.account.mamager;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.openapi.domain.account.RestAccount;
import com.huawei.sharedrive.app.openapi.domain.account.RestCreateAccountRequest;
import com.huawei.sharedrive.app.openapi.domain.account.RestModifyAccountRequest;

public interface AccountManager
{
    RestAccount create(RestCreateAccountRequest account, String appId);
    
    Account getById(long id);
    
    RestAccount getRestAccountById(long id);
    
    RestAccount modify(RestModifyAccountRequest accountDao, String appId, Long accountId, String authorization);
}
