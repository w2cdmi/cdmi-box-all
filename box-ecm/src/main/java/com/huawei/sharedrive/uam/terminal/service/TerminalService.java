package com.huawei.sharedrive.uam.terminal.service;

import java.util.Date;
import java.util.List;

import pw.cdmi.common.domain.Terminal;

public interface TerminalService
{
    /**
     * 
     * @param terminal
     */
    void create(Terminal terminal);
    
    /**
     * @param appId
     * @param deviceType
     * @param beginDate
     * @param endDate
     * @return
     */
    long getAgentCountByLoginAt(String appId, int deviceType, Date beginDate, Date endDate);
    
    List<Terminal> getBySnUser(Terminal terminal);
    
    List<Terminal> getByUser(long cloudUserId, long offset, int limit);
    
    int getByUserCount(long cloudUserId);
    
    List<Terminal> getWebTerminal(Terminal terminal);
    
    void updateLastAccessAt(Terminal terminal);
    
    void updateToken(long cloudUserId, String oldToken, String newToken);
    
    /**
     * 
     * @param terminal
     */
    void updateWebTerminal(Terminal terminal);
    
    void updateTerminal(Terminal terminal);
    
    void updateNoWebTerminalStatus(Terminal terminal);
    
    Terminal getByUserLastLogin(long cloudUserId);
    
}
