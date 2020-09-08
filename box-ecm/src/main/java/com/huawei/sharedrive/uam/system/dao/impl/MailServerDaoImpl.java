package com.huawei.sharedrive.uam.system.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.system.dao.MailServerDao;
import com.huawei.sharedrive.uam.system.domain.MailServer;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.box.domain.Limit;
import pw.cdmi.box.domain.Order;
import pw.cdmi.core.utils.EDToolsEnhance;

@Service
@SuppressWarnings(
{"unchecked", "deprecation"})
public class MailServerDaoImpl extends AbstractDAOImpl implements MailServerDao
{
    
    @Override
    public MailServer get(Long id)
    {
        MailServer mailServer = (MailServer) sqlMapClientTemplate.queryForObject("MailServer.get", id);
        if (mailServer != null && StringUtils.isNotBlank(mailServer.getAuthPassword()))
        {
            mailServer.setAuthPassword(EDToolsEnhance.decode(mailServer.getAuthPassword(),
                mailServer.getAuthPasswordEncodeKey()));
            mailServer.setAuthPasswordEncodeKey(null);
        }
        return mailServer;
    }
    
    @Override
    public MailServer getByAppId(String appId)
    {
        MailServer mailServer = (MailServer) sqlMapClientTemplate.queryForObject("MailServer.getByAppId",
            appId);
        if (mailServer != null && StringUtils.isNotBlank(mailServer.getAuthPassword()))
        {
            mailServer.setAuthPassword(EDToolsEnhance.decode(mailServer.getAuthPassword(),
                mailServer.getAuthPasswordEncodeKey()));
            mailServer.setAuthPasswordEncodeKey(null);
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
        for(MailServer mailServer:serverLists)
        {
            if (mailServer.isEnableAuth() && StringUtils.isNotBlank(mailServer.getAuthPassword()))
            {
                mailServer.setAuthPassword(EDToolsEnhance.decode(mailServer.getAuthPassword(),
                    mailServer.getAuthPasswordEncodeKey()));
                mailServer.setAuthPasswordEncodeKey(null);
            }
        }
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
    public MailServer getDefaultMailServer()
    {
        MailServer mailServer = (MailServer) sqlMapClientTemplate.queryForObject("MailServer.getDefaultMailServer");
        if (mailServer != null && StringUtils.isNotBlank(mailServer.getAuthPassword()))
        {
            mailServer.setAuthPassword(EDToolsEnhance.decode(mailServer.getAuthPassword(),
                mailServer.getAuthPasswordEncodeKey()));
            mailServer.setAuthPasswordEncodeKey(null);
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

	@Override
	public MailServer getByAccountId(long accountId) {
		// TODO Auto-generated method stub

        MailServer mailServer = (MailServer) sqlMapClientTemplate.queryForObject("MailServer.getByAccountId",accountId);
        if (mailServer != null && StringUtils.isNotBlank(mailServer.getAuthPassword()))
        {
            mailServer.setAuthPassword(EDToolsEnhance.decode(mailServer.getAuthPassword(),
                mailServer.getAuthPasswordEncodeKey()));
            mailServer.setAuthPasswordEncodeKey(null);
        }
        return mailServer;
	}
    
}
