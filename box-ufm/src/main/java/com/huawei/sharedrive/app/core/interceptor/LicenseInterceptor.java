package com.huawei.sharedrive.app.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.huawei.sharedrive.app.system.service.LicenseChecker;

import pw.cdmi.core.exception.InnerException;
import pw.cdmi.core.utils.SpringContextUtil;

public class LicenseInterceptor extends HandlerInterceptorAdapter
{
    @Autowired
    private LicenseChecker licenseChecker;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        if (null == licenseChecker)
        {
            licenseChecker = (LicenseChecker) SpringContextUtil.getBean("licenseChecker");
        }
        licenseChecker.checkCurrentNode();
        try
        {
            return super.preHandle(request, response, handler);
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
    }
}
