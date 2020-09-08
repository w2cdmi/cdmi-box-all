package pw.cdmi.box.disk.httpclient.rest;

import pw.cdmi.box.disk.oauth2.domain.UserToken;

public class UserTestHttpClient extends AbstractTestRestClientService {

	private UserToken currentUserToken;

	public UserTestHttpClient() {
	}

	public static UserTestHttpClient instance() {
		return new UserTestHttpClient();
	}

	public static UserTestHttpClient instance(UserToken userToken) {
		UserTestHttpClient instance = new UserTestHttpClient();
		instance.setUserToken(userToken);
		return instance;
	}

	@Override
	protected UserToken getUserToken() {
		if (currentUserToken == null) {
			currentUserToken = new UserToken();
		}
		return currentUserToken;
	}

	public void setUserToken(UserToken userToken) {
		this.currentUserToken = userToken;
	}

	@Override
	protected String getServerUrl() {
		return "http://10.169.37.104:8080";
	}
}
