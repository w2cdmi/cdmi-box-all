package pw.cdmi.box.uam.system.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.box.uam.system.dao.MailServerDao;
import pw.cdmi.box.uam.system.domain.MailServer;
import pw.cdmi.box.uam.system.domain.MailServerCryptUtil;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class MailServerDaoImpl extends AbstractDAOImpl implements MailServerDao
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MailServerDaoImpl.class);
    
    @Override
    public MailServer get(Long id)
    {
        MailServer mailServer = (MailServer) sqlMapClientTemplate.queryForObject("MailServer.get", id);
        if (mailServer != null && mailServer.getAuthPassword() != null)
        {
            mailServer = MailServerCryptUtil.decode(mailServer);
        }
        return mailServer;
    }
    
    @Override
    public MailServer getByAppId(String appId)
    {
        MailServer mailServer = (MailServer) sqlMapClientTemplate.queryForObject("MailServer.getByAppId",
            appId);
        if (mailServer != null && mailServer.getAuthPassword() != null)
        {
            mailServer = MailServerCryptUtil.decode(mailServer);
        }
        return mailServer;
    }
    
    @Override
    public List<MailServer> getFilterd(MailServer filter, Order order, Limit limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("filter", filter);
        map.put("order", order);
        map.put("limit", limit);
        List<MailServer> serverLists = sqlMapClientTemplate.queryForList("MailServer.getFilterd", map);
        return serverLists;
    }
    
    @Override
    public int getFilterdCount(MailServer filter)
    {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("filter", filter);
        return (Integer) sqlMapClientTemplate.queryForObject("MailServer.getFilterdCount", map);
    }
    
    @Override
    public void delete(Long id)
    {
        sqlMapClientTemplate.delete("MailServer.delete", id);
    }
    
    @Override
    public void create(MailServer mailServer)
    {
        if (StringUtils.isNotBlank(mailServer.getAuthPassword()))
        {
            mailServer = MailServerCryptUtil.encode(mailServer);
            LOGGER.info("change crypt in bms.MailServer");
        }
        sqlMapClientTemplate.insert("MailServer.insert", mailServer);
    }
    
    @Override
    public void updateMailServer(MailServer mailServer)
    {
        if (mailServer != null && StringUtils.isNotBlank(mailServer.getAuthPassword()))
        {
            mailServer = MailServerCryptUtil.encode(mailServer);
            LOGGER.info("change crypt in bms.MailServer");
        }
        sqlMapClientTemplate.update("MailServer.updateMailServer", mailServer);
    }
    
    @Override
    public MailServer getDefaultMailServer()
    {
        MailServer mailServer = (MailServer) sqlMapClientTemplate.queryForObject("MailServer.getDefaultMailServer");
        if (mailServer != null && mailServer.getAuthPassword() != null)
        {
            mailServer = MailServerCryptUtil.decode(mailServer);
        }
        return mailServer;
    }
    
    @Override
    public long getNextAvailableId()
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("param", "mailServerId");
        sqlMapClientTemplate.queryForObject("getNextId", map);
        long id = (Long) map.get("returnid");
        return id;
    }
    
}
