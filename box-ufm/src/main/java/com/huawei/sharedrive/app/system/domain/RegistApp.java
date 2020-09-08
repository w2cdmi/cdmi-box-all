/**
 * 
 */
package com.huawei.sharedrive.app.system.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @author s00108907
 * 
 */
public class RegistApp implements Serializable
{
    private static final long serialVersionUID = -8870118831128775123L;
    
    private Date createAt;
    
    private String description;
    
    private String id;
    
    private Date modifyAt;
    
    private String name;
    
    private String redirectUrl;
    
    private String secretKey;
    
    private int status;
    
    public Date getCreateAt()
    {
        if (createAt == null)
        {
            return null;
        }
        return (Date) createAt.clone();
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getId()
    {
        return id;
    }
    
    public Date getModifyAt()
    {
        if (modifyAt == null)
        {
            return null;
        }
        return (Date) modifyAt.clone();
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getRedirectUrl()
    {
        return redirectUrl;
    }
    
    public String getSecretKey()
    {
        return secretKey;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public void setCreateAt(Date createAt)
    {
        if (createAt == null)
        {
            this.createAt = null;
        }
        else
        {
            this.createAt = (Date) createAt.clone();
        }
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setId(String id)
    {
        this.id = id;
    }
    
    public void setModifyAt(Date modifyAt)
    {
        if (modifyAt == null)
        {
            this.modifyAt = null;
        }
        else
        {
            this.modifyAt = (Date) modifyAt.clone();
        }
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }
    
    public void setSecretKey(String secretKey)
    {
        this.secretKey = secretKey;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
}
