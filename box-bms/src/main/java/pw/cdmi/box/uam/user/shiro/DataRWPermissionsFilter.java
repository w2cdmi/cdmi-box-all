package pw.cdmi.box.uam.user.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

public class DataRWPermissionsFilter extends PermissionsAuthorizationFilter
{
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
        throws IOException
    {
        return false;
    }
    
    protected String[] buildPermissions(ServletRequest request)
    {
        String[] perms = new String[1];
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getServletPath();
        perms[0] = path;
        return perms;
    }
}
