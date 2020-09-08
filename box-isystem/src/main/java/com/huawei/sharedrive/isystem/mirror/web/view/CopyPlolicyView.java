package com.huawei.sharedrive.isystem.mirror.web.view;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.isystem.mirror.domain.CopyPolicy;

public class CopyPlolicyView implements Serializable
{
    private static final long serialVersionUID = 7706835213250108688L;
    
    private int id;
    
    private String name;
    
    private String description;
    
    private String appId;
    
    private int copyType;
    
    private int state;
    
    private Date createdAt;
    
    private List<CopyPolicySiteInfoView> siteViews;
    
    public CopyPlolicyView()
    {
        
    }
    
    public CopyPlolicyView(CopyPolicy copyPolicy)
    {
        this.id = copyPolicy.getId();
        this.name = copyPolicy.getName();
        this.description = copyPolicy.getDescription();
        this.appId = copyPolicy.getAppId();
        this.state = copyPolicy.getState();
        this.createdAt = copyPolicy.getCreatedAt();
        this.copyType = copyPolicy.getCopyType();
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public int getState()
    {
        return state;
    }
    
    public void setState(int state)
    {
        this.state = state;
    }
    
    public int getCopyType()
    {
        return copyType;
    }
    
    public void setCopyType(int copyType)
    {
        this.copyType = copyType;
    }
    
    public Date getCreatedAt()
    {
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        this.createdAt = createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public List<CopyPolicySiteInfoView> getSiteViews()
    {
        return siteViews;
    }
    
    public void setSiteViews(List<CopyPolicySiteInfoView> siteViews)
    {
        this.siteViews = siteViews;
    }
    
}
