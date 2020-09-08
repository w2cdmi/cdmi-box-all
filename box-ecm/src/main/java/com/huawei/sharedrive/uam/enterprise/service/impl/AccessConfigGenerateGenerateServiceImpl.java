package com.huawei.sharedrive.uam.enterprise.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.AccessConfigDao;
import com.huawei.sharedrive.uam.enterprise.service.AccessConfigGenerateService;
import com.huawei.sharedrive.uam.idgenerate.util.IdGenerateUtil;

import pw.cdmi.core.exception.ZookeeperException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class AccessConfigGenerateGenerateServiceImpl implements SeedInitializer, AccessConfigGenerateService
{
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Value("${zk.root.path}")
    private String rootPath;
    
    @Autowired
    private AccessConfigDao accessConfigDao;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public long getNextId()
    {
        return sequenceGenerator.getSequence(IdGenerateUtil.ACCESS_CONFIG_SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return accessConfigDao.getMaxId();
    }
    
    @PostConstruct
    public void init() throws ZookeeperException
    {
        client = zookeeperServer.getClient();
        sequenceGenerator = new SequenceGenerator(client, this, rootPath
            + IdGenerateUtil.ACCESS_CONFIG_SUB_PATH);
    }
    
}