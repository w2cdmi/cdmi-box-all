package com.huawei.sharedrive.uam.idgenerate.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseuser.dao.EnterpriseUserDao;
import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.idgenerate.service.EnterpriseUserGenerateService;
import com.huawei.sharedrive.uam.idgenerate.util.IdGenerateUtil;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class EnterpriseUserGenerateServiceImpl implements SeedInitializer, EnterpriseUserGenerateService
{
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Value("${zk.root.path}")
    private String rootPath;
    
    @Autowired
    private EnterpriseUserDao enterpriseUserDao;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public long getNextUserId()
    {
        return sequenceGenerator.getSequence(IdGenerateUtil.ENTERPRISE_USER_SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return enterpriseUserDao.getMaxUserId();
    }
    
    @PostConstruct
    public void init() throws InternalServerErrorException
    {
        try
        {
            client = zookeeperServer.getClient();
            sequenceGenerator = new SequenceGenerator(client, this, rootPath
                + IdGenerateUtil.ENTERPRISE_USER_BASE_PATH);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
    }
    
}
