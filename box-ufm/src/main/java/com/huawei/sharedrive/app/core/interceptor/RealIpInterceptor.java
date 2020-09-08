package com.huawei.sharedrive.app.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.huawei.sharedrive.app.security.service.SecurityMatrixHelper;
import com.huawei.sharedrive.app.utils.Constants;

import pw.cdmi.core.exception.InnerException;

public class RealIpInterceptor extends HandlerInterceptorAdapter
{
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        SecurityMatrixHelper.clear();
        String realIp = request.getHeader(Constants.HTTP_X_REAL_IP);
        if (StringUtils.isNotBlank(realIp))
        {
            SecurityMatrixHelper.setRealIP(realIp);
        }
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
