package com.huawei.sharedrive.app.openapi.domain.user;

import java.io.Serializable;

/**
 * @author l90003768
 * 
 */
public class RestUserSearchRequest implements Serializable
{
    private static final long serialVersionUID = -4460630795057681581L;
    
    private String keyword;
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
}
