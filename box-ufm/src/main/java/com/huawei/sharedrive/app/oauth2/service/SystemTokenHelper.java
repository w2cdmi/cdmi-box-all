package com.huawei.sharedrive.app.oauth2.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.app.exception.AuthFailedException;
import com.huawei.sharedrive.app.statistics.domain.StatisticsAccessKey;
import com.huawei.sharedrive.app.statistics.service.StatisticsAccessKeyService;

import pw.cdmi.core.utils.EDToolsEnhance;

@Component("systemTokenHelper")
public class SystemTokenHelper
{
    public final static String AUTH_SYSTEM = "system";
    
    @Autowired
    private StatisticsAccessKeyService statisticsAccessKeyService;
    
    public void checkSystemToken(String authorization, String date)
    {
        if (!authorization.startsWith(AUTH_SYSTEM))
        {
            throw new AuthFailedException("Bad statistics authorization: " + authorization);
        }
        statisticsToken(authorization, date);
    }
    
    
    private StatisticsAccessKey statisticsToken(String authorization, String date)
    {
        String[] strArr = TokenChecker.checkLength(authorization);
        StatisticsAccessKey key = statisticsAccessKeyService.get(StringUtils.trimToEmpty(strArr[1]));
        if (null == key)
        {
            throw new AuthFailedException("Can not find the key " + authorization);
        }
        TokenChecker.checkSignature(EDToolsEnhance.decode(key.getSecretKey(), key.getSecretKeyEncodeKey()), date, strArr[2]);
        return key;
    }
    
}
