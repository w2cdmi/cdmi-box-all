package pw.cdmi.box.disk.client.domain.node;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.disk.client.domain.common.RestBaseObject;

public class RestFileVersionInfo extends RestBaseObject
{
    
    private String sha1;
    
    private String version;
    
    private boolean previewable;
    
    public RestFileVersionInfo()
    {
        
    }
    
    public RestFileVersionInfo(INode iNode)
    {
        this.setId(iNode.getId());
        this.setParent(iNode.getParentId());
        this.setType(INode.TYPE_VERSION);
        this.setSize(iNode.getSize());
        this.setSha1(iNode.getSha1());
        
        this.setVersion(String.valueOf(iNode.getObjectId()));
        this.setStatus(iNode.getStatus());
        this.setCreatedAt(iNode.getCreatedAt());
        this.setModifiedAt(iNode.getModifiedAt());
        this.setOwnedBy(iNode.getOwnedBy());
        this.setCreatedBy(iNode.getCreatedBy());
        this.setModifiedBy(iNode.getModifiedBy());
        this.setName(iNode.getName());
        this.setIsEncrypt(StringUtils.isNotBlank(iNode.getEncryptKey()));
        
        Long contentCreatedTime = iNode.getContentCreatedAt() == null ? null : iNode.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedTime);
        
        Long contentModifiedAt = iNode.getContentModifiedAt() == null ? null : iNode.getContentModifiedAt()
            .getTime();
        this.setContentModifiedAt(contentModifiedAt);
        
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
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
