package com.huawei.sharedrive.app.openapi.domain.group;

import com.huawei.sharedrive.app.exception.InvalidParamException;
import com.huawei.sharedrive.app.group.domain.GroupConstants;
import com.huawei.sharedrive.app.group.domain.GroupMemberships;
import com.huawei.sharedrive.app.utils.FilesCommonUtils;

public class RestGroupMember
{
    private Long id;
    
    private Long groupId;
    
    private String name;
    
    private Long userId;
    
    private String userType;
    
    private String username;
    
    private String loginName;
    
    private String groupRole;
    
    public RestGroupMember()
    {
        
    }
    
    public RestGroupMember(GroupMemberships memberships)
    {
        id = memberships.getId();
        groupId = memberships.getGroupId();
        name = memberships.getName();
        userId = memberships.getUserId();
        groupRole = transRole(memberships.getGroupRole());
        username = memberships.getUsername();
        loginName = memberships.getLoginName();
        userType = transUserType(memberships.getUserType());
    }
    
    private String transUserType(byte userType)
    {
        if (userType == GroupConstants.GROUP_USERTYPE_GROUP)
        {
            return GroupConstants.USERTYPE_GROUP;
        }
        return GroupConstants.USERTYPE_USER;
    }
    
    public void checkParameter()
    {
        FilesCommonUtils.checkNonNegativeIntegers(userId);
        if (groupRole != null && !GroupConstants.belongAllRole(groupRole))
        {
            throw new InvalidParamException("groupRole error:" + groupRole);
        }
    }
    
    private String transRole(byte groupRole2)
    {
        if (groupRole2 == GroupConstants.GROUP_ROLE_ADMIN)
        {
            return GroupConstants.ROLE_ADMIN;
        }
        else if (groupRole2 == GroupConstants.GROUP_ROLE_MANAGER)
        {
            return GroupConstants.ROLE_MANAGER;
        }
        return GroupConstants.ROLE_MEMBER;
    }
    
    public Long getId()
    {
        return id;
    }
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    public Long getGroupId()
    {
        return groupId;
    }
    
    public void setGroupId(Long groupId)
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
    
    public Long getUserId()
    {
        return userId;
    }
    
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }
    
    public String getUserType()
    {
        return userType;
    }
    
    public void setUserType(String userType)
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
    
    public String getGroupRole()
    {
        return groupRole;
    }
    
    public void setGroupRole(String groupRole)
    {
        this.groupRole = groupRole;
    }
    
}
