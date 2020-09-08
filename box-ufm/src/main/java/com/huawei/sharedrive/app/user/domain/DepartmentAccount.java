package com.huawei.sharedrive.app.user.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

public class DepartmentAccount {
    public static final int STATUS_DISABLE = 1;
    public static final int STATUS_ENABLE = 0;

    public static final long SPACE_QUOTA_UNLIMITED = -1;

    public final static int SPACE_UNIT = 1024;

    private long id;

    @NotNull
    @Size(max = 20)
    private long accountId;

    @NotNull
    @Size(max = 20)
    private long enterpriseId;

    @NotNull
    @Size(max = 20)
    private long deptId;

    @NotNull
    @Size(max = 20)
    private long cloudUserId;

    @NotNull
    @Size(max = 1)
    private Integer status;

    @Size(max = 128)
    private String accessKeyId;

    @Size(max = 128)
    private String secretKey;

    private Date createdAt;

    private Date modifiedAt;

    private Long resourceType;

    @NotNull
    @Size(max = 20)
    private long regionId;

    private Integer maxVersions;

    private Long spaceQuota;

    private Integer teamSpaceFlag;

    private Long teamSpaceQuota;

    private Integer teamSpaceMaxNum;

    private Long uploadBandWidth;

    private Long downloadBandWidth;

    private Boolean fileNeedApprove;

    public Boolean getFileNeedApprove() {
        return fileNeedApprove;
    }

    public void setFileNeedApprove(Boolean fileNeedApprove) {
        this.fileNeedApprove = fileNeedApprove;
    }

    public DepartmentAccount() {
    }

    public DepartmentAccount(long accountId, long enterpriseId, long deptId) {
        this.accountId = accountId;
        this.enterpriseId = enterpriseId;
        this.deptId = deptId;
        this.status = DepartmentAccount.STATUS_ENABLE;
    }

    public DepartmentAccount(long accountId, long enterpriseId, long deptId, long cloudUserId) {
        this.accountId = accountId;
        this.enterpriseId = enterpriseId;
        this.deptId = deptId;
        this.cloudUserId = cloudUserId;
        this.status = DepartmentAccount.STATUS_ENABLE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public long getDeptId() {
        return deptId;
    }

    public void setDeptId(long deptId) {
        this.deptId = deptId;
    }

    public long getCloudUserId() {
        return cloudUserId;
    }

    public void setCloudUserId(long cloudUserId) {
        this.cloudUserId = cloudUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Long getResourceType() {
        return resourceType;
    }

    public void setResourceType(Long resourceType) {
        this.resourceType = resourceType;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public Integer getMaxVersions() {
        return maxVersions;
    }

    public void setMaxVersions(Integer maxVersions) {
        this.maxVersions = maxVersions;
    }

    public Long getSpaceQuota() {
        return spaceQuota;
    }

    public void setSpaceQuota(Long spaceQuota) {
        this.spaceQuota = spaceQuota;
    }

    public Integer getTeamSpaceFlag() {
        return teamSpaceFlag;
    }

    public void setTeamSpaceFlag(Integer teamSpaceFlag) {
        this.teamSpaceFlag = teamSpaceFlag;
    }

    public Long getTeamSpaceQuota() {
        return teamSpaceQuota;
    }

    public void setTeamSpaceQuota(Long teamSpaceQuota) {
        this.teamSpaceQuota = teamSpaceQuota;
    }

    public Integer getTeamSpaceMaxNum() {
        return teamSpaceMaxNum;
    }

    public void setTeamSpaceMaxNum(Integer teamSpaceMaxNum) {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }

    public Long getUploadBandWidth() {
        return uploadBandWidth;
    }

    public void setUploadBandWidth(Long uploadBandWidth) {
        this.uploadBandWidth = uploadBandWidth;
    }

    public Long getDownloadBandWidth() {
        return downloadBandWidth;
    }

    public void setDownloadBandWidth(Long downloadBandWidth) {
        this.downloadBandWidth = downloadBandWidth;
    }
}
