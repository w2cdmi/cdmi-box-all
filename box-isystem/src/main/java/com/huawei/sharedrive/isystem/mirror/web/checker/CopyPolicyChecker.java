package com.huawei.sharedrive.isystem.mirror.web.checker;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;
import com.huawei.sharedrive.isystem.mirror.manager.MirrorQueryManager;
import com.huawei.sharedrive.isystem.util.FormValidateUtil;

import pw.cdmi.uam.domain.AuthApp;

public final class CopyPolicyChecker
{
    
    public static boolean checkPolicy(CopyPolicy copyPolicy, MirrorQueryManager mirrorQueryManager)
    {
        InvalidParameterException invalidParameterException = null;
        
        ensurePolicyNotEmpty(copyPolicy);
        checkNameAndDesc(copyPolicy);
        checkAppId(copyPolicy, mirrorQueryManager);
        checkTime(copyPolicy);
        checkType(copyPolicy);
        checkState(copyPolicy);
        
        if (copyPolicy.getExeType() == 1
            && copyPolicy.getExeStartAt().compareTo(copyPolicy.getExeEndAt()) > 0)
        {
            invalidParameterException = new InvalidParameterException("copyPolicy exe time wrong Exception ");
            throw invalidParameterException;
        }
        return true;
    }
    
    private static void checkAppId(CopyPolicy copyPolicy, MirrorQueryManager mirrorQueryManager)
    {
        if (StringUtils.isBlank(copyPolicy.getAppId()))
        {
            throw new InvalidParameterException("copyPolicy.getAppId() is blank Exception");
        }
        List<AuthApp> authApps = mirrorQueryManager.getAuthAppList();
        boolean temp = false;
        for (AuthApp authApp : authApps)
        {
            if (authApp.getAuthAppId().equals(copyPolicy.getAppId()))
            {
                temp = true;
                break;
            }
        }
        if (!temp)
        {
            throw new InvalidParameterException("copyPolicy.getAppId() is wrong Exception "
                + copyPolicy.getAppId());
        }
    }
    
    private static void checkNameAndDesc(CopyPolicy copyPolicy)
    {
        if (copyPolicy.getName() == null || StringUtils.isBlank(copyPolicy.getName()))
        {
            throw new InvalidParameterException("copyPolicy.getName()  is blank");
        }
        if (copyPolicy.getDescription() != null && copyPolicy.getDescription().length() > 512)
        {
            throw new InvalidParameterException("copyPolicy.getDescription()  Excetpion");
        }
        int len = copyPolicy.getName().length();
        if (len > 128)
        {
            throw new InvalidParameterException("copyPolicy.getName() len Exception >128");
        }
    }
    
    private static void checkState(CopyPolicy copyPolicy)
    {
        if (copyPolicy.getState() != 0 && copyPolicy.getState() != 1)
        {
            throw new InvalidParameterException("copyPolicy.getState() is wrong Exception "
                + copyPolicy.getState());
        }
    }
    
    private static void checkTime(CopyPolicy copyPolicy)
    {
        if (!FormValidateUtil.isTimeNotNull(copyPolicy.getExeStartAt())
            || !FormValidateUtil.isTimeNotNull(copyPolicy.getExeEndAt()))
        {
            throw new InvalidParameterException(
                "copyPolicy.getExeStartAt() or copyPolicy.getExeEndAt() is wrong Exception "
                    + copyPolicy.getExeStartAt() + copyPolicy.getExeEndAt());
        }
    }
    
    private static void checkType(CopyPolicy copyPolicy)
    {
        if (copyPolicy.getType() != 0 && copyPolicy.getType() != 1)
        {
            throw new InvalidParameterException("copyPolicy.getType() is wrong Exception "
                + copyPolicy.getType());
        }
        if (copyPolicy.getExeType() != 0 && copyPolicy.getExeType() != 1)
        {
            throw new InvalidParameterException("copyPolicy.getExeType() is wrong Exception "
                + copyPolicy.getExeType());
        }
    }
    
    private static void ensurePolicyNotEmpty(CopyPolicy copyPolicy)
    {
        if (null == copyPolicy)
        {
            throw new InvalidParameterException("copyPolicy  is null");
        }
    }
    
    
    private CopyPolicyChecker()
    {
        
    }
    
   
}
