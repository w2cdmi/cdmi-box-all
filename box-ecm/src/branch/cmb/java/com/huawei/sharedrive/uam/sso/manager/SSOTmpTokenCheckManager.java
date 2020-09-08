package com.huawei.sharedrive.uam.sso.manager;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;

public interface SSOTmpTokenCheckManager
{
    void checkSsoParamter(String appId, String ssoToken, String domainName, HttpServletRequest request)
        throws InvalidParamterException;
}
