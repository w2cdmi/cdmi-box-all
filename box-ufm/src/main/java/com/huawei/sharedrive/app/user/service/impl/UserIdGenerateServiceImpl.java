/**
 * 
 */
package com.huawei.sharedrive.app.user.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.exception.InternalServerErrorException;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.service.UserIdGenerateService;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * User Id生成器，zk中无数据时，自动使用user表中最大值作为初始种子
 * 
 * @author q90003805
 * 
 */
@Component
public class UserIdGenerateServiceImpl implements SeedInitializer, UserIdGenerateService
{
    private static final String BASE_PATH = "/user";

    private static final String SUB_PATH = "userId";
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private UserDAOV2 userDao;
    
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
    public void init() throws InternalServerErrorException
    {
        try
        {
            client = zookeeperServer.getClient();
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        }
        catch(Exception e)
        {
            throw new InternalServerErrorException(e);
        }
    }
    
}
