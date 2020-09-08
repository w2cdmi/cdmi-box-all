package pw.cdmi.box.disk.httpclient.rest.request;

import java.io.Serializable;

public class RestUserLoginCreateRequest implements Serializable{
	private String appId;

	private String loginName;

	private String password;
	
	private String domain;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
}
