package com.huawei.sharedrive.app.openapi.domain.account;

import com.huawei.sharedrive.app.account.domain.Account;
import com.huawei.sharedrive.app.account.domain.AccountConstants;
import pw.cdmi.core.utils.DateUtils;

public class RestAccount
{
    /** accountID */
    private long id;
    
    /** 应用ID */
    private String appId;
    
    private String domain;
    
    /** 创建时间 */
    private Long createdAt;
    
    /** 修改时间 */
    private Long modifiedAt;
    
    /** 企业状态 */
    private String status;
    
    /** 企业ID */
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
    
    /** 是否启用文件预览 否：false 是：true */
    private boolean filePreviewable;
    
    /** 是否启用文件预览 否：false 是：true */
    private boolean fileScanable;
    
    private RestAccessKey accessToken;
    
    private Integer currentMembers;
    
    private Integer currentTeamspaces;
    
    private Long spaceUsed;
    
    private Long currentFiles;

    private Long currentSpace;

    private Long currentShareSpace;

    public RestAccount(Account account)
    {
        appId = account.getAppId();
        createdAt = DateUtils.getDateTime(account.getCreatedAt()) / 1000 * 1000;
        domain = account.getDomain();
        enterpriseId = account.getEnterpriseId();
        filePreviewable = account.getFilePreviewable();
        fileScanable = account.getFileScanable();
        id = account.getId();
        transNum(account);
        modifiedAt = DateUtils.getDateTime(account.getModifiedAt()) / 1000 * 1000;
        status = transStatus(account.getStatus());
        currentMembers = account.getCurrentMember();
        currentTeamspaces = account.getCurrentTeamspace();
        spaceUsed = account.getCurrentSpace();
        currentFiles = account.getCurrentFile();
        currentSpace = account.getCurrentSpace();//
        currentShareSpace = account.getCurrentShareSpace();
    }

    private void transNum(Account account) {
        if (account.getMaxFiles() == null || account.getMaxFiles() == AccountConstants.UNLIMIT_NUM_RESTORE) {
            maxFiles = AccountConstants.UNLIMIT_NUM;
        } else {
            maxFiles = account.getMaxFiles();
        }

        if (account.getMaxMember() == AccountConstants.UNLIMIT_NUM_RESTORE) {
            maxMember = AccountConstants.UNLIMIT_NUM;
        } else {
            maxMember = account.getMaxMember();
        }

        if (account.getMaxSpace() == AccountConstants.UNLIMIT_NUM_RESTORE_SPACE) {
            maxSpace = (long) AccountConstants.UNLIMIT_NUM;
        } else {
            maxSpace = account.getMaxSpace();
        }

        if (account.getMaxShareSpace() == AccountConstants.UNLIMIT_NUM_RESTORE_SPACE) {
            maxShareSpace = (long) AccountConstants.UNLIMIT_NUM;
        } else {
            maxShareSpace = account.getMaxShareSpace();
        }

        if (account.getMaxTeamspace() == AccountConstants.UNLIMIT_NUM_RESTORE) {
            maxTeamspace = AccountConstants.UNLIMIT_NUM;
        } else {
            maxTeamspace = account.getMaxTeamspace();
        }
    }

    private String transStatus(byte status2) {
        if (status2 == AccountConstants.STATUS_DISABLE) {
            return AccountConstants.STATUS_DIS;
        }
        return AccountConstants.STATUS_EN;
    }
    
    public long getId()
    {
        return id;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public Long getCreatedAt()
    {
        return createdAt;
    }
    
    public void setCreatedAt(Long createdAt)
    {
        this.createdAt = createdAt;
    }
    
    public Long getModifiedAt()
    {
        return modifiedAt;
    }
    
    public void setModifiedAt(Long modifiedAt)
    {
        this.modifiedAt = modifiedAt;
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public Long getEnterpriseId()
    {
        return enterpriseId;
    }
    
    public void setEnterpriseId(Long enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }
    
    public Long getMaxSpace()
    {
        return maxSpace;
    }
    
    public void setMaxSpace(Long maxSpace)
    {
        this.maxSpace = maxSpace;
    }

    public Long getMaxShareSpace() {
        return maxShareSpace;
    }

    public void setMaxShareSpace(Long maxShareSpace) {
        this.maxShareSpace = maxShareSpace;
    }

    public Integer getMaxMember()
    {
        return maxMember;
    }
    
    public void setMaxMember(Integer maxMember)
    {
        this.maxMember = maxMember;
    }
    
    public Integer getMaxFiles()
    {
        return maxFiles;
    }
    
    public void setMaxFiles(Integer maxFiles)
    {
        this.maxFiles = maxFiles;
    }
    
    public Integer getMaxTeamspace()
    {
        return maxTeamspace;
    }
    
    public void setMaxTeamspace(Integer maxTeamspace)
    {
        this.maxTeamspace = maxTeamspace;
    }
    
    public boolean isFilePreviewable()
    {
        return filePreviewable;
    }
    
    public void setFilePreviewable(boolean filePreviewable)
    {
        this.filePreviewable = filePreviewable;
    }
    
    public boolean isFileScanable()
    {
        return fileScanable;
    }
    
    public void setFileScanable(boolean fileScanable)
    {
        this.fileScanable = fileScanable;
    }
    
    public RestAccessKey getAccessToken()
    {
        return accessToken;
    }
    
    public void setAccessToken(RestAccessKey accessToken)
    {
        this.accessToken = accessToken;
    }
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public Integer getCurrentMembers()
    {
        return currentMembers;
    }
    
    public Integer getCurrentTeamspaces()
    {
        return currentTeamspaces;
    }
    
    public void setCurrentTeamspaces(Integer currentTeamspaces)
    {
        this.currentTeamspaces = currentTeamspaces;
    }
    
    public void setCurrentMembers(Integer currentMembers)
    {
        this.currentMembers = currentMembers;
    }
    
    public Long getSpaceUsed()
    {
        return spaceUsed;
    }
    
    public void setSpaceUsed(Long spaceUsed)
    {
        this.spaceUsed = spaceUsed;
    }
    
    public Long getCurrentFile()
    {
        return currentFiles;
    }
    
    public void setCurrentFile(Long currentFiles)
    {
        this.currentFiles = currentFiles;
    }

    public Long getCurrentFiles() {
        return currentFiles;
    }

    public void setCurrentFiles(Long currentFiles) {
        this.currentFiles = currentFiles;
    }

    public Long getCurrentSpace() {
        return currentSpace;
    }

    public void setCurrentSpace(Long currentSpace) {
        this.currentSpace = currentSpace;
    }

    public Long getCurrentShareSpace() {
        return currentShareSpace;
    }

    public void setCurrentShareSpace(Long currentShareSpace) {
        this.currentShareSpace = currentShareSpace;
    }
}
