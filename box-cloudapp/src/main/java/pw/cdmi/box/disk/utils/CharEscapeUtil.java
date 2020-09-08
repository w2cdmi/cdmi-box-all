package pw.cdmi.box.disk.utils;

import org.apache.commons.lang.StringUtils;

public final class CharEscapeUtil
{
    private CharEscapeUtil()
    {
        
    }
    
    public static String transferString(String source)
    {
        if (StringUtils.isBlank(source))
        {
            return "";
        }
        return source.replaceAll("\\\\", "\\\\\\\\")
            .replaceAll("\"", "\\\\\"")
            .replaceAll("'", "\\\\'")
            .replaceAll("<", "\\\\<")
            .replaceAll(">", "\\\\>");
    }
    
}
