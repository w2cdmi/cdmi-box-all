package com.huawei.sharedrive.app.test.domain.user;

import java.io.Serializable;

public class RestUserUpdateRequest implements Serializable
{
    private static final long serialVersionUID = 3856611918780631589L;
    
    private String loginName;
    
    private String oldPassword;
    
    private String newPassword;
    
    private String password;
    
    private String name;
    
    private Integer regionId;
    
    private String email;
    
    private Long spaceQuota;
    
    private String status;
    
    private Integer maxVersions;
    
    private String description;
    
    private Byte teamSpaceFlag;
    
    private Integer teamSpaceMaxNum;
    
    public String getDescription()
    {
        return description;
    }
    
    public String getEmail()
    {
        return email;
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getNewPassword()
    {
        return newPassword;
    }
    
    public String getOldPassword()
    {
        return oldPassword;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public Byte getTeamSpaceFlag()
    {
        return teamSpaceFlag;
    }
    
    public Integer getTeamSpaceMaxNum()
    {
        return teamSpaceMaxNum;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setEmail(String email)
    {
        this.email = email;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setNewPassword(String newPassword)
    {
        this.newPassword = newPassword;
    }
    
    public void setOldPassword(String oldPassword)
    {
        this.oldPassword = oldPassword;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setTeamSpaceFlag(Byte teamSpaceFlag)
    {
        this.teamSpaceFlag = teamSpaceFlag;
    }
    
    public void setTeamSpaceMaxNum(Integer teamSpaceMaxNum)
    {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }
    
}
