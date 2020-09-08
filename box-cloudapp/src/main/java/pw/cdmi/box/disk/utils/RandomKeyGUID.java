package pw.cdmi.box.disk.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcc.crypt.EncryptHelper;

import pw.cdmi.core.exception.BusinessException;


public final class RandomKeyGUID
{
    private RandomKeyGUID()
    {
        
    }
    
    private static Logger logger = LoggerFactory.getLogger(RandomKeyGUID.class);
    
    public static String getSecureRandomGUID()
    {
        try
        {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            byte[] keyBytes = new byte[28];
            sr.nextBytes(keyBytes);
            return EncryptHelper.parseByte2HexStr(keyBytes);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("Error:" + e);
            throw new BusinessException(e);
        }
    }
    
}
