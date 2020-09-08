package pw.cdmi.box.disk.share.domain;

import java.util.List;

import pw.cdmi.box.disk.client.domain.common.RestBaseObject;
import pw.cdmi.box.disk.client.domain.node.ThumbnailUrl;
import pw.cdmi.box.disk.filelabel.dto.BaseFileLabelInfo;

public class RestLinkFileInfo extends RestBaseObject
{
    private String description;
    
    private Boolean isShare;
    
    private Boolean isSharelink;
    
    private Boolean isSync;
    
    private String md5;
    
    // V2
    private String objectId;
    
    private String sha1;
    
    private String thumbnailUrl;
    
    private List<ThumbnailUrl> thumbnailUrlList;
    
    // V1
    private String version;
    
    private int versions;
    
    private int linkCount;
    
    private boolean previewable;
    
    
    // 文件標簽
    private List<BaseFileLabelInfo> fileLabelList;
    
    public String getDescription()
    {
        return description;
    }
    
    public Boolean getIsShare()
    {
        return isShare;
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
    
    public int getLinkCount()
    {
        return linkCount;
    }
    
    public void setLinkCount(int linkCount)
    {
        this.linkCount = linkCount;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }

    public List<BaseFileLabelInfo> getFileLabelList() {
        return fileLabelList;
    }

    public void setFileLabelList(List<BaseFileLabelInfo> fileLabelList) {
        this.fileLabelList = fileLabelList;
    }
    
}
