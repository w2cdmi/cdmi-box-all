package com.huawei.sharedrive.app.openapi.domain.node;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.domain.Terminal;

/**
 * 文件版本对象
 * 
 */
public class RestFileVersionInfo extends RestBaseObject
{
    // MD5摘要字符串长度
    private static final int MD5_LENGTH = 32;
    
    // Sha1摘要字符串长度
    private static final int SHA1_LENGTH = 40;
    
    private Long contentCreatedAt;
    
    private Long contentModifiedAt;
    
    private Boolean isEncrypt;
    
    private String md5;
    
    private String name;
    
    private String createdByName;
    
    private String objectId;
    
    private Long parent;
    
    private String sha1;
    
    private Long size;
    
    private byte status;
    
    private boolean previewable;
    
    // 安全标示
    @JsonInclude(Include.NON_NULL)
    private Integer kiaStatus;
    
    // 是否返回kiaStatus
    private static final boolean KIA_STATUS_ENABLE = Boolean.parseBoolean(PropertiesUtils.getProperty("kia.status.enable",
        "false"));
    
    public RestFileVersionInfo()
    {
    }
    
    @SuppressWarnings("deprecation")
    public RestFileVersionInfo(INode node, int clientType)
    {
        this.setId(node.getId());
        this.setParent(node.getParentId());
        this.setType(INode.TYPE_VERSION);
        this.setSize(node.getSize());
        
        // 设置为对象ID
        this.setStatus(node.getStatus());
        this.setCreatedAt(node.getCreatedAt());
        this.setModifiedAt(node.getModifiedAt());
        
        this.setOwnedBy(node.getOwnedBy());
        // 兼容老的客户端
        if (Constants.IS_FILED_NAME_COMPATIBLE)
        {
            this.setOwnerBy(node.getOwnedBy());
        }
        
        if (StringUtils.isNotBlank(node.getSha1()))
        {
            // 兼容移动客户端, 同时返回sha1字段和MD5字段
            if (Constants.IS_DIGEST_NAME_COMPATIBLE
                && (Terminal.CLIENT_TYPE_ANDROID == clientType || Terminal.CLIENT_TYPE_IOS == clientType))
            {
                this.setMd5(node.getSha1());
                this.setSha1(node.getSha1());
            }
            else
            {
                if (node.getSha1().length() == MD5_LENGTH)
                {
                    this.setMd5(node.getSha1());
                }
                else if (node.getSha1().length() == SHA1_LENGTH)
                {
                    this.setSha1(node.getSha1());
                }
            }
        }
        
        this.setCreatedBy(node.getCreatedBy());
        this.setModifiedBy(node.getModifiedBy());
        this.setName(node.getName());
        this.setIsEncrypt(StringUtils.isNotBlank(node.getEncryptKey()));
        this.setObjectId(node.getObjectId());
        
        Long contentCreatedTime = node.getContentCreatedAt() == null ? null : node.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedTime);
        
        Long contentModifiedAt = node.getContentModifiedAt() == null ? null : node.getContentModifiedAt()
            .getTime();
        this.setContentModifiedAt(contentModifiedAt);
        this.setPreviewable(node.isPreviewable());
        
        if (KIA_STATUS_ENABLE)
        {
            this.setKiaStatus(INode.getKiaStatusFromKIALabel(node.getKiaLabel()));
        }
        else
        {
            this.setKiaStatus(null);
        }
    }
    
    public Long getContentCreatedAt()
    {
        return contentCreatedAt;
    }
    
    public Long getContentModifiedAt()
    {
        return contentModifiedAt;
    }
    
    public Boolean getIsEncrypt()
    {
        return isEncrypt;
    }
    
    public String getMd5()
    {
        return md5;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public Long getParent()
    {
        return parent;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public Long getSize()
    {
        return size;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public void setContentCreatedAt(Long contentCreatedAt)
    {
        this.contentCreatedAt = contentCreatedAt;
    }
    
    public void setContentModifiedAt(Long contentModifiedAt)
    {
        this.contentModifiedAt = contentModifiedAt;
    }
    
    public void setIsEncrypt(Boolean isEncrypt)
    {
        this.isEncrypt = isEncrypt;
    }
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setParent(Long parent)
    {
        this.parent = parent;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setSize(Long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }
    
    public Integer getKiaStatus()
    {
        return kiaStatus;
    }
    
    public void setKiaStatus(Integer kiaStatus)
    {
        this.kiaStatus = kiaStatus;
    }

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

    
    
}
