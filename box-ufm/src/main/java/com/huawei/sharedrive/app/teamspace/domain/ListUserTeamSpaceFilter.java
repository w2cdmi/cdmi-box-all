package com.huawei.sharedrive.app.teamspace.domain;

public class ListUserTeamSpaceFilter
{
    private String userId;
    
    private String userType;
    
    private Integer length;
    
    private Long offset;

    private Long total;
    
    public ListUserTeamSpaceFilter(String userId, String userType)
    {
        this.userId = userId;
        this.userType = userType;
    }
    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
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

    public Integer getLength()
    {
        return length;
    }

    public void setLength(Integer length)
    {
        this.length = length;
    }

    public Long getOffset()
    {
        return offset;
    }

    public void setOffset(Long offset)
    {
        this.offset = offset;
    }

    public Long getTotal()
    {
        return total;
    }

    public void setTotal(Long total)
    {
        this.total = total;
    }
    

    
}
