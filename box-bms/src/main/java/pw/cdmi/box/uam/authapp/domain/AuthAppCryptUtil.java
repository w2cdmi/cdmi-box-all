package pw.cdmi.box.uam.authapp.domain;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.uam.exception.BusinessException;
import pw.cdmi.core.utils.EDToolsEnhance;
import pw.cdmi.uam.domain.AuthApp;

public final class AuthAppCryptUtil
{
    private AuthAppCryptUtil()
    {
    }
    public static AuthApp encode(AuthApp authApp)
    {
        if (authApp == null)
        {
            return authApp;
        }
        
        String secretKey = authApp.getUfmSecretKey();
        String key = authApp.getUfmSecretKeyEncodeKey();
        if (StringUtils.isBlank(secretKey))
        {
            throw new BusinessException();
        }
        else if (StringUtils.isBlank(key))
        {
            Map<String, String> map = EDToolsEnhance.encode(secretKey);
            authApp.setUfmSecretKey(map.get(EDToolsEnhance.ENCRYPT_CONTENT));
            authApp.setUfmSecretKeyEncodeKey(map.get(EDToolsEnhance.ENCRYPT_KEY));
        }
        else
        {
            authApp.setUfmSecretKey(EDToolsEnhance.encode(secretKey, key));
        }
        return authApp;
    }
    
    public static AuthApp decode(AuthApp authApp)
    {
        if (authApp == null)
        {
            return authApp;
        }
        
        String secretKey = authApp.getUfmSecretKey();
        String key = authApp.getUfmSecretKeyEncodeKey();
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
            authApp.setUfmSecretKey(EDToolsEnhance.decode(secretKey, key));
        }
        return authApp;
    }
}
