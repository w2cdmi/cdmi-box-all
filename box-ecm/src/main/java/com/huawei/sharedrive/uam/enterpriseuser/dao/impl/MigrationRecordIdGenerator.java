
package com.huawei.sharedrive.uam.enterpriseuser.dao.impl;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterpriseuser.dao.IMigrationRecordDao;

import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

/**
 * 
 * Desc  : 主键生成器
 * Author: 77235
 * Date	 : 2016年12月26日
 */
@Component("migrationRecordIdGenerator")
public class MigrationRecordIdGenerator implements SeedInitializer {
    private static final String BASE_PATH = "/migration_record_id";
    
    private CuratorFramework client;
    
    @Autowired
    private IMigrationRecordDao migrationRecordDao;
    
    private SequenceGenerator sequenceGenerator;
    
    @Autowired
    private ZookeeperServer zookeeperServer;
    
    @PostConstruct
    public void init()  {
        client = zookeeperServer.getClient();
        
        try {
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public long getSeed(String subPath) {
        return migrationRecordDao.getMaxRecordId();
    }
    
    public long getNextRecordId() {
        return sequenceGenerator.getSequence("maxId");
    }
    
    public void delete(){
        sequenceGenerator.delete("maxId");
    }
}
