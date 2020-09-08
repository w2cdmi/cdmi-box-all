package pw.cdmi.box.disk.client.api.domain;

public class SecretStaff {


	private long accountId;
	private byte staffLevel;
	private byte secretLevel;
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public byte getStaffLevel() {
		return staffLevel;
	}
	public void setStaffLevel(byte staffLevel) {
		this.staffLevel = staffLevel;
	}
	public byte getSecretLevel() {
		return secretLevel;
	}
	public void setSecretLevel(byte secretLevel) {
		this.secretLevel = secretLevel;
	}
	


}
