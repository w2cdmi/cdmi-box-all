package com.huawei.sharedrive.app.files.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.filelabel.domain.BaseFileLabelInfo;
import com.huawei.sharedrive.app.filelabel.domain.FileLabel;
import com.huawei.sharedrive.app.utils.BusinessConstants;

/**
 * 节点对象,包括文件,文件夹及文件版本
 * 
 */
public class INode implements Serializable
{
    public static final String FIRST_VER_NUM = "1";
    
    /** 文件安全类型未设置 */
    public static final byte SECURITY_ID_UNSET = -1;
    
    /** 根节点ID */
    public final static long FILES_ROOT = 0;
    
    public final static int SHA1_LENGTH = 32;
    
    
    
    
    
    


    /** INODE状态 0.正常 */
    public final static byte STATUS_NORMAL = 0;

    /** INODE状态 1.创建中 */
    public final static byte STATUS_CREATING = 1;

    /** INODE状态 2.回收站 */
    public final static byte STATUS_TRASH = 2;

    /** INODE状态 3.回收站节点的子节点 */
    public final static byte STATUS_TRASH_DELETE = 3;

    /** INODE状态 4.删除 */
    public final static byte STATUS_DELETE = 4;
    
    
    
    
    
    
    /** 共享状态 1.已共享 */
    public final static byte SHARE_STATUS_SHARED = 1;
    
    /** 共享状态 0.未共享 */
    public final static byte SHARE_STATUS_UNSHARED = 0;
    
    /** 设置外链状态 1.已设置外链 */
    public final static byte LINK_STATUS_SET = 1;
    
    /** 设置外链状态 0.未设置外链 */
    public final static byte LINK_STATUS_UNSET = 0;
    
    
    
    
    /** 同步状态 0.未设置同步 */
    public final static byte SYNC_STATUS_UNSET = 0;
    
    /** 同步状态 1.已设置同步 */
    public final static byte SYNC_STATUS_SETTED = 1;
    
    /** 同步状态 2.同步目录下的子项目 */
    public final static byte SYNC_STATUS_SUBITEM = 2;
    
    /** 同步状态 3.全盘备份文件夹的同步状态 */
    public final static byte SYNC_STATUS_BACKUP = 3;
    
    /** 同步状态 4.邮件归档文件夹，及其子文件夹 */
    public final static byte SYNC_STATUS_EMAIL = 4;

    
    
    
    
    
    /** 默认同步版本号 */
    public final static long SYNC_VERSION_DEFAULT = 0;
    
    /** 清空回收站时传-1给PC客户端，通知其获取全量元数据 */
    public final static long SYNC_VERSION_DELETE = -1;
    
    
    /** INODE类型 1.文件 */
    public final static byte TYPE_FILE = 1;
    
    /** INODE类型 -1.所有类型 */
    public final static byte TYPE_ALL = -1;
    
    /** INODE类型 0.文件夹 */
    public final static byte TYPE_FOLDER = 0;
    
    /** INODE类型-10.文件夹类型集合: -4,-3,-2,0 */
    public final static byte TYPE_FOLDER_ALL = -10;
    
    /**收件箱 */
    public final static byte TYPE_INBOX = -7;
    
    /** INODE类型 -6.数据迁移文件夹：离职用户个人文件在接受用户个人文件中的文件夹 */
    public final static byte TYPE_MIGRATION = -6;
    
    /**微信文件夹 */
    public final static byte TYPE_WECHAT = -5;
    
    /**微信相册 */
    public final static byte TYPE_IMAGE = -51;
    
    /**微信文档*/
    public final static byte TYPE_DOCUMENT = -52;
    
    /**微信视频*/
    public final static byte TYPE_VIDEO = -53;
    
    /** INODE类型 -4.邮件归档文件夹 */
    public final static byte TYPE_BACKUP_EMAIL = -4;
    
    /** INODE类型 -3.全盘备份的计算机名文件夹 */
    public final static byte TYPE_BACKUP_COMPUTER = -3;
    
    /** INODE类型 -2.全盘备份的盘符文件夹 */
    public final static byte TYPE_BACKUP_DISK = -2;
    
  
    

    
    public final static String TYPE_MIGRATION_STR = "migration";
    
    public final static String TYPE_WECHAT_STR = "wechat";
    
    public final static String TYPE_BACKUP_EMAIL_STR = "email";
    
    public final static String TYPE_BACKUP_COMPUTER_STR = "computer";
    
    public final static String TYPE_BACKUP_DISK_STR = "disk";
    
    /** INODE类型 2.版本 */
    public final static byte TYPE_VERSION = 2;


    

   
    
    /**
     * 如果是inode节点支持多外链，则在linkcode字段作区分， 调用新接口设置外链时，如果linkcode字段为空，则设置“true”字段，否则不做操作
     * 调用老接口设置外链时，如果linkcode字段为空或为“true”字段，则设置外链码改，否则提示外链已存在异常
     * 
     * */
    public final static String LINKCODE_NEW_SET = "true";
    
    private static final long serialVersionUID = 7124022200504836063L;
    
    // 限定年份1971至2300
    private static final int DATE_YEAR_BEGIN = 1965;
    
    private static final int DATE_YEAR_END = 2400;
    
    private String blockMD5;
    
    private static final int BASE_MASK_LENGTH = 4;
    
    private static final int VIRUS_MASK_LENGTH = 3;
    
    private static final int BASE_MASK_VALUE = 7;
    
    private static final int KIA_MASK_VALUE = 7;
    
    private static final int VIRUS_MASK_VALUE = 1;
    
    public static final long KIA_LABEL_UNSET = 0L;
    
    /** KIA安全标示,采用版本号+是否KIA标示,其中低2位是kiastatus ,高位是kiaversion */
    private long kiaLabel = KIA_LABEL_UNSET;
    
    /** 客户端创建时间 */
    private Date contentCreatedAt;
    
    /** 客户端修改时间 */
    private Date contentModifiedAt;
    
    /** 创建时间 */
    private Date createdAt;
    
    /** 创建者 */
    private long createdBy;
    
    /** 节点描述 */
    private String description;
    
    /** 加密Key */
    private String encryptKey;
    
    /** 文件数,仅供文件夹删除/复制时统计使用 */
    private long fileCount = 1L;
    
    /** 节点ID */
    private Long id;
    
    /** 链接码 */
    private String linkCode;
    
    /** 外链是否设置 */
    private byte linkStatus;
    
    private String md5;
    
    /** 修改时间 */
    private Date modifiedAt;
    
    /** 修改者 */
    private long modifiedBy;
    
    /** 节点名称 */
    private String name;
    
    /** 对象ID */
    private String objectId;
    
    /** 资源拥有者 */
    private long ownedBy;
    
    /** 节点父ID */
    private long parentId;
    
    /** 资源组ID */
    private int resourceGroupId;
    
    /** sha1值，闪传使用 */
    private String sha1 = "";
    
    /** 共享状态 */
    private byte shareStatus;
    
    /** 对象大小 */
    private long size;
    
    /** 对象状态 */
    private byte status;
    
    /** 同步状态 */
    private byte syncStatus;
    
    /** 同步版本号 */
    private long syncVersion;
    
    private int tableSuffix;
    
    /** 大缩略图URL V1接口使用 */
    private String thumbnailBigURL;
    
    /** 小缩略图URL V1接口使用 */
    private String thumbnailUrl;
    
    /** 缩略图URL V2接口使用 */
    private List<ThumbnailUrl> thumbnailUrlList;
    
    /** 节点类型，0-目录；1-文件；2-文件版本 */
    private Byte type;
    
    /** 版本ID(Object ID, V1版本使用) */
    private String version;
    
    /** 文件历史版本总数, V2版本新增 */
    private int versions;
    
    private int linkCount;
    
    private boolean previewable;
    
    private byte secretLevel;
    
    /** 安全类型id */
    private byte securityId = SECURITY_ID_UNSET;
    
    private Integer doctype;
    /** 文件标签返回属性 */
    private transient String filelabelIds;
    /** 文件标签属性 */
    private transient List<BaseFileLabelInfo> fileLabelList;
    /** 原始节点类型 */
    private transient Long primaryNodeType;
    
    //文件夹是否为私密  列举时设置
    private Boolean isSecret;
    
    //如果私密文件，判断用户是否拥有浏览权限
    private Boolean isListAcl;
    
    public INode()
    {
        this.syncStatus = SYNC_STATUS_UNSET;
        this.shareStatus = SHARE_STATUS_UNSHARED;
        this.syncVersion = SYNC_VERSION_DEFAULT;
        thumbnailUrlList = new ArrayList<ThumbnailUrl>(BusinessConstants.INITIAL_CAPACITIES);
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
    
    public void setSyncStatusFromParent(INode parentNode)
    {
        this.setSyncStatus(parentNode.getSyncStatus());
        this.setSyncVersion(parentNode.getSyncVersion());
        this.setModifiedAt(new Date());
        this.setModifiedBy(parentNode.getModifiedBy());
    }
    
    public static INode valueOf(INode srcNode)
    {
        INode node = new INode();
        node.copyFrom(srcNode);
        return node;
    }
    
    public void addThumbnailUrl(ThumbnailUrl url)
    {
        if (url == null)
        {
            return;
        }
        if (thumbnailUrlList == null)
        {
            thumbnailUrlList = new ArrayList<ThumbnailUrl>(BusinessConstants.INITIAL_CAPACITIES);
        }
        thumbnailUrlList.add(url);
    }
    
    public String getBlockMD5()
    {
        return blockMD5;
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
    
    public long getFileCount()
    {
        return fileCount;
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
    
    public String getMd5()
    {
        return md5;
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
    
    public int getResourceGroupId()
    {
        return resourceGroupId;
    }
    
    public byte getSecurityId()
    {
        return securityId;
    }
    
    public String getSha1()
    {
        return sha1;
    }
    
    public byte getShareStatus()
    {
        return shareStatus;
    }
    
    public long getSize()
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
    
    public void setBlockMD5(String blockMD5)
    {
        this.blockMD5 = blockMD5;
    }
    
    public void setContentCreatedAt(Date contentCreatedAt)
    {
        if (contentCreatedAt == null)
        {
            this.contentCreatedAt = null;
        }
        else
        {
            Date obj = checkAndGetValidDate(contentCreatedAt);
            if (null != obj)
            {
                this.contentCreatedAt = (Date) obj.clone();
            }
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
            Date obj = checkAndGetValidDate(contentModifiedAt);
            if (null != obj)
            {
                this.contentModifiedAt = (Date) obj.clone();
            }
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
            this.createdAt = (Date) checkAndGetValidDate(createdAt).clone();
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
    
    public void setFileCount(long fileCount)
    {
        this.fileCount = fileCount;
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
    
    public void setMd5(String md5)
    {
        this.md5 = md5;
    }
    
    public void setModifiedAt(Date modifiedAt)
    {
        if (modifiedAt == null)
        {
            this.modifiedAt = null;
        }
        else
        {
            this.modifiedAt = (Date) checkAndGetValidDate(modifiedAt).clone();
        }
    }
    
    public void setModifiedBy(long modifiedBy)
    {
        this.modifiedBy = modifiedBy;
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
    
    public void setPreviewable(boolean previewable)
    {
        this.previewable = previewable;
    }
    
    public void setResourceGroupId(int resourceGroupID)
    {
        this.resourceGroupId = resourceGroupID;
    }
    
    public void setSecurityId(byte securityId)
    {
        this.securityId = securityId;
    }
    
    public void setSha1(String sha1)
    {
        this.sha1 = sha1;
    }
    
    public void setShareStatus(byte shareStatus)
    {
        this.shareStatus = shareStatus;
    }
    
    public void setSize(long size)
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
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public void setVersions(int versions)
    {
        this.versions = versions;
    }
    
    private Date checkAndGetValidDate(Date input)
    {
        if (input == null)
        {
            return null;
        }
        
        Calendar ca = Calendar.getInstance();
        ca.setTime(input);
        if (ca.before(getCalendarByYear(DATE_YEAR_BEGIN)) || ca.after(getCalendarByYear(DATE_YEAR_END)))
        {
            return null;
        }
        return input;
    }
    
    public Integer getDoctype()
    {
         return doctype;
    }


    public void setDoctype(Integer doctype)
    {
         this.doctype = doctype;
    }
    
    /**
     * 
     * @param year 年
     * @return Calendar 转换后的Calendar
     */
    private Calendar getCalendarByYear(int year)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar;
    }
    
    public String getFilelabelIds() {
        return filelabelIds;
    }

    public void setFilelabelIds(String filelabelIds) {
        this.filelabelIds = filelabelIds;
    }

    public void copyFrom(INode srcNode)
    {
        this.setBlockMD5(srcNode.getBlockMD5());
        this.setContentCreatedAt(srcNode.getContentCreatedAt());
        this.setContentModifiedAt(srcNode.getContentModifiedAt());
        this.setCreatedAt(srcNode.getCreatedAt());
        this.setCreatedBy(srcNode.getCreatedBy());
        this.setDescription(srcNode.getDescription());
        this.setEncryptKey(srcNode.getEncryptKey());
        this.setFileCount(srcNode.getFileCount());
        this.setId(srcNode.getId());
        this.setLinkCode(srcNode.getLinkCode());
        this.setLinkStatus(srcNode.getLinkStatus());
        this.setMd5(srcNode.getMd5());
        this.setModifiedAt(srcNode.getModifiedAt());
        this.setModifiedBy(srcNode.getModifiedBy());
        this.setName(srcNode.getName());
        this.setObjectId(srcNode.getObjectId());
        this.setOwnedBy(srcNode.getOwnedBy());
        this.setParentId(srcNode.getParentId());
        this.setResourceGroupId(srcNode.getResourceGroupId());
        this.setSha1(srcNode.getSha1());
        this.setShareStatus(srcNode.getShareStatus());
        this.setSize(srcNode.getSize());
        this.setStatus(srcNode.getStatus());
        this.setSyncStatus(srcNode.getSyncStatus());
        this.setSyncVersion(srcNode.getSyncVersion());
        this.setTableSuffix(srcNode.getTableSuffix());
        this.setThumbnailBigURL(srcNode.getThumbnailBigURL());
        this.setThumbnailUrl(srcNode.getThumbnailUrl());
        this.setThumbnailUrlList(srcNode.getThumbnailUrlList());
        this.setType(srcNode.getType());
        this.setVersion(srcNode.getVersion());
        this.setVersions(srcNode.getVersions());
        this.setLinkCount(srcNode.getLinkCount());
        this.setPreviewable(srcNode.isPreviewable());
        this.setSecurityId(srcNode.getSecurityId());
        this.setKiaLabel(srcNode.getKiaLabel());
        this.setDoctype(srcNode.getDoctype());
    }
    
    public long getKiaLabel()
    {
        return kiaLabel;
    }
    
    public void setKiaLabel(long kiaLabel)
    {
        this.kiaLabel = kiaLabel;
    }
    
    // KIA安全标示,采用版本号+是否KIA标示,其中低2位是kiastatus ,高位是kiaversion
    public static long buildKIALabel(int kiaVersion, int kiaStatus)
    {
        long kiaLable = 0L;
        if (kiaVersion > 0)
        {
            kiaLable = kiaVersion << BASE_MASK_LENGTH;
        }
        kiaLable = kiaLable + kiaStatus;
        return kiaLable;
    }
    
    // KIA安全标示,采用版本号+是否KIA标示,其中低2位是kiastatus ,高位是kiaversion
    public static int getKiaStatusFromKIALabel(long kiaLable)
    {
        return (int) (kiaLable & KIA_MASK_VALUE);
    }
    
    // 判断是否有病毒，右移3位，然后根据倒数第一位的取值
    public static boolean getVirusStatusFromKIALabel(long kiaLable)
    {
        long kiaStatus = ((kiaLable >> VIRUS_MASK_LENGTH ) & VIRUS_MASK_VALUE);
        return kiaStatus == 0 ? false:true;
    }
    
    // KIA安全标示,采用版本号+是否KIA标示,其中低2位是kiastatus ,高位是kiaversion
    public static int getKiaVersionFromKIALabel(long kiaLable)
    {
        return (int) (kiaLable >> BASE_MASK_LENGTH);
    }
    
    public void copyDest(long userId, long newId, INode destFolder)
    {
        this.setId(newId);
        this.setParentId(destFolder.getId());
        Date date = new Date();
        this.setCreatedAt(date);
        this.setCreatedBy(userId);
        this.setModifiedAt(date);
        this.setModifiedBy(userId);
        this.setOwnedBy(destFolder.getOwnedBy());
        this.setVersion(FIRST_VER_NUM);
        
        // 复制节点需要去掉共享外链状态
        this.setLinkCode(null);
        this.setShareStatus(INode.SHARE_STATUS_UNSHARED);
        this.setDoctype(destFolder.doctype);
    }
    

    public List<BaseFileLabelInfo> getFileLabelList() {
        return fileLabelList;
    }


    public Long getPrimaryNodeType() {
		return primaryNodeType;
	}

	public void setPrimaryNodeType(Long primaryNodeType) {
		this.primaryNodeType = primaryNodeType;
	}

	public void setFileLabelList(List<FileLabel> fls) {
        if (fls != null){
            if (this.fileLabelList == null){
                fileLabelList = new ArrayList<BaseFileLabelInfo>();
            }
            
            for(FileLabel fl : fls){
                fileLabelList.add(new BaseFileLabelInfo(fl.getId(), fl.getLabelName())) ;
            } 
        }
       
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

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}
