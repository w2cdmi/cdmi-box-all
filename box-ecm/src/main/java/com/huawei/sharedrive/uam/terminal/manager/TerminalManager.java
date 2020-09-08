package com.huawei.sharedrive.uam.terminal.manager;

import com.huawei.sharedrive.uam.openapi.domain.ListTerminalResonse;

import pw.cdmi.common.domain.Terminal;

public interface TerminalManager
{
    ListTerminalResonse listTerminal(long cloudUserId, long offset, int limit);
    
    void updateClientTerminalStatus(long cloudUserId, String deviceSn, Byte status);
    
    void saveOrUpdateTerminalWhenLogin(Terminal terminal);
}
