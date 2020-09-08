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
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import com.huawei.sharedrive.isystem.user.shiro.ShiroConstants;

public class DisableUrlSessionFilter implements Filter
{
    private static final class HttpServletResponseWrapperExt extends HttpServletResponseWrapper
    {
        private HttpServletResponseWrapperExt(HttpServletResponse response)
        {
            super(response);
        }
        
        @Override
        public String encodeRedirectUrl(String url)
        {
            return url;
        }
        
        @Override
        public String encodeRedirectURL(String url)
        {
            return url;
        }
        
        @Override
        public String encodeUrl(String url)
        {
            return url;
        }
        
        @Override
        public String encodeURL(String url)
        {
            return url;
        }
    }
    
    /**
     * Filters requests to disable URL-based session identifiers.
     */
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
        HttpServletResponseWrapperExt wrappedResponse = new DisableUrlSessionFilter.HttpServletResponseWrapperExt(httpResponse);
        HttpServletRequest req = (HttpServletRequest) request;
        if ("get".equalsIgnoreCase(req.getMethod())
            && req.getRequestURI().endsWith('/' + ShiroConstants.LOGIN_PAGE))
        {
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }
        // process next request in chain
        chain.doFilter(request, wrappedResponse);
    }
    
    public void init(FilterConfig config) throws ServletException
    {
    }
    
    public void destroy()
    {
    }
}