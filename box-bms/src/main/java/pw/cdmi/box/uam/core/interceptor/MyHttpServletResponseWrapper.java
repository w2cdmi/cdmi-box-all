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
package pw.cdmi.box.uam.core.interceptor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * 
 * @author s90006125
 *
 */
public final class MyHttpServletResponseWrapper extends HttpServletResponseWrapper
{
    public MyHttpServletResponseWrapper(HttpServletResponse response)
    {
        super(response);
    }
    
    @Override
    public String encodeRedirectUrl(String url)
    {
        return url;
    }
    
    @Override
    public String encodeRedirectURL(String url)
    {
        return url;
    }
    
    @Override
    public String encodeUrl(String url)
    {
        return url;
    }
    
    @Override
    public String encodeURL(String url)
    {
        return url;
    }
}
