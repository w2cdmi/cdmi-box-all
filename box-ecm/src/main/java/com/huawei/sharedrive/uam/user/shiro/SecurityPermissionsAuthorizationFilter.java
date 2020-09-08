package com.huawei.sharedrive.uam.user.shiro;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

public class SecurityPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter
{
    
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
        throws IOException
    {
        return super.isAccessAllowed(request, response, mappedValue);
    }
    
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException
    {
        return super.onAccessDenied(request, response);
    }
    
}
