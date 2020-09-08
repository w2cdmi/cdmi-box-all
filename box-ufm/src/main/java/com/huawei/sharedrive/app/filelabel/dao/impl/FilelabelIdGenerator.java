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
 * Desc  : 文件標簽主鍵生成器
 * Author: 77235
 * Date	 : 2016年12月15日
 */
@Component("filelabelIdGenerator")
public class FilelabelIdGenerator implements SeedInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilelabelIdGenerator.class);

    private static final String BASE_PATH = "/filelabel_id";
    
    private static final String SUB_PATH = "filelabelId";
    
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
        	LOGGER.error("[FilelabelIdGenerator] init error:" + e.getMessage(), e);
        }
    }
    
    @Override
    public long getSeed(String subPath) {
        return filelabelDao.getMaxFilelabelId();
    }
    
    public long getNextFilelabelId() {
        return sequenceGenerator.getSequence(SUB_PATH);
    }
    
    public void delete(){
        sequenceGenerator.delete(SUB_PATH);
    }
}
