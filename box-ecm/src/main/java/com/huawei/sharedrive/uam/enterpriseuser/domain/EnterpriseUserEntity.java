package com.huawei.sharedrive.uam.enterpriseuser.domain;

public class EnterpriseUserEntity extends EnterpriseUser
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String appId;
    
    private Integer teamSpaceMaxNum;
    
    private Integer maxVersions;
    
    private Long spaceQuota;
    
    private Boolean flag;
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public Integer getTeamSpaceMaxNum()
    {
        return teamSpaceMaxNum;
    }
    
    public void setTeamSpaceMaxNum(Integer teamSpaceMaxNum)
    {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public Boolean getFlag()
    {
        return flag;
    }
    
    public void setFlag(Boolean flag)
    {
        this.flag = flag;
    }
    
}
