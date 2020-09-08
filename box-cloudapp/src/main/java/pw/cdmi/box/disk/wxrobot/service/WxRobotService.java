package pw.cdmi.box.disk.wxrobot.service;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import pw.cdmi.box.disk.httpclient.rest.request.PersonRobotLoginRequest;
import pw.cdmi.box.disk.httpclient.rest.request.RestLoginResponse;
import pw.cdmi.box.disk.httpclient.rest.request.RobotLoginRequest;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.system.service.AccessAddressService;
import pw.cdmi.box.disk.user.domain.WxRobot;
import pw.cdmi.box.disk.wxrobot.model.WxRobotConfig;
import pw.cdmi.box.disk.wxrobot.util.CookieUtil;
import pw.cdmi.box.disk.wxrobot.util.Matchers;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.DateUtils;
import pw.cdmi.core.utils.JsonUtils;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * 微信API实现
 *
 * @author biezhi 16/06/2017
 */
@Component
public class WxRobotService {

	// private String redirect_uri;
	private static final Logger LOGGER = LoggerFactory.getLogger(WxRobotService.class);

	public static final String WX_PREFIX = "w";

	public static final int DEFAULT_LIMIT = 400;

	@Resource
	private RestClient uamClientService;

	@Autowired
	private AccessAddressService accessAddressService;

	@Resource(name = "cacheClient")
	private CacheClient cacheClient;

	WxRobotService() {
		System.setProperty("jsse.enableSNIExtension", "false");
	}

	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	public String getUUID() {
		String url = "https://login.weixin.qq.com/jslogin";
		HttpRequest request = HttpRequest.get(url, true, "appid", "wx782c26e4c19acffb", "fun", "new", "lang", "zh_CN",
				"_", DateKit.getCurrentUnixTime());
		String res = request.body();
		request.disconnect();
		if (StringKit.isNotBlank(res)) {
			String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
			if (null != code) {
				if (code.equals("200")) {
					return Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
				} else {
					LOGGER.info("[*] 错误的状态码: %s", code);
				}
			}
		}
		return null;
	}

	/**
	 * 等待登录
	 */
	public String waitForLogin(String uuid, JSONObject loginUser, int tip) {
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
		HttpRequest request = HttpRequest.get(url, true, "tip", tip, "uuid", uuid, "_", DateKit.getCurrentUnixTime());
		try {
			String res = request.body();
			request.disconnect();
			if (null == res) {
				return "fail";
			}
			String code = Matchers.match("window.code=(\\d+);", res);
			if (code.equals("200")) {
				String redirect_uri = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res) + "&fun=new";
				loginUser.put("redirect_uri", redirect_uri);
				loginUser.put("base_uri", redirect_uri.substring(0, redirect_uri.lastIndexOf("/")));
				String wxHost = redirect_uri.split("://")[1].split("/")[0];
				loginUser.put("wxHost", getWxHost(wxHost));
			}
			return code;
		} catch (Exception e) {
			// TODO: handle exception
			return "fail";
		}
	}

	/**
	 * 登录
	 */
	public JSONObject login(JSONObject loginUser,CacheClient cacheClient,long cloudUserId) {
		HttpRequest request = HttpRequest.get(loginUser.getString("redirect_uri"));
		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			return null;
		}
		if(res.indexOf("<ret>1203</ret>")>-1){
			cacheClient.addCache("r"+cloudUserId, "1203");
		}else{
			cacheClient.deleteCache("r"+cloudUserId);
		}
		loginUser.put("pass_ticket", Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res));
		loginUser.put("cookie", CookieUtil.getCookie(request));
		JSONObject BaseRequest = new JSONObject();
		BaseRequest.put("Uin", Matchers.match("<wxuin>(\\S+)</wxuin>", res));
		BaseRequest.put("Sid", Matchers.match("<wxsid>(\\S+)</wxsid>", res));
		BaseRequest.put("Skey", Matchers.match("<skey>(\\S+)</skey>", res));
		BaseRequest.put("DeviceID", "e" + DateKit.getCurrentUnixTime());
		loginUser.put("BaseRequest", BaseRequest);
		return loginUser;
	}

	/**
	 * 微信初始化
	 */
	public boolean wxInit(JSONObject loginUser,WxRobotService wxRobotService) {
		JSONObject baseRequset = loginUser.getJSONObject("BaseRequest");
		if(baseRequset.get("Uin").toString().equals("null")){
			WxRobot updateWxRobot=new WxRobot();
			updateWxRobot.setWxUin(baseRequset.getString("Uin"));
			updateWxRobot.setAccountId(loginUser.getLong("accountId", 0));
			updateWxRobot.setCloudUserId(loginUser.getLong("cloudUserId", 0));
			updateWxRobot.setStatus(WxRobot.STATUS_NONSUPPORT);
			wxRobotService.updateBySystem(updateWxRobot);
		}
		String url = loginUser.getString("base_uri") + "/webwxinit?r=" + DateKit.getCurrentUnixTime() + "&pass_ticket="
				+ loginUser.getString("pass_ticket") + "&skey=" + baseRequset.getString("Skey");
		HttpRequest request = HttpRequest.post(url).header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", loginUser.getString("cookie")).send(loginUser.toString());
		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			return false;
		}
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			if (null != jsonObject) {
				JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
				if (null != BaseResponse) {
					int ret = BaseResponse.getInt("Ret", -1);
					if (ret == 0) {
						loginUser.put("SyncKey", jsonObject.getJSONObject("SyncKey"));
						loginUser.put("user", jsonObject.getJSONObject("User"));
						JSONArray contactList = jsonObject.getJSONArray("ContactList");
						JSONArray groupList = new JSONArray();
						for (int i = 0; i < contactList.size(); i++) {
							if (contactList.getJSONObject(i).getString("UserName").indexOf("@@") != -1) {
								groupList.add(contactList.getJSONObject(i));
							}
						}
						loginUser.put("groupList", groupList);
						StringBuffer synckey = new StringBuffer();
						JSONArray list = loginUser.getJSONObject("SyncKey").getJSONArray("List");
						for (int i = 0, len = list.size(); i < len; i++) {
							JSONObject item = list.getJSONObject(i);
							synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
						}
						loginUser.put("SyncKeyString", synckey.substring(1));
						return true;
					}
				}
			}
		} catch (Exception e) {
		
		}
		return false;
	}

	/**
	 * 登录
	 */
	public boolean checkWxuserIsExist(JSONObject loginUser) {

		return true;
	}

	/**
	 * 微信状态通知
	 */
	public boolean wxStatusNotify(JSONObject loginUser) {

		String url = loginUser.getString("base_uri") + "/webwxstatusnotify?lang=zh_CN&pass_ticket="
				+ loginUser.getString("pass_ticket");

		JSONObject body = new JSONObject();
		body.put("BaseRequest", loginUser.getJSONObject("BaseRequest"));
		body.put("Code", 3);
		body.put("FromUserName", loginUser.getJSONObject("user").getString("UserName"));
		body.put("ToUserName", loginUser.getJSONObject("user").getString("UserName"));
		body.put("ClientMsgId", DateKit.getCurrentUnixTime());

		HttpRequest request = HttpRequest.post(url).header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", loginUser.getString("cookie")).send(body.toString());
		String res = request.body();
		request.disconnect();

		if (StringKit.isBlank(res)) {
			return false;
		}

		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject baseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != baseResponse) {
				int ret = baseResponse.getInt("Ret", -1);
				return ret == 0;
			}
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 获取联系人
	 */
	public boolean getContact(JSONObject loginUser) {

		String url = loginUser.getString("base_uri") + "/webwxgetcontact?pass_ticket="
				+ loginUser.getString("pass_ticket") + "&skey="
				+ loginUser.getJSONObject("BaseRequest").getString("Skey") + "&r=" + DateKit.getCurrentUnixTime();
		JSONObject body = new JSONObject();
		body.put("BaseRequest", loginUser.getJSONObject("BaseRequest"));
		HttpRequest request = HttpRequest.post(url).header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", loginUser.getString("cookie")).send(body.toString());
		LOGGER.info("[*] " + request);
		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			return false;
		}
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				int ret = BaseResponse.getInt("Ret", -1);
				if (ret == 0) {
					JSONArray MemberList = jsonObject.getJSONArray("MemberList");
					JSONArray grouplist=new JSONArray();
					for (int i = 0; i < MemberList.size(); i++) {
						JSONObject contact = MemberList.getJSONObject(i);
						if (contact.getString("UserName").indexOf("@@") != -1) {
							grouplist.add(contact);
						}

					}
					loginUser.put("grouplist", grouplist);
				}
			}
		} catch (Exception e) {
		}
		return false;
	}
	

	public void webwxbatchgetcontact(JSONObject wxUser, String toUserName) {

		String url = wxUser.getString("base_uri") + "/webwxbatchgetcontact?type=ex&pass_ticket="
				+ wxUser.getString("pass_ticket") + "&skey="
				+ wxUser.getJSONObject("BaseRequest").getString("Skey") + "&r=" + DateKit.getCurrentUnixTime();
		JSONObject body = new JSONObject();
		body.put("BaseRequest", wxUser.getJSONObject("BaseRequest"));
		body.put("Count", 1);
		List<JSONObject> userNames=new ArrayList();
		JSONObject user=new JSONObject();
		user.put("UserName", toUserName);
		user.put("ChatRoomId","");
		userNames.add(user);
		body.put("List", userNames);
		HttpRequest request = HttpRequest.post(url).header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", wxUser.getString("cookie")).send(body.toString());
		LOGGER.info("[*] " + request);
		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			return ;
		}
		try {
			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				int ret = BaseResponse.getInt("Ret", -1);
				if (ret == 0) {
					JSONArray MemberList = jsonObject.getJSONArray("MemberList");
					wxUser.put("MemberList", MemberList);
					for (int i = 0; i < MemberList.size(); i++) {
						 System.out.println(MemberList.get(i).toString());
						JSONObject contact = MemberList.getJSONObject(i);
						if (contact.getString("UserName").indexOf("@@") != -1) {
							System.out.println(contact);
							continue;
						}

					}
				}
			}
		} catch (Exception e) {
		}
	}

	public RestLoginResponse loginByUserToken(JSONObject loginInfo, JSONObject headers) {
		
		String url;
		Map<String, String> headerMap = new HashMap<>();
		setHeader(headerMap,headers);
		if(loginInfo.getString("wxUnionId")!=null){
			url = "/api/v2/token/person";
			PersonRobotLoginRequest personRobotLoginRequest = new PersonRobotLoginRequest();
			personRobotLoginRequest.setAppId(loginInfo.getString("appId"));
			personRobotLoginRequest.setWxUnionId(loginInfo.getString("wxUnionId"));
			TextResponse response = uamClientService.performJsonPostTextResponse(url, headerMap, personRobotLoginRequest);
			String content = response.getResponseBody();
			if (response.getStatusCode() == HttpStatus.CREATED.value()
					|| response.getStatusCode() == HttpStatus.OK.value()) {
				return JsonUtils.stringToObject(content, RestLoginResponse.class);
			}
			RestException exception = JsonUtils.stringToObject(content, RestException.class);
			throw exception;
		}else{
			
			url = "/api/v2/token/wxrobot";
			RobotLoginRequest robotLoginRequest = new RobotLoginRequest();
			robotLoginRequest.setLoginName(loginInfo.getString("loginName"));
			robotLoginRequest.setAppId(loginInfo.getString("appId"));
			robotLoginRequest.setDomain(loginInfo.getString("domain"));
			robotLoginRequest.setEnterpriseId(loginInfo.getLong("enterpriseId", 0));
			robotLoginRequest.setEnterpriseUserId(loginInfo.getLong("enterpriseUserId", 0));
			TextResponse response = uamClientService.performJsonPostTextResponse(url, headerMap, robotLoginRequest);
			String content = response.getResponseBody();
			if (response.getStatusCode() == HttpStatus.CREATED.value()
					|| response.getStatusCode() == HttpStatus.OK.value()) {
				return JsonUtils.stringToObject(content, RestLoginResponse.class);
			}
			RestException exception = JsonUtils.stringToObject(content, RestException.class);
			throw exception;
		}
		
	
	

		
	}
	
	private void setHeader(Map<String, String> headerMap ,JSONObject headers){
		headerMap.put("x-device-type", headers.getString("x-device-type"));
		headerMap.put("x-device-sn", "");
		headerMap.put("x-device-os", headers.getString("x-device-os"));
		headerMap.put("x-device-name", "");
		headerMap.put("x-client-version", headers.getString("x-client-version"));
		headerMap.put("x-real-ip", headers.getString("x-real-ip"));
		headerMap.put("x-proxy-ip", headers.getString("x-proxy-ip"));
	}

	public void createQrCode(String qrcodePath, String uuid) {
		// TODO Auto-generated method stub
		String url = "https://login.weixin.qq.com/qrcode/" + uuid;
		File output = new File(qrcodePath);
		HttpRequest.post(url, true, "t", "webwx", "_", DateKit.getCurrentUnixTime()).receive(output);

	}

	public WxRobot getRunningWxRobot(String userToken) {
		List<WxRobot> wxRobotList=listRobots(userToken);
		for(int i=0;i<wxRobotList.size();i++){
			WxRobot wxRobot=wxRobotList.get(i);
			if(wxRobot.getStatus()==WxRobot.STATUS_RUNNING){
				return wxRobot;
			}
		}
	    return null;
	}
	
	
	
	

	public void create(String uin, String wxName, UserToken userToken, String token) {
		String url = "/api/v2/wx/robot/create";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", token);
		WxRobot wxRobot = new WxRobot();
		wxRobot.setAccountId(userToken.getAccountId());
		wxRobot.setCloudUserId(userToken.getCloudUserId());
		wxRobot.setWxUin(uin);
		wxRobot.setWxName(wxName);
		wxRobot.setStatus(WxRobot.STATUS_RUNNING);
		wxRobot.setCreatedAt(new Date());
		wxRobot.setLastStartAt(new Date());
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, wxRobot);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return;
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}

	public void update(WxRobot userRobot, String userToken) {
		// TODO Auto-generated method stub
		String url = "/api/v2/wx/robot/update";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, userRobot);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return;
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}

	public void updateBySystem(WxRobot userRobot) {
		// TODO Auto-generated method stub
		String url = "/api/v2/wx/robot/updateBySystem";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", accessAddressService.getAccessAddress().getAppId());
		userRobot.setLastStartAt(new Date());
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, userRobot);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return;
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}

	public WxRobot getRobotByUin(String uin, String userToken) {
		// TODO Auto-generated method stub
		String url = "/api/v2/wx/robot/getRobotByUin?uin=" + uin;
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performGetText(url, headers);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			return JsonUtils.stringToObject(content, WxRobot.class);
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;

	}

	public List<WxRobot> ListRunning() {
		Map<String, String> headers = new HashMap<>();
		String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
		headers.put("Authorization", accessAddressService.getAccessAddress().getAppId());
		headers.put("Date", dateStr);
		int offset = getRobotNextOffset();
		String url = "/api/v2/wx/robot/listRunning?offset=" + offset + "&limit=" + DEFAULT_LIMIT;
		TextResponse response = uamClientService.performGetText(url, headers);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			List<WxRobot> list = (List<WxRobot>) JsonUtils.stringToList(content, List.class, WxRobot.class);
			refreshOffset(list.size(), DEFAULT_LIMIT);
			return list;
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}
	
	
	
	public List<WxRobotConfig> listConfigBySystem(long robotId) {

		Map<String, String> headers = new HashMap<>();
		String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, new Date(), null);
		headers.put("Authorization", accessAddressService.getAccessAddress().getAppId());
		headers.put("Date", dateStr);
		String url = "/api/v2/wx/robot/listConfigBySystem?robotId="+robotId;
		TextResponse response = uamClientService.performGetText(url, headers);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			List<WxRobotConfig> list = (List<WxRobotConfig>) JsonUtils.stringToList(content, List.class, WxRobotConfig.class);
			return list;
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	
		
	}


	private int getRobotNextOffset() {
		if (cacheClient.getCache("RobotOffset") != null) {
			int offset = (int) cacheClient.getCache("RobotOffset");
			return offset;
		} else {
			cacheClient.addCache("RobotOffset", 0);
			return 0;
		}

	}

	private void refreshOffset(int size, int limit) {
		int offset = getRobotNextOffset();
		if (size < limit) {
			cacheClient.replaceCache("RobotOffset", 0);
		} else {
			cacheClient.replaceCache("RobotOffset", offset + size);
		}

	}
	
	public List<WxRobot> listRobots(String userToken) {

		String url = "/api/v2/wx/robot/list";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performGetText(url, headers);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			List<WxRobot> list = (List<WxRobot>) JsonUtils.stringToList(content, List.class, WxRobot.class);
			refreshOffset(list.size(), DEFAULT_LIMIT);
			return list;
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}

	public List<WxRobotConfig> listWxRobotConfig(long robotId, String userToken) {
		String url = "/api/v2/wx/robot/listConfigByRobotId?robotId="+robotId;
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performGetText(url, headers);
		String content = response.getResponseBody();
		if (response.getStatusCode() == HttpStatus.OK.value()) {
			List<WxRobotConfig> list = (List<WxRobotConfig>) JsonUtils.stringToList(content, List.class,
					WxRobotConfig.class);
			return list;
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}
	
	public void createConfig(WxRobotConfig wxRobotConfig, String userToken) {

		String url = "/api/v2/wx/robot/createConfig";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, wxRobotConfig);
		String content = response.getResponseBody();
		if (response.getStatusCode() != HttpStatus.OK.value()) {
			RestException exception = JsonUtils.stringToObject(content, RestException.class);
			throw exception;
		}
	}
	
	public void updateConfig(WxRobotConfig wxRobotConfig, String userToken) {

		String url = "/api/v2/wx/robot/updateConfig";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization",userToken);
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, wxRobotConfig);
		String content = response.getResponseBody();
		if (response.getStatusCode() != HttpStatus.OK.value()) {
			RestException exception = JsonUtils.stringToObject(content, RestException.class);
			throw exception;
		}
	}

	public void deleteConfig(WxRobotConfig wxRobotConfig, String userToken) {

		String url = "/api/v2/wx/robot/deleteConfig";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, wxRobotConfig);
		String content = response.getResponseBody();
		if (response.getStatusCode() != HttpStatus.OK.value()) {
			RestException exception = JsonUtils.stringToObject(content, RestException.class);
			throw exception;
		}
	}

	public JSONObject getWxHost(String wxHost) {
		JSONObject host = new JSONObject();
		String login = "login.weixin.qq.com";
		String file = "file.wx.qq.com";
		String webpush = "webpush.weixin.qq.com";

		if (wxHost.indexOf("wx2.qq.com") > -1) {
			login = "login.wx2.qq.com";
			file = "file.wx2.qq.com";
			webpush = "webpush.wx2.qq.com";
		} else if (wxHost.indexOf("wx8.qq.com") > -1) {
			login = "login.wx8.qq.com";
			file = "file.wx8.qq.com";
			webpush = "webpush.wx8.qq.com";
		} else if (wxHost.indexOf("qq.com") > -1) {
			login = "login.wx.qq.com";
			file = "file.wx.qq.com";
			webpush = "webpush.wx.qq.com";
		} else if (wxHost.indexOf("web2.wechat.com") > -1) {
			login = "login.web2.wechat.com";
			file = "file.web2.wechat.com";
			webpush = "webpush.web2.wechat.com";
		} else if (wxHost.indexOf("wechat.com") > -1) {
			login = "login.web.wechat.com";
			file = "file.web.wechat.com";
			webpush = "webpush.web.wechat.com";
		}
		host.put("login", login);
		host.put("file", file);
		host.put("webpush", webpush);
		host.put("host", wxHost);
		return host;
	}

	public void stopRobot(long robotId, String userToken) {

		String url = "/api/v2/wx/robot/stopRobot";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, robotId);
		String content = response.getResponseBody();
		if (response.getStatusCode() != HttpStatus.OK.value()) {
			RestException exception = JsonUtils.stringToObject(content, RestException.class);
			throw exception;
		}
	}

	

	

}