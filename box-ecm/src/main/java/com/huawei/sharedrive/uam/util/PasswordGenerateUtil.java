package com.huawei.sharedrive.uam.util;

import java.security.SecureRandom;

public final class PasswordGenerateUtil
{
    private PasswordGenerateUtil()
    {
        
    }

//    //去掉小写的l与大写的I，因为这两个字符有时不容易区分，会导致用户密码输入错误
//    private final static char[] COMMON_CHAR = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', /*'l',*/
//        'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
//        'G', 'H', /*'I',*/ 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
//
//    private final static char[] NUMBER_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
//
//    private final static char[] SPECIAL_CHAR = {'-', '!', '@', '$', '^', '*', '+', '.'};

    //简化密码的构成：去掉大写（手机上输入不方便）、去掉$字符（消息模板替换时String.replaceAll()，需要对密码中的$做特殊处理，不使用此特殊字符）、去掉.^字符（不方便用户识别）
    private final static char[] COMMON_CHAR = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', /*'l',*/ 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private final static char[] NUMBER_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final static char[] SPECIAL_CHAR = {'-', '!', '@', '*', '+'};

    private final static int MAX_LENGTH = 20;
    
    private final static int MIN_LENGTH = 8;
    
    public static String getRandomPassword()
    {
        return getRandomPassword(getRangRandom(MIN_LENGTH, MAX_LENGTH));
    }
    
    public static String getRandomPassword(int length)
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
