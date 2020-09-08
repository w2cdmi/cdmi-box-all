package com.huawei.sharedrive.uam.user.shiro;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyRedirectView
{
    
    public static final String DEFAULT_ENCODING_SCHEME = "UTF-8";
    
    private String url;
    
    private boolean contextRelative = false;
    
    private boolean http10Compatible = true;
    
    private String encodingScheme = DEFAULT_ENCODING_SCHEME;
    
    public MyRedirectView()
    {
    }
    
    public MyRedirectView(String url)
    {
        setUrl(url);
    }
    
    public MyRedirectView(String url, boolean contextRelative)
    {
        this(url);
        this.contextRelative = contextRelative;
    }
    
    public MyRedirectView(String url, boolean contextRelative, boolean http10Compatible)
    {
        this(url);
        this.contextRelative = contextRelative;
        this.http10Compatible = http10Compatible;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
    public void setContextRelative(boolean contextRelative)
    {
        this.contextRelative = contextRelative;
    }
    
    public void setHttp10Compatible(boolean http10Compatible)
    {
        this.http10Compatible = http10Compatible;
    }
    
    public void setEncodingScheme(String encodingScheme)
    {
        this.encodingScheme = encodingScheme;
    }
    
    public final void renderMergedOutputModel(@SuppressWarnings("rawtypes") Map model,
        HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        StringBuilder targetUrl = new StringBuilder();
        if (this.contextRelative && getUrl().charAt(0) == '/')
        {
            targetUrl.append(request.getContextPath());
        }
        targetUrl.append(getUrl());
        appendQueryProperties(targetUrl, model, this.encodingScheme);
        
        sendRedirect(request, response, targetUrl.toString(), this.http10Compatible);
    }
    
    @SuppressWarnings("rawtypes")
    protected void appendQueryProperties(StringBuilder targetUrl, Map model, String encodingScheme)
        throws UnsupportedEncodingException
    {
        
        // Extract anchor fragment, if any.
        // The following code does not use JDK 1.4's StringBuffer.indexOf(String)
        // method to retain JDK 1.3 compatibility.
        String fragment = null;
        int anchorIndex = targetUrl.toString().indexOf('#');
        if (anchorIndex > -1)
        {
            fragment = targetUrl.substring(anchorIndex);
            targetUrl.delete(anchorIndex, targetUrl.length());
        }
        
        // If there aren't already some parameters, we need a "?".
        boolean first = (getUrl().indexOf('?') < 0);
        Map queryProps = queryProperties(model);
        Map.Entry entry;
        String encodedKey;
        String encodedValue;
        if (queryProps != null)
        {
            for (Object o : queryProps.entrySet())
            {
                if (first)
                {
                    targetUrl.append('?');
                    first = false;
                }
                else
                {
                    targetUrl.append('&');
                }
                entry = (Map.Entry) o;
                encodedKey = urlEncode(entry.getKey().toString(), encodingScheme);
                encodedValue = (entry.getValue() != null ? urlEncode(entry.getValue().toString(),
                    encodingScheme) : "");
                targetUrl.append(encodedKey).append('=').append(encodedValue);
            }
        }
        
        // Append anchor fragment, if any, to end of URL.
        if (fragment != null)
        {
            targetUrl.append(fragment);
        }
    }
    
    protected String urlEncode(String input, String encodingScheme) throws UnsupportedEncodingException
    {
        return URLEncoder.encode(input, encodingScheme);
    }
    
    @SuppressWarnings("rawtypes")
    protected Map queryProperties(Map model)
    {
        return model;
    }
    
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String targetUrl,
        boolean http10Compatible) throws IOException
    {
        if (http10Compatible)
        {
            // Always send status code 302.
            response.sendRedirect(targetUrl);
        }
        else
        {
            // Correct HTTP status code is 303, in particular for POST requests.
            response.setStatus(303);
            response.setHeader("Location", response.encodeRedirectURL(targetUrl));
        }
    }
    
}
