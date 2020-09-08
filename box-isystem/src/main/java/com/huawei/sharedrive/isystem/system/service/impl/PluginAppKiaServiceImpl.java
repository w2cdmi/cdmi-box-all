package com.huawei.sharedrive.isystem.system.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.isystem.system.service.PluginAppKiaService;

import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.SystemConfig;

@Service("pluginAppKiaService")
public class PluginAppKiaServiceImpl implements PluginAppKiaService
{
    private static final String KIA_CONFIG_ENABLE_KEY = "security.scan.enable";
    
    private static final String KIA_CONFIG_VERSION_KEY = "security.scan.engine.version";
    
    private static final String SYSTEM_CONFIG_CHANGE = "system_config_change";
    
    private static final String SYSTEM_SCAN_MODE = "system.scan.mode";
    
    private static final String SCAN_TIMELY_BEGIN = "scan.timely.begin";
    
    private static final String SCAN_TIMELY_END = "scan.timely.end";
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    @Override
    public SystemConfig updateEnableKey(String value)
    {
        updateEnableKeyInNewTrans(value);
        configManager.setConfig(SYSTEM_CONFIG_CHANGE, KIA_CONFIG_ENABLE_KEY);
        return getEnbleKey();
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateEnableKeyInNewTrans(String value)
    {
        SystemConfig sysconfig = systemConfigDAO.get(KIA_CONFIG_ENABLE_KEY);
        if (null == sysconfig)
        {
            sysconfig = new SystemConfig("-1", KIA_CONFIG_ENABLE_KEY, value);
            systemConfigDAO.create(sysconfig);
        }
        else
        {
            sysconfig.setValue(value);
            systemConfigDAO.update(sysconfig);
        }
    }
    
    @Override
    public SystemConfig updateVersionKey(String value)
    {
        SystemConfig sysconfig = updateVersionKeyInNewTrans(value);
        configManager.setConfig(SYSTEM_CONFIG_CHANGE, KIA_CONFIG_VERSION_KEY);
        return sysconfig;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SystemConfig updateVersionKeyInNewTrans(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            value = "1";
        }
        SystemConfig sysconfig = systemConfigDAO.get(KIA_CONFIG_VERSION_KEY);
        if (null == sysconfig)
        {
            sysconfig = new SystemConfig("-1", KIA_CONFIG_VERSION_KEY, value);
            systemConfigDAO.create(sysconfig);
            
        }
        else
        {
            Integer temp = Integer.parseInt(sysconfig.getValue());
            temp++;
            sysconfig.setValue(temp.toString());
            systemConfigDAO.update(sysconfig);
        }
        return sysconfig;
    }
    
    @Override
    public SystemConfig getEnbleKey()
    {
        SystemConfig sysconfig = systemConfigDAO.get(KIA_CONFIG_ENABLE_KEY);
        return sysconfig;
    }
    
    @Override
    public SystemConfig getVersionKey()
    {
        SystemConfig sysconfig = systemConfigDAO.get(KIA_CONFIG_VERSION_KEY);
        return sysconfig;
    }
    
    @Override
    public List<SystemConfig> getScanModel()
    {
        List<SystemConfig> sysConfigs = new ArrayList<SystemConfig>(3);
        SystemConfig sysconfig = systemConfigDAO.get(SYSTEM_SCAN_MODE);
        sysConfigs.add(sysconfig);
        if (null != sysconfig)
        {
            sysconfig = systemConfigDAO.get(SCAN_TIMELY_BEGIN);
            sysConfigs.add(sysconfig);
            sysconfig = systemConfigDAO.get(SCAN_TIMELY_END);
            sysConfigs.add(sysconfig);
        }
        return sysConfigs;
    }
    
    @Override
    public void updateScanMode(boolean isScan, String startTime, String endTime)
    {
        List<SystemConfig> sysConfigs = getConfigs(isScan, startTime, endTime);
        updateConfigs(sysConfigs);
    }
    
    private List<SystemConfig> getConfigs(boolean isScan, String startTime, String endTime)
    {
        List<SystemConfig> sysConfigs = new ArrayList<SystemConfig>(3);
        SystemConfig newScanModel = new SystemConfig("-1", SYSTEM_SCAN_MODE, String.valueOf(isScan));
        sysConfigs.add(newScanModel);
        if (null != startTime && null != endTime)
        {
            newScanModel = new SystemConfig("-1", SCAN_TIMELY_BEGIN, startTime);
            sysConfigs.add(newScanModel);
            newScanModel = new SystemConfig("-1", SCAN_TIMELY_END, endTime);
            sysConfigs.add(newScanModel);
        }
        return sysConfigs;
    }
    
    private void updateConfigs(List<SystemConfig> sysConfigs)
    {
        SystemConfig currentSysconfig;
        for (SystemConfig sysConfig : sysConfigs)
        {
            currentSysconfig = systemConfigDAO.get(sysConfig.getId());
            updateConfigsInNewTrans(currentSysconfig, sysConfig);
            configManager.setConfig(SYSTEM_CONFIG_CHANGE, sysConfig.getId());
        }
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateConfigsInNewTrans(SystemConfig currentSysconfig, SystemConfig sysConfig)
    {
        if(null == currentSysconfig)
        {
            systemConfigDAO.create(sysConfig);            
        }
        else
        {
            systemConfigDAO.update(sysConfig);            
        }
    }
}
