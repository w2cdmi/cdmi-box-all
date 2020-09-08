package com.huawei.sharedrive.isystem.mirror.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 复制策略
 * 
 * @author c00287749
 * 
 */
public class CopyPolicy implements Serializable
{
    private static final long serialVersionUID = -5170970395926944754L;

    private int id;
    
    private String name;
    
    private String description;
    
    private String appId;
    
    // 0：全APP数据复制，1：部分用户复制
    private int type;
    
    private int state;
    
    // 复制类型，1：表示容灾，10：表示就近访问
    private int copyType;
    
    // 执行类型，0表示及时执行，1表示定时执行
    private int exeType;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    // 10:02:00 12:00:00
    private String exeStartAt;
    
    private String exeEndAt;
    
    private List<CopyPolicySiteInfo> lstCopyPolicyDataSiteInfo;
    
    private CopyPolicyUserConfig userConfig;
    
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
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
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
    
    public int getExeType()
    {
        return exeType;
    }
    
    public void setExeType(int exeType)
    {
        this.exeType = exeType;
    }
    
    public Date getCreatedAt()
    {
        return createdAt != null ? new Date(createdAt.getTime()) : null;
    }
    
    public void setCreatedAt(Date createdAt)
    {
        if (createdAt != null)
        {
            this.createdAt = new Date(createdAt.getTime());
        }
    }
    
    public Date getModifiedAt()
    {
        return modifiedAt != null ? new Date(modifiedAt.getTime()) : null;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt != null)
        {
            this.modifiedAt = new Date(modifiedAt.getTime());
        }
    }
    
    public String getExeStartAt()
    {
        return exeStartAt;
    }
    
    public void setExeStartAt(String exeStartAt)
    {
        this.exeStartAt = exeStartAt;
    }
    
    public String getExeEndAt()
    {
        return exeEndAt;
    }
    
    public List<CopyPolicySiteInfo> getLstCopyPolicyDataSiteInfo()
    {
        return lstCopyPolicyDataSiteInfo;
    }
    
    public void setLstCopyPolicyDataSiteInfo(List<CopyPolicySiteInfo> lstCopyPolicyDataSiteInfo)
    {
        this.lstCopyPolicyDataSiteInfo = lstCopyPolicyDataSiteInfo;
    }
    
    public void setExeEndAt(String exeEndAt)
    {
        this.exeEndAt = exeEndAt;
    }
    
    public CopyPolicyUserConfig getUserConfig()
    {
        return userConfig;
    }
    
    public void setUserConfig(CopyPolicyUserConfig userConfig)
    {
        this.userConfig = userConfig;
    }
}
