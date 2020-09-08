/*
 * Copyright Notice:
 *      Copyright  1998-2009, Huawei Technologies Co., Ltd.  ALL Rights Reserved.
 *
 *      Warning: This computer software sourcecode is protected by copyright law
 *      and international treaties. Unauthorized reproduction or distribution
 *      of this sourcecode, or any portion of it, may result in severe civil and
 *      criminal penalties, and will be prosecuted to the maximum extent
 *      possible under the law.
 */
package com.huawei.sharedrive.app.oauth2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.huawei.sharedrive.app.oauth2.service.UserTokenHelper;

import pw.cdmi.core.exception.InnerException;

/**
 * 
 * @author s90006125
 * 
 */
public class UserTokenInterceptor extends HandlerInterceptorAdapter
{
    @Autowired
    private UserTokenHelper userTokenHelper;
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
        Exception ex)
    {
        userTokenHelper.clearCurrentToken();
        try
        {
            super.afterCompletion(request, response, handler, ex);
        }
        catch (Exception e)
        {
            throw new InnerException(e);
        }
    }
}
