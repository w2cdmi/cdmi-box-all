package com.huawei.sharedrive.uam.openapi.domain;

import com.huawei.sharedrive.uam.user.domain.RestAccessKey;


public class RestEnterpriseAccountResponse {

    private long id;

    private String appId;

    private Long createdAt;

    private Long modifiedAt;

    private String status;

    private Long enterpriseId;

    private Long maxSpace;

    private Long maxShareSpace;

    private int maxMember;

    private int maxFiles;

    private int maxTeamspace;

    private boolean filePreviewable;

    private boolean fileScanable;

    private RestAccessKey accessToken;

    private Long currentSpace;

    private Long currentShareSpace;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Long modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Long getMaxSpace() {
        return maxSpace;
    }

    public void setMaxSpace(Long maxSpace) {
        this.maxSpace = maxSpace;
    }

    public int getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(int maxMember) {
        this.maxMember = maxMember;
    }

    public int getMaxFiles() {
        return maxFiles;
    }

    public void setMaxFiles(int maxFiles) {
        this.maxFiles = maxFiles;
    }

    public int getMaxTeamspace() {
        return maxTeamspace;
    }

    public void setMaxTeamspace(int maxTeamspace) {
        this.maxTeamspace = maxTeamspace;
    }

    public boolean isFilePreviewable() {
        return filePreviewable;
    }

    public void setFilePreviewable(boolean filePreviewable) {
        this.filePreviewable = filePreviewable;
    }

    public boolean isFileScanable() {
        return fileScanable;
    }

    public void setFileScanable(boolean fileScanable) {
        this.fileScanable = fileScanable;
    }

    public RestAccessKey getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(RestAccessKey accessToken) {
        this.accessToken = accessToken;
    }

    public Long getMaxShareSpace() {
        return maxShareSpace;
    }

    public void setMaxShareSpace(Long maxShareSpace) {
        this.maxShareSpace = maxShareSpace;
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
