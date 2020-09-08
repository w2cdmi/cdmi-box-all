package com.huawei.sharedrive.app.oauth2.domain;

import java.util.Date;

/**
 * @author c90006080
 * @version 1.0
 * @created 26-十一月-2013 17:21:48
 */
public class AuthClient
{
    
    private Date createdAt;
    
    /**
     * 客户端描述
     */
    private String desc;
    
    /**
     * 客户端描述
     */
    private String description;
    
    /**
     * 客户端ID
     */
    private String id;
    
    private Date modifiedAt;
    
    /**
     * 客户端应用名称， 全局唯一
     */
    private String name;
    
    /**
     * 客户端应用密码
     */
    private String password;
    
    /**
     * Auth2授权过程用到的重定向地址
     */
    private String redirectUrl;
    
    private String status;
    
    /**
     * @return the createdAt
     */
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    /**
     * @return the desc
     */
    public String getDesc()
    {
        return desc;
    }
    
    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }
    
    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }
    
    /**
     * @return the modifiedAt
     */
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }
    
    /**
     * @return the redirectUrl
     */
    public String getRedirectUrl()
    {
        return redirectUrl;
    }
    
    /**
     * @return the status
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt == null)
        {
            this.createdAt = null;
        }
        else
        {
            this.createdAt = (Date) createdAt.clone();
        }
    }
    
    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    
    /**
     * @param description the description to set
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }
    
    /**
     * @param modifiedAt the modifiedAt to set
     */
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) modifiedAt.clone();
        }
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    /**
     * @param redirectUrl the redirectUrl to set
     */
    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }
    
    /**
     * @param status the status to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }
    
}