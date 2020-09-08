package com.huawei.sharedrive.app.oauth2.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.account.dao.AccountDao;
import com.huawei.sharedrive.app.account.domain.Account;

@Service("accountIdAdapter")
public class AccountIdAdapter
{
    @Autowired
    private AccountDao accountDao;
    
    private static Logger logger = LoggerFactory.getLogger(AccountIdAdapter.class);
    
    private static final Map<String, Long> APPIDDEFAULTACCOUNTMAP = new HashMap<String, Long>(10);
    
    public long getAccountId(String appId)
    {
        Long value = AccountIdAdapter.APPIDDEFAULTACCOUNTMAP.get(appId);
        if(value != null)
        {
            return value;
        }
        List<Account> list = accountDao.getListByAppId(appId);
        if(list.isEmpty())
        {
            return -1;
        }
        if(list.size() != 1)
        {
            logger.warn(appId + " AppId has account number is " + list.size());
        }
        value = list.get(0).getId();
        AccountIdAdapter.APPIDDEFAULTACCOUNTMAP.put(appId, value);
        return value;
    }
    
}
