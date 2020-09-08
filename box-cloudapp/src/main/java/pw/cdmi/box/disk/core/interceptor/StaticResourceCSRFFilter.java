package pw.cdmi.box.disk.core.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.utils.PropertiesUtils;

public class StaticResourceCSRFFilter implements Filter
{
    private final static String SPLIT_CHAR = ",";
    
    private final static String PREFIX_HTTP = "http://";
    
    private final static String PREFIX_HTTPS = "https://";
    
    private static Logger logger = LoggerFactory.getLogger(StaticResourceCSRFFilter.class);
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
        ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        
        String referer = request.getHeader("Referer");
        String allowedUrl = PropertiesUtils.getProperty("security.csrf.allowedurl");
        if (StringUtils.isNotBlank(allowedUrl) && StringUtils.isNotBlank(referer))
        {
            String refererPrefix = checkRefererRule(referer, response);
            if (StringUtils.isBlank(refererPrefix))
            {
                response.sendError(401);
                return;
            }
            String[] spilitUrl = allowedUrl.split(SPLIT_CHAR);
            for (String str : spilitUrl)
            {
                if (refererPrefix.indexOf(str) == 0)
                {
                    chain.doFilter(request, response);
                }
                else
                {
                    response.sendError(401);
                }
            }
            
        }
        if (referer == null)
        {
            chain.doFilter(request, response);
        }
    }
    
    private String checkRefererRule(String referer, HttpServletResponse response)
    {
        String refererPrefix = null;
        if (referer.startsWith(PREFIX_HTTP))
        {
            refererPrefix = referer.substring(PREFIX_HTTP.length());
        }
        else if (referer.startsWith(PREFIX_HTTPS))
        {
            refererPrefix = referer.substring(PREFIX_HTTPS.length());
        }
        else
        {
            try
            {
                response.sendError(401);
            }
            catch (IOException e)
            {
                logger.debug("", e);
            }
        }
        return refererPrefix;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }
    
    @Override
    public void destroy()
    {
    }
}
