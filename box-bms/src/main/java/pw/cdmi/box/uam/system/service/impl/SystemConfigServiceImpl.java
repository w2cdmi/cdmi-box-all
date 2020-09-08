package pw.cdmi.box.uam.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.system.service.SystemConfigService;
import pw.cdmi.common.domain.LinkRule;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.domain.SystemVersions;

@Service("systemConfigService")
public class SystemConfigServiceImpl implements SystemConfigService
{
    
    @Autowired
    private SystemConfigDAO systemConfigDao;
    
    @Override
    public SystemConfig getSystemConfig(String key, String appId)
    {
        return systemConfigDao.get(key);
    }
    
    @Override
    public List<SystemConfig> listSystemConfig(String key, String appId)
    {
        List<SystemConfig> list = new ArrayList<SystemConfig>(16);
        
        if (null == key)
        {
            SystemConfig maxVersion = systemConfigDao.get(SystemVersions.KEY_SYSTEM_MAX_VERSIONS);
            SystemConfig linkRule = systemConfigDao.get(LinkRule.KEY_LINK_ACCESSKEY_RULE);
            list.add(maxVersion);
            list.add(linkRule);
        }
        else
        {
            list.add(systemConfigDao.get(key));
        }
        return list;
    }
    
}
