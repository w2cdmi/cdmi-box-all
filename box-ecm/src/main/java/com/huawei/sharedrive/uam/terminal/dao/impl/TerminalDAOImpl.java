package com.huawei.sharedrive.uam.terminal.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.core.dao.util.HashTool;
import com.huawei.sharedrive.uam.terminal.dao.TerminalDAO;

import pw.cdmi.box.dao.impl.AbstractDAOImpl;
import pw.cdmi.common.domain.Terminal;

@Service
@SuppressWarnings({"unchecked", "deprecation"})
public class TerminalDAOImpl extends AbstractDAOImpl implements TerminalDAO
{
    private static final int TABLE_COUNT = 100;
    
    public static int getTableSuffix(long cloudUserId)
    {
        int table = (int) (HashTool.apply(String.valueOf(cloudUserId)) % TABLE_COUNT);
        return table;
    }
    
    @Override
    public void create(Terminal terminal)
    {
        terminal.setTableSuffix(getTableSuffix(terminal.getCloudUserId()));
        sqlMapClientTemplate.insert("Terminal.insert", terminal);
    }
    
    @Override
    public long getAgentCountByLoginAt(String appId, int deviceType, Date beginDate, Date endDate)
    {
        long total = 0;
        Map<String, Object> map = new HashMap<String, Object>(5);
        if (null != appId)
        {
            map.put("appId", appId);
        }
        
        if (null != beginDate)
        {
            map.put("beginDate", beginDate);
        }
        if (null != endDate)
        {
            map.put("endDate", endDate);
        }
        map.put("deviceType", deviceType);
        Long query;
        for (int i = 0; i < TABLE_COUNT; i++)
        {
            map.put("tableSuffix", i);
            query = (Long) sqlMapClientTemplate.queryForObject("Terminal.getAgentCountByLoginAt", map);
            total = total + query;
        }
        
        return total;
    }
    
    @Override
    public List<Terminal> getBySnUser(Terminal terminal)
    {
        terminal.setTableSuffix(getTableSuffix(terminal.getCloudUserId()));
        List<Terminal> selTerminal = sqlMapClientTemplate.queryForList("Terminal.getClientTerminalBySnUser",
            terminal);
        return selTerminal;
    }
    
    @Override
    public List<Terminal> getByUser(long cloudUserId, long offset, int limit)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("offset", offset);
        map.put("limit", limit);
        map.put("cloudUserId", cloudUserId);
        map.put("tableSuffix", getTableSuffix(cloudUserId));
        List<Terminal> list = sqlMapClientTemplate.queryForList("Terminal.getByUser", map);
        return (list);
    }
    
    @Override
    public int getByUserCount(long cloudUserId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("cloudUserId", cloudUserId);
        map.put("tableSuffix", getTableSuffix(cloudUserId));
        return (Integer) sqlMapClientTemplate.queryForObject("Terminal.getByUserCount", map);
    }
    
    @Override
    public long getMaxTerminalId()
    {
        long maxTerminalId = 1L;
        long selMaxTerminalId;
        String tableSuffix;
        Object maxTerminalIdObject;
        for (int i = 0; i < TABLE_COUNT; i++)
        {
            tableSuffix = String.valueOf(i);
            maxTerminalIdObject = sqlMapClientTemplate.queryForObject("Terminal.getMaxTerminalId",
                tableSuffix);
            selMaxTerminalId = maxTerminalIdObject == null ? 0L : (long) maxTerminalIdObject;
            if (maxTerminalId < selMaxTerminalId)
            {
                maxTerminalId = selMaxTerminalId;
            }
        }
        return maxTerminalId;
    }
    
    @Override
    public List<Terminal> getWebTerminal(Terminal terminal)
    {
        terminal.setTableSuffix(getTableSuffix(terminal.getCloudUserId()));
        List<Terminal> list = sqlMapClientTemplate.queryForList("Terminal.getWebTerminal", terminal);
        return (list);
    }
    
    @Override
    public void updateLastAccessAt(Terminal terminal)
    {
        terminal.setTableSuffix(getTableSuffix(terminal.getCloudUserId()));
        sqlMapClientTemplate.update("Terminal.updateLastAccessAt", terminal);
    }
    
    @Override
    public void updateNoWebStatus(Terminal terminal)
    {
        terminal.setTableSuffix(getTableSuffix(terminal.getCloudUserId()));
        sqlMapClientTemplate.update("Terminal.updateNoWebStatus", terminal);
    }

    @Override
    public void updateTerminal(Terminal terminal)
    {
        terminal.setTableSuffix(getTableSuffix(terminal.getCloudUserId()));
        if(terminal.getStatus() == Terminal.STATUS_IGNORE)
        {
            terminal.setToken(null);
        }
        sqlMapClientTemplate.update("Terminal.updateNoWebTerminal", terminal);
    }

    @Override
    public void updateToken(long cloudUserId, String oldToken, String newToken)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        int tableSuffix = getTableSuffix(cloudUserId);
        map.put("tableSuffix", tableSuffix);
        map.put("oldToken", oldToken);
        map.put("newToken", newToken);
        map.put("cloudUserId", cloudUserId);
        sqlMapClientTemplate.update("Terminal.updateToken", map);
        
    }

    @Override
    public void updateWebTerminal(Terminal terminal)
    {
        terminal.setTableSuffix(getTableSuffix(terminal.getCloudUserId()));
        sqlMapClientTemplate.update("Terminal.updateWebTerminal", terminal);
    }

    
    @Override
    public Terminal getByUserLastLogin(long cloudUserId)
    {
        Map<String, Object> map = new HashMap<String, Object>(2);
        map.put("cloudUserId", cloudUserId);
        map.put("tableSuffix", getTableSuffix(cloudUserId));
        return (Terminal) sqlMapClientTemplate.queryForObject("Terminal.getByUserLastLogin", map);
    }
    
}
