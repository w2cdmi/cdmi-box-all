package com.huawei.sharedrive.isystem;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.core.encrypt.HashPassword;

public class TestMain {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestMain.class);
	
	private static final int BYTE_LENGTH = 6;
	private final static int ITERATIONS = 50000;

	public static void main(String[] args) throws Exception{
        String password = "oZpklFSfgsC$9";
//        String salt = "efbfbd4cefbfbd4f61efbfbd7c15efbfbdefbfbd0c2f1c16efbfbd";
//        char[] chars = password.toCharArray();
//
//

        HashPassword hashPassword = generateHashPassword(password, ITERATIONS);
        System.out.println(hashPassword.getHashPassword());

        String slat = hashPassword.getSalt();
        int interations = hashPassword.getIterations();

        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), slat.getBytes(), interations, 64 * BYTE_LENGTH);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        System.out.println(toHex(hash));
    }

    private static HashPassword generateHashPassword(String password, int iterations)
            throws NoSuchAlgorithmException, InvalidKeySpecException
        {
            HashPassword hashPassword = new HashPassword();
            char[] chars = password.toCharArray();
            byte[] salt = null;
            try
            {
                salt = getSalt().getBytes("utf8");
            }
            catch (UnsupportedEncodingException e)
            {
                LOGGER.error("", e);
                throw new NoSuchAlgorithmException("UnsupportedEncodingException " + e.getMessage(), e);
            }
            
            PBEKeySpec spec = new PBEKeySpec(chars, salt, ITERATIONS, 64 * BYTE_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            hashPassword.setIterations(iterations);
            hashPassword.setSalt(toHex(salt));
            hashPassword.setHashPassword(toHex(hash));
            return hashPassword;
        }
    
    private static String toHex(byte[] array)
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0)
        {
            return String.format("%0" + paddingLength + 'd', 0) + hex;
        }
        return hex;
    }
    
    private static byte[] fromHex(String hex)
    {
        byte[] bytes = new byte[hex.length() / 2];
        try
        {
            for (int i = 0; i < bytes.length; i++)
            {
                bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
            }
            return bytes;
        }
        catch (NumberFormatException e)
        {
            LOGGER.error("NumberFormatException ", e);
            throw e;
        }
        
    }
    
    private static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        try
        {
            return new String(salt, "utf8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new NoSuchAlgorithmException("UnsupportedEncodingException " + e.getMessage(), e);
        }
    }
}
