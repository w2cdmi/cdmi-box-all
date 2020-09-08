package com.huawei.sharedrive.uam.system.service.impl;

import com.huawei.sharedrive.uam.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.uam.system.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.cdmi.common.domain.LinkRule;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.domain.SystemVersions;

import java.util.ArrayList;
import java.util.List;

@Service("systemConfigService")
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigDAO systemConfigDao;

    @Override
    public SystemConfig getSystemConfig(String key, String appId) {
        return systemConfigDao.getByPriKey(appId,key);
    }

    @Override
    public List<SystemConfig> listSystemConfig(String key, String appId) {
        List<SystemConfig> list = new ArrayList<SystemConfig>(10);
        if (key == null) {
            SystemConfig maxVersion = systemConfigDao.get(SystemVersions.KEY_SYSTEM_MAX_VERSIONS);
            SystemConfig linkRule = systemConfigDao.get(LinkRule.KEY_LINK_ACCESSKEY_RULE);
            list.add(maxVersion);
            list.add(linkRule);
        } else {
            list.add(systemConfigDao.get(key));
        }
        return list;
    }

    @Override
    public void saveSystemConfig(SystemConfig config) {
        SystemConfig dbOne = systemConfigDao.getByPriKey(config.getAppId(), config.getId());
        if(dbOne == null) {
            systemConfigDao.create(config);
        } else {
            systemConfigDao.update(config);
        }
    }
}
