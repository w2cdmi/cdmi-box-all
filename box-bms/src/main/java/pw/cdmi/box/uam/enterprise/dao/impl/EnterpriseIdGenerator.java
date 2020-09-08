/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package pw.cdmi.box.uam.enterprise.dao.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.enterprise.dao.EnterpriseDao;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * 
 */
@Service("enterpriseIdGenerator")
public class EnterpriseIdGenerator implements SeedInitializer
{
    private static final String BASE_PATH = "/enterprise_id";
    
    
    private CuratorFramework client;

    @Autowired
    private EnterpriseDao enterpriseDao;

    private SequenceGenerator sequenceGenerator;

    @Autowired
    private ZookeeperServer zookeeperServer;

    public CuratorFramework getClient()
    {
        return client;
    }

    public EnterpriseDao getEnterpriseDao()
    {
        return enterpriseDao;
    }

    public long getNextId()
    {
        return sequenceGenerator.getSequence("maxId");
    }


    @Override
    public long getSeed(String subPath)
    {
        return enterpriseDao.queryMaxExecuteRecordId();
    }
    
    public SequenceGenerator getSequenceGenerator()
    {
        return sequenceGenerator;
    }
    
    public ZookeeperServer getZookeeperServer()
    {
        return zookeeperServer;
    }
    
    @PostConstruct
    public void init() throws Exception
    {
        client = zookeeperServer.getClient();
        sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
    }
    
    public void setClient(CuratorFramework client)
    {
        this.client = client;
    }
    
    public void setEnterpriseDao(EnterpriseDao enterpriseDao)
    {
        this.enterpriseDao = enterpriseDao;
    }

    public void setSequenceGenerator(SequenceGenerator sequenceGenerator)
    {
        this.sequenceGenerator = sequenceGenerator;
    }


    public void setZookeeperServer(ZookeeperServer zookeeperServer)
    {
        this.zookeeperServer = zookeeperServer;
    }
}
