package com.huawei.sharedrive.uam.message.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.InternalServerErrorException;
import com.huawei.sharedrive.uam.message.dao.MessageDAO;
import com.huawei.sharedrive.uam.message.service.MessageIdGenerateService;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * 
 */
@Component("messageIdGenerateService")
public class MessageIdGenerateServiceImpl implements MessageIdGenerateService, SeedInitializer
{
    
    private static final String BASE_PATH = "/message_id";
    
    private static final String SUB_PATH_MESSAGE = "message";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private MessageDAO messageDAO;
    
    @PostConstruct
    public void init() throws InternalServerErrorException
    {
        try
        {
            client = zookeeperServer.getClient();
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e);
        }
    }
    
    @Override
    public long getSeed(String type)
    {
        return messageDAO.getMaxId();
    }
    
    @Override
    public long getNextId()
    {
        return sequenceGenerator.getSequence(SUB_PATH_MESSAGE);
    }
}
