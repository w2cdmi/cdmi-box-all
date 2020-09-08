package com.huawei.sharedrive.uam.security.service;

import javax.servlet.ServletRequest;

import org.aspectj.weaver.Dump.INode;

import com.huawei.sharedrive.uam.security.domain.SecurityPrincipal;

public interface MatrixContext
{
    
    INode getAccessResource();
    
    void setAccessResource(INode accessResource);
    
    String getPermission();
    
    void setPermission(String permission);
    
    void setRequest(ServletRequest request);
    
    void setSecurityPrincipal(SecurityPrincipal principal);
    
    SecurityPrincipal getSecurityPrincipal();
}