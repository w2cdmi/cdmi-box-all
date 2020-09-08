package pw.cdmi.box.uam.authapp.domain;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.core.utils.EDToolsEnhance;

public final class AppAccessKeyCryptUtil
{
    private AppAccessKeyCryptUtil()
    {
    }
    
    public static AppAccessKey encode(AppAccessKey appAccessKey)
    {
        if (appAccessKey == null)
        {
            return appAccessKey;
        }
        
        String secretKey = appAccessKey.getSecretKey();
        String key = appAccessKey.getSecretKeyEncodeKey();
        if (StringUtils.isBlank(secretKey))
        {
            throw new BusinessException();
        }
        else if (StringUtils.isBlank(key))
        {
            Map<String, String> map = EDToolsEnhance.encode(secretKey);
            appAccessKey.setSecretKey(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            appAccessKey.setSecretKeyEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
        }
        else
        {
            appAccessKey.setSecretKey(EDToolsEnhance.encode(secretKey, key));
        }
        
        return appAccessKey;
    }
    
    public static AppAccessKey decode(AppAccessKey appAccessKey)
    {
        if (appAccessKey == null)
        {
            return appAccessKey;
        }
        
        String secretKey = appAccessKey.getSecretKey();
        String key = appAccessKey.getSecretKeyEncodeKey();
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
            appAccessKey.setSecretKey(EDToolsEnhance.decode(secretKey, key));
        }
        return appAccessKey;
    }
}
