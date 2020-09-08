package com.huawei.sharedrive.app.acl.domain;

import java.util.Date;

/**
 * 
 * 个 十 百 千 万 十万 百万 千 角色 浏览 上传 下载 预览 删除 编辑 获取链接 权限变更 拥有者 1 1 1 1 1 1 1 1 编辑者 1 1 1 1 1 1 1
 * 0 查看者、上传者 1 1 1 1 0 0 1 0 预览者、上传者 1 1 0 1 0 0 0 0 查看者 1 0 1 1 0 0 0 0 上传者 1 1 0 0 0 0 0
 * 0 预览者 1 0 0 1 0 0 0 0 禁止访问者 0 0 0 0 0 0 0 0
 */

public class ResourceRole
{
    private String role;
    
    private long acl;
    
    private long createdBy;
    
    private String description;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private long modifiedBy;
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
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
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
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
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public long getACL()
    {
        return acl;
    }
    
    public void setACL(long acl)
    {
        this.acl = acl;
    }
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public String getResourceRole()
    {
        return role;
    }
    
    public void setResourceRole(String resourceRole)
    {
        this.role = resourceRole;
    }
    
    public static final String AUTHER = "auther";
    
    public static final String EDITER = "editor";
    
    public static final String UPLOAD_VIEWER = "uploadAndView";
    
    public static final String VIEWER = "viewer";
    
    public static final String UPLOADER = "uploader";
    
    public static final String DOWNLOADER = "downloader";
    
    public static final String PREVIEWER = "previewer";
    
    public static final String LISTER = "lister";
    
    public static final String PROHIBIT_VISITORS = "prohibitVisitors";
}
