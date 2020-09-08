package com.huawei.sharedrive.uam.idgenerate.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.accountuser.dao.UserAccountDao;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.idgenerate.service.UserAccountGenerateService;
import com.huawei.sharedrive.uam.idgenerate.util.IdGenerateUtil;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class UserAccountGenerateServiceImpl implements SeedInitializer, UserAccountGenerateService
{
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Value("${zk.root.path}")
    private String rootPath;
    
    @Autowired
    private UserAccountDao userAccountDao;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public long getNextUserId()
    {
        return sequenceGenerator.getSequence(IdGenerateUtil.ACCOUNT_USER_SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return userAccountDao.getMaxUserId();
    }
    
    @PostConstruct
    public void init() throws InternalServerErrorException
    {
        try
        {
            client = zookeeperServer.getClient();
            sequenceGenerator = new SequenceGenerator(client, this, rootPath
                + IdGenerateUtil.ACCOUNR_USER_BASE_PATH);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
    }
    
}
