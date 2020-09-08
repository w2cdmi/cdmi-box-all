package com.huawei.sharedrive.uam.terminal.dao;

import java.util.Date;
import java.util.List;

import pw.cdmi.common.domain.Terminal;

public interface TerminalDAO
{
    void create(Terminal terminal);
    
    long getAgentCountByLoginAt(String appId, int deviceType, Date beginDate, Date endDate);
    
    List<Terminal> getBySnUser(Terminal terminal);
    
    List<Terminal> getByUser(long cloudUserId, long offset, int limit);
    
    int getByUserCount(long cloudUserId);
    
    long getMaxTerminalId();
    
    List<Terminal> getWebTerminal(Terminal terminal);
    
    void updateLastAccessAt(Terminal terminal);
    
    void updateNoWebStatus(Terminal terminal);
    
    void updateTerminal(Terminal terminal);
    
    void updateToken(long cloudUserId, String oldToken, String newToken);
    
    void updateWebTerminal(Terminal terminal);
    
    Terminal getByUserLastLogin(long cloudUserId);
}