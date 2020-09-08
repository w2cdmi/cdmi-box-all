package com.huawei.sharedrive.isystem.account.service;

import com.huawei.sharedrive.isystem.account.domain.Account;
import com.huawei.sharedrive.isystem.account.domain.AccountPageCondition;

import pw.cdmi.box.domain.Page;

public interface AccountService
{

    Page<Account> queryPage(AccountPageCondition condition);
}
