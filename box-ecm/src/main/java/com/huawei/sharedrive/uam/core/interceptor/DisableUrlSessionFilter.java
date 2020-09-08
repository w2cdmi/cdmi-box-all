package com.huawei.sharedrive.uam.core.interceptor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

public class DisableUrlSessionFilter implements Filter
{
    /**
     * Filters requests to disable URL-based session identifiers.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        // skip non-http requests
        if (!(request instanceof HttpServletRequest))
        {
            chain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // clear session if session id in URL
        if (httpRequest.isRequestedSessionIdFromURL())
        {
            HttpSession session = httpRequest.getSession();
            if (session != null)
            {
                session.invalidate();
            }
        }
        
        // wrap response to remove URL encoding
        HttpServletResponseWrapper wrappedResponse = new MyHttpServletResponseWrapper(httpResponse);
        
        // process next request in chain
        chain.doFilter(request, wrappedResponse);
    }
    
    @Override
    public void init(FilterConfig config) throws ServletException
    {
    }
    
    @Override
    public void destroy()
    {
    }
}