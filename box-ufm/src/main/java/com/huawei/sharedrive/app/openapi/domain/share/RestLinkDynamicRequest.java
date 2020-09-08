package com.huawei.sharedrive.app.openapi.domain.share;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class RestLinkDynamicRequest
{
    private String identity;
    
    
    private String linkCode;
    
    public String getIdentity()
    {
        return identity;
    }
    
    public void setIdentity(String identity)
    {
        this.identity = identity;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (StringUtils.isBlank(linkCode))
        {
            throw new InvalidParamException("linkCode is blank");
        }
        FilesCommonUtils.checkLinkCodeVaild(linkCode);
        
        if (identity == null)
        {
            throw new InvalidParamException("identity is blank");
        }
        identity = identity.trim();
        if (identity.length() == 0)
        {
            throw new InvalidParamException("identity is blank");
        }
        if (identity.length() > 255)
        {
            throw new InvalidParamException("identity exceed max length: " + identity.length());
        }
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
}
