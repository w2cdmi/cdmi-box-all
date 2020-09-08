package com.huawei.sharedrive.app.account.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.account.dao.AccountDao;
import com.huawei.sharedrive.app.account.service.AccountIdService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class AccountIdServiceImpl implements SeedInitializer, AccountIdService
{
    
    private static final String BASE_PATH = "/account";
    
    private static final String SUB_PATH = "accountId";
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private AccountDao accountDao;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public long getNextAccountId()
    {
        return sequenceGenerator.getSequence(SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return accountDao.getMaxAccountId();
    }
    
    @PostConstruct
    public void init()
    {
        client = zookeeperServer.getClient();
        try
        {
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
    }
    
}
