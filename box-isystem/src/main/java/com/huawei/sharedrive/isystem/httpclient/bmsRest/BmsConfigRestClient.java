package com.huawei.sharedrive.isystem.httpclient.bmsRest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.sharedrive.isystem.httpclient.BMSRestClient;
import com.huawei.sharedrive.isystem.initConfig.domain.AccessAddressConfig;
import com.huawei.sharedrive.isystem.initConfig.domain.AdminAccount;
import com.huawei.sharedrive.isystem.initConfig.domain.BmsUser;
import com.huawei.sharedrive.isystem.initConfig.domain.Enterprise;
import com.huawei.sharedrive.isystem.initConfig.domain.RestAdmins;

import pw.cdmi.common.domain.AppAccessKey;
import pw.cdmi.common.domain.MailServer;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.uam.domain.AuthApp;

public class BmsConfigRestClient {

	private static final String BMS_URL = "/api/v2/isystem";

	private BMSRestClient bmsRestClient;

	public BmsConfigRestClient(BMSRestClient client) {
		this.bmsRestClient = client;
	}

	public String createMail(MailServer request) {

		String url = BMS_URL + "/config/mail";

		TextResponse response = bmsRestClient.performJsonPostTextResponse(url, null, request);

		String responseStr = response.getResponseBody();

		return responseStr;

	}

	public String configAccessAddress(AccessAddressConfig accessAddressConfig) {
		String url = BMS_URL + "/config/accessAddress";

		TextResponse response = bmsRestClient.performJsonPostTextResponse(url, null, accessAddressConfig);

		String responseStr = response.getResponseBody();

		return responseStr;
	}

	public AccessAddressConfig getAccessAddress() {
		String url = BMS_URL + "/find/accessAddress";

		TextResponse response = bmsRestClient.performGetText(url, null);

		AccessAddressConfig responseStr = JsonUtils.stringToObject(response.getResponseBody(),
				AccessAddressConfig.class);

		return responseStr;
	}

	public List<AdminAccount> getAllBMSAdmin() {

		String url = BMS_URL + "/list/bms/admin";

		TextResponse response = bmsRestClient.performGetText(url, null);
		String json = response.getResponseBody();
		RestAdmins restAdmins = JsonUtils.stringToObject(json, RestAdmins.class);

		return restAdmins.getAdmins();
	}

	public Enterprise findEnterpriseById(long id) {
		String url = BMS_URL + "/find/enterprise";
		Map<String, String> headers = new HashMap<String, String>(2);
		headers.put("enterpriseId", String.valueOf(id));
		TextResponse response = bmsRestClient.performGetText(url, headers);
		String json = response.getResponseBody();
		Enterprise enterprise = JsonUtils.stringToObject(json, Enterprise.class);
		return enterprise;
	}

	public Enterprise findEnterpriseByOwnerId(long id) {
		String url = BMS_URL + "/find/carrier";
		Map<String, String> headers = new HashMap<String, String>(2);
		headers.put("ownerId", String.valueOf(id));
		TextResponse response = bmsRestClient.performGetText(url, headers);
		String json = response.getResponseBody();
		Enterprise enterprise = JsonUtils.stringToObject(json, Enterprise.class);
		return enterprise;
	}
	
	public String configBmsUserLocal(BmsUser admin) {
		String url = BMS_URL + "/config/bms/add";

		TextResponse response = bmsRestClient.performJsonPostTextResponse(url, null, admin);

		String responseStr = response.getResponseBody();

		return responseStr;
	}

	public String configBmsApp(AppAccessKey accessKey, String appId) {
		String url = BMS_URL + "/config/bmsApp/add";

		AuthApp authApp = new AuthApp();
		authApp.setAuthAppId(accessKey.getAppId());
		authApp.setUfmAccessKeyId(accessKey.getId());
		authApp.setUfmSecretKey(accessKey.getSecretKey());
		// authApp.setu
		authApp.setType((byte) 1);
		TextResponse response = bmsRestClient.performJsonPostTextResponse(url, null, authApp);

		String responseStr = response.getResponseBody();

		return responseStr;
	}

	public TextResponse configEnterprise(Enterprise enterprise) {
		String url = BMS_URL + "/config/enterprise/add";

		// Map<String, String> headers = new HashMap<String, String>(2);
		// headers.put("language", language);
		// headers.put("appId", appId);

		TextResponse response = bmsRestClient.performJsonPostTextResponse(url, null, enterprise);

		// String responseStr = response.getResponseBody();

		return response;
	}

	public TextResponse updateEnterprise(Enterprise enterprise) {
		String url = BMS_URL + "/config/enterprise/update";
		TextResponse response = bmsRestClient.performJsonPostTextResponse(url, null, enterprise);

		return response;
	}

	public String configEnterpriseAdmin(String email, String language, String appId) {
		String url = BMS_URL + "/config/enterprise/admin/add";

		Map<String, String> headers = new HashMap<String, String>(3);
		headers.put("language", language);
		headers.put("appId", appId);
		headers.put("contactEmail", email);

		TextResponse response = bmsRestClient.performJsonPostTextResponse(url, headers,null);

		String responseStr = response.getResponseBody();

		return responseStr;
	}
	
	public String saveAppBaseConfig(String appId){
		String url = BMS_URL + "/save/appBasic";

		Map<String, String> headers = new HashMap<String, String>(3);
		headers.put("appId", appId);

		TextResponse response = bmsRestClient.performJsonPostTextResponse(url, headers,null);

		String responseStr = response.getResponseBody();

		return responseStr;
	}

}
