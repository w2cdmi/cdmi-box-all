package pw.cdmi.box.uam.user.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

public class DataViewerPermissionsFilter extends PermissionsAuthorizationFilter
{
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
        throws IOException
    {
        
        Subject subject = getSubject(request, response);
        
        String[] perms = (String[]) mappedValue;
        boolean isPermitted = true;
        if (perms != null && perms.length > 0)
        {
            if (perms.length == 1)
            {
                if (!subject.isPermitted(perms[0]))
                {
                    isPermitted = false;
                }
            }
            else if (!subject.isPermittedAll(perms))
            {
                isPermitted = false;
            }
            
        }
        return isPermitted;
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
