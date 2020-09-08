package com.huawei.sharedrive.app.plugins.preview.dao.impl;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.app.plugins.preview.dao.AccountWatermarkDao;
import com.huawei.sharedrive.app.plugins.preview.domain.AccountWatermark;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;

@Service("accountWatermarkDao")
@SuppressWarnings({"deprecation"})
public class AccountWatermarkDaoImpl extends AbstractDAOImpl implements AccountWatermarkDao
{
    
    @Override
    public void replace(AccountWatermark watermark)
    {
        sqlMapClientTemplate.insert("AccountWatermark.replace", watermark);
    }
    
    @Override
    public AccountWatermark get(long accountId)
    {
        return (AccountWatermark) sqlMapClientTemplate.queryForObject("AccountWatermark.get", accountId);
    }
    
}
