package pw.cdmi.box.uam.message.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.message.dao.MessageDAO;
import pw.cdmi.box.uam.message.service.MessageIdGenerateService;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * 用户消息id生成器
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
    public void init() throws Exception
    {
        client = zookeeperServer.getClient();
        sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
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
