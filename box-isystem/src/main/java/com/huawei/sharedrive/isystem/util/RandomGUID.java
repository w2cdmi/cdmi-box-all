package com.huawei.sharedrive.isystem.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huawei.sharedrive.isystem.exception.BusinessException;

/**
 * GUID随机生成类
 * 
 * @author c90004011
 * @version CloudStore CSE Service Platform Subproject, 2010-5-1
 * @see
 * @since
 */
public class RandomGUID
{
    private static Logger logger = LoggerFactory.getLogger(RandomGUID.class);
    
    // 数字8的常量表达式
    private static final int N8 = 8;
    
    // 数字12的常量表达式
    private static final int N12 = 12;
    
    // 数字16的常量表达式
    private static final int N16 = 16;
    
    // 数字20的常量表达式
    private static final int N20 = 20;
    
    // 数字32的常量表达式
    private static final int N32 = 32;
    
    // 数字64的常量表达式
    private static final int N64 = 64;
    
    // 数字128的常量表达式
    private static final int N128 = 128;
    
    // PAD_BELOW
    private static final int PAD_BELOW = 0x10;
    
    // TWO_BYTES
    private static final int TWO_BYTES = 0xFF;
    
    // 随机码
    private static Random myRand;
    
    // 安全码
    private static SecureRandom mySecureRand;
    
    // id
    private static String sId;
    
    // 转换前的值
    private String valueBeforeMD5 = "";
    
    // 转换后的值
    private String valueAfterMD5 = "";
    
    // 截取字符串开始位(从0开始)
    private static final int BEGIN_INDEX = 7;
    
    // 截取字符串结束位(从0开始)
    private static final int END_INDEX = 23;
    
    // UDS用户前缀
    private static final String PREFIX = "csecloud";
    
    static
    {
        mySecureRand = new SecureRandom();
        // 得到一个伪随机数
        long secureInitializer = mySecureRand.nextLong();
        myRand = new Random(secureInitializer);
        try
        {
            sId = InetAddress.getLocalHost().toString();
        }
        catch (UnknownHostException e)
        {
            logger.error(e.getMessage(), e);
        }
    }
    
    /**
     * RandomGUID类的构造方法
     * 
     * @author c90004011
     */
    public RandomGUID()
    {
        getRandomGUID(false);
    }
    
    /**
     * 覆盖toString方法
     * 
     * @return String
     */
    @Override
    public String toString()
    {
        String raw = valueAfterMD5.toUpperCase(Locale.getDefault());
        StringBuffer sb = new StringBuffer(N64);
        sb.append(raw.substring(0, N8));
        sb.append('-');
        sb.append(raw.substring(N8, N12));
        sb.append('-');
        sb.append(raw.substring(N12, N16));
        sb.append('-');
        sb.append(raw.substring(N16, N20));
        sb.append('-');
        sb.append(raw.substring(N20));
        return sb.toString();
    }
    
    /*
     * 生成随机的GUID
     * 
     * @param secure secure参数
     */
    private void getRandomGUID(boolean secure)
    {
        StringBuffer sbValueBeforeMD5 = new StringBuffer(N128);
        try
        {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            
            long time = System.currentTimeMillis();
            long rand = 0;
            if (secure)
            {
                rand = mySecureRand.nextLong();
            }
            else
            {
                rand = myRand.nextLong();
            }
            sbValueBeforeMD5.append(sId);
            sbValueBeforeMD5.append(':');
            sbValueBeforeMD5.append(Long.toString(time));
            sbValueBeforeMD5.append(':');
            sbValueBeforeMD5.append(Long.toString(rand));
            valueBeforeMD5 = sbValueBeforeMD5.toString();
            md5.update(valueBeforeMD5.getBytes("UTF8"));
            byte[] array = md5.digest();
            StringBuffer sb = new StringBuffer(N32);
            int b;
            for (int j = 0; j < array.length; ++j)
            {
                b = array[j] & TWO_BYTES;
                if (b < PAD_BELOW)
                {
                    sb.append('0');
                    
                }
                sb.append(Integer.toHexString(b));
            }
            valueAfterMD5 = sb.toString();
        }
        catch (RuntimeException e)
        {
            throw new BusinessException(e);
        }
        catch (Exception e)
        {
            logger.error("Error:" + e);
        }
    }
    
    /**
     * valueBeforeMD5的get方法
     * 
     * @return 返回 valueBeforeMD5
     */
    public String getValueBeforeMD5()
    {
        return valueBeforeMD5;
    }
    
    /**
     * valueBeforeMD5的set方法
     * 
     * @param valueBeforeMD5 对valueBeforeMD5进行赋值
     */
    public void setValueBeforeMD5(String valueBeforeMD5)
    {
        this.valueBeforeMD5 = valueBeforeMD5;
    }
    
    /**
     * valueAfterMD5的get方法
     * 
     * @return 返回 valueAfterMD5
     */
    public String getValueAfterMD5()
    {
        return valueAfterMD5;
    }
    
    /**
     * valueAfterMD5的set方法
     * 
     * @param valueAfterMD5 对valueAfterMD5进行赋值
     */
    public void setValueAfterMD5(String valueAfterMD5)
    {
        this.valueAfterMD5 = valueAfterMD5;
    }
    
    
    /**
     * Constructor with security option. Setting secure true enables each random number
     * generated to be cryptographically strong. Secure false defaults to the standard
     * Random function seeded with a single cryptographically strong random number.
     * 
     * @param secure
     */
    public RandomGUID(boolean secure)
    {
        getRandomGUID(secure);
    }
    
    /*
     * Static block to take care of one time secureRandom seed. It takes a few seconds to
     * initialize SecureRandom. You might want to consider removing this static block or
     * replacing it with a "time since first loaded" seed to reduce this time. This block
     * will run only once per JVM instance.
     */
    static
    {
        mySecureRand = new SecureRandom();
        long secureInitializer = mySecureRand.nextLong();
        myRand = new Random(secureInitializer);
        try
        {
            sId = InetAddress.getLocalHost().toString();
        }
        catch (UnknownHostException e)
        {
            logger.error(e.getMessage(), e);
        }
        
    }
    
    /**
     * 为UDS生成GUID</br> 结构:</br> 用户标识(8位)+时间戳(8位)+截取GUID串(16位)
     * 
     * @return
     */
    public String getGUID4uds()
    {
        StringBuffer sb = new StringBuffer();
        
        // 时间转成16进制后的形式:13a0c443dc7,我们只要后8位
        String timestamp = Long.toHexString(System.currentTimeMillis()).substring(3);
        
        sb.append(PREFIX).append(timestamp).append(valueAfterMD5.substring(BEGIN_INDEX, END_INDEX));
        
        return sb.toString();
    }
    
    private void executeGetRandomGUID(boolean secure, MessageDigest md5)
    {
        long time = System.currentTimeMillis();
        long rand = 0;
        
        if (secure)
        {
            rand = mySecureRand.nextLong();
        }
        else
        {
            rand = myRand.nextLong();
        }

        StringBuffer sbValueBeforeMD5 = new StringBuffer(128);
        sbValueBeforeMD5.append(sId);
        sbValueBeforeMD5.append(':');
        sbValueBeforeMD5.append(Long.toString(time));
        sbValueBeforeMD5.append(':');
        sbValueBeforeMD5.append(Long.toString(rand));
        
        valueBeforeMD5 = sbValueBeforeMD5.toString();
        if (md5 != null)
        {
            mD5ToString(md5);
        }
    }
    
    private void mD5ToString(MessageDigest md5)
    {
        md5.update(valueBeforeMD5.getBytes(Charset.defaultCharset()));
        byte[] array = md5.digest();
        StringBuffer sb = new StringBuffer(32);
        int b = 0;
        for (int j = 0; j < array.length; ++j)
        {
            b = array[j] & TWO_BYTES;
            if (b < PAD_BELOW)
            {
                sb.append('0');
            }
            sb.append(Integer.toHexString(b));
        }
        valueAfterMD5 = sb.toString();
    }
}
