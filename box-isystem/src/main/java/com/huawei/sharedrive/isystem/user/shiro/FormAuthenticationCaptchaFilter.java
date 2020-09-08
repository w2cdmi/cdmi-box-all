/**
 * 
 */
package com.huawei.sharedrive.isystem.user.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;

import pw.cdmi.core.utils.IpUtils;

/**
 * 扩展原始默认的过滤
 * 
 * @author s00108907
 * 
 */
public class FormAuthenticationCaptchaFilter extends FormAuthenticationFilter
{
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response)
    {
        String username = getUsername(request);
        String password = getPassword(request);
        String captcha = request.getParameter("captcha");
        String capKey = request.getParameter("key");
        boolean rememberMe = isRememberMe(request);
        String host = IpUtils.getClientAddress((HttpServletRequest) request);
        
        UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(username,
            password == null ? null : password.toCharArray(), rememberMe, host);
        token.setNtlm(false);
        token.setObjectSid("");
        token.setCaptcha(captcha);
        
        token.setCapKey(capKey);
        return token;
    }
    
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException
    {
        String loginUrl = getLoginUrl();
        HttpServletResponse rsp = (HttpServletResponse) response;
        rsp.sendRedirect(loginUrl);
    }
    
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
        ServletResponse response) throws IOException
    {
        String fallbackUrl = getSuccessUrl();
        String successUrl = null;
        boolean contextRelative = true;
        SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(request);
        if (savedRequest != null && savedRequest.getMethod().equalsIgnoreCase(AccessControlFilter.GET_METHOD))
        {
            successUrl = savedRequest.getRequestUrl();
            contextRelative = false;
        }
        if (successUrl == null)
        {
            successUrl = fallbackUrl;
        }
        
        if (successUrl == null)
        {
            throw new IllegalStateException("Success URL not available via saved request or via the "
                + "successUrlFallback method parameter. One of these must be non-null for "
                + "issueSuccessRedirect() to work.");
        }
        
        MyWebUtils.issueRedirect(request, response, successUrl, null, contextRelative);
        
        return false;
    }
}
