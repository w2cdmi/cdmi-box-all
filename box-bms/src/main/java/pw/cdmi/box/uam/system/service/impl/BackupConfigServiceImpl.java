package pw.cdmi.box.uam.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.system.service.BackupConfigService;
import pw.cdmi.common.domain.SystemConfig;

@Component
public class BackupConfigServiceImpl implements BackupConfigService
{
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.uam.system.service.AppBasicConfigService#getAppBasicConfig
     * (java.lang.String)
     */
    @Override
    public List<SystemConfig> getAllConfig(String appId)
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(appId, null, BACKUP_RULE_CONFIG_PREFIX);
        return itemList;
    }
    
    @Override
    public SystemConfig getConfigById(String appId, String id)
    {
        SystemConfig config = systemConfigDAO.getByPriKey(appId, id);
        return config;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.uam.system.service.AppBasicConfigService#saveAppBasicConfig
     * (com.huawei.sharedrive.common.domain.AppBasicConfig)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveConfigList(List<SystemConfig> itemList)
    {
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.getByPriKey(systemConfig.getAppId(), systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
    }
    
    @Override
    public void saveConfig(SystemConfig systemConfig)
    {
        if (systemConfigDAO.getByPriKey(systemConfig.getAppId(), systemConfig.getId()) == null)
        {
            systemConfigDAO.create(systemConfig);
        }
        else
        {
            systemConfigDAO.update(systemConfig);
        }
    }
    
}