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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.cdmi.box.disk.utils.PropertiesUtils;

public class InterfaceFilter implements Filter
{
    private static final int HTTP_STATUS_METHOD_NOT_SUPPORTED = 405;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InterfaceFilter.class);
    
    private static final boolean V1_INTERFACE_SUPPORTED = Boolean.parseBoolean(PropertiesUtils.getProperty("v1.interface.supported",
        "false"));
    
    @Override
    public void destroy()
    {
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (!V1_INTERFACE_SUPPORTED)
        {
            LOGGER.warn("HTTP Method is not supported by this URL: {}", req.getRequestURL());
            resp.setStatus(HTTP_STATUS_METHOD_NOT_SUPPORTED);
        }
        else
        {
            chain.doFilter(request, response);
        }
        
    }
    
    @Override
    public void init(FilterConfig arg0) throws ServletException
    {
    }
}
