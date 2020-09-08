package com.huawei.sharedrive.app.account.domain;

import java.io.Serializable;
import java.util.Date;


public class Account implements Serializable
{
    private static final long serialVersionUID = 6898284144753937176L;
    
    public static final String CACHE_KEY_PREFIX_ID = "account_id_";
    
    /** 企业ID */
    private long id;
    
    /** 企业二级域名 */
    private String domain;
    
    /** 应用ID */
    private String appId;
    
    /** 创建时间 */
    private Date createdAt;
    
    /** 修改时间 */
    private Date modifiedAt;
    
    /** 企业状态 */
    private Byte status;
    
    /** 接入ID */
    private Long enterpriseId;
    
    /** 空间容量配额 */
    private Long maxSpace;

    private Long maxShareSpace;

    /** 成员（用户）数量配额 */
    private Integer maxMember;
    
    /** 文件数量配额 */
    private Integer maxFiles;
    
    /** 团队空间数量配额 */
    private Integer maxTeamspace;
    
    /** 是否启用文件预览 */
    private Boolean filePreviewable;
    
    /** 是否启用文件预览 */
    private Boolean fileScanable;
    
    /** 当前成员（用户）数量 */
    private Integer currentMember;
    
    /** 当前团队空间数量 */
    private Integer currentTeamspace;
    
    private Long currentSpace;

    protected Long currentShareSpace;

    private Long currentFiles;
    
    public Long getCurrentSpace()
    {
        return currentSpace;
    }
    
    public void setCurrentSpace(Long currentSpace)
    {
        this.currentSpace = currentSpace;
    }
    
    public Integer getCurrentMember()
    {
        return currentMember;
    }
    
    public Integer getCurrentTeamspace()
    {
        return currentTeamspace;
    }
    
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
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
    
    public long getId()
    {
        return id;
    }
    
    public Integer getMaxFiles()
    {
        return maxFiles;
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
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public Byte getStatus()
    {
        return status;
    }
    
    public void lossToMax()
    {
        if (maxFiles == null || maxFiles.intValue() == AccountConstants.UNLIMIT_NUM)
        {
            maxFiles = AccountConstants.UNLIMIT_NUM_RESTORE;
        }
        if (maxMember == null || maxMember.intValue() == AccountConstants.UNLIMIT_NUM)
        {
            maxMember = AccountConstants.UNLIMIT_NUM_RESTORE;
        }
        if (maxSpace == null || maxSpace.longValue() == AccountConstants.UNLIMIT_NUM)
        {
            maxSpace = AccountConstants.UNLIMIT_NUM_RESTORE_SPACE;
        }
        if (maxTeamspace == null || maxTeamspace.intValue() == AccountConstants.UNLIMIT_NUM)
        {
            maxTeamspace = AccountConstants.UNLIMIT_NUM_RESTORE;
        }
        
    }
    
    public void maxToloss()
    {
        if (maxFiles == null || maxFiles.intValue() == AccountConstants.UNLIMIT_NUM_RESTORE)
        {
            maxFiles = AccountConstants.UNLIMIT_NUM;
        }
        if (maxMember == null || maxMember.intValue() == AccountConstants.UNLIMIT_NUM_RESTORE)
        {
            maxMember = AccountConstants.UNLIMIT_NUM;
        }
        if (maxSpace == null || maxSpace.longValue() == AccountConstants.UNLIMIT_NUM_RESTORE_SPACE)
        {
            maxSpace = Long.valueOf(AccountConstants.UNLIMIT_NUM);
        }
        if (maxTeamspace == null || maxTeamspace.intValue() == AccountConstants.UNLIMIT_NUM_RESTORE)
        {
            maxTeamspace = AccountConstants.UNLIMIT_NUM;
        }
        
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
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
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setMaxFiles(Integer maxFiles)
    {
        this.maxFiles = maxFiles;
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

    public Long getMaxShareSpace() {
        return maxShareSpace;
    }

    public void setMaxShareSpace(Long maxShareSpace) {
        this.maxShareSpace = maxShareSpace;
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
    
    public void setStatus(Byte status)
    {
        this.status = status;
    }
    
    public void setCurrentMember(Integer currentMember)
    {
        this.currentMember = currentMember;
    }
    
    public void setCurrentTeamspace(Integer currentTeamspace)
    {
        this.currentTeamspace = currentTeamspace;
    }
    
    public Long getCurrentFile()
    {
        return currentFiles;
    }
    
    public void setCurrentFile(Long currentFiles)
    {
        this.currentFiles = currentFiles;
    }

    public Long getCurrentShareSpace() {
        return currentShareSpace;
    }

    public void setCurrentShareSpace(Long currentShareSpace) {
        this.currentShareSpace = currentShareSpace;
    }

    public Long getCurrentFiles() {
        return currentFiles;
    }

    public void setCurrentFiles(Long currentFiles) {
        this.currentFiles = currentFiles;
    }
}
