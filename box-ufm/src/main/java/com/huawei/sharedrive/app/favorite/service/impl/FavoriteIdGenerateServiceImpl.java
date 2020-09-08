package com.huawei.sharedrive.app.favorite.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.favorite.dao.FavoriteNodeDao;
import com.huawei.sharedrive.app.favorite.service.FavoriteIdGenerateService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class FavoriteIdGenerateServiceImpl implements SeedInitializer, FavoriteIdGenerateService
{
    
    private static final String BASE_PATH = "/user_favorite_id";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private FavoriteNodeDao favoriteNodeDao;
    
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
        return favoriteNodeDao.getMaxId(Long.parseLong(subPath));
    }
    
    @Override
    public long getNextId(long userId)
    {
        return sequenceGenerator.getSequence(String.valueOf(userId));
    }
    
    @Override
    public void delete(long userId)
    {
        sequenceGenerator.delete(String.valueOf(userId));
    }
}
