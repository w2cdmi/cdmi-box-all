package pw.cdmi.box.disk.share.domain;

//审批关系表
public class LinkApproveUser {
	
	/**
	 * 1.主送2.抄送
	 **/
	public static final byte TYPE_MASTER = 1;
	
	public static final byte TYPE_ASSISTANT = 2;
	
	private String linkCode;
	
	private byte type;
	//用户id
	private long cloudUserId;

	public String getLinkCode() {
		return linkCode;
	}

	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getCloudUserId() {
		return cloudUserId;
	}

	public void setCloudUserId(long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}
	
}
