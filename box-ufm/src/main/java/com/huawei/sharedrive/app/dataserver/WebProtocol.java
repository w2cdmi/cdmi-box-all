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
package com.huawei.sharedrive.app.dataserver;

/**
 * 
 * @author s90006125
 * 
 */
public enum WebProtocol
{
    HTTP("http", "http://"), HTTPS("https", "https://");
    
    private String scheme;
    
    private String urlPrefix;
    
    private WebProtocol(String scheme, String urlPrefix)
    {
        this.scheme = scheme;
        this.urlPrefix = urlPrefix;
    }
    
    public static WebProtocol parseByScheme(String scheme)
    {
        for (WebProtocol p : WebProtocol.values())
        {
            if (p.getScheme().equalsIgnoreCase(scheme))
            {
                return p;
            }
        }
        
        return null;
    }
    
    public String getScheme()
    {
        return scheme;
    }
    
    public String getUrlPrefix()
    {
        return urlPrefix;
    }
}
