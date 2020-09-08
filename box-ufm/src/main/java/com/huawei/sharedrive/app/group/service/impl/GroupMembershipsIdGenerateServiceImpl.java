package com.huawei.sharedrive.app.group.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.group.dao.GroupMembershipsDAO;
import com.huawei.sharedrive.app.group.service.GroupMembershipsIdGenerateService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class GroupMembershipsIdGenerateServiceImpl implements GroupMembershipsIdGenerateService,
    SeedInitializer
{
    
    private static final String BASE_PATH = "/memberships_id";
    
    private CuratorFramework client;
    
    @Autowired
    private GroupMembershipsDAO membershipsDao;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public void delete(long groupId)
    {
        sequenceGenerator.delete(String.valueOf(groupId));
    }
    
    @Override
    public long getNextMembershipsId(long groupId)
    {
        return sequenceGenerator.getSequence(String.valueOf(groupId));
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return membershipsDao.getMaxMembershipsId(Long.parseLong(subPath));
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
