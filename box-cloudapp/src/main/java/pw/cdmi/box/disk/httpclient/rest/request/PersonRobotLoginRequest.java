package pw.cdmi.box.disk.httpclient.rest.request;

public class PersonRobotLoginRequest extends UserLoginRequest{

	
    private String wxUnionId;
	
	private String appId;

	public String getWxUnionId() {
		return wxUnionId;
	}

	public void setWxUnionId(String wxUnionId) {
		this.wxUnionId = wxUnionId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
	
}
