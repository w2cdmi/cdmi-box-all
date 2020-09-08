package com.huawei.sharedrive.uam.terminal.manager.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.openapi.domain.ListTerminalResonse;
import com.huawei.sharedrive.uam.terminal.manager.TerminalManager;
import com.huawei.sharedrive.uam.terminal.service.TerminalService;

import pw.cdmi.common.domain.Terminal;

@Component
public class TerminalManagerImpl implements TerminalManager
{
    private final static Logger LOGGER = LoggerFactory.getLogger(TerminalManager.class);
    
    @Autowired
    private TerminalService terminalService;
    
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Override
    public ListTerminalResonse listTerminal(long cloudUserId, long offset, int limit)
    {
        int total = terminalService.getByUserCount(cloudUserId);
        ListTerminalResonse listTerminalResonse = new ListTerminalResonse();
        listTerminalResonse.setTotalCount(total);
        listTerminalResonse.setLimit(limit);
        listTerminalResonse.setOffset(offset);
        List<Terminal> list = terminalService.getByUser(cloudUserId, offset, limit);
        fillStatus(list);
        listTerminalResonse.setTerminalList(list);
        return listTerminalResonse;
    }
    
    /**
     * 
     * @param list
     */
    private void fillStatus(List<Terminal> list)
    {
        long now = new Date().getTime();
        for (Terminal terminal : list)
        {
            if (terminal.getStatus() == Terminal.STATUS_CURRENT)
            {
                if (terminal.getLastAccessAt() == null)
                {
                    continue;
                }
                long lastAccessAt = terminal.getLastAccessAt().getTime();
                if (Math.abs(lastAccessAt - now) > 15 * 60 * 1000)
                {
                    terminal.setStatus(Terminal.STATUS_NORMAL);
                }
            }
        }
    }
    
    @Override
    public void updateClientTerminalStatus(long cloudUserId, String deviceSn, Byte status)
    {
        LOGGER.info("[terminal] update terminal status cloudUserId:" + cloudUserId + " deviceSn:" + deviceSn);
        Terminal condition = new Terminal();
        condition.setDeviceSn(deviceSn);
        condition.setCloudUserId(cloudUserId);
        List<Terminal> list = terminalService.getBySnUser(condition);
        if (CollectionUtils.isEmpty(list))
        {
            throw new InvalidParamterException("[terminal] deviceSN is invalid");
        }
        for (Terminal dbTerminal : list)
        {
            dbTerminal.setStatus(status);
            this.terminalService.updateNoWebTerminalStatus(dbTerminal);
            if (StringUtils.isNotEmpty(dbTerminal.getToken()))
            {
                this.userTokenHelper.delUserToken(dbTerminal.getToken());
            }
        }
    }
    
    @Override
    public void saveOrUpdateTerminalWhenLogin(Terminal terminal)
    {
        int deviceType = terminal.getDeviceType();
        Date newDate = new Date();
        if (deviceType == Terminal.CLIENT_TYPE_WEB)
        {
            List<Terminal> selTerminalList = terminalService.getWebTerminal(terminal);
            if (null == selTerminalList || selTerminalList.size() < 1)
            {
                terminal.setLastAccessAt(newDate);
                terminal.setCreatedAt(newDate);
                terminalService.create(terminal);
            }
            else
            {
                terminal.setCreatedAt(newDate);
                terminal.setLastAccessAt(newDate);
                terminal.setId(selTerminalList.get(0).getId());
                terminalService.updateWebTerminal(terminal);
            }
        }
        else
        {
            List<Terminal> selTerminalList = terminalService.getBySnUser(terminal);
            if (CollectionUtils.isEmpty(selTerminalList))
            {
                terminal.setLastAccessAt(newDate);
                terminal.setCreatedAt(newDate);
                terminalService.create(terminal);
            }
            else
            {
                terminal.setCreatedAt(newDate);
                terminal.setLastAccessAt(newDate);
                terminal.setId(selTerminalList.get(0).getId());
                terminalService.updateTerminal(terminal);
            }
        }
        
    }
}
