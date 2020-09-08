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
package pw.cdmi.box.uam.announcement.service.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.announcement.dao.AnnouncementDao;
import pw.cdmi.box.uam.announcement.service.AnnouncementIdGenerateService;
import pw.cdmi.core.exception.ZookeeperException;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;


@Service("announcementIdGenerateService")
public class AnnouncementIdGenerateServiceImpl implements SeedInitializer, AnnouncementIdGenerateService
{
    private static final String BASE_PATH = "/announcement_id";
    
    private static final String SUB_PATH = "announcement";
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    private CuratorFramework client;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private AnnouncementDao announcementDao;
    
    @PostConstruct
    public void init() throws ZookeeperException 
    {
        client = zookeeperServer.getClient();
        try {
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    public long getNextId()
    {
        return sequenceGenerator.getSequence(SUB_PATH);
    }
    
    @Override
    public long getSeed(String subPath)
    {
        return announcementDao.getMaxId();
    }
    
}
