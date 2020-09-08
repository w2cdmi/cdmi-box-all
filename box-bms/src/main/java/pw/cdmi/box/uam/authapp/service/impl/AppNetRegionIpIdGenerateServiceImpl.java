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
package pw.cdmi.box.uam.authapp.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.authapp.dao.AuthAppNetRegionIpDao;
import pw.cdmi.box.uam.authapp.service.AppNetRegionIpIdGenerateService;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;


@Service("appNetRegionIpIdGenerateService")
public class AppNetRegionIpIdGenerateServiceImpl implements SeedInitializer, AppNetRegionIpIdGenerateService
{
    private static final String BASE_PATH = "/appnetregionip_id";
    
    private static final String SUB_PATH = "appnetregionip";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private AuthAppNetRegionIpDao authAppNetRegionIpDao;
    
    @PostConstruct
    public void init() throws Exception
    {
        client = zookeeperServer.getClient();
        sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
    }
    
    @Override
    public long getNextId()
    {
        return sequenceGenerator.getSequence(SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return authAppNetRegionIpDao.getMaxId();
    }
    
}
