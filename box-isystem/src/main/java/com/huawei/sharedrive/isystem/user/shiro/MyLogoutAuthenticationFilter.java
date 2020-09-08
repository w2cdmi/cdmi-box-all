package com.huawei.sharedrive.isystem.user.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huawei.sharedrive.isystem.syslog.domain.UserLogType;
import com.huawei.sharedrive.isystem.syslog.service.UserLogService;
import com.huawei.sharedrive.isystem.user.domain.Admin;

import pw.cdmi.common.log.UserLog;

public class MyLogoutAuthenticationFilter extends LogoutFilter
{
    private static Logger logger = LoggerFactory.getLogger(MyLogoutAuthenticationFilter.class);
    
    @Autowired
    private UserLogService userLogService;
    
    private void clearHttpSession(HttpSession session)
    {
        if (null != session)
        {
            try
            {
//                session.setMaxInactiveInterval(1);
                session.invalidate();
            }
            catch (Exception e)
            {
                logger.warn("Error to clear http session", e);
            }
        }
    }
    
    /**
     * 跳转登录页面
     * 
     * @param response
     * @param req
     * @throws IOException
     */
    private void jumpToLogin(ServletResponse response, HttpServletRequest req) throws IOException
    {
        StringBuffer contextPath = new StringBuffer();
        contextPath.append(req.getContextPath());
        if (contextPath.length() > 0
            && contextPath.toString().charAt(contextPath.toString().length() - 1) != '/')
        {
            contextPath.append('/');
        }
        contextPath.append(ShiroConstants.LOGIN_PAGE);
        HttpServletResponse rsp = (HttpServletResponse) response;
        
        rsp.sendRedirect(contextPath.toString());
    }
    
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws IOException
    {
        HttpServletRequest req = (HttpServletRequest) request;
        if (req.getSession(false) == null)
        {
            jumpToLogin(response, req);
            return false;
            
        }
        clearHttpSession(req.getSession(false));
        
        Subject subject = getSubject(request, response);
        // 鐧诲嚭鍚庯紝鑷姩璺宠浆鑷砽ogin鐧诲綍椤甸潰
        UserLog userLog = userLogService.initUserLog(req, UserLogType.LOGOUT, null);
        Admin admin = (Admin) subject.getPrincipal();
        
        try
        {
            subject.logout();
            subject.releaseRunAs();
            userLog.setDetail(UserLogType.LOGOUT.getDetails(null));
            userLog.setLevel(UserLogService.SUCCESS_LEVEL);
        }
        catch (SessionException ise)
        {
            logger.debug("Encountered session exception during logout.  This can generally safely be ignored.",
                ise);
            userLog.setLevel(UserLogService.FIAL_LEVEL);
        }
        if (admin != null)
        {
            userLog.setLoginName(admin.getLoginName());
            userLogService.saveUserLog(userLog);
        }
        
        // issueRedirect(request, response, redirectUrl);
        jumpToLogin(response, req);
        return false;
    }
}
