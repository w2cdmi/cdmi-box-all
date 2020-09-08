package com.huawei.sharedrive.uam.user.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.uam.user.domain.Admin;
import com.huawei.sharedrive.uam.user.service.AdminService;

public class MyUserFilter extends UserFilter
{
    @Autowired
    private AdminService adminService;

    private SsoManager ssoManager;

    public SsoManager getSsoManager() {
        return ssoManager;
    }

    public void setSsoManager(SsoManager ssoManager) {
        this.ssoManager = ssoManager;
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest req, ServletResponse resp, Object mappedValue)
    {
        if (isLoginRequest(req, resp)) {
            return true;
        }
        
        Subject subject = getSubject(req, resp);
        if (subject.getPrincipal() != null) {
            Admin admin = (Admin) subject.getPrincipal();
            if (isDisabledAdmin(admin.getId())) {
                subject.logout();
                subject.releaseRunAs();
                return false;
            }
            return true;
        }
        
        return false;
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse resp = (HttpServletResponse)response;

        //配置了单点登录服务器
        if(ssoManager != null && ssoManager.isSupported(req)) {
            return ssoManager.authentication(req, resp);
        }

        ((HttpServletResponse) response).setStatus(404);
        return super.onAccessDenied(request, response);
    }
    
    @Override
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException
    {
        if (this.appliedPaths == null || this.appliedPaths.isEmpty())
        {
            return;
        }
        String realPath;
        StringBuffer contextPath;
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
                if (contextPath.length() == 0 || contextPath.charAt(contextPath.length() - 1) != '/')
                {
                    contextPath.append('/');
                }
                HttpServletResponse rsp = (HttpServletResponse) response;
                contextPath.append("login");
                rsp.sendRedirect(contextPath.toString());
            }
        }
    }
    
    private boolean isDisabledAdmin(long adminId)
    {
        Admin selAdmin = adminService.get(adminId);
        return selAdmin != null && Admin.STATUS_DISABLE == (selAdmin.getStatus());
    }
}
