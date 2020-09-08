package com.huawei.sharedrive.app.files.domain;

public class INodeSecret {
	
	private long INodeId;
	
	private long ownedBy;
	
	private long accountId;
	
	private byte type;
	
	/** sha1值，闪传使用 */
    private String sha1 = ""; 
    
    private String blockMD5;
    
    private String md5;

	public long getINodeId() {
		return INodeId;
	}

	public void setINodeId(long iNodeId) {
		INodeId = iNodeId;
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

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public String getBlockMD5() {
		return blockMD5;
	}

	public void setBlockMD5(String blockMD5) {
		this.blockMD5 = blockMD5;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
    
    
    

}
