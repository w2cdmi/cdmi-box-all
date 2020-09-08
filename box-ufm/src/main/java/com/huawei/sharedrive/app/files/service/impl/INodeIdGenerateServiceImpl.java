/**
 * 
 */
package com.huawei.sharedrive.app.files.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.files.dao.INodeDAO;
import com.huawei.sharedrive.app.files.service.INodeIdGenerateService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * INode Id生成器，按用户做自增，zk中无数据时，自动使用inode表中最大值作为初始种子
 * 
 * @author q90003805
 * 
 */
@Component
public class INodeIdGenerateServiceImpl implements SeedInitializer, INodeIdGenerateService
{
    private static final String BASE_PATH = "/user_inode_id";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private INodeDAO iNodeDAO;
    
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
        return iNodeDAO.getMaxINodeId(Long.parseLong(subPath));
    }
    
    @Override
    public long getNextUserNodeId(long userId)
    {
        return sequenceGenerator.getSequence(String.valueOf(userId));
    }
    
    @Override
    public void delete(long userId)
    {
        sequenceGenerator.delete(String.valueOf(userId));
    }
}
