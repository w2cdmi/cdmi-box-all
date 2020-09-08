package com.huawei.sharedrive.app.openapi.domain.node;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.utils.Constants;
import com.huawei.sharedrive.app.utils.PropertiesUtils;

import pw.cdmi.common.domain.Terminal;

public class RestFileInfo extends RestBaseFileInfo
{
    private static final int MD5_LENGTH = 32;
    
    private static final int SHA1_LENGTH = 40;
    
    private List<RestBaseObject> path;
    
    /** 文件標簽屬性 */
    private List<BaseFileLabelInfo> fileLabelList;
    
    // 安全标示
    @JsonInclude(Include.NON_NULL)
    private Integer kiaStatus;
    
    private byte secretLevel;
    
    // 是否返回kiaStatus
    private static final boolean KIA_STATUS_ENABLE = Boolean.parseBoolean(PropertiesUtils.getProperty("kia.status.enable",
        "false"));
    
    public RestFileInfo()
    {
        
    }
    
    @SuppressWarnings("deprecation")
    public RestFileInfo(INode node, int clientType)
    {
        this.setId(node.getId());
        this.setType(INode.TYPE_FILE);
        this.setName(node.getName());
        this.setDescription(node.getDescription());
        this.setSize(node.getSize());
        this.setSecretLevel(node.getSecretLevel());
        // 文件ObjectID(V1版本使用), V2版本version字段修改为objectId, 此处为V1,V2版本共用RestFileInfo对象做的兼容处理
        if (node.getVersion() != null)
        {
            this.setVersion(node.getObjectId());
        }
        else
        {
            // 文件ObjectID(V2版本使用)
            this.setObjectId(node.getObjectId());
            // 文件版本总数, V2版本新增(含当前版本)
            this.setVersions(node.getVersions());
        }
        this.setStatus(node.getStatus());
        
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
        
        long createAtTime = node.getCreatedAt().getTime() / 1000 * 1000;
        
        this.setCreatedAt(new Date(createAtTime));
        
        long modifiedAtTime = node.getModifiedAt().getTime() / 1000 * 1000;
        
        this.setModifiedAt(new Date(modifiedAtTime));
        
        this.setOwnedBy(node.getOwnedBy());
        if (Constants.IS_FILED_NAME_COMPATIBLE)
        {
            this.setOwnerBy(node.getOwnedBy());
        }
        
        this.setCreatedBy(node.getCreatedBy());
        this.setModifiedBy(node.getModifiedBy());
        this.setParent(node.getParentId());
        this.setIsSharelink(StringUtils.isNotBlank(node.getLinkCode()));
        this.setIsEncrypt(StringUtils.isNotBlank(node.getEncryptKey()));
        this.setThumbnailUrl(node.getThumbnailUrl());
        this.setThumbnailUrlList(node.getThumbnailUrlList());
        this.setIsShare(node.getShareStatus() == INode.SHARE_STATUS_SHARED);
        this.setIsSync(node.getSyncStatus() == INode.SYNC_STATUS_SETTED);
        this.setFileLabelList(node.getFileLabelList());
        Long contentCreatedTime = node.getContentCreatedAt() == null ? null : node.getContentCreatedAt()
            .getTime();
        this.setContentCreatedAt(contentCreatedTime);
        
        Long contentModifiedTime = node.getContentModifiedAt() == null ? null : node.getContentModifiedAt()
            .getTime();
        this.setContentModifiedAt(contentModifiedTime);
        this.setPreviewable(node.isPreviewable());
        this.setIsVirus(INode.getVirusStatusFromKIALabel(node.getKiaLabel()));
        if (KIA_STATUS_ENABLE)
        {
            this.setKiaStatus(INode.getKiaStatusFromKIALabel(node.getKiaLabel()));
        }
        else
        {
            this.setKiaStatus(null);
        }
    }
    
    public List<RestBaseObject> getPath()
    {
        return path;
    }
    
    public void setPath(List<RestBaseObject> path)
    {
        this.path = path;
    }
    
    public Integer getKiaStatus()
    {
        return kiaStatus;
    }
    
    public void setKiaStatus(Integer kiaStatus)
    {
        this.kiaStatus = kiaStatus;
    }

    public List<BaseFileLabelInfo> getFileLabelList() {
        return fileLabelList;
    }

    public void setFileLabelList(List<BaseFileLabelInfo> fileLabelList) {
        this.fileLabelList = fileLabelList;
    }

	public byte getSecretLevel() {
		return secretLevel;
	}

	public void setSecretLevel(byte secretLevel) {
		this.secretLevel = secretLevel;
	}
    
    
}
