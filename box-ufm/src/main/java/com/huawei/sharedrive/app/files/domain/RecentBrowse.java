package com.huawei.sharedrive.app.files.domain;

import java.util.Date;

public class RecentBrowse {
    private long inodeId;
    private long ownedBy;
    private long accountId;
    private long userId;
    private Date lastBrowseTime;
	public long getInodeId() {
		return inodeId;
	}
	public void setInodeId(long inodeId) {
		this.inodeId = inodeId;
	}
	public long getOwnedBy() {
		return ownedBy;
	}
	public void setOwnedBy(long ownedBy) {
		this.ownedBy = ownedBy;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public Date getLastBrowseTime() {
		return lastBrowseTime;
	}
	public void setLastBrowseTime(Date lastBrowseTime) {
		this.lastBrowseTime = lastBrowseTime;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
    
	
	  
}
