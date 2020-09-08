package com.huawei.sharedrive.app.mirror.domain;

import java.util.Date;
import java.util.List;

/**
 * 复制策略
 * 
 * @author c00287749
 * 
 */
public class CopyPolicy
{
    private String appId;
    
    // 复制类型，1：表示容灾，10：表示就近访问
    private int copyType;
    
    private Date createdAt;
    
    private String description;
    
    private String exeEndAt;
    
    private String exeStartAt;
    
    // 执行类型，0表示及时执行，1表示定时执行
    private int exeType;
    
    private int id;
    
    private List<CopyPolicySiteInfo> lstCopyPolicyDataSiteInfo;
    
    private Date modifiedAt;
    
    private String name;
    
    private int state;
    
    // 0：全APP数据复制，1：部分用户复制
    private int type;
    
    private CopyPolicyUserConfig userConfig;
    
    public String getAppId()
    {
        return appId;
    }
    
    public int getCopyType()
    {
        return copyType;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getExeEndAt()
    {
        return exeEndAt;
    }
    
    public String getExeStartAt()
    {
        return exeStartAt;
    }
    
    public int getExeType()
    {
        return exeType;
    }
    
    public int getId()
    {
        return id;
    }
    
    public List<CopyPolicySiteInfo> getLstCopyPolicyDataSiteInfo()
    {
        return lstCopyPolicyDataSiteInfo;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getState()
    {
        return state;
    }
    
    public int getType()
    {
        return type;
    }
    
    public CopyPolicyUserConfig getUserConfig()
    {
        return userConfig;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public void setCopyType(int copyType)
    {
        this.copyType = copyType;
    }
    
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
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setExeEndAt(String exeEndAt)
    {
        this.exeEndAt = exeEndAt;
    }
    
    public void setExeStartAt(String exeStartAt)
    {
        this.exeStartAt = exeStartAt;
    }
    
    public void setExeType(int exeType)
    {
        this.exeType = exeType;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public void setLstCopyPolicyDataSiteInfo(List<CopyPolicySiteInfo> lstCopyPolicyDataSiteInfo)
    {
        this.lstCopyPolicyDataSiteInfo = lstCopyPolicyDataSiteInfo;
    }
    
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
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public void setUserConfig(CopyPolicyUserConfig userConfig)
    {
        this.userConfig = userConfig;
    }
    
}
