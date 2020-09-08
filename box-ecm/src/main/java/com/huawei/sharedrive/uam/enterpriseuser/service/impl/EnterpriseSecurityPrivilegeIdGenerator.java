
package com.huawei.sharedrive.uam.enterpriseuser.service.impl;

import com.huawei.sharedrive.uam.enterpriseuser.dao.EnterpriseSecurityPrivilegeDao;
import com.huawei.sharedrive.uam.enterpriseuser.dao.IMigrationRecordDao;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pw.cdmi.core.utils.SeedInitializer;
import pw.cdmi.core.utils.SequenceGenerator;
import pw.cdmi.core.zk.ZookeeperServer;

import javax.annotation.PostConstruct;

/**
 * Desc  : 主键生成器
 */
@Component("enterpriseSecurityPrivilegeIdGenerator")
public class EnterpriseSecurityPrivilegeIdGenerator implements SeedInitializer {
    private static final String BASE_PATH = "/uam/generateId/enterpriseSecurityPrivilege";

    @Autowired
    private EnterpriseSecurityPrivilegeDao privilegeDao;

    private SequenceGenerator sequenceGenerator;

    @Autowired
    private ZookeeperServer zookeeperServer;

    @PostConstruct
    public void init() {
        CuratorFramework client = zookeeperServer.getClient();

        try {
            sequenceGenerator = new SequenceGenerator(client, this, BASE_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getSeed(String subPath) {
        return privilegeDao.getMaxId();
    }

    public long getNextId() {
        return sequenceGenerator.getSequence("maxId");
    }

    public void delete() {
        sequenceGenerator.delete("maxId");
    }
}
