package com.huawei.sharedrive.app.message.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.message.dao.MessageDAO;
import com.huawei.sharedrive.app.message.service.MessageIdGenerateService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * 用户消息id生成器
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2015-3-23
 * @see
 * @since
 */
@Component("messageIdGenerateService")
public class MessageIdGenerateServiceImpl implements MessageIdGenerateService, SeedInitializer
{
    
    private static final String BASE_PATH = "/user_message_id";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private MessageDAO messageDAO;
    
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
    
    @Override
    public long getSeed(String subPath)
    {
        return messageDAO.getMaxId(Long.parseLong(subPath));
    }
    
    @Override
    public long getNextMessageId(long receiverId)
    {
        return sequenceGenerator.getSequence(String.valueOf(receiverId));
    }
    
    @Override
    public void delete(long userId)
    {
        sequenceGenerator.delete(String.valueOf(userId));
    }
    
}
