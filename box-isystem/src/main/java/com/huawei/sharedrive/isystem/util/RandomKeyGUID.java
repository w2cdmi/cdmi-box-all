package com.huawei.sharedrive.isystem.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcc.crypt.EncryptHelper;

import com.huawei.sharedrive.isystem.exception.BusinessException;

public final class RandomKeyGUID
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomKeyGUID.class);
    
    private RandomKeyGUID()
    {
    
    }
    
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
            LOGGER.error("Error:" + e);
            throw new BusinessException(e);
        }
    }
    
}
