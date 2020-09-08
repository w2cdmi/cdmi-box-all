package com.huawei.sharedrive.app.openapi.domain.account;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.app.account.domain.AccountConstants;
import com.huawei.sharedrive.app.exception.InvalidParamException;

public class RestCreateAccountRequest
{
    private static final long MAXSPACEVALUE = 100000000000L;
    
    private static final int MAXTEAMSPACE = 10000000;
    
    private static final int MAXVALUE = 1000000;
    
    private static final int LIMITLESS = -1;
    
    public static final int MAXLEN = 255;
    
    public static final int MINVALUE = -1;
    
    public static final String PERTN = "^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)";
    
    /*** 企业二级域名 */
    private String domain;
    
    /** 接入ID */
    private Long enterpriseId;
    
    /** 是否启用文件预览 否：false 是：true */
    private Boolean filePreviewable;
    
    /** 是否启用文件预览 否：false 是：true */
    private Boolean fileScanable;
    
    /** 成员（用户）数量配额 */
    private Integer maxMember;
    
    /** 空间容量配额 */
    private Long maxSpace;
    
    /** 团队空间数量配额 */
    private Integer maxTeamspace;
    
    /** 企业状态 */
    private String status;
    
    public void checkParameter()
    {
        checkEnterpriseId();
        checkDomain();
        checkMaxSpace();
        checkMaxMembers();
        checkMaxTeamspace();
        checkFileScanable();
        checkFilePreviewable();
        checkStatus();
    }
    
    private void checkEnterpriseId()
    {
        if (enterpriseId == null)
        {
            throw new InvalidParamException("enterpriseId is null");
        }
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public Boolean getFilePreviewable()
    {
        return filePreviewable;
    }
    
    public Boolean getFileScanable()
    {
        return fileScanable;
    }
    
    public Integer getMaxMember()
    {
        return maxMember;
    }
    
    public Long getMaxSpace()
    {
        return maxSpace;
    }
    
    public Integer getMaxTeamspace()
    {
        return maxTeamspace;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public void setFilePreviewable(Boolean filePreviewable)
    {
        this.filePreviewable = filePreviewable;
    }
    
    public void setFileScanable(Boolean fileScanable)
    {
        this.fileScanable = fileScanable;
    }
    
    public void setMaxMember(Integer maxMember)
    {
        this.maxMember = maxMember;
    }
    
    public void setMaxSpace(Long maxSpace)
    {
        this.maxSpace = maxSpace;
    }
    
    public void setMaxTeamspace(Integer maxTeamspace)
    {
        this.maxTeamspace = maxTeamspace;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    /**
     * 
     */
    private void checkDomain()
    {
        if (domain == null)
        {
            throw new InvalidParamException(" null is domain");
        }
        domain = domain.trim();
        if (domain.length() == 0)
        {
            throw new InvalidParamException(" null is domain");
        }
        else
        {
            domain = domain.trim();
            if (domain.length() > MAXLEN)
            {
                throw new InvalidParamException("[domain.len>255]" + domain.length());
            }
        }
    }
    
    private void checkMaxSpace()
    {
        if (maxSpace == null)
        {
            maxSpace = Long.valueOf(LIMITLESS);
        }
        else if (maxSpace.intValue() < LIMITLESS)
        {
            throw new InvalidParamException("maxSpace<" + LIMITLESS);
        }
        else if (maxSpace.longValue() > MAXSPACEVALUE)
        {
            throw new InvalidParamException("maxSpace>" + MAXSPACEVALUE);
        }
    }
    
    private void checkMaxTeamspace()
    {
        if (maxTeamspace == null)
        {
            maxTeamspace = -1;
        }
        else if (maxTeamspace.intValue() < LIMITLESS)
        {
            throw new InvalidParamException("maxTeamspace<" + LIMITLESS);
        }
        else if (maxTeamspace.intValue() > MAXTEAMSPACE)
        {
            throw new InvalidParamException("maxTeamspace>" + MAXTEAMSPACE);
        }
    }
    
    private void checkMaxMembers()
    {
        if (maxMember == null)
        {
            maxMember = -1;
        }
        else if (maxMember.intValue() < LIMITLESS)
        {
            throw new InvalidParamException("maxMember<" + LIMITLESS);
        }
        else if (maxMember.intValue() > MAXVALUE)
        {
            throw new InvalidParamException("maxMember>" + MAXVALUE);
        }
    }
    
    private void checkStatus()
    {
        if (status == null)
        {
            status = AccountConstants.STATUS_DIS;
        }
        else if (!(StringUtils.equals(status, AccountConstants.STATUS_EN) || StringUtils.equals(status,
            AccountConstants.STATUS_DIS)))
        {
            String errorMsg = "error status :" + status;
            throw new InvalidParamException(errorMsg);
        }
    }
    
    private void checkFilePreviewable()
    {
        if (filePreviewable == null)
        {
            filePreviewable = false;
        }
    }
    
    private void checkFileScanable()
    {
        if (fileScanable == null)
        {
            fileScanable = false;
        }
    }
}
