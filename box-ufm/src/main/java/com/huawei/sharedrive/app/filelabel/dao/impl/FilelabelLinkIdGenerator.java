package com.huawei.sharedrive.app.filelabel.dao.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.filelabel.dao.IFileLabelDAO;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * 
 * Desc  : 文件標簽與文件關聯表主鍵生成器
 * Author: 77235
 * Date	 : 2016年12月15日
 */
@Component("filelabelLinkIdGenerator")
public class FilelabelLinkIdGenerator implements SeedInitializer {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(FilelabelLinkIdGenerator.class);

    private static final String BASE_PATH = "/filelabel_link_id";
    
    private CuratorFramework client;
    
    @Autowired
    private IFileLabelDAO filelabelDao;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @PostConstruct
    public void init()  {
        client = zookeeperServer.getClient();
        
        try {
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        } catch (Exception e) {
        	LOGGER.error("[FilelabelLinkIdGenerator] init error", e);
        }
    }
    
    @Override
    public long getSeed(String subPath) {
        return filelabelDao.getMaxFilelabelLinkId(Long.valueOf(subPath));
    }
    
    public long getNextFilelabelLinkId(long ownerId) {
        return sequenceGenerator.getSequence(String.valueOf(ownerId));
    }

    public void delete(long ownerId){
        sequenceGenerator.delete(String.valueOf(ownerId));
    }
}
