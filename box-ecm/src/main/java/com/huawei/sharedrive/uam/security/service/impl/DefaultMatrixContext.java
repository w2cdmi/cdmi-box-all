package com.huawei.sharedrive.uam.security.service.impl;

import javax.servlet.ServletRequest;

import org.aspectj.weaver.Dump.INode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.huawei.sharedrive.uam.security.domain.SecurityPrincipal;
import com.huawei.sharedrive.uam.security.service.MatrixContext;

@Component
@Scope("prototype")
public class DefaultMatrixContext implements MatrixContext
{
    
    private int accesOnwerId;
    
    private INode accessResource;
    
    private String permission;
    
    private SecurityPrincipal principal;
    
    @Override
    public void setSecurityPrincipal(SecurityPrincipal principal)
    {
        this.principal = principal;
    }
    
    @Override
    public SecurityPrincipal getSecurityPrincipal()
    {
        return this.principal;
    }
    
    public int getAccesOnwerId()
    {
        return accesOnwerId;
    }
    
    public void setAccesOnwerId(int accesOnwerId)
    {
        this.accesOnwerId = accesOnwerId;
    }
    
    @Override
    public INode getAccessResource()
    {
        return accessResource;
    }
    
    @Override
    public void setAccessResource(INode accessResource)
    {
        this.accessResource = accessResource;
    }
    
    @Override
    public String getPermission()
    {
        return permission;
    }
    
    @Override
    public void setPermission(String permission)
    {
        this.permission = permission;
    }
    
    @Override
    public void setRequest(ServletRequest request)
    {
    }
}
