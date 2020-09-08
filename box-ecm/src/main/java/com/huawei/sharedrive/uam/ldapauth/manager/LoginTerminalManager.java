package com.huawei.sharedrive.uam.ldapauth.manager;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.exception.LoginAuthFailedException;
import com.huawei.sharedrive.uam.openapi.domain.RestTerminalRsp;

import pw.cdmi.common.domain.Terminal;

public interface LoginTerminalManager
{
    void checkLoginTerminalStatus(long cloudUserId, String deviceSn, int deviceType)
        throws LoginAuthFailedException;
    
    Terminal fillTerminal(HttpServletRequest request, boolean isNtlm);
    
    @SuppressWarnings("PMD.ExcessiveParameterList")
    Terminal saveOrUpdateTerminalWhenLogin(long cloudUserId, String appId, long accountId,
        HttpServletRequest request, String tokenStr, boolean isNtlm);
    
    RestTerminalRsp getByUserLastLogin(long cloudUserId);
    
}
