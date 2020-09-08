package com.huawei.sharedrive.app.account.mamager;

import com.huawei.sharedrive.app.account.domain.AccountAccessKey;

public interface AccountAccessKeyManager
{
    AccountAccessKey getAccountKeyById(String appId,String id);
}
