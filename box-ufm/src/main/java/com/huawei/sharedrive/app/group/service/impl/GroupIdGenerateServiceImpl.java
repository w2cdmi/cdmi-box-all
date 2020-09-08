package com.huawei.sharedrive.app.group.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.group.dao.GroupDAO;
import com.huawei.sharedrive.app.group.service.GroupIdGenerateService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class GroupIdGenerateServiceImpl implements GroupIdGenerateService,SeedInitializer
{

    private static final String BASE_PATH = "/group_id";

    private CuratorFramework client;
    
    private static final String SUB_PATH = "groupId";
    
    @Autowired
    private GroupDAO groupDao;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public void delete()
    {
        sequenceGenerator.delete(SUB_PATH);
    }
    
    @Override
    public long getNextGroupId()
    {
        return sequenceGenerator.getSequence(SUB_PATH);
    }
 
    @Override
    public long getSeed(String sub)
    {
        return groupDao.getMaxGroupId();
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
