package pw.cdmi.box.uam.enterprise.domain;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.utils.EDToolsEnhance;

public final class EnterpriseAccountCryptUtil
{
    private EnterpriseAccountCryptUtil()
    {
    }
    
    public static EnterpriseAccount encode(EnterpriseAccount enterpriseAccount)
    {
        if (enterpriseAccount == null)
        {
            return enterpriseAccount;
        }
        
        String secretKey = enterpriseAccount.getSecretKey();
        String key = enterpriseAccount.getSecretKeyEncodeKey();
        if (StringUtils.isBlank(secretKey))
        {
            enterpriseAccount.setSecretKey("");
        }
        else if (StringUtils.isBlank(key))
        {
            Map<String, String> map = EDToolsEnhance.encode(secretKey);
            enterpriseAccount.setSecretKey(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            enterpriseAccount.setSecretKeyEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
        }
        else
        {
            enterpriseAccount.setSecretKey(EDToolsEnhance.encode(secretKey, key));
        }
        return enterpriseAccount;
    }
    
    public static EnterpriseAccount decode(EnterpriseAccount enterpriseAccount)
    {
        if (enterpriseAccount == null)
        {
            return enterpriseAccount;
        }
        
        String secretKey = enterpriseAccount.getSecretKey();
        String key = enterpriseAccount.getSecretKeyEncodeKey();
        if (StringUtils.isBlank(secretKey))
        {
            throw new BusinessException();
        }
        else if (StringUtils.isBlank(key))
        {
            throw new BusinessException();
        }
        else
        {
            enterpriseAccount.setSecretKey(EDToolsEnhance.decode(secretKey, key));
        }
        
        return enterpriseAccount;
    }
    
    public static String decodeSecretKey(final EnterpriseAccount enterpriseAccount)
    {
        if (enterpriseAccount == null)
        {
            throw new BusinessException();
        }
        
        String orgSecretKey = "";
        
        String secretKey = enterpriseAccount.getSecretKey();
        String key = enterpriseAccount.getSecretKeyEncodeKey();
        if (StringUtils.isBlank(secretKey))
        {
            throw new BusinessException();
        }
        else if (StringUtils.isBlank(key))
        {
            throw new BusinessException();
        }
        else
        {
            orgSecretKey = EDToolsEnhance.decode(secretKey, key);
        }
        
        return orgSecretKey;
    }
}
