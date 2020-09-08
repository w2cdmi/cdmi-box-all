package pw.cdmi.box.disk.files.domain;

public class ObjectSecretLevel {



	   /** 区域ID */
 private int regionId;
 
 /** 对象sha1值 */
 private String sha1;
 
 private byte secretLevel;
 
 private long accountId;
 
 private int tableSuffix;

	public int getRegionId() {
		return regionId;
	}

	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}

	public String getSha1() {
		return sha1;
	}

	public void setSha1(String sha1) {
		this.sha1 = sha1;
	}

	public byte getSecretLevel() {
		return secretLevel;
	}

	public void setSecretLevel(byte secretLevel) {
		this.secretLevel = secretLevel;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public int getTableSuffix() {
		return tableSuffix;
	}

	public void setTableSuffix(int tableSuffix) {
		this.tableSuffix = tableSuffix;
	}
 
 

}
