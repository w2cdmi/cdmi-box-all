package com.huawei.sharedrive.uam.openapi.domain;

import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.exception.InvalidParamterException;

public class RestUserUpdateRequest extends BasicUserUpdateRequest
{
    public static void copyToEnterpriseUser(EnterpriseUser enterpriseUser,
        RestUserUpdateRequest restUserUpdateRequest)
    {
        enterpriseUser.setName(restUserUpdateRequest.getLoginName());
        enterpriseUser.setPassword(restUserUpdateRequest.getPassword());
        enterpriseUser.setEmail(restUserUpdateRequest.getEmail());
        enterpriseUser.setAlias(restUserUpdateRequest.getName());
        enterpriseUser.setDescription(restUserUpdateRequest.getDescription());
    }
    
    public static RestUserUpdateRequest castTo(BasicUserUpdateRequest basicRequest)
    {
        RestUserUpdateRequest request = new RestUserUpdateRequest();
        request.setName(basicRequest.getName());
        request.setDescription(basicRequest.getDescription());
        request.setMaxVersions(basicRequest.getMaxVersions());
        request.setNewPassword(basicRequest.getNewPassword());
        request.setOldPassword(basicRequest.getOldPassword());
        request.setRegionId(basicRequest.getRegionId());
        request.setEmail(basicRequest.getEmail());
        return request;
    }
    
    private String loginName;
    
    private String password;
    
    private Long spaceQuota;
    
    private String status;
    
    private Byte teamSpaceFlag;
    
    private Integer teamSpaceMaxNum;
    
    public void checkAndSetStatus()
    {
        if (this.getStatus() == null)
        {
            this.setStatus("enable");
            return;
        }
        if ("enable".equalsIgnoreCase(this.getStatus()))
        {
            return;
        }
        if ("disable".equalsIgnoreCase(this.getStatus()))
        {
            return;
        }
        throw new InvalidParamterException("Bad status for " + this.getStatus());
    }
    
    public String getLoginName()
    {
        return loginName;
    }
    
    public String getPassword()
    {
        return password;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public int getStatusInt()
    {
        if (this.getStatus() == null)
        {
            return 0;
        }
        if ("enable".equalsIgnoreCase(this.getStatus()))
        {
            return 0;
        }
        if ("disable".equalsIgnoreCase(this.getStatus()))
        {
            return 1;
        }
        return 0;
        
    }
    
    public Byte getTeamSpaceFlag()
    {
        return teamSpaceFlag;
    }
    
    public Integer getTeamSpaceMaxNum()
    {
        return teamSpaceMaxNum;
    }
    
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
    public void setPassword(String password)
    {
        this.password = password;
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
