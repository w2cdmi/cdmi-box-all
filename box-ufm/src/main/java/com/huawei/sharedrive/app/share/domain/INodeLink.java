package com.huawei.sharedrive.app.share.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huawei.sharedrive.app.core.domain.ThumbnailUrl;
import com.huawei.sharedrive.app.files.domain.INode;
import com.huawei.sharedrive.app.openapi.domain.share.LinkIdentityInfo;
import com.huawei.sharedrive.app.utils.BusinessConstants;


public class INodeLink implements Serializable
{
	public static final byte STATUS_STATIC = 1;

	public static final String PLAINACCESSCODE_STATIC = "static";

	public static final byte STATUS_MAIL = 2;

	public static final String PLAINACCESSCODE_MAIL = "mail";

	public static final byte STATUS_PHONE = 3;

	public static final String PLAINACCESSCODE_PHONE = "phone";

	private static final long serialVersionUID = 986694753446784873L;

	private String access;

	private long createdBy;

	private String creator;

	@JsonIgnore
	private String downloadUrl;

	private Date effectiveAt;

	private Date expireAt;

	private Date createdAt;

	private Date modifiedAt;

	private String id;

	private long modifiedBy;

	private Long iNodeId;

	private long ownedBy;

	@JsonIgnore
	private String password;

	private String encryptedPassword;

	private String passwordKey;

	/**
	 * 动态提前码 如果是静态提前码时，为空
	 */
	private String plainAccessCode;

	private String role;
	/**
	 * 是否登录
	 */
	private boolean needLogin;
	/**
	 * 提取码状态 1-固定，2-邮箱，3-手机
	 */
	@JsonIgnore
	private byte status;

	/**
	 * 对应状态的字符串 1-static，2-mail，3-phone
	 */
	private String accessCodeMode;
	/***
	 * 对应的认证信息 状态1为null，2则代表邮箱， 3则代表手机号
	 */
	private List<LinkIdentityInfo> identities;

	private String subINodes;

	private List<INode> subFileList;

	// 不能转存
	private Boolean disdump;

	private Boolean isProgram;

	private String alias;

	private List<ThumbnailUrl> thumbnailUrlList;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public List<INode> getSubFileList() {
		return subFileList;
	}

	public void setSubFileList(List<INode> subFileList) {
		this.subFileList = subFileList;
	}

	private int tableSuffix;

	private String url;

	public INodeLink() {

	}

	public INodeLink(String linkCode) {
		// TODO Auto-generated constructor stub
		this.id = linkCode;
	}

	public INodeLink(String id, String access, byte status, String url) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.access = access;
		this.status = status;
		this.url = url;

	}

	public String getAccess() {
		return this.access;
	}

	public long getCreatedBy() {
		return this.createdBy;
	}

	public String getCreator() {
		return this.creator;
	}

	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	public String getId() {
		return this.id;
	}

	public long getModifiedBy() {
		return this.modifiedBy;
	}

	public long getOwnedBy() {
		return this.ownedBy;
	}

	public String getPassword() {
		return this.password;
	}

	public String getPlainAccessCode() {
		return this.plainAccessCode;
	}

	public String getRole() {
		return this.role;
	}

	public byte getStatus() {
		return this.status;
	}

	public int getTableSuffix() {
		return this.tableSuffix;
	}

	public String getUrl() {
		return this.url;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setModifiedBy(long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setOwnedBy(long ownedBy) {
		this.ownedBy = ownedBy;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPlainAccessCode(String plainAccessCode) {
		this.plainAccessCode = plainAccessCode;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public void setTableSuffix(int tableSuffix) {
		this.tableSuffix = tableSuffix;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccessCodeMode() {
		return accessCodeMode;
	}

	public void setAccessCodeMode(String accessCodeMode) {
		this.accessCodeMode = accessCodeMode;
	}

	public List<LinkIdentityInfo> getIdentities() {
		return identities;
	}

	public void setIdentities(List<LinkIdentityInfo> identities) {
		this.identities = identities;
	}

	public String getSubINodes() {
		return subINodes;
	}

	public void setSubINodes(String subINodes) {
		this.subINodes = subINodes;
	}

	public Long getiNodeId() {
		return iNodeId;
	}

	public void setiNodeId(Long iNodeId) {
		this.iNodeId = iNodeId;
	}

	public Date getEffectiveAt() {
		return effectiveAt;
	}

	public void setEffectiveAt(Date effectiveAt) {
		this.effectiveAt = effectiveAt;
	}

	public Date getExpireAt() {
		return expireAt;
	}

	public void setExpireAt(Date expireAt) {
		this.expireAt = expireAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getPasswordKey() {
		return passwordKey;
	}

	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}

	public boolean isNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}

	public Boolean getDisdump() {
		return disdump;
	}

	public void setDisdump(Boolean disdump) {
		this.disdump = disdump;
	}

	public Boolean getIsProgram() {
		return isProgram;
	}

	public void setIsProgram(Boolean isProgram) {
		this.isProgram = isProgram;
	}

	public List<ThumbnailUrl> getThumbnailUrlList() {
		return thumbnailUrlList;
	}

	public void setThumbnailUrlList(List<ThumbnailUrl> thumbnailUrlList) {
		this.thumbnailUrlList = thumbnailUrlList;
	}

	public void addThumbnailUrl(ThumbnailUrl url) {
		if (url == null) {
			return;
		}
		if (thumbnailUrlList == null) {
			thumbnailUrlList = new ArrayList<ThumbnailUrl>(BusinessConstants.INITIAL_CAPACITIES);
		}
		thumbnailUrlList.add(url);
	}

}