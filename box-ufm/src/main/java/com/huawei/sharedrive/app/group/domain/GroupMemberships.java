package com.huawei.sharedrive.app.group.domain;

public class GroupMemberships
{
    
    /** 群组成员关系ID */
    private long id;
    
    /** 群组ID */
    private long groupId;
    
    /** 群组名称 */
    private String name;
    
    /** 用户ID或子群组ID */
    private long userId;
    
    /** 成员用户类型 0：表示用户 ，1：表示群组 */
    private byte userType;
    
    /** 成员用户全名或成员群组名称 */
    private String username;
    
    /** 成员用户登录名或者成员群组名称 */
    private String loginName;
    
    /** 群组角色 0：表示拥有者，1：表示管理员，2表示普通成员 */
    private byte groupRole;
    
    private int tableSuffix;
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public long getGroupId()
    {
        return groupId;
    }
    
    public void setGroupId(long groupId)
    {
        this.groupId = groupId;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public long getUserId()
    {
        return userId;
    }
    
    public void setUserId(long userId)
    {
        this.userId = userId;
    }
    
    public byte getUserType()
    {
        return userType;
    }
    
    public void setUserType(byte userType)
    {
        this.userType = userType;
    }
    
    public String getUsername()
    {
        return username;
    }
    
    public void setUsername(String username)
    {
        this.username = username;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public byte getGroupRole()
    {
        return groupRole;
    }
    
    public void setGroupRole(byte groupRole)
    {
        this.groupRole = groupRole;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
}
