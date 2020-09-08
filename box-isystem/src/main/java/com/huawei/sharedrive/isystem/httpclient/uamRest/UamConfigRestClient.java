package com.huawei.sharedrive.isystem.httpclient.uamRest;

import java.util.HashMap;
import java.util.Map;

import com.huawei.sharedrive.isystem.httpclient.UAMRestClient;
import com.huawei.sharedrive.isystem.initConfig.domain.Enterprise;

import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

public class UamConfigRestClient {
	private static final String UAM_URL = "/api/v2/isystem";

	private UAMRestClient uamRestClient;

	public UamConfigRestClient(UAMRestClient client) {
		this.uamRestClient = client;
	}

	public String enterpriseBindApp(Enterprise enterprise, String appId) {
		String url = UAM_URL + "/enterprise/app/bind";

		Map<String, String> headers = new HashMap<String, String>(1);
		headers.put("appId", appId);

		TextResponse response = uamRestClient.performJsonPostTextResponse(url, headers, enterprise);

		String responseStr = response.getResponseBody();

		return responseStr;
	}

	public String createEnterpriseUser(Enterprise enterprise, String appId) {
		String url = UAM_URL + "/enterprise/user/create";

		Map<String, String> headers = new HashMap<String, String>(1);
		headers.put("appId", appId);

		TextResponse response = uamRestClient.performJsonPostTextResponse(url, headers, enterprise);

		String responseStr = response.getResponseBody();

		return responseStr;
	}

	public Enterprise getEnterpriseUser(String loginName, String domain) {
		String url = UAM_URL + "/enterprise/user/get";

		Map<String, String> headers = new HashMap<String, String>(2);
		headers.put("loginName", loginName);
		headers.put("domain", domain);

		TextResponse response = uamRestClient.performGetText(url, headers);

		Enterprise enterpriseUser = JsonUtils.stringToObject(response.getResponseBody(), Enterprise.class);

		return enterpriseUser;
	}
}
