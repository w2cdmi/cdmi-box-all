package com.huawei.sharedrive.app.files.domain;

import java.util.Date;

public class Shortcut {
	
	public static byte TYPE_TEAM=2;
	
	public static byte TYPE_USER=1;
	
	private long id;
	private long createBy;
	private long nodeId;
	private long ownerId;
	private Date createAt;
	/**
	 * 冗余字段 ，用于界面展示
	 */
	private String nodeName;
	/**
	 * 冗余字段 ，用于界面展示
	 */
	private String ownerName;
	/**
	 * 个人文件目录 -1， 空间文件目录-2；
	 */
	private byte type;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getCreateBy() {
		return createBy;
	}
	public void setCreateBy(long createBy) {
		this.createBy = createBy;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public long getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	public Date getCreateAt() {
		return createAt;
	}
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	

}
