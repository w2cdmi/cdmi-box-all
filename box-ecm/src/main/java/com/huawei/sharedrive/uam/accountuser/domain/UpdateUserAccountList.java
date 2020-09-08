package com.huawei.sharedrive.uam.accountuser.domain;

import java.io.Serializable;

public class UpdateUserAccountList implements Serializable
{
    private static final long serialVersionUID = 3856611918780631589L;
    
    private String userIds;
    
    private String selLdapDn;
    
    private String selTag;
    
    private String selStatus;
    
    private String selFilter;
    
    private Integer regionId;
    
    private Long spaceQuota;
    
    private Integer maxVersions;
    
    private Integer teamSpaceMaxNum;
    
    private Byte teamSpaceFlag;
    
    private String tagId;
    
    private Long uploadBandWidth;
    
    private Long downloadBandWidth;
    
    private Boolean isRegionId;
    
    private Boolean isSpaceQuota;
    
    private Boolean isMaxVersions;
    
    private Boolean isTeamSpaceMaxNum;
    
    private Boolean isTeamSpaceFlag;
    
    private Boolean isTagId;
    
    private Boolean isDownloadBandWidthInput;
    
    private Boolean isUploadBandWidthInput;
    
    public String getSelLdapDn()
    {
        return selLdapDn;
    }
    
    public void setSelLdapDn(String selLdapDn)
    {
        this.selLdapDn = selLdapDn;
    }
    
    public String getSelTag()
    {
        return selTag;
    }
    
    public void setSelTag(String selTag)
    {
        this.selTag = selTag;
    }
    
    public String getSelStatus()
    {
        return selStatus;
    }
    
    public void setSelStatus(String selStatus)
    {
        this.selStatus = selStatus;
    }
    
    public String getSelFilter()
    {
        return selFilter;
    }
    
    public void setSelFilter(String selFilter)
    {
        this.selFilter = selFilter;
    }
    
    public Long getUploadBandWidth()
    {
        return uploadBandWidth;
    }
    
    public void setUploadBandWidth(Long uploadBandWidth)
    {
        this.uploadBandWidth = uploadBandWidth;
    }
    
    public Long getDownloadBandWidth()
    {
        return downloadBandWidth;
    }
    
    public void setDownloadBandWidth(Long downloadBandWidth)
    {
        this.downloadBandWidth = downloadBandWidth;
    }
    
    public String getUserIds()
    {
        return userIds;
    }
    
    public void setUserIds(String userIds)
    {
        this.userIds = userIds;
    }
    
    public Integer getRegionId()
    {
        return regionId;
    }
    
    public void setRegionId(Integer regionId)
    {
        this.regionId = regionId;
    }
    
    public Long getSpaceQuota()
    {
        return spaceQuota;
    }
    
    public void setSpaceQuota(Long spaceQuota)
    {
        this.spaceQuota = spaceQuota;
    }
    
    public Integer getMaxVersions()
    {
        return maxVersions;
    }
    
    public void setMaxVersions(Integer maxVersions)
    {
        this.maxVersions = maxVersions;
    }
    
    public Byte getTeamSpaceFlag()
    {
        return teamSpaceFlag;
    }
    
    public void setTeamSpaceFlag(Byte teamSpaceFlag)
    {
        this.teamSpaceFlag = teamSpaceFlag;
    }
    
    public String getTagId()
    {
        return tagId;
    }
    
    public void setTagId(String tagId)
    {
        this.tagId = tagId;
    }
    
    public Boolean getIsRegionId()
    {
        return isRegionId;
    }
    
    public void setIsRegionId(Boolean isRegionId)
    {
        this.isRegionId = isRegionId;
    }
    
    public Boolean getIsSpaceQuota()
    {
        return isSpaceQuota;
    }
    
    public void setIsSpaceQuota(Boolean isSpaceQuota)
    {
        this.isSpaceQuota = isSpaceQuota;
    }
    
    public Boolean getIsMaxVersions()
    {
        return isMaxVersions;
    }
    
    public void setIsMaxVersions(Boolean isMaxVersions)
    {
        this.isMaxVersions = isMaxVersions;
    }
    
    public Boolean getIsTeamSpaceFlag()
    {
        return isTeamSpaceFlag;
    }
    
    public void setIsTeamSpaceFlag(Boolean isTeamSpaceFlag)
    {
        this.isTeamSpaceFlag = isTeamSpaceFlag;
    }
    
    public Boolean getIsTagId()
    {
        return isTagId;
    }
    
    public void setIsTagId(Boolean isTagId)
    {
        this.isTagId = isTagId;
    }
    
    public Boolean getIsDownloadBandWidthInput()
    {
        return isDownloadBandWidthInput;
    }
    
    public void setIsDownloadBandWidthInput(Boolean isDownloadBandWidthInput)
    {
        this.isDownloadBandWidthInput = isDownloadBandWidthInput;
    }
    
    public Boolean getIsUploadBandWidthInput()
    {
        return isUploadBandWidthInput;
    }
    
    public void setIsUploadBandWidthInput(Boolean isUploadBandWidthInput)
    {
        this.isUploadBandWidthInput = isUploadBandWidthInput;
    }
    
    public Integer getTeamSpaceMaxNum()
    {
        return teamSpaceMaxNum;
    }
    
    public void setTeamSpaceMaxNum(Integer teamSpaceMaxNum)
    {
        this.teamSpaceMaxNum = teamSpaceMaxNum;
    }
    
    public Boolean getIsTeamSpaceMaxNum()
    {
        return isTeamSpaceMaxNum;
    }
    
    public void setIsTeamSpaceMaxNum(Boolean isTeamSpaceMaxNum)
    {
        this.isTeamSpaceMaxNum = isTeamSpaceMaxNum;
    }
    
}
