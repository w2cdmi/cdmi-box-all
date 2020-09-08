/**
 * 
 */
package com.huawei.sharedrive.app.system.service.impl;

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

import com.huawei.sharedrive.app.dataserver.exception.BusinessException;
import com.huawei.sharedrive.app.event.domain.Event;
import com.huawei.sharedrive.app.event.domain.EventType;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.system.dao.SystemConfigDAO;
import com.huawei.sharedrive.app.system.service.SyslogServerService;
import com.huawei.sharedrive.app.user.dao.UserDAOV2;
import com.huawei.sharedrive.app.user.domain.User;

import pw.cdmi.common.domain.SysLogServer;
import pw.cdmi.common.domain.SystemConfig;
import pw.cdmi.common.log.LoggerUtil;
import pw.cdmi.common.log.syslog.Syslog;
import pw.cdmi.common.log.syslog.SyslogFactory;
import pw.cdmi.common.log.syslog.SyslogProtocolType;
import pw.cdmi.core.utils.SpringContextUtil;

/**
 * @author d00199602
 * 
 */
@Component("syslogServerService")
public class SyslogServerServiceImpl implements SyslogServerService
{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SyslogServerServiceImpl.class);

    private static final String STRING_NULL = "null";
    
    private static Syslog syslog;
    
    private static final String SYSLOG_LINE = "-";
    
    private UserDAOV2 getUserDAO()
    {
        return (UserDAOV2)SpringContextUtil.getBean("userDAOV2");
    }
    
    private final Lock reentrantLock = new ReentrantLock(true);
    
    @Value("${syslog.split}")
    private String syslogSplit;
    
    @Autowired
    private SystemConfigDAO systemConfigDAO;  
    
    
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
        if (event.getType() == null || event.getType() == EventType.OTHERS)
        {
            return;
        }
        Syslog syslogger = getLogger();
        if (null != syslogger)
        {
            String log = buildSyslog(event);
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Send Log [ " + log + " ] To : "
                    + ReflectionToStringBuilder.toString(syslogger.getSyslogWriter()));
            }
            // 发送syslog
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
    
    public String getLoginName(long ownerId, long modifiedBy)
    {
        String value = getLoginName(ownerId);
        if(null == value)
        {
            return getLoginName(modifiedBy);
        }
        return value;
    }
    
    @Override
    public SysLogServer getSyslogServer()
    {
        List<SystemConfig> itemList = systemConfigDAO.getByPrefix(null, SysLogServer.SYSLOG_CONFIG_PREFIX);
        return SysLogServer.buildSysLogServer(itemList);
    }
    
    @PostConstruct
    public void init()
    {
        SysLogServer server = getSyslogServer();
        initSyslogger(server);
    }
    
    private String buildSyslog(Event event)
    {
        StringBuilder builder = new StringBuilder();
        builder.append(UUID.randomUUID().toString().replaceAll(SYSLOG_LINE, "")).append(syslogSplit);
        if(event.getUserToken() != null)
        {
            String loginName = event.getUserToken().getLoginName();
            if(StringUtils.isBlank(loginName))
            {
                loginName = getLoginName(event.getUserToken().getId());
            }
            builder.append(loginName).append(syslogSplit);
            builder.append(event.getType().toString()).append(syslogSplit);
            builder.append(event.getCreatedAt()).append(syslogSplit);
            builder.append(event.getUserToken().getDeviceType()).append(syslogSplit);
            builder.append(event.getUserToken().getDeviceSN()).append(syslogSplit);
            builder.append(event.getUserToken().getDeviceAgent()).append(syslogSplit);
            builder.append(event.getUserToken().getDeviceAddress());
        }
        else
        {
            builder.append(event.getCreatedBy()).append(syslogSplit);
            builder.append(event.getType().toString()).append(syslogSplit);
            builder.append(event.getCreatedAt()).append(syslogSplit);
            builder.append(event.getDeviceType()).append(syslogSplit);
            builder.append(event.getDeviceSN()).append(syslogSplit);
            builder.append(event.getDeviceAgent()).append(syslogSplit);
            builder.append(event.getDeviceAddress());
        }

        INode node = event.getSource();
        if (node != null)
        {
            builder.append(syslogSplit);
            builder.append(node.getId()).append(syslogSplit);
            builder.append(node.getParentId()).append(syslogSplit);
            builder.append(getLoginName(node.getOwnedBy(), node.getModifiedBy())).append(syslogSplit);
            builder.append(node.getName()).append(syslogSplit);
            builder.append(node.getSize()).append(syslogSplit);
            builder.append(node.getCreatedAt()).append(syslogSplit);
            builder.append(node.getModifiedAt()).append(syslogSplit);
            
        }
        node = event.getDest();
        if (node != null)
        {
            builder.append(getLoginName(node.getOwnedBy(), node.getModifiedBy())).append(syslogSplit);
            builder.append(node.getId());
        }
        else
        {
            builder.append(STRING_NULL).append(syslogSplit);
            builder.append(STRING_NULL);
        }
        return builder.toString();
    }
    
    /**
     * @param ownerId
     * @return
     */
    private String getLoginName(long ownerId)
    {
        User user = getUserDAO().get(ownerId);
        if(null != user)
        {
            return user.getLoginName();
        }
        return null;
    }
    
    /**
     * 初始化logger
     */
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
        catch (BusinessException e)
        {
            LOGGER.warn("Syslog Server Init Error.");
        }
        catch (Exception e)
        {
            LOGGER.warn("Syslog Server Init Error:" + e.getMessage());
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
