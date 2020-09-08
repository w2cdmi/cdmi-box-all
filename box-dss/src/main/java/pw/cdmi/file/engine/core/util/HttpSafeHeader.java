package pw.cdmi.file.engine.core.util;

import org.apache.commons.lang.StringUtils;

public final class HttpSafeHeader
{
   private HttpSafeHeader()
   {
   }
   
   private static final char[] UNSAFE_CHAR = new char[] {'\n', '\r'};
   
   /** 
    * 过滤不安全的特殊字符
    * 
    * @param value 将要设置到HTTP Response头部的值
    * @return 过滤后的值
    */
   public static String toSafeValue(String value)
   {
       if (StringUtils.isEmpty(value))
       {
           return value;
       }
       String safeValue = value;
       for (char c : UNSAFE_CHAR)
       {
           safeValue = StringUtils.remove(safeValue, c);
       }
       return safeValue;
   }
}