package pw.cdmi.box.uam.user.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.user.dao.UserDAO;
import pw.cdmi.box.uam.user.service.UserIdGenerateService;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class UserIdGenerateServiceImpl implements SeedInitializer, UserIdGenerateService
{
    private static final String BASE_PATH = "/user";
    
    private static final String SUB_PATH = "userId";
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Value("${zk.root.path}")
    private String rootPath;
    
    @Autowired
    private UserDAO userDao;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public long getNextUserId()
    {
        return sequenceGenerator.getSequence(SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return userDao.getMaxUserId();
    }
    
    @PostConstruct
    public void init() throws Exception
    {
        client = zookeeperServer.getClient();
        sequenceGenerator = new SequenceGenerator(client, this, rootPath + BASE_PATH);
    }
    
}
