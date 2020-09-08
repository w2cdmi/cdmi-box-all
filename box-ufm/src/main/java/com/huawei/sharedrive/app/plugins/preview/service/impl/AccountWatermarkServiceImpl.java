package com.huawei.sharedrive.app.plugins.preview.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.plugins.preview.dao.AccountWatermarkDao;
import com.huawei.sharedrive.app.plugins.preview.domain.AccountWatermark;
import com.huawei.sharedrive.app.plugins.preview.service.AccountWatermarkService;

@Service("accountWatermarkService")
public class AccountWatermarkServiceImpl implements AccountWatermarkService
{
    @Autowired
    private AccountWatermarkDao accountWatermarkDao;
    
    @Override
    public AccountWatermark getWatermarkByAccountId(long accountId)
    {
        AccountWatermark mark = accountWatermarkDao.get(accountId);
        if (mark == null)
        {
            return null;
        }
        if (mark.getWatermark() == null)
        {
            mark.setWatermark(new byte[0]);
        }
        return mark;
    }
    
    @Override
    public void setWatermarkByAccountId(long accountId, byte[] data)
    {
        AccountWatermark mark = new AccountWatermark();
        mark.setAccountId(accountId);
        mark.setWatermark(data);
        mark.setLastConfigTime(new Date());
        accountWatermarkDao.replace(mark);
    }
}
