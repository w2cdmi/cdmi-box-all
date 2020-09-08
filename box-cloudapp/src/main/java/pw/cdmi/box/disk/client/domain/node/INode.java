package pw.cdmi.box.disk.client.domain.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import pw.cdmi.box.disk.client.domain.common.RestBaseObject;
import pw.cdmi.box.disk.filelabel.dto.BaseFileLabelInfo;
import pw.cdmi.box.disk.share.domain.RestLinkFileInfo;
import pw.cdmi.box.disk.share.domain.RestLinkFolderInfo;

public class INode implements Serializable
{
    public final static long FILES_ROOT = 0;
    
    public final static byte LINK_STATUS_SET = 1;
    
    public final static byte LINK_STATUS_UNSET = 0;
    
    public final static int SHA1_LENGTH = 32;
    
    public final static byte SHARE_STATUS_SHARED = 1;
    
    public final static byte SHARE_STATUS_UNSHARED = 0;
    
    public final static byte VIRUS_STATUS_HAS = 1;
    
    public final static byte VIRUS_STATUS_NONE = 0;
    
    public final static byte STATUS_CREATING = 1;
    
    public final static byte STATUS_DELETE = 4;
    
    public final static byte STATUS_NORMAL = 0;
    
    public final static byte STATUS_TRASH = 2;
    
    public final static byte STATUS_TRASH_DELETE = 3;
    
    public final static byte SYNC_STATUS_SETTED = 1;
    
    public final static byte SYNC_STATUS_SUBITEM = 2;
    
    public final static byte SYNC_STATUS_UNSET = 0;
    
    public final static long SYNC_VERSION_DEFAULT = 0;
    
    public final static long SYNC_VERSION_DELETE = -1;
    
    public final static byte TYPE_ALL = -1;
    
    public final static byte TYPE_FILE = 1;
    
    public final static byte TYPE_FOLDER = 0;
    
    public final static byte TYPE_VERSION = 2;
    
    /** INODE类型 5.数据迁移文件夹：离职用户个人文件在接受用户个人文件中的文件夹 */
    public final static byte TYPE_WECHAT =-5;
    
    /** INODE类型 5.数据迁移文件夹：离职用户个人文件在接受用户个人文件中的文件夹 */
    public final static byte TYPE_MIGRATION =-6;
    
    public final static String TYPE_MIGRATION_STR = "migration";
    
    /**
     * 
     */
    private static final long serialVersionUID = -5098785790971301880L;
    
    private final static int SIZE_THUMBNAIL = 2;
    
    public final static byte TYPE_FOLDER_ALL = -10;
    
    public final static byte TYPE_BACKUP_COMPUTER = -3;
    
    public final static byte TYPE_BACKUP_DISK = -2;
    
    public final static String TYPE_BACKUP_COMPUTER_STR = "computer";
    
    public final static String TYPE_BACKUP_DISK_STR = "disk";
    
    public static INode valueOf(INode srcNode)
    {
        INode node = new INode();
        node.setId(srcNode.getId());
        node.setType(srcNode.getType());
        node.setParentId(srcNode.getParentId());
        node.setOwnedBy(srcNode.getOwnedBy());
        node.setCreatedBy(srcNode.getCreatedBy());
        node.setModifiedBy(srcNode.getModifiedBy());
        node.setName(srcNode.getName());
        node.setSize(srcNode.getSize());
        node.setStatus(srcNode.getStatus());
        node.setSyncStatus(srcNode.getSyncStatus());
        node.setSyncVersion(srcNode.getSyncVersion());
        node.setShareStatus(srcNode.getShareStatus());
        node.setResourceGroupId(srcNode.getResourceGroupId());
        node.setObjectId(srcNode.getObjectId());
        node.setContentModifiedAt(srcNode.getContentModifiedAt());
        node.setContentCreatedAt(srcNode.getContentCreatedAt());
        node.setCreatedAt(srcNode.getCreatedAt());
        node.setModifiedAt(srcNode.getModifiedAt());
        node.setLinkCode(srcNode.getLinkCode());
        node.setEncryptKey(srcNode.getEncryptKey());
        node.setDescription(srcNode.getDescription());
        node.setThumbnailUrl(srcNode.getThumbnailUrl());
        node.setThumbnailUrlList(srcNode.getThumbnailUrlList());
        node.setVersion(srcNode.getVersion());
        node.setSha1(srcNode.getSha1());
        return node;
    }
    
    private Date contentCreatedAt;
    
    private Date contentModifiedAt;
    
    private Date createdAt;
    
    private long createdBy;
    
    private String createdByName;
    
    private String description;
    
    private String encryptKey;
    
    private long id;
    
    private String linkCode;
    
    private int linkCount;
    
    private byte linkStatus;
    
    private Date modifiedAt;
    
    private long modifiedBy;
    
    private String modifiedByName;
    
    private String name;
    
    private String objectId;
    
    private long ownedBy;
    
    private long parentId;
    
    private String path;
    
    private boolean previewable;
    
    private int resourceGroupId;
    
    private String sha1 = "";
    
    private byte shareStatus;
    
    private Long size;
    
    private byte status;
    
    private byte syncStatus;
    
    private byte virusStatus;

	private long syncVersion;
    
    private int tableSuffix;
    
    private String thumbnailBigURL;
    
    private String thumbnailUrl;
    
    private List<ThumbnailUrl> thumbnailUrlList;
    
    private byte type;
    
    private String version;
    
    private int versions;
    
    private Integer docType;
	
    private int viewFlag = 0;
    
    private byte secretLevel;
    
    //文件夹是否为私密
    private Boolean isSecret;
    
    //如果私密文件，判断用户是否拥有浏览权限
    private Boolean isListAcl;


    private transient List<BaseFileLabelInfo> fileLabels;
    
    public INode()
    {
        this.syncStatus = SYNC_STATUS_UNSET;
        this.shareStatus = SHARE_STATUS_UNSHARED;
        this.syncVersion = SYNC_VERSION_DEFAULT;
        thumbnailUrlList = new ArrayList<ThumbnailUrl>(SIZE_THUMBNAIL);
    }
    
    public INode(long ownedBy, long nodeId)
    {
        this();
        this.ownedBy = ownedBy;
        this.id = nodeId;
    }
    
    public INode(long ownedBy, long nodeId, long parentId)
    {
        this();
        this.ownedBy = ownedBy;
        this.id = nodeId;
        this.parentId = parentId;
    }
    
    public INode(RestFileInfo fileInfo)
    {
        initBaseBaseObject(fileInfo);
        this.setDescription(fileInfo.getDescription());
        this.setVersion(fileInfo.getVersion());
        this.setVersions(fileInfo.getVersions());
        this.setShareStatus(fileInfo.getIsShare() ? INode.SHARE_STATUS_SHARED : INode.SHARE_STATUS_UNSHARED);
        this.setSyncStatus(fileInfo.getIsSync() ? INode.SYNC_STATUS_SETTED : INode.SYNC_STATUS_UNSET);
        this.setLinkStatus(fileInfo.getIsSharelink() ? INode.LINK_STATUS_SET : INode.LINK_STATUS_UNSET);
        this.setThumbnailUrlList(fileInfo.getThumbnailUrlList());
        this.setPreviewable(fileInfo.isPreviewable());
        this.setModifiedByName(fileInfo.getMenderName());
        this.setSecretLevel(fileInfo.getSecretLevel());
        this.setIsSecret(fileInfo.getIsSecret());
        this.setIsListAcl(fileInfo.getIsListAcl());
        
        if(fileInfo.getIsVirus() != null && fileInfo.getIsVirus() == true)
        {
        	this.setVirusStatus(INode.VIRUS_STATUS_HAS);
        }
        else
        {
        	this.setVirusStatus(INode.VIRUS_STATUS_NONE);
        }
        this.setFileLabels(fileInfo.getFileLabelList());
    }
    
    public INode(RestFileVersionInfo versionInfo)
    {
        this.setId(versionInfo.getId());
        this.setType(versionInfo.getType());
        this.setSize(versionInfo.getSize());
        this.setName(versionInfo.getName());
        this.setVersion(String.valueOf(versionInfo.getVersion()));
        this.setStatus(versionInfo.getStatus());
        this.setCreatedAt(versionInfo.getCreatedAt());
        this.setModifiedAt(versionInfo.getModifiedAt());
        this.setOwnedBy(versionInfo.getOwnedBy());
        this.setCreatedBy(versionInfo.getCreatedBy());
        this.setModifiedBy(versionInfo.getModifiedBy());
        this.setParentId(versionInfo.getParent());
        this.setContentCreatedAt(versionInfo.getContentCreatedAt() == null ? null : new Date(
            versionInfo.getContentCreatedAt()));
        this.setContentModifiedAt(versionInfo.getContentModifiedAt() == null ? null : new Date(
            versionInfo.getContentModifiedAt()));
        this.setPreviewable(versionInfo.isPreviewable());
    }
    
    public INode(RestFolderInfo folderInfo)
    {
        initBaseBaseObject(folderInfo);
        if (StringUtils.equals(folderInfo.getExtraType(), INode.TYPE_BACKUP_COMPUTER_STR))
        {
            this.setType(INode.TYPE_BACKUP_COMPUTER);
        }
        else if (StringUtils.equals(folderInfo.getExtraType(), INode.TYPE_BACKUP_DISK_STR))
        {
            this.setType(INode.TYPE_BACKUP_DISK);
        }
        else
        {
            this.setType(folderInfo.getType());
        }
        this.setDescription(folderInfo.getDescription());
        this.setShareStatus(folderInfo.getIsShare() ? INode.SHARE_STATUS_SHARED : INode.SHARE_STATUS_UNSHARED);
        this.setSyncStatus(folderInfo.getIsSync() ? INode.SYNC_STATUS_SETTED : INode.SYNC_STATUS_UNSET);
        this.setLinkStatus(folderInfo.getIsSharelink() ? INode.LINK_STATUS_SET : INode.LINK_STATUS_UNSET);
        this.setModifiedByName(folderInfo.getMenderName());
        this.setIsSecret(folderInfo.getIsSecret());
        this.setIsListAcl(folderInfo.getIsListAcl());
    }
    
    public INode(RestLinkFileInfo fileInfo)
    {
        this.initBaseBaseObject(fileInfo);
        this.setDescription(fileInfo.getDescription());
        this.setVersion(fileInfo.getVersion());
        this.setVersions(fileInfo.getVersions());
        this.setShareStatus(fileInfo.getIsShare() ? INode.SHARE_STATUS_SHARED : INode.SHARE_STATUS_UNSHARED);
        this.setSyncStatus(fileInfo.getIsSync() ? INode.SYNC_STATUS_SETTED : INode.SYNC_STATUS_UNSET);
        this.setLinkStatus(fileInfo.getIsSharelink() ? INode.LINK_STATUS_SET : INode.LINK_STATUS_UNSET);
        this.setThumbnailUrlList(fileInfo.getThumbnailUrlList());
        this.setLinkCount(fileInfo.getLinkCount());
        this.setPreviewable(fileInfo.isPreviewable());
        this.setFileLabels(fileInfo.getFileLabelList());
    }
    
    public INode(RestLinkFolderInfo folderInfo)
    {
        initBaseBaseObject(folderInfo);
        if (StringUtils.equals(folderInfo.getExtraType(), INode.TYPE_BACKUP_COMPUTER_STR))
        {
            this.setType(INode.TYPE_BACKUP_COMPUTER);
        }
        else if (StringUtils.equals(folderInfo.getExtraType(), INode.TYPE_BACKUP_DISK_STR))
        {
            this.setType(INode.TYPE_BACKUP_DISK);
        }
        else
        {
            this.setType(folderInfo.getType());
        }
        this.setDescription(folderInfo.getDescription());
        this.setShareStatus(folderInfo.getIsShare() ? INode.SHARE_STATUS_SHARED : INode.SHARE_STATUS_UNSHARED);
        this.setSyncStatus(folderInfo.getIsSync() ? INode.SYNC_STATUS_SETTED : INode.SYNC_STATUS_UNSET);
        this.setLinkStatus(folderInfo.getIsSharelink() ? INode.LINK_STATUS_SET : INode.LINK_STATUS_UNSET);
        this.setLinkCount(folderInfo.getLinkCount());
    }
    
    public void addThumbnailUrl(ThumbnailUrl url)
    {
        if (url == null)
        {
            return;
        }
        if (thumbnailUrlList == null)
        {
            thumbnailUrlList = new ArrayList<ThumbnailUrl>(SIZE_THUMBNAIL);
        }
        thumbnailUrlList.add(url);
    }
    
    public Date getContentCreatedAt()
    {
        if (contentCreatedAt == null)
        {
            return null;
        }
        return (Date) contentCreatedAt.clone();
    }
    
    public Date getContentModifiedAt()
    {
        if (contentModifiedAt == null)
        {
            return null;
        }
        return (Date) contentModifiedAt.clone();
    }
    
    public Date getCreatedAt()
    {
        if (createdAt == null)
        {
            return null;
        }
        return (Date) createdAt.clone();
    }
    
    public long getCreatedBy()
    {
        return createdBy;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public String getEncryptKey()
    {
        return encryptKey;
    }
    
    public long getId()
    {
        return id;
    }
    
    public String getLinkCode()
    {
        return linkCode;
    }
    
    public int getLinkCount()
    {
        return linkCount;
    }
    
    public byte getLinkStatus()
    {
        return linkStatus;
    }
    
    public byte getVirusStatus() {
		return virusStatus;
	}

	public void setVirusStatus(byte virusStatus) {
		this.virusStatus = virusStatus;
	}
    
    public Date getModifiedAt()
    {
        if (modifiedAt == null)
        {
            return null;
        }
        return (Date) modifiedAt.clone();
    }
    
    public long getModifiedBy()
    {
        return modifiedBy;
    }
    
    public String getModifiedByName()
    {
        return modifiedByName;
    }
    
    public String getName()
    {
        return name;
    }
    
    public String getObjectId()
    {
        return objectId;
    }
    
    public long getOwnedBy()
    {
        return ownedBy;
    }
    
    public long getParentId()
    {
        return parentId;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public byte getShareStatus()
    {
        return shareStatus;
    }
    
    public Long getSize()
    {
        return size;
    }
    
    public byte getStatus()
    {
        return status;
    }
    
    public byte getSyncStatus()
    {
        return syncStatus;
    }
    
    public long getSyncVersion()
    {
        return syncVersion;
    }
    
    public int getTableSuffix()
    {
        return tableSuffix;
    }
    
    public String getThumbnailBigURL()
    {
        return thumbnailBigURL;
    }
    
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
    
    public List<ThumbnailUrl> getThumbnailUrlList()
    {
        return thumbnailUrlList;
    }
    
    public byte getType()
    {
        return type;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public int getVersions()
    {
        return versions;
    }
    
    public boolean isPreviewable()
    {
        return previewable;
    }
    
    public void setContentCreatedAt(Date contentCreatedAt)
    {
        if (contentCreatedAt == null)
        {
            this.contentCreatedAt = null;
        }
        else
        {
            this.contentCreatedAt = (Date) contentCreatedAt.clone();
        }
    }
    
    public void setContentModifiedAt(Date contentModifiedAt)
    {
        if (contentModifiedAt == null)
        {
            this.contentModifiedAt = null;
        }
        else
        {
            this.contentModifiedAt = (Date) contentModifiedAt.clone();
        }
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
    
    public void setCreatedBy(long createdBy)
    {
        this.createdBy = createdBy;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public void setEncryptKey(String encryptKey)
    {
        this.encryptKey = encryptKey;
    }
    
    public void setId(long id)
    {
        this.id = id;
    }
    
    public void setLinkCode(String linkCode)
    {
        this.linkCode = linkCode;
    }
    
    public void setLinkCount(int linkCount)
    {
        this.linkCount = linkCount;
    }
    
    public void setLinkStatus(byte linkStatus)
    {
        this.linkStatus = linkStatus;
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
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
    }
    
    public void setModifiedByName(String modifiedByName)
    {
        this.modifiedByName = modifiedByName;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public void setObjectId(String objectId)
    {
        this.objectId = objectId;
    }
    
    public void setOwnedBy(long ownedBy)
    {
        this.ownedBy = ownedBy;
    }
    
    public void setParentId(long parentId)
    {
        this.parentId = parentId;
    }
    
    public void setPath(String path)
    {
        this.path = path;
    }
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }
    
    public void setResourceGroupId(int resourceGroupID)
    {
        this.resourceGroupId = resourceGroupID;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setShareStatus(byte shareStatus)
    {
        this.shareStatus = shareStatus;
    }
    
    public void setSize(Long size)
    {
        this.size = size;
    }
    
    public void setStatus(byte status)
    {
        this.status = status;
    }
    
    public void setSyncStatus(byte syncStatus)
    {
        this.syncStatus = syncStatus;
    }
    
    public void setSyncVersion(long syncVersion)
    {
        this.syncVersion = syncVersion;
    }
    
    public void setTableSuffix(int tableSuffix)
    {
        this.tableSuffix = tableSuffix;
    }
    
    public void setThumbnailBigURL(String thumbnailBigURL)
    {
        this.thumbnailBigURL = thumbnailBigURL;
    }
    
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }
    
    public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList)
    {
        this.thumbnailUrlList = thumbnailUrlList;
    }
    
    public void setType(byte type)
    {
        this.type = type;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public void setVersions(int versions)
    {
        this.versions = versions;
    }
    
    public int getViewFlag() {
		return viewFlag;
	}

	public void setViewFlag(int viewFlag) {
		this.viewFlag = viewFlag;
	}

	private void initBaseBaseObject(RestBaseObject baseObject)
    {
        this.setId(baseObject.getId());
        this.setType(baseObject.getType());
        this.setSize(baseObject.getSize());
        this.setName(baseObject.getName());
        this.setStatus(baseObject.getStatus());
        this.setCreatedAt(baseObject.getCreatedAt());
        this.setModifiedAt(baseObject.getModifiedAt());
        this.setOwnedBy(baseObject.getOwnedBy());
        this.setCreatedBy(baseObject.getCreatedBy());
        this.setModifiedBy(baseObject.getModifiedBy());
        this.setParentId(baseObject.getParent());
        this.setContentCreatedAt(baseObject.getContentCreatedAt() == null ? null : new Date(
            baseObject.getContentCreatedAt()));
        this.setContentModifiedAt(baseObject.getContentModifiedAt() == null ? null : new Date(
            baseObject.getContentModifiedAt()));
        
    }
    
    public Integer getDocType()
    {
         return docType;
    }


    public void setDocType(Integer docType)
    {
         this.docType = docType;
    }

    public List<BaseFileLabelInfo> getFileLabels() {
        return fileLabels;
    }

    public void setFileLabels(List<BaseFileLabelInfo> fileLabels) {
        this.fileLabels = fileLabels;
    }

	public byte getSecretLevel() {
		return secretLevel;
	}

	public void setSecretLevel(byte secretLevel) {
		this.secretLevel = secretLevel;
	}

	public Boolean getIsSecret() {
		return isSecret;
	}

	public void setIsSecret(Boolean isSecret) {
		this.isSecret = isSecret;
	}

	public Boolean getIsListAcl() {
		return isListAcl;
	}

	public void setIsListAcl(Boolean isListAcl) {
		this.isListAcl = isListAcl;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}
}
