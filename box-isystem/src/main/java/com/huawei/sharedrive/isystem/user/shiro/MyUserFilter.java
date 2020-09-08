/**
 * 
 */
package com.huawei.sharedrive.isystem.user.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.UserFilter;

/**
 * @author d00199602
 * 
 */
public class MyUserFilter extends UserFilter
{
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception
    {
        ((HttpServletResponse) response).setStatus(404);
        return super.onAccessDenied(request, response);
    }
    
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException
    {
        if (this.appliedPaths == null || this.appliedPaths.isEmpty())
        {
            return;
        }
        String realPath = null;
        StringBuffer contextPath = null;
        for (String path : this.appliedPaths.keySet())
        {
            if ("/**".equals(path))
            {
                realPath = "/";
            }
            else
            {
                realPath = path;
            }
            
            if (pathsMatch(realPath, request))
            {
                HttpServletRequest req = (HttpServletRequest) request;
                contextPath = new StringBuffer();
                contextPath.append(req.getContextPath());
                if(contextPath.length() == 0)
                {
                    contextPath.append('/');
                }
                else if (contextPath.toString().charAt(contextPath.toString().length() - 1) != '/')
                {
                    contextPath.append('/');
                }
                HttpServletResponse rsp = (HttpServletResponse) response;
                contextPath.append("login");
                rsp.sendRedirect(contextPath.toString());
            }
        }
    }
}
