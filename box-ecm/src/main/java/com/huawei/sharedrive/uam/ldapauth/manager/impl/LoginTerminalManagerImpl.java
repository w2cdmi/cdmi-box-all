package com.huawei.sharedrive.uam.ldapauth.manager.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.ldapauth.manager.LoginTerminalManager;
import com.huawei.sharedrive.uam.openapi.domain.RestTerminalRsp;
import com.huawei.sharedrive.uam.terminal.manager.TerminalManager;
import com.huawei.sharedrive.uam.terminal.service.TerminalService;
import com.huawei.sharedrive.uam.util.RequestUtils;
import com.huawei.sharedrive.uam.util.StringEncodeUtils;

import pw.cdmi.common.domain.Terminal;
import pw.cdmi.core.utils.DateUtils;

@Component
public class LoginTerminalManagerImpl implements LoginTerminalManager
{
    private final static Logger LOGGER = LoggerFactory.getLogger(LoginTerminalManager.class);
    
    @Autowired
    private TerminalService terminalService;
    
    @Autowired
    private TerminalManager terminalManager;
    
    @Override
    public void checkLoginTerminalStatus(long cloudUserId, String deviceSn, int deviceType)
        throws LoginAuthFailedException
    {
        Terminal terminal = new Terminal();
        terminal.setDeviceSn(deviceSn);
        terminal.setCloudUserId(cloudUserId);
        if (deviceType != Terminal.CLIENT_TYPE_WEB)
        {
            List<Terminal> selTerminalList = terminalService.getBySnUser(terminal);
            for (Terminal selTerminal : selTerminalList)
            {
                if (selTerminal.getStatus() == Terminal.STATUS_IGNORE)
                {
                    LOGGER.error("terminal status is disable cloudUserId:" + cloudUserId + " deviceSn:"
                        + deviceSn + " deviceType:" + deviceType);
                    throw new LoginAuthFailedException();
                }
            }
        }
    }
    
    @Override
    public Terminal fillTerminal(HttpServletRequest request, boolean isNtlm)
    {
        String deviceTypeStr = request.getHeader("x-device-type");
        String deviceSN = request.getHeader("x-device-sn");
        String deviceOS = request.getHeader("x-device-os");
        String deviceName = request.getHeader("x-device-name");
        String deviceAgent = request.getHeader("x-client-version");
        String requestIp = request.getHeader("x-request-ip");
        if (!isNtlm)
        {
            deviceTypeStr = StringEncodeUtils.decodeUft8ValueNoException(deviceTypeStr);
            deviceSN = StringEncodeUtils.decodeUft8ValueNoException(deviceSN);
            deviceOS = StringEncodeUtils.decodeUft8ValueNoException(deviceOS);
            deviceName = StringEncodeUtils.decodeUft8ValueNoException(deviceName);
            deviceAgent = StringEncodeUtils.decodeUft8ValueNoException(deviceAgent);
            requestIp = StringEncodeUtils.decodeUft8ValueNoException(requestIp);
        }
        int deviceType = getDeviceType(deviceTypeStr);
        if (StringUtils.isBlank(requestIp))
        {
            requestIp = RequestUtils.getRealIP(request);
        }
        Terminal terminal = new Terminal();
        terminal.setDeviceAgent(deviceAgent);
        terminal.setDeviceName(deviceName);
        terminal.setDeviceOS(deviceOS);
        terminal.setDeviceSn(deviceSN);
        terminal.setDeviceType(deviceType);
        terminal.setLastAccessIP(requestIp);
        return terminal;
    }
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @Override
    public Terminal saveOrUpdateTerminalWhenLogin(long cloudUserId, String appId, long accountId,
        HttpServletRequest request, String tokenStr, boolean isNtlm)
    {
        Terminal terminal = fillTerminal(request, isNtlm);
        checkLoginTerminalStatus(cloudUserId, terminal.getDeviceSn(), terminal.getDeviceType());
        terminal.setAccountId(accountId);
        terminal.setAppId(appId);
        terminal.setCloudUserId(cloudUserId);
        terminal.setStatus(Terminal.STATUS_CURRENT);
        terminal.setToken(tokenStr);
        terminalManager.saveOrUpdateTerminalWhenLogin(terminal);
        return terminal;
    }
    
    private int getDeviceType(String deviceType)
    {
        if (Terminal.CLIENT_TYPE_ANDROID_STR.equals(deviceType))
        {
            return Terminal.CLIENT_TYPE_ANDROID;
        }
        if (Terminal.CLIENT_TYPE_IOS_STR.equals(deviceType))
        {
            return Terminal.CLIENT_TYPE_IOS;
        }
        if (Terminal.CLIENT_TYPE_PC_STR.equals(deviceType))
        {
            return Terminal.CLIENT_TYPE_PC;
        }
        if (Terminal.CLIENT_TYPE_WEB_STR.equals(deviceType))
        {
            return Terminal.CLIENT_TYPE_WEB;
        }
        return Terminal.CLIENT_TYPE_WEB;
    }
    
    @Override
    public RestTerminalRsp getByUserLastLogin(long cloudUserId)
    {
        Terminal terminal = null;
        try
        {
            terminal = terminalService.getByUserLastLogin(cloudUserId);
        }
        catch (RuntimeException e)
        {
            return null;
        }
        if (null == terminal)
        {
            return null;
        }
        RestTerminalRsp restTerminal = new RestTerminalRsp();
        
        switch (terminal.getDeviceType())
        {
            case Terminal.CLIENT_TYPE_ANDROID:
                restTerminal.setDeviceType(Terminal.CLIENT_TYPE_ANDROID_STR);
                break;
            case Terminal.CLIENT_TYPE_IOS:
                restTerminal.setDeviceType(Terminal.CLIENT_TYPE_IOS_STR);
                break;
            case Terminal.CLIENT_TYPE_PC:
                restTerminal.setDeviceType(Terminal.CLIENT_TYPE_PC_STR);
                break;
            case Terminal.CLIENT_TYPE_WEB:
                restTerminal.setDeviceType(Terminal.CLIENT_TYPE_WEB_STR);
                break;
            default:
                break;
        }
        restTerminal.setDeviceName(terminal.getDeviceName());
        restTerminal.setDeviceOS(terminal.getDeviceOS());
        restTerminal.setDeviceAgent(terminal.getDeviceAgent());
        restTerminal.setLastAccessIP(terminal.getLastAccessIP());
        Long lastAccessAt = DateUtils.getDateTime(terminal.getLastAccessAt()) / 1000 * 1000;
        restTerminal.setLastAccessAt(lastAccessAt);
        
        return restTerminal;
        
    }
}
