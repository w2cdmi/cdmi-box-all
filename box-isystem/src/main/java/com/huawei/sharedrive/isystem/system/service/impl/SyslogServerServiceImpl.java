/**
 * 
 */
package com.huawei.sharedrive.isystem.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.huawei.sharedrive.isystem.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.isystem.system.service.SyslogServerService;

import pw.cdmi.common.config.service.ConfigListener;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.LogConstants;
import pw.cdmi.common.log.LogLanguage;
import pw.cdmi.common.log.syslog.Syslog;
import pw.cdmi.common.log.syslog.SyslogFactory;
import pw.cdmi.common.log.syslog.SyslogProtocolType;

/**
 * @author d00199602
 * 
 */
@Component
public class SyslogServerServiceImpl implements SyslogServerService, ConfigListener
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogServerServiceImpl.class);
    
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);
    
    private SysLogServer localCache;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private ConfigManager configManager;
    
    private Syslog syslogger;
    
    @Value("${syslog.split}")
    private String syslogSplit;
    
    @Override
    public SysLogServer getSyslogServer()
    {
        if (localCache == null)
        {
            List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null, SysLogServer.SYSLOG_CONFIG_PREFIX);
            localCache = SysLogServer.buildSysLogServer(itemList);
            if (localCache != null)
            {
                configManager.setConfig(SysLogServer.class.getSimpleName(), localCache);
            }
        }
        return localCache;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.huawei.sharedrive.system.general.service.MailServerService#saveMailServer(com
     * .huawei.sharedrive.system.general.domain.MailServer)
     */
    @Override
    public void saveSysLogServer(SysLogServer syslogServer)
    {
        saveSysLogServerInNewTrans(syslogServer);
        configManager.setConfig(SysLogServer.class.getSimpleName(), syslogServer);
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSysLogServerInNewTrans(SysLogServer syslogServer)
    {
        List<SystemConfig> itemList = syslogServer.toConfigItem();
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.get(systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
        localCache = syslogServer;
    }
    @Override
    public Syslog getLogger()
    {
        if (null == syslogger)
        {
            initSyslogger();
        }
        
        if (null != syslogger && !syslogger.isShutdown())
        {
            return syslogger;
        }
        
        return null;
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(SysLogServer.class.getSimpleName()))
        {
            LOGGER.info("Change Syslog Config By Cluseter Notify.");
            localCache = (SysLogServer) value;
            try
            {
                LOCK.writeLock().lock();
                this.syslogger = null;
            }
            finally
            {
                try
                {
                    LOCK.writeLock().unlock();
                }
                catch (Exception e)
                {
                    LOGGER.warn("Unlock Failed.", e);
                }
            }
            initSyslogger();
        }
    }
    
    /**
     * 初始化logger
     */
    private void initSyslogger()
    {
        SysLogServer server = getSyslogServer();
        if (null == server || StringUtils.isBlank(server.getServer()) || server.getPort() <= 0)
        {
            LOGGER.warn("Syslog Server Info Is Not Exists.");
            this.syslogger = null;
            return;
        }
        
        SyslogProtocolType protocolType = SyslogProtocolType.TCP;
        if (SysLogServer.PROTOCOL_TYPE_UDP == server.getProtocolType())
        {
            protocolType = SyslogProtocolType.UDP;
        }
        
        try
        {
            LOCK.writeLock().lock();
            if (null != this.syslogger)
            {
                this.syslogger.shutdown();
                this.syslogger = null;
            }
            this.syslogger = SyslogFactory.getSyslog(protocolType,
                server.getServer(),
                server.getPort(),
                server.getCharset(),
                syslogSplit,
                server.isSendLocalTimestamp(),
                server.isSendLocalName());
        }
        catch (Exception e)
        {
            LOGGER.warn("Syslog Server Config Error", e);
        }
        finally
        {
            try
            {
                LOCK.writeLock().unlock();
            }
            catch (Exception e)
            {
                LOGGER.warn("Unlock Failed.", e);
            }
        }
    }
    
    public String getSyslogSplit()
    {
        return syslogSplit;
    }
    
    @Override
    public boolean setUserLanguage(LogLanguage log)
    {
        setUserLanguageInNewTrans(log);
        configManager.setConfig(LogConstants.ZK_LOGLANGUAGE_KEY, log);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setUserLanguageInNewTrans(LogLanguage log)
    {
        List<SystemConfig> list = log.toConfigItem(log.getLanguage(), log.getConfig());
        for (SystemConfig systemConfig : list)
        {
            if (systemConfigDAO.get(systemConfig.getId()) == null)
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
    public LogLanguage getLogLanguage()
    {
        List<SystemConfig> list = new ArrayList<SystemConfig>(2);
        list.add(systemConfigDAO.get(LogLanguage.USER_LOG_CONFIG_LANGUAGE));
        list.add(systemConfigDAO.get(LogLanguage.USER_LOG_ISCONFIG));
        LogLanguage logLanguage = LogLanguage.buildLogLanguage(list);
        return logLanguage;
    }
}
