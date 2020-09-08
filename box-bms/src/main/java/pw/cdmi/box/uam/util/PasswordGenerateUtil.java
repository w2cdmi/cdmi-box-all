package pw.cdmi.box.uam.util;

import java.security.SecureRandom;

public final class PasswordGenerateUtil
{
    
    private final static char[] COMMON_CHAR = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
        'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
        'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    
    private final static char[] NUMBER_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    
    private final static char[] SPECIAL_CHAR = {'-', '!', '@', '$', '^', '*', '+', '.'};
    
    private final static int MAX_LENGTH = 20;
    
    private final static int MIN_LENGTH = 8;
    
    private PasswordGenerateUtil()
    {
    }
    
    public static String getRandomPassword()
    {
        return getRandomPassword(getRangRandom(MIN_LENGTH, MAX_LENGTH));
    }
    
    private static String getRandomPassword(int length)
    {
        StringBuffer randomPassword = new StringBuffer();
        int charLength = getRangRandom(1, length - 2);
        int specialLength = getRangRandom(1, length - charLength - 1);
        int numLength = length - charLength - specialLength;
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < charLength; i++)
        {
            randomPassword.append(COMMON_CHAR[Math.abs(random.nextInt(COMMON_CHAR.length))]);
        }
        for (int i = 0; i < specialLength; i++)
        {
            randomPassword.append(SPECIAL_CHAR[Math.abs(random.nextInt(SPECIAL_CHAR.length))]);
        }
        for (int i = 0; i < numLength; i++)
        {
            randomPassword.append(NUMBER_CHAR[Math.abs(random.nextInt(NUMBER_CHAR.length))]);
        }
        return randomPassword.toString();
    }
    
    private static int getRangRandom(int min, int max)
    {
        SecureRandom random = new SecureRandom();
        int num = Math.abs(random.nextInt(max)) % (max - min + 1) + min;
        return num;
    }
    
}
