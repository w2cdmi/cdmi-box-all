package com.huawei.sharedrive.app.teamspace.domain;

import java.util.Date;

import com.huawei.sharedrive.app.user.domain.User;

/**
 * 团队空间关系
 * 
 * @author c00110381
 * 
 */
public class TeamSpaceMemberships
{
    public static final String TYPE_USER = "user";
    
    public static final String TYPE_GROUP = "group";
    
    public static final String TYPE_SYSTEM = "system";
    
    public static final String TYPE_DEPT = "department";
    
    // 不设置小于1的数值是因为分表参数要求
   // public final static String ID_TEAM_PUBLIC = BusinessConstants.ID_TEAM_PUBLIC;
    
    public static final int STATUS_NORMAL = 0;
    
    private long id;
    
    private long cloudUserId;
    
    private long userId;
    
    private String userType;
    
    private String username;
    
    private String loginName;
    
    private String teamRole;
    
    private Date createdAt;
    
    private Date modifiedAt;
    
    private Long createdBy;
    
    private Long modifiedBy;
    
    private int status;
    
    private String role;
    
    // 接口调用需要下面几个属性
    private User member;
    
    private TeamSpace teamSpace;
    
    private int tableSuffix;
    
    public long getCloudUserId()
    {
        return cloudUserId;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public Long getCreatedBy()
    {
        return createdBy;
    }
    
    public long getId()
    {
        return id;
    }
    
    public User getMember()
    {
        return member;
    }
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public Long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public String getRole()
    {
        return role;
    }
    
    public int getStatus()
    {
        return status;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public String getTeamRole()
    {
        return teamRole;
    }
    
    public TeamSpace getTeamSpace()
    {
        return teamSpace;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public String getLoginName()
    {
        return loginName;
    }

    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }

    public String getUserType()
    {
        return userType;
    }
    
    public void setCloudUserId(long cloudUserId)
    {
        this.cloudUserId = cloudUserId;
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
    
    public void setCreatedBy(Long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setMember(User member)
    {
        this.member = member;
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
    
    public void setModifiedBy(Long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setRole(String role)
    {
        this.role = role;
    }
    
    public void setStatus(int status)
    {
        this.status = status;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public void setTeamRole(String teamRole)
    {
        this.teamRole = teamRole;
    }
    
    public void setTeamSpace(TeamSpace teamSpace)
    {
        this.teamSpace = teamSpace;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public void setUserType(String userType)
    {
        this.userType = userType;
    }
    
}
