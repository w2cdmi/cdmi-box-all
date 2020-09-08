package pw.cdmi.box.disk.security.service;

public final class SecurityMatrixHelper
{
    private static final ThreadLocal<String> ACCESS_REAL_IP = new ThreadLocal<String>();
    
    private SecurityMatrixHelper()
    {
        
    }
    
    public static String getRealIP()
    {
        return ACCESS_REAL_IP.get();
    }
    
    /**
     * 清理
     */
    public static void clear()
    {
        ACCESS_REAL_IP.remove();
    }
    
    public static void setRealIP(String realIp)
    {
        ACCESS_REAL_IP.set(realIp);
    }
}
