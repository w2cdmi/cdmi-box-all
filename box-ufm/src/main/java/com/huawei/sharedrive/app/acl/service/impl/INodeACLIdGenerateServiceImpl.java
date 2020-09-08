package com.huawei.sharedrive.app.acl.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.acl.dao.INodeACLDAO;
import com.huawei.sharedrive.app.acl.service.INodeACLIdGenerateService;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * ACL Id生成器，按团队空间ID做自增，zk中无数据时，自动使用INodeACL表中最大值作为初始种子
 * 
 * @author t00159390
 * 
 */
@Component
public class INodeACLIdGenerateServiceImpl implements SeedInitializer, INodeACLIdGenerateService
{
    private static final String BASE_PATH = "/space_inode_acl_id";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private INodeACLDAO iNodeACLDAO;
    
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
        return iNodeACLDAO.getMaxINodeACLId(Long.parseLong(subPath));
    }
    
    @Override
    public long getNextNodeACLId(long cloudUserId)
    {
        return sequenceGenerator.getSequence(String.valueOf(cloudUserId));
    }
    
    @Override
    public void delete(long cloudUserId)
    {
        sequenceGenerator.delete(String.valueOf(cloudUserId));
    }
    
}
