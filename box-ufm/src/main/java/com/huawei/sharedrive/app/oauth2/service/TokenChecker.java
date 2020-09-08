package com.huawei.sharedrive.app.oauth2.service;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.AuthFailedException;

import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.utils.DateUtils;

public final class TokenChecker
{
    private TokenChecker()
    {
        
    }
    
    private final static int LENG_ARRAY_APP = 3;
    
    static void checkSignature(String securityKey, String date, String result) throws AuthFailedException
    {
        String calcuRes = SignatureUtils.getSignature(StringUtils.trimToEmpty(securityKey), date);
        if (!StringUtils.equals(result, calcuRes))
        {
            throw new AuthFailedException("signature result is false. calcuRes is " + calcuRes);
        }
    }
    
    static String[] checkLength(String authorization)
    {
        String[] strArr = authorization.split(",");
        if (strArr.length != LENG_ARRAY_APP)
        {
            throw new AuthFailedException("Bad app authorization: " + authorization);
        }
        return strArr;
    }
    
    public static String getAk(String authorization)
    {
        String[] strArr = checkLength(authorization);
        return strArr[1];
    }
}
