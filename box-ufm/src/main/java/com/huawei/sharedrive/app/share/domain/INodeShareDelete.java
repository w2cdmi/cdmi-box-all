package com.huawei.sharedrive.app.share.domain;


public class INodeShareDelete {

    public static final byte TAG_DELETE = 1;
    
    private long deleteUserId;
    
    /** 被共享者ID */
    private Long sharedUserId;
	
    /** 共享项目ID */
    private long iNodeId;
    
    /** 共享资源拥有者ID */
    private long ownerId;
    
    /** 被共享者类型 */
    private byte sharedUserType;
    
    /** 分享类型 */
    private String shareType;
    
    /** 分享类型 */
    private String linkCode;
    
    private byte tag;

	public long getiNodeId() {
		return iNodeId;
	}

	public void setiNodeId(long iNodeId) {
		this.iNodeId = iNodeId;
	}

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getSharedUserId() {
		return sharedUserId;
	}

	public void setSharedUserId(Long sharedUserId) {
		this.sharedUserId = sharedUserId;
	}

	public byte getSharedUserType() {
		return sharedUserType;
	}

	public void setSharedUserType(byte sharedUserType) {
		this.sharedUserType = sharedUserType;
	}

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public String getLinkCode() {
		return linkCode;
	}

	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}

	public byte getTag() {
		return tag;
	}

	public void setTag(byte tag) {
		this.tag = tag;
	}

	public long getDeleteUserId() {
		return deleteUserId;
	}

	public void setDeleteUserId(long deleteUserId) {
		this.deleteUserId = deleteUserId;
	}
    
	
 

}
