package com.huawei.sharedrive.uam.enterprise.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.dao.FileCopySecurityDao;
import com.huawei.sharedrive.uam.enterprise.service.FileCopySecurityIdService;
import com.huawei.sharedrive.uam.idgenerate.util.IdGenerateUtil;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

@Component
public class FileCopySecurityIdServiceImpl implements FileCopySecurityIdService, SeedInitializer
{
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Value("${zk.root.path}")
    private String rootPath;
    
    @Autowired
    private FileCopySecurityDao copySecurityDao;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @Override
    public long getNextId()
    {
        return sequenceGenerator.getSequence(IdGenerateUtil.FILE_COPY_SECURITY_SUB_PATH);
    }
    
    @PostConstruct
    public void init() throws Exception
    {
        client = zookeeperServer.getClient();
        sequenceGenerator = new SequenceGenerator(client, this, rootPath
            + IdGenerateUtil.FILE_COPY_SECURITY_SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return copySecurityDao.getMaxId();
    }
}
