package pw.cdmi.box.disk.httpclient.rest.request;

public class RobotLoginRequest extends UserLoginRequest{

	private long enterpriseUserId;
	private long enterpriseId;
	public long getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public long getEnterpriseUserId() {
		return enterpriseUserId;
	}
	public void setEnterpriseUserId(long enterpriseUserId) {
		this.enterpriseUserId = enterpriseUserId;
	}
	
	
}
