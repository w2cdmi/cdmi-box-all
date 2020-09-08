package pw.cdmi.box.disk.user.domain;

import java.util.Date;

public class WxRobot {
	
	
	
	public static byte STATUS_RUNNING=1;
	
	public static byte STATUS_STOP=0;
	
	public static byte STATUS_NONSUPPORT=2;
	
	private long id;
	private long accountId;
	private long cloudUserId;
	private String wxUin;
	private String wxName;
	//是否运行
	private byte status;
	//备份策略
	private Date createdAt;
	private Date lastStartAt;
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public long getCloudUserId() {
		return cloudUserId;
	}
	public void setCloudUserId(long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}
	public String getWxUin() {
		return wxUin;
	}
	public void setWxUin(String wxUin) {
		this.wxUin = wxUin;
	}
	public String getWxName() {
		return wxName;
	}
	public void setWxName(String wxName) {
		this.wxName = wxName;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getLastStartAt() {
		return lastStartAt;
	}
	public void setLastStartAt(Date lastStartAt) {
		this.lastStartAt = lastStartAt;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	

}
