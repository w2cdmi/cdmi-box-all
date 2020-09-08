package com.huawei.sharedrive.uam.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public final class RequestUtils
{
    private RequestUtils()
    {
        
    }
    
    private final static String X_REGION_IP = "x-region-ip";
    
    /**
     * if want to use this function, need add parameter to nginx: proxy_set_header
     * x-region-ip $remote_addr;
     * 
     * @param request
     * @return
     */
    public static String getLoginRegionIp(HttpServletRequest request)
    {
        return request.getHeader(X_REGION_IP);
    }
    
    /**
     * 
     * @param request
     * @return
     */
    public static String getRealIP(HttpServletRequest request)
    {
        String ipAddress = request.getHeader("x-real-ip");
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getRemoteAddr();
        }
        
        if (ipAddress != null && ipAddress.indexOf(",") != -1)
        {
            String[] ips = ipAddress.split(",");
            int i = 0;
            while (i < ips.length && "unknown".equalsIgnoreCase(ips[i]))
            {
                i++;
            }
            ipAddress = ips[i];
        }
        return ipAddress;
    }
    
    /**
     * 
     * @param request
     * @return
     */
    public static String getProxyIP(HttpServletRequest request)
    {
        String ipAddress = request.getHeader("X-Proxy-IP");
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getHeader("x-forwarded-for");
        }
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getHeader("x-real-ip");
        }
        if (StringUtils.isBlank(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
        {
            ipAddress = request.getRemoteAddr();
        }
        
        if (ipAddress != null && ipAddress.indexOf(",") != -1)
        {
            String[] ips = ipAddress.split(",");
            int i = ips.length - 1;
            while (i > -1 && "unknown".equalsIgnoreCase(ips[i]))
            {
                i--;
            }
            ipAddress = ips[i];
        }
        return ipAddress;
    }
}
