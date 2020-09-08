package pw.cdmi.box.disk.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import org.springframework.web.util.WebUtils;
import pw.cdmi.core.exception.InvalidParamException;


public final class RequestUtils
{
    private RequestUtils()
    {
        
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
            if (i >= ips.length)
            {
                throw new InvalidParamException("ips length is larger than ips.length");
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

    public static String getCorpId(HttpServletRequest request) {
        //先查看session中有没有保存corpId
        String corpId = null;

        try {
            HttpSession session = request.getSession();
            if(session != null) {
                corpId = (String)request.getSession().getAttribute("corpId");
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }

        if(corpId != null) {
            return corpId;
        }

        //查看URL中有没有携带corpId
        corpId = request.getParameter("weixinCorpId");
        if(corpId != null) {
            return corpId;
        }

        //检查Cookie中是否携带
        Cookie cookie = WebUtils.getCookie(request, "corpId");
        if(cookie != null) {
            return cookie.getValue();
        }

        return null;
    }
}
