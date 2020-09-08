package com.huawei.sharedrive.uam.cmb.oa.domain;

import java.io.Serializable;

public class CMBUserRelationInfo implements Serializable
{
    private static final long serialVersionUID = 3829028774832744539L;
    
    public static final Byte NORMOL = 0;
    
    public static final Byte UNNORMOL = 1;
    
    private Integer orgId;
    
    private String groupId;
    
    private String name;
    
    private String userId;
    
    private Integer userOrd;
    
    private Byte status;
    
    public Integer getOrgId()
    {
        return orgId;
    }
    
    public void setOrgId(Integer orgId)
    {
        this.orgId = orgId;
    }
    
    public String getGroupId()
    {
        return groupId;
    }
    
    public void setGroupId(String groupId)
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
    
    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    
    public Integer getUserOrd()
    {
        return userOrd;
    }
    
    public void setUserOrd(Integer userOrd)
    {
        this.userOrd = userOrd;
    }
    
    public Byte getStatus()
    {
        return status;
    }
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }
    
}
