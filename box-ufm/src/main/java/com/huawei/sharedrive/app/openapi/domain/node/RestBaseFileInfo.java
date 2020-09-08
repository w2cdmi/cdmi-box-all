package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.List;

import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;

public class RestBaseFileInfo extends RestBaseObject
{
    
    private String description;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    private Boolean isVirus;

	private String md5;
    
    // 文件ObjectID, 适用于V2版本
    private String objectId;
    
    private String sha1;
    
    private String thumbnailUrl;
    
    private List<ThumbnailUrl> thumbnailUrlList;
    
    // 文件objectID, 适用于V1版本
    private String version;
    
    // 文件历史版本总数
    private int versions;
    
    private boolean previewable;
    
    public String getDescription()
    {
        return description;
    }
    
    public Boolean getIsShare()
    {
        return isShare;
    }
    
    public Boolean getIsVirus() {
		return isVirus;
	}

	public void setIsVirus(Boolean isVirus) {
		this.isVirus = isVirus;
	}
    
    public Boolean getIsSharelink()
    {
        return isSharelink;
    }
    
    public Boolean getIsSync()
    {
        return isSync;
    }
    
    public String getMd5()
    {
        return md5;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
    
    public List<ThumbnailUrl> getThumbnailUrlList()
    {
        return thumbnailUrlList;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public int getVersions()
    {
        return versions;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setIsShare(Boolean isShare)
    {
        this.isShare = isShare;
    }
    
    public void setIsSharelink(Boolean isSharelink)
    {
        this.isSharelink = isSharelink;
    }
    
    public void setIsSync(Boolean isSync)
    {
        this.isSync = isSync;
    }
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList)
    {
        this.thumbnailUrlList = thumbnailUrlList;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public void setVersions(int versions)
    {
        this.versions = versions;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }
}
