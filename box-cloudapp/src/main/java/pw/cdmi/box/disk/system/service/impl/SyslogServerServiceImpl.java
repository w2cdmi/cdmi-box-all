package pw.cdmi.box.disk.system.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pw.cdmi.box.disk.authapp.service.AuthAppService;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.event.domain.Event;
import pw.cdmi.box.disk.event.domain.EventType;
import pw.cdmi.box.disk.system.dao.SystemConfigDAO;
import pw.cdmi.box.disk.system.service.SyslogServerService;
import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.syslog.Syslog;
import pw.cdmi.common.log.syslog.SyslogFactory;
import pw.cdmi.common.log.syslog.SyslogProtocolType;
import pw.cdmi.core.log.LoggerUtil;

@Component("syslogServerService")
public class SyslogServerServiceImpl implements SyslogServerService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogServerServiceImpl.class);
    
    private final Lock reentrantLock = new ReentrantLock(true);
    
    private static Syslog syslog;
    
    private static final String SYSLOG_LINE = "-";
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;
    
    @Autowired
    private AuthAppService authAppService;
    
    @Value("${syslog.split}")
    private String syslogSplit;
    
    @PostConstruct
    public void init()
    {
        SysLogServer server = getSyslogServer();
        initSyslogger(server);
    }
    
    @Override
    public void configChanged(String key, Object value)
    {
        if (key.equals(SysLogServer.class.getSimpleName()))
        {
            LoggerUtil.regiestThreadLocalLog();
            LOGGER.info("Change Syslog Config By Cluseter Notify. "
                + ReflectionToStringBuilder.toString(value));
            initSyslogger((SysLogServer) value);
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
        if (null != syslog && !syslog.isShutdown())
        {
            return syslog;
        }
        
        return null;
    }
    
    @Override
    public SysLogServer getSyslogServer()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(authAppService.getCurrentAppId(),
            null,
            SysLogServer.SYSLOG_CONFIG_PREFIX);
        return SysLogServer.buildSysLogServer(itemList);
    }
    
    private String buildSyslog(Event event)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(UUID.randomUUID().toString().replaceAll(SYSLOG_LINE, "")).append(syslogSplit);
        builder.append(event.getCreatedBy()).append(syslogSplit);
        builder.append(event.getType().toString()).append(syslogSplit);
        builder.append(event.getCreatedAt()).append(syslogSplit);
        builder.append(event.getDeviceType()).append(syslogSplit);
        builder.append(event.getDeviceSn()).append(syslogSplit);
        builder.append(event.getDeviceAgent()).append(syslogSplit);
        builder.append(event.getDeviceAddress());
        
        INode node = event.getSource();
        if (node != null)
        {
            builder.append(syslogSplit);
            builder.append(node.getId()).append(syslogSplit);
            builder.append(node.getParentId()).append(syslogSplit);
            builder.append(node.getOwnedBy()).append(syslogSplit);
            builder.append(node.getName()).append(syslogSplit);
            builder.append(node.getSize()).append(syslogSplit);
            builder.append(node.getCreatedAt()).append(syslogSplit);
            builder.append(node.getModifiedAt()).append(syslogSplit);
            builder.append(node.getVersion()).append(syslogSplit);
            builder.append(node.getType());
        }
        
        node = event.getDest();
        if (node != null)
        {
            builder.append(syslogSplit);
            builder.append(node.getId()).append(syslogSplit);
            builder.append(node.getParentId()).append(syslogSplit);
            builder.append(node.getOwnedBy()).append(syslogSplit);
            builder.append(node.getName()).append(syslogSplit);
            builder.append(node.getSize()).append(syslogSplit);
            builder.append(node.getCreatedAt()).append(syslogSplit);
            builder.append(node.getModifiedAt()).append(syslogSplit);
            builder.append(node.getVersion()).append(syslogSplit);
            builder.append(node.getType());
        }
        
        return builder.toString();
    }
    
    private void initSyslogger(SysLogServer server)
    {
        if (null == server || StringUtils.isBlank(server.getServer()) || server.getPort() <= 0)
        {
            LOGGER.warn("Syslog Server Info Is Not Exists.");
            syslog = null;
            return;
        }
        
        SyslogProtocolType protocolType = SyslogProtocolType.TCP;
        if (SysLogServer.PROTOCOL_TYPE_UDP == server.getProtocolType())
        {
            protocolType = SyslogProtocolType.UDP;
        }
        reentrantLock.lock();
        try
        {
            if (null != syslog)
            {
                syslog.shutdown();
                syslog = null;
            }
            syslog = SyslogFactory.getSyslog(protocolType,
                server.getServer(),
                server.getPort(),
                server.getCharset(),
                syslogSplit,
                server.isSendLocalTimestamp(),
                server.isSendLocalName());
            LOGGER.info("Syslog Init Success.");
        }
        catch (Exception e)
        {
            LOGGER.warn("Syslog Server Init Error.",e);
        }
        finally
        {
            try
            {
                reentrantLock.unlock();
            }
            catch (Exception e)
            {
                LOGGER.warn("Unlock Failed.", e);
            }
        }
    }
}
