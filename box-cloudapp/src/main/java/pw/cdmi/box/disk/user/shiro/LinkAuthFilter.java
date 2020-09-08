package pw.cdmi.box.disk.user.shiro;

import org.apache.shiro.web.filter.authc.UserFilter;
import pw.cdmi.box.disk.share.service.LinkService;
import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.box.disk.user.service.UserLoginService;
import pw.cdmi.box.disk.user.service.UserService;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.core.utils.SpringContextUtil;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LinkAuthFilter extends UserFilter
{
    private SecurityService securityService;
    
    private UserLoginService userLoginService;
    
    private UserService userService;
    
    public SecurityService getSecurityService()
    {
        return securityService;
    }
    
    public UserLoginService getUserLoginService()
    {
        return userLoginService;
    }
    
    public UserService getUserService()
    {
        return userService;
    }
    
    public void setSecurityService(SecurityService securityService)
    {
        this.securityService = securityService;
    }
    
    public void setUserLoginService(UserLoginService userLoginService)
    {
        this.userLoginService = userLoginService;
    }
    
    public void setUserService(UserService userService)
    {
        this.userService = userService;
    }
    
    @Override
    protected boolean isAccessAllowed(ServletRequest req, ServletResponse resp, Object mappedValue)
    {
    	LinkService linkService = (LinkService) SpringContextUtil.getBean("linkService");
        SecurityConfig securityConfig = securityService.getSecurityConfig();
        String reqProtocol = securityConfig.getProtocolType();
        HttpServletRequest request = (HttpServletRequest) req;
        request.getSession().setAttribute("reqProtocol", reqProtocol);
        
        if (linkService.allowAnonyAccess())
        {
            return allowAnony(req, resp, mappedValue);
        }
        return denyAnony(req, resp, mappedValue);
    }
    
    /**
     * @param req
     * @param resp
     * @param mappedValue
     * @return
     */
    boolean allowAnony(ServletRequest req, ServletResponse resp, Object mappedValue)
    {
        return true;
    }
    
    /**
     * @param req
     * @param resp
     * @param mappedValue
     * @return
     */
    boolean denyAnony(ServletRequest req, ServletResponse resp, Object mappedValue)
    {
        AutoLoginAuthenticationFilter autoFilter = (AutoLoginAuthenticationFilter) SpringContextUtil.getBean("autoLoginFilter");
        return autoFilter.isAccessAllowed(req, resp, mappedValue);
    }
    
}
