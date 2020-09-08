package com.huawei.sharedrive.app.openapi.checker;

import com.huawei.sharedrive.app.authapp.service.AuthAppService;
import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.exception.NoSuchApplicationException;

import pw.cdmi.uam.domain.AuthApp;

public final class ApplicationChecker
{
    private ApplicationChecker()
    {
        
    }
    
    public static void checkAppIdAllowEmpty(String appId, AuthAppService authAppService)
    {
        if(null == appId)
        {
            return;
        }
        if(appId.length() > 255)
        {
            throw new InvalidParamException("appId length is more than 255");
        }
        AuthApp authApp = authAppService.getByAuthAppID(appId);
        if(null == authApp)
        {
            throw new NoSuchApplicationException();
        }
    }
    
    
}
