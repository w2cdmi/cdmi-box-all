package com.huawei.sharedrive.uam.oauth2.manager.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.enterprise.manager.EnterpriseAccountManager;
import com.huawei.sharedrive.uam.exception.AccountAuthFailedException;
import com.huawei.sharedrive.uam.oauth2.manager.AuthManager;
import com.huawei.sharedrive.uam.util.Constants;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.EDToolsEnhance;

@Component
public class AuthManagerImpl implements AuthManager
{
    
    public final static String APP_PREFIX = "app,";
    
    public final static String ACCOUNT_PREFIX = "account,";
    
    private final static int LENG_ARRAY_APP = 3;
    
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthManager.class);
    
    @Autowired
    private EnterpriseAccountManager enterpriseAccountManager;
    
    @Override
    public EnterpriseAccount checkAppToken(String authorization, String date)
        throws AccountAuthFailedException
    {
        if (!authorization.startsWith(APP_PREFIX) && !authorization.startsWith(ACCOUNT_PREFIX))
        {
            LOGGER.error("Bad app authorization: " + authorization);
            throw new AccountAuthFailedException();
        }
        String[] strArr = authorization.split(",");
        if (strArr.length != LENG_ARRAY_APP)
        {
            LOGGER.error("Bad app authorization: " + authorization);
            throw new AccountAuthFailedException();
        }
        EnterpriseAccount enterpriseAccount = enterpriseAccountManager.getByAccessKeyId(StringUtils.trimToEmpty(strArr[1]));
        if (null == enterpriseAccount)
        {
            LOGGER.error("Can not find the key " + authorization);
            throw new AccountAuthFailedException();
        }
        if (Constants.STATUS_OF_ACCOUNT_ENABLE != enterpriseAccount.getStatus())
        {
            LOGGER.error("enterpriseAccount status is disable, the key: " + authorization);
            throw new AccountAuthFailedException();
        }
        String decodedKey = EDToolsEnhance.decode(enterpriseAccount.getSecretKey(),
            enterpriseAccount.getSecretKeyEncodeKey());
        checkSignature(decodedKey, date, strArr[2]);
        return enterpriseAccount;
    }
    
    private void checkSignature(String securityKey, String date, String result)
        throws AccountAuthFailedException
    {
        String calcuRes = SignatureUtils.getSignature(StringUtils.trimToEmpty(securityKey), date);
        if (!StringUtils.equals(result, calcuRes))
        {
            LOGGER.error("signature result is false. calcuRes is " + calcuRes);
            throw new AccountAuthFailedException();
        }
    }
}
