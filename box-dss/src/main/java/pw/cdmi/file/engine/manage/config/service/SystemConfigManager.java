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
package pw.cdmi.file.engine.manage.config.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.core.exception.InnerException;
import pw.cdmi.file.engine.core.config.SystemConfig;
import pw.cdmi.file.engine.core.config.SystemConfigContainer;
import pw.cdmi.file.engine.manage.config.SystemConfigLoader;

/**
 * 
 * @author s90006125
 * 
 */
@Service("systemConfigManager")
public class SystemConfigManager implements ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigManager.class);
    
    private static final String CONFIG_ZOOKEEPER_KEY_SYSTEM_CONFIG = "config.zookeeper.key.system.config";
    
    public static final String MIRROR_GLOBAL_ENABLE = "mirror.global.enable";
    
    public static final String MIRROR_GLOBAL_ENABLE_TIMER = "mirror.global.enable.timer";
    // 全局任务状态，对外提供暂停PAUSE 和正常
    public static final String MIRROR_GLOBAL_TASK_STATE = "mirror.global.task.state";
    
    // 异地复制总开关打开
    public static final String MIRROR_GLOBAL_ENABLE_OPEN = "0";
    
    // 异地复制总开关关闭
    public static final String MIRROR_GLOBAL_ENABLE_CLOSE = "1";
    
    // 异地复制暂停
    public static final String MIRROR_GLOBAL_TASK_STATE_PAUSE = "4";
    
    // 异地复制未被暂停
    public static final String MIRROR_GLOBAL_TASK_STATE_RUN = "0";
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    @Autowired
    private ConfigManager configManager;
    
    @Resource
    private SystemConfigLoader systemConfigLoader;
    
    public void changeSystemConfig(SystemConfig config)
    {
        systemConfigService.changeSystemConfig(config);
        notifyCluster();
    }
    
    public void changeSystemConfigs(SystemConfig... configs)
    {
        systemConfigService.changeSystemConfigs(configs);
        notifyCluster();
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (!SystemConfigManager.CONFIG_ZOOKEEPER_KEY_SYSTEM_CONFIG.equals(key))
        {
            return;
        }
        LOGGER.info("Refresh SystemConfig By Cluster Notify.");
        try
        {
            Thread.sleep(1000L);
        }
        catch (InterruptedException e)
        {
            LOGGER.warn("Wait Failed.", e);
        }
        systemConfigLoader.load();
    }
    
    private void notifyCluster()
    {
        try
        {
            configManager.setConfig(CONFIG_ZOOKEEPER_KEY_SYSTEM_CONFIG, "");
            systemConfigLoader.load();
        }
        catch (Exception e)
        {
            LOGGER.warn("Notify to Cluster Failed.", e);
            throw new InnerException(e);
        }
    }
    
    public void changeMirrorConfig(String key, String value)
    {
        LOGGER.info("mirror configChanged:" + key + ":" + value);
        if (MIRROR_GLOBAL_ENABLE.equalsIgnoreCase(key))
        {
            SystemConfig sysconfig = new SystemConfig(MIRROR_GLOBAL_ENABLE, value, false, true);
            changeSystemConfig(sysconfig);
            
        }
        else if (MIRROR_GLOBAL_ENABLE_TIMER.equalsIgnoreCase(key))
        {
            SystemConfig sysconfig = new SystemConfig(MIRROR_GLOBAL_ENABLE_TIMER, value, false, true);
            changeSystemConfig(sysconfig);
        }
        else if (MIRROR_GLOBAL_TASK_STATE.equalsIgnoreCase(key))
        {
            SystemConfig sysconfig = new SystemConfig(MIRROR_GLOBAL_TASK_STATE, value, false, true);
            changeSystemConfig(sysconfig);
        }
    }
    
    /**
     * 根据异地复制总开关判断是否允许继续执行异地复制任务 返回true 表示 开关打开 允许执行
     * 
     * @return
     */
    public boolean isAllowCopyTaskRun()
    {
        boolean flag = false;
        String nowEnable = SystemConfigContainer.getConfig(MIRROR_GLOBAL_ENABLE).getValue();
        
        if (null == nowEnable || StringUtils.isBlank(nowEnable))
        {
            LOGGER.info("cannot get mirror.global.enable config ");
        }
        
        if (MIRROR_GLOBAL_ENABLE_OPEN.equalsIgnoreCase(nowEnable))
        {
            flag = true;
        }
        return flag;
    }
    
    /**
     * 根据异地复制定时配置判断当前是否允许继续执行异地复制任务 返回true 表示 开关打开 允许执行
     * 
     * @return
     */
    public boolean isAllowCopyTaskRunByTime()
    {
        boolean flag = false;
        String nowEnable = SystemConfigContainer.getConfig(MIRROR_GLOBAL_ENABLE_TIMER).getValue();
        
        if (null == nowEnable || StringUtils.isBlank(nowEnable))
        {
            LOGGER.info("cannot get mirror.global.enable config ");
        }
        
        if (MIRROR_GLOBAL_ENABLE_OPEN.equalsIgnoreCase(nowEnable))
        {
            flag = true;
        }
        return flag;
    }
    
    /**
     * 异地复制暂停开关控制 返回 true 表示暂停开关开启，不允许继续执行
     * 
     * @return
     */
    public boolean isAllowCopyTaskPause()
    {
        boolean flag = false;
        String pauseEnable = SystemConfigContainer.getConfig(MIRROR_GLOBAL_TASK_STATE).getValue();
        
        if (null == pauseEnable || StringUtils.isBlank(pauseEnable))
        {
            LOGGER.info("cannot get mirror.global.task.state ");
        }
        
        if (MIRROR_GLOBAL_TASK_STATE_PAUSE.equalsIgnoreCase(pauseEnable))
        {
            flag = true;
        }
        return flag;
    }
    
}
