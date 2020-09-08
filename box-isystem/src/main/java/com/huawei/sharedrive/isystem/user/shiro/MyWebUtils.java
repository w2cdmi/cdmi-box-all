/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.huawei.sharedrive.isystem.user.shiro;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.util.WebUtils;

import com.huawei.sharedrive.isystem.user.domain.WebUtilsBean;

/**
 * Simple utility class for operations used across multiple class hierarchies in the web
 * framework code.
 * <p/>
 * Some methods in this class were copied from the Spring Framework so we didn't have to
 * re-invent the wheel, and in these cases, we have retained all license, copyright and
 * author information.
 * 
 * @since 0.9
 */
@SuppressWarnings("rawtypes")
public final class MyWebUtils extends WebUtils
{
    
    private MyWebUtils()
    {
        
    }
    
    /**
     * Redirects the current request to a new URL based on the given parameters and
     * default values for unspecified parameters.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * @param url the URL to redirect the user to.
     * @throws java.io.IOException if thrown by response methods.
     */
    public static void issueRedirect(ServletRequest request, ServletResponse response, String url)
        throws IOException
    {
        issueRedirect(request, response, url, null, true, true);
    }
    
    /**
     * Redirects the current request to a new URL based on the given parameters and
     * default values for unspecified parameters.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * @param url the URL to redirect the user to.
     * @param queryParams a map of parameters that should be set as request parameters for
     *            the new request.
     * @throws java.io.IOException if thrown by response methods.
     */
    public static void issueRedirect(ServletRequest request, ServletResponse response, String url,
        Map queryParams) throws IOException
    {
        issueRedirect(request, response, url, queryParams, true, true);
    }
    
    /**
     * Redirects the current request to a new URL based on the given parameters and
     * default values for unspecified parameters.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * @param url the URL to redirect the user to.
     * @param queryParams a map of parameters that should be set as request parameters for
     *            the new request.
     * @param contextRelative true if the URL is relative to the servlet context path, or
     *            false if the URL is absolute.
     * @throws java.io.IOException if thrown by response methods.
     */
    public static void issueRedirect(ServletRequest request, ServletResponse response, String url,
        Map queryParams, boolean contextRelative) throws IOException
    {
        WebUtilsBean wb = new WebUtilsBean();
        wb.setContextRelative(contextRelative);
        wb.setHttp10Compatible(true);
        wb.setUrl(url);
        issueRedirect(request, response, queryParams, wb);
    }
    
    /**
     * Redirects the current request to a new URL based on the given parameters.
     * 
     * @param request the servlet request.
     * @param response the servlet response.
     * @param url the URL to redirect the user to.
     * @param queryParams a map of parameters that should be set as request parameters for
     *            the new request.
     * @param contextRelative true if the URL is relative to the servlet context path, or
     *            false if the URL is absolute.
     * @param http10Compatible whether to stay compatible with HTTP 1.0 clients.
     * @throws java.io.IOException if thrown by response methods.
     */
    public static void issueRedirect(ServletRequest request, ServletResponse response, Map queryParams,
        WebUtilsBean wb) throws IOException
    {
        String url = wb.getUrl();
        boolean contextRelative = wb.isContextRelative();
        boolean http10Compatible = wb.isHttp10Compatible();
        MyRedirectView view = new MyRedirectView(url, contextRelative, http10Compatible);
        view.renderMergedOutputModel(queryParams, toHttp(request), toHttp(response));
    }
    
}
