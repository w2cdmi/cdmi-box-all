package pw.cdmi.box.uam.system.domain;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.core.utils.EDToolsEnhance;

public final class MailServerCryptUtil
{
    private MailServerCryptUtil()
    {
    }
    
    public static MailServer decode(MailServer mailServer)
    {
        if (mailServer == null)
        {
            return mailServer;
        }
        
        String authPassword = mailServer.getAuthPassword();
        String key = mailServer.getAuthPasswordEncodeKey();
        if (StringUtils.isBlank(authPassword))
        {
            mailServer.setAuthPassword("");
        }
        else if (StringUtils.isNotBlank(key))
        {
            authPassword = EDToolsEnhance.decode(mailServer.getAuthPassword(), key);
            mailServer.setAuthPassword(authPassword);
        }
        else
        {
            mailServer.setAuthPassword("");
        }
        return mailServer;
        
    }
    
    public static MailServer encode(MailServer mailServer)
    {
        if (mailServer == null)
        {
            return mailServer;
        }
        
        String encodeResult = null;
        String authPassword = mailServer.getAuthPassword();
        String keyEncodeSecretKey = mailServer.getAuthPasswordEncodeKey();
        if (StringUtils.isBlank(authPassword))
        {
            mailServer.setAuthPassword("");
        }
        else if (StringUtils.isBlank(keyEncodeSecretKey))
        {
            Map<String, String> map = EDToolsEnhance.encode(authPassword);
            encodeResult = map.get(EDToolsEnhance.ENCRYPT_CONTENT);
            keyEncodeSecretKey = map.get(EDToolsEnhance.ENCRYPT_KEY);
            mailServer.setAuthPasswordEncodeKey(keyEncodeSecretKey);
            mailServer.setAuthPassword(encodeResult);
        }
        else
        {
            encodeResult = EDToolsEnhance.encode(authPassword, keyEncodeSecretKey);
            mailServer.setAuthPassword(encodeResult);
        }
        
        return mailServer;
    }
}
