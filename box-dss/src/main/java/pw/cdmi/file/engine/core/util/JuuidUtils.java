package pw.cdmi.file.engine.core.util;

import org.apache.commons.codec.digest.DigestUtils;

import pw.cdmi.core.exception.InnerException;

/**
 * uuid生成器
 * 
 * @author c90003207
 * 
 */
public final class JuuidUtils
{
    private JuuidUtils()
    {
    }
    
    private final static String UUID_PATH_TEMPLATE = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
    
    /**
     * 根据path生成uuid
     * 
     * @param path 文件路径
     * @return uuid
     * @throws Exception 异常
     */
    public static String generateUUID(String arg)
    {
        if (null == arg)
        {
            throw new InnerException("Parameter Is Null.");
        }
        StringBuilder sb = new StringBuilder();
        char[] chars = DigestUtils.md5Hex(arg).toCharArray();
        int cnt = 0;
        int length = UUID_PATH_TEMPLATE.length();
        char ch = '0';
        for (int i = 0; i < length; i++)
        {
            ch = UUID_PATH_TEMPLATE.charAt(i);
            if (ch == 'x' && cnt < chars.length)
            {
                ch = chars[cnt++];
                if (ch == '-')
                {
                    ch = chars[cnt++];
                }
            }
            sb.append(ch);
        }
        return sb.toString();
    }
}
