package com.huawei.sharedrive.isystem.account.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.isystem.account.dao.AccountDao;
import com.huawei.sharedrive.isystem.account.domain.Account;
import com.huawei.sharedrive.isystem.account.domain.AccountPageCondition;
import com.huawei.sharedrive.isystem.account.service.AccountService;

import pw.cdmi.box.domain.Page;
import pw.cdmi.box.domain.PageImpl;

@Service("accountService")
public class AccountServiceImpl implements AccountService
{
    @Autowired
    private AccountDao accountDao;
    
    @Override
    public Page<Account> queryPage(AccountPageCondition condition)
    {
        int total = accountDao.getFilterdCount(condition);
        List<Account> content = accountDao.getFilterd(condition);
        Page<Account> page = new PageImpl<Account>(content, condition.getPageRequest(), total);
        return page;
    }   
}
