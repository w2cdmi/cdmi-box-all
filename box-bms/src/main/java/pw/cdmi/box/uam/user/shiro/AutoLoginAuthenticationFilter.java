package pw.cdmi.box.uam.user.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.UserFilter;

public class AutoLoginAuthenticationFilter extends UserFilter
{
    
    @Override
    protected boolean isAccessAllowed(ServletRequest req, ServletResponse resp, Object mappedValue)
    {
        return false;
    }
    
    /**
     * 
     * @param src
     * @param priNtlmManager
     * @param sessionId
     * @param response
     * @throws IOException
     */
    
}
