package com.huawei.sharedrive.app.test.openapi.common;


public class HexUtil
{
    public static String asHex(byte[] buf)
    {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        
        for (int i = 0; i < buf.length; i++)
        {
            if ((buf[i] & 0xff) < 0x10)
            {
                strbuf.append(0);
            }
            strbuf.append(Long.toString(buf[i] & 0xff, 16));
        }
        
        return strbuf.toString();
    }
    
    public static byte[] asBin(String src)
    {
        if (src.length() < 1)
        {
            return null;
        }
        byte[] encrypted = new byte[src.length() / 2];
        int size = src.length() / 2;
        int high = 0;
        int low = 0;
        for (int i = 0; i < size; i++)
        {
            high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);
            low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);
            
            encrypted[i] = (byte) (high * 16 + low);
        }
        return encrypted;
    }
}
