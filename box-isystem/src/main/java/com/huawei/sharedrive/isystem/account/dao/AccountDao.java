package com.huawei.sharedrive.isystem.account.dao;

import java.util.List;

import com.huawei.sharedrive.isystem.account.domain.Account;
import com.huawei.sharedrive.isystem.account.domain.AccountPageCondition;

public interface AccountDao
{

    List<Account> getFilterd(AccountPageCondition filter);

    int getFilterdCount(AccountPageCondition filter);

}
