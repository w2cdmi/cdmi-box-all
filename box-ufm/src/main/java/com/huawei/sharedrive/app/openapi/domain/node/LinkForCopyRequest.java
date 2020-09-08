package com.huawei.sharedrive.app.openapi.domain.node;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.exception.InvalidParamException;

public class LinkForCopyRequest
{
    // 外链码
    private String linkCode;
    
    // 外链提取码签名字符串
    private String plainAccessCode;
    
    public LinkForCopyRequest()
    {
        
    }
    
    public LinkForCopyRequest(String linkCode, String plainAccessCode)
    {
        this.linkCode = linkCode;
        this.plainAccessCode = plainAccessCode;
    }
    
    public void checkParameter() throws InvalidParamException
    {
        if (StringUtils.isBlank(linkCode) || StringUtils.isBlank(plainAccessCode))
        {
            throw new InvalidParamException();
        }
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
    
}
