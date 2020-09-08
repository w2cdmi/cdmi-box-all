package com.huawei.sharedrive.isystem.core.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CSRFHandler implements Filter
{
    private final static String PREFIX_HTTP = "http://";
    
    private final static String PREFIX_HTTPS = "https://";
    
    /**
     * 对请求进行过滤操作
     * 
     * @param req request对象
     * @param resp response对象
     * @param chain 过滤链
     * @throws IOException IO异常
     * @throws ServletException Servlet异常
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
        ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        String referer = request.getHeader("REFERER");
        String host = request.getHeader("Host");
        
        if (referer == null)
        {
            chain.doFilter(request, response);
        }
        else if (referer.startsWith(PREFIX_HTTP + host) || referer.startsWith(PREFIX_HTTPS + host))
        {
            chain.doFilter(request, response);
        }
        else if (request.getRequestURI().indexOf("syscommon/initset") != -1)
        {
            chain.doFilter(request, response);
        }
        else if (request.getRequestURI().indexOf("syscommon/reset") != -1)
        {
            chain.doFilter(request, response);
        }
        else
        {
            response.sendError(401);
        }
    }
    
    /**
     * 过滤器初始化方法
     * 
     * @param filterConfig 初始化参数
     * @throws ServletException servlet异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }
    
    /**
     * 过滤器销毁时执行操作
     */
    @Override
    public void destroy()
    {
    }
}
