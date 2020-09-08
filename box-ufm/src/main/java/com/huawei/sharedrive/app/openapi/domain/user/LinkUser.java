package com.huawei.sharedrive.app.openapi.domain.user;

/**
 * 外链用户
 * @author l90003768
 *
 */
public class LinkUser
{
    private String linkCode;
    
    /**
     * 签名后的提取码
     */
    private String plainAccessCode;

    public String getLinkCode()
    {
        return linkCode;
    }

    /**
     * 获取签名后的提取码
     * @return
     */
    public String getPlainAccessCode()
    {
        return plainAccessCode;
    }

    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }

    /**
     * 设置签名后的提取码
     * @param plainAccessCode
     */
    public void setPlainAccessCode(String plainAccessCode)
    {
        this.plainAccessCode = plainAccessCode;
    }
    
}
