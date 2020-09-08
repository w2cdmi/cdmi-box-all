package pw.cdmi.box.uam.util.custom;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.uam.util.PropertiesUtils;

public final class ForgetPwdUtils
{
    private ForgetPwdUtils()
    {
    }
    
    public static boolean enableForget()
    {
        String value = PropertiesUtils.getProperty("custom.forgetPwd", "false");
        if (StringUtils.equalsIgnoreCase(value, "true"))
        {
            return true;
        }
        return false;
    }
    
}
