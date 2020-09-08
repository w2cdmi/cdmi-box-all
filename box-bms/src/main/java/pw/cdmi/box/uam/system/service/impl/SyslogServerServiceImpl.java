package pw.cdmi.box.uam.system.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pw.cdmi.box.uam.event.domain.Event;
import pw.cdmi.box.uam.event.domain.EventType;
import pw.cdmi.box.uam.system.dao.SystemConfigDAO;
import pw.cdmi.box.uam.system.service.SyslogServerService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.common.config.service.ConfigManager;
import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.syslog.Syslog;
import pw.cdmi.common.log.syslog.SyslogFactory;
import pw.cdmi.common.log.syslog.SyslogProtocolType;

public class SyslogServerServiceImpl implements SyslogServerService
{
    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock(true);
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogServerServiceImpl.class);
    
    private static final String STRING_NULL = "null";
    
    @Autowired
    private ConfigManager configManager;
    
    private SysLogServer localCache;
    
    private Syslog syslogger;
    
    @Value("${syslog.split}")
    private String syslogSplit;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
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
    
    @Override
    public void consumeEvent(Event event)
    {
        Syslog syslogger = getLogger();
        if (null != syslogger)
        {
            String log = buildSyslog(event);
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Send Log [ " + log + " ] To : "
                    + ReflectionToStringBuilder.toString(syslogger.getSyslogWriter()));
            }
            syslogger.info(log);
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Send Log End.");
            }
        }
        else
        {
            LOGGER.warn("Syslog Is Not Init.");
        }
        
    }
    
    @Override
    public EventType[] getInterestedEvent()
    {
        return EventType.values();
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
    public SysLogServer getSyslogServer()
    {
        if (localCache == null)
        {
            List<SystemConfig> itemList = systemConfigDAO.getByPrefix(Constants.UAM_DEFAULT_APP_ID,
                null,
                SysLogServer.SYSLOG_CONFIG_PREFIX);
            localCache = SysLogServer.buildSysLogServer(itemList);
            if (localCache != null)
            {
                configManager.setConfig(SysLogServer.class.getSimpleName(), localCache);
            }
        }
        return localCache;
    }
    
    @Override
    public String getSyslogSplit()
    {
        return syslogSplit;
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
        saveSysLogInTransaction(syslogServer);
        localCache = syslogServer;
        configManager.setConfig(SysLogServer.class.getSimpleName(), syslogServer);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSysLogInTransaction(SysLogServer syslogServer)
    {
        syslogServer.setAppId(Constants.UAM_DEFAULT_APP_ID);
        List<SystemConfig> itemList = syslogServer.toConfigItem();
        for (SystemConfig systemConfig : itemList)
        {
            if (systemConfigDAO.getByPriKey(Constants.UAM_DEFAULT_APP_ID, systemConfig.getId()) == null)
            {
                systemConfigDAO.create(systemConfig);
            }
            else
            {
                systemConfigDAO.update(systemConfig);
            }
        }
    }
    
    private String buildSyslog(Event event)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(UUID.randomUUID().toString().replaceAll("-", "")).append(syslogSplit);
        builder.append(event.getLoginName()).append(syslogSplit);
        builder.append(event.getType().toString()).append(syslogSplit);
        builder.append(event.getCreatedAt()).append(syslogSplit);
        builder.append(event.getDeviceType()).append(syslogSplit);
        builder.append(event.getDeviceSN()).append(syslogSplit);
        builder.append(event.getDeviceAgent()).append(syslogSplit);
        builder.append(event.getDeviceAddress());
        for (int i = 0; i < 9; i++)
        {
            builder.append(syslogSplit).append(STRING_NULL);
        }
        return builder.toString();
    }
    
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
}
