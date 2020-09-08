package pw.cdmi.box.disk.user.service;

import pw.cdmi.box.disk.system.service.SecurityService;
import pw.cdmi.common.domain.SecurityConfig;
import pw.cdmi.core.utils.SpringContextUtil;

public final class LoginRule
{
    private LoginRule()
    {
        
    }
    
    public static boolean forgetPwd()
    {
        
        Object obj = SpringContextUtil.getBean("securityService");
        SecurityService securityService = (SecurityService) obj;
        SecurityConfig securityConfig = securityService.getSecurityConfig();
        if (null == securityConfig)
        {
            return false;
        }
        if (null == securityConfig.getForgetPwd())
        {
            return false;
        }
        return securityConfig.getForgetPwd();
    }
    
}
