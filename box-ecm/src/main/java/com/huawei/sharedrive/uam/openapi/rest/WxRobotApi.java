package com.huawei.sharedrive.uam.openapi.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobot;
import com.huawei.sharedrive.uam.wxrobot.domain.WxRobotConfig;
import com.huawei.sharedrive.uam.wxrobot.service.WxRobotConfigService;
import com.huawei.sharedrive.uam.wxrobot.service.WxRobotService;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import net.rubyeye.xmemcached.exception.MemcachedException;
import pw.cdmi.common.cache.CacheClient;

@Controller
@RequestMapping(value = "/api/v2/wx/robot/")
public class WxRobotApi {

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	private WxRobotService wxRobotService;
	

	@Resource(name = "cacheClient")
	private CacheClient cacheClient;
	
	@Autowired
	private WxRobotConfigService wxRobotConfigService;
	
	@RequestMapping(value = "listRunning", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> listRunning(@RequestHeader("Authorization") String authorization,
			@RequestHeader(value = "Date", required = false) String date, int offset, int limit) {
//		userTokenHelper.checkAppToken(authorization, date);
		List<WxRobot> robotList = wxRobotService.listRuning(WxRobot.STATUS_RUNNING, offset, limit);
		return new ResponseEntity<List<WxRobot>>(robotList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "listConfigBySystem", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> listConfigBySystem(@RequestHeader("Authorization") String authorization,
			@RequestHeader(value = "Date", required = false) String date, long robotId) {
//		userTokenHelper.checkAppToken(authorization, date);
		List<WxRobotConfig> configList = wxRobotConfigService.listByRobotId(robotId);
		return new ResponseEntity<List<WxRobotConfig>>(configList, HttpStatus.OK);
	}

	@RequestMapping(value = "list", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> listByUser(@RequestHeader("Authorization") String authorization, HttpServletRequest req,
			HttpServletResponse resp) {

		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
		List<WxRobot> robotList = wxRobotService.listByCloudUserId(userToken.getCloudUserId(),userToken.getAccountId());
		return new ResponseEntity<List<WxRobot>>(robotList, HttpStatus.OK);
	}


	@RequestMapping(value = "checkRobotStatus", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> checkRobotStatus(@RequestHeader("Authorization") String authorization, HttpServletRequest req,
										HttpServletResponse resp) {

		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
		List<WxRobot> robotList = wxRobotService.listByCloudUserId(userToken.getCloudUserId(),userToken.getAccountId());
		for(int i=0;i<robotList.size();i++){
             if(robotList.get(i).getStatus()==1){
				 return new ResponseEntity<WxRobot>(robotList.get(i), HttpStatus.OK);
			 }
		}
		return new ResponseEntity<String>("false", HttpStatus.OK);
	}

	@RequestMapping(value = "getRobotByUin", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getByUinAndUser(@RequestHeader("Authorization") String authorization, String uin) {
		
		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);

		WxRobot WxRobot = new WxRobot();
		WxRobot.setAccountId(userToken.getAccountId());
		WxRobot.setCloudUserId(userToken.getCloudUserId());
		WxRobot.setWxUin(uin);
		WxRobot newWxRobot = wxRobotService.getByUinAndUser(WxRobot);
		return new ResponseEntity<WxRobot>(newWxRobot, HttpStatus.OK);
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<WxRobot> addRobot(@RequestHeader("Authorization") String authorization,
			@RequestBody WxRobot wxRobot) {
		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
		wxRobot.setId(wxRobotService.getNextRobotId());
		WxRobotConfig userConfig=getUserDefaultConfig(wxRobot,userToken.getAccountId());
		WxRobotConfig groupConfig=getGroupDefaultConfig(wxRobot,userToken.getAccountId());
		wxRobotConfigService.cerate(userConfig);
		wxRobotConfigService.cerate(groupConfig);
		wxRobotService.create(wxRobot);
		return new ResponseEntity<WxRobot>(wxRobot, HttpStatus.OK);
	}

	@RequestMapping(value = "stopRobot", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> stopRobot(@RequestHeader("Authorization") String authorization,
			@RequestBody long robotId) {
		userTokenHelper.checkTokenAndGetUser(authorization);
		wxRobotService.stopRobot(robotId);
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<WxRobot> updateRobot(@RequestHeader("Authorization") String authorization,
			@RequestBody WxRobot robot) {
		userTokenHelper.checkTokenAndGetUser(authorization);
		wxRobotService.updateRobot(robot);
		return new ResponseEntity<WxRobot>(robot, HttpStatus.OK);
	}
	
	@RequestMapping(value = "updateBySystem", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<WxRobot> updateBySystem(@RequestHeader("Authorization") String authorization,
			@RequestHeader(value = "Date", required = false) String date,
			@RequestBody WxRobot robot) {
//		userTokenHelper.checkAppToken(authorization, date);
		wxRobotService.updateRobotStatus(robot);
		return new ResponseEntity<WxRobot>(robot, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "listConfigByRobotId", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<WxRobotConfig>> listConfigByUin(@RequestHeader("Authorization") String authorization,
			long robotId) {
		userTokenHelper.checkTokenAndGetUser(authorization);
		List<WxRobotConfig> listConfig=wxRobotConfigService.listByRobotId(robotId);
		for(WxRobotConfig wxRobotConfig : listConfig){
			wxRobotConfig.initConfig();
		}
		return new ResponseEntity<List<WxRobotConfig>>(listConfig, HttpStatus.OK);
	}
	
	@RequestMapping(value = "deleteConfig", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> deleteConfig(@RequestHeader("Authorization") String authorization,
			@RequestBody WxRobotConfig wxRobotConfig) {
		userTokenHelper.checkTokenAndGetUser(authorization);
		wxRobotConfigService.delete(wxRobotConfig);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "createConfig", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> createConfig(@RequestHeader("Authorization") String authorization,
			@RequestBody WxRobotConfig wxRobotConfig) {
		userTokenHelper.checkTokenAndGetUser(authorization);
		wxRobotConfigService.cerate(wxRobotConfig);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "wxRobotConfig", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putConfig(@RequestHeader("Authorization") String authorization,@RequestBody WxRobotConfig wxRobotConfig) {
		userTokenHelper.checkTokenAndGetUser(authorization);
		wxRobotConfig.parseConfig();
		wxRobotConfigService.updateConfig(wxRobotConfig);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "updateConfig", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> updateConfig(@RequestHeader("Authorization") String authorization,
			@RequestBody WxRobotConfig wxRobotConfig) {
		userTokenHelper.checkTokenAndGetUser(authorization);
		wxRobotConfigService.updateConfig(wxRobotConfig);
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "whiteList", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> updateWhiteList(@RequestHeader("Authorization") String authorization,long robotId,
			@RequestBody List<String> whiteList) {
		userTokenHelper.checkTokenAndGetUser(authorization);
		WxRobotConfig filter = new WxRobotConfig();
		filter.setRobotId(robotId);
		filter.setType(WxRobotConfig.TYPE_WhiteList);
		wxRobotConfigService.delete(filter);
		for(int i = 0;i<whiteList.size();i++){
			WxRobotConfig config = new WxRobotConfig();
			config.setRobotId(robotId);
			config.setType(WxRobotConfig.TYPE_WhiteList);
			config.setName(whiteList.get(i));
			wxRobotConfigService.cerate(config);
		}
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	
	private WxRobotConfig getUserDefaultConfig(WxRobot wxRobot,long accountId){
		WxRobotConfig wxRobotConfig=new WxRobotConfig();
		wxRobotConfig.setRobotId(wxRobot.getId());
		if(accountId==0){
			wxRobotConfig.setType(wxRobotConfig.TYPE_USER);
			wxRobotConfig.setName(WxRobotConfig.NAME_USER);
			wxRobotConfig.setValue(0);
		}else{
			wxRobotConfig.setType(wxRobotConfig.TYPE_USER);
			wxRobotConfig.setName(WxRobotConfig.NAME_USER);
			wxRobotConfig.setValue(WxRobotConfig.VALUE_DEFAULT);
		}
		return wxRobotConfig;
		
	}
	
	private WxRobotConfig getGroupDefaultConfig(WxRobot wxRobot,long accountId){
		WxRobotConfig wxRobotConfig=new WxRobotConfig();
		if(accountId==0){
			wxRobotConfig.setRobotId(wxRobot.getId());
			wxRobotConfig.setType(wxRobotConfig.TYPE_GROUP);
			wxRobotConfig.setName(WxRobotConfig.NAME_GROUP);
			wxRobotConfig.setValue(WxRobotConfig.VALUE_DEFAULT);
		}else{
			wxRobotConfig.setRobotId(wxRobot.getId());
			wxRobotConfig.setType(wxRobotConfig.TYPE_GROUP);
			wxRobotConfig.setName(WxRobotConfig.NAME_GROUP);
			wxRobotConfig.setValue(WxRobotConfig.VALUE_DEFAULT);
		}
	
		return wxRobotConfig;
		
	}
	
	@RequestMapping(value = "/listWxRobotGroups", method = RequestMethod.POST)
	@ResponseBody
	public List<String> listWxRobotGroups(String uin, HttpServletRequest req, HttpServletResponse resp,@RequestHeader("Authorization") String authorization) throws IOException, ServletException {
		
		userTokenHelper.checkTokenAndGetUser(authorization);
		String key = "w" + uin;
		JSONObject wxUser = null;
		try {
			wxUser = (JSONObject) cacheClient.getCacheNoPrefix(key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> groupNames = new ArrayList<>();
		if(wxUser!=null){
			JSONArray groupList =getGroupContact(wxUser, wxRobotService);
			
			for (int i = 0; i < groupList.size(); i++) {
				JSONValue NickName = groupList.getJSONObject(i).get("NickName");
				if (!NickName.isNull()) {
					groupNames.add(NickName.asString());
				}
			}
		}
		
		return groupNames;
	}
	
	
	/**
	 *获取群组
	 */
	private JSONArray getGroupContact(JSONObject loginUser,WxRobotService wxRobotService) {
		
		JSONArray groupList = loginUser.getJSONArray("groupList");
		return groupList;
//		JSONObject baseRequset = loginUser.getJSONObject("BaseRequest");
//		String url = loginUser.getString("base_uri") + "/webwxinit?r=" + DateKit.getCurrentUnixTime() + "&pass_ticket="
//				+ loginUser.getString("pass_ticket") + "&skey=" + baseRequset.getString("Skey");
//		HttpRequest request = HttpRequest.post(url).header("Content-Type", "application/json;charset=utf-8")
//				.header("Cookie", loginUser.getString("cookie")).send(loginUser.toString());
//		String res = request.body();
//		request.disconnect();
//		if (StringKit.isBlank(res)) {
//			return null;
//		}
//		try {
//			JSONObject jsonObject = JSON.parse(res).asObject();
//			if (null != jsonObject) {
//				JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
//				if (null != BaseResponse) {
//					int ret = BaseResponse.getInt("Ret", -1);
//					if (ret == 0) {
//						loginUser.put("SyncKey", jsonObject.getJSONObject("SyncKey"));
//						loginUser.put("user", jsonObject.getJSONObject("User"));
//						JSONArray contactList = jsonObject.getJSONArray("ContactList");
//						StringBuffer synckey = new StringBuffer();
//						JSONArray list = loginUser.getJSONObject("SyncKey").getJSONArray("List");
//						for (int i = 0, len = list.size(); i < len; i++) {
//							JSONObject item = list.getJSONObject(i);
//							synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
//						}
//						loginUser.put("SyncKeyString", synckey.substring(1));
//						JSONArray groupList = new JSONArray();
//						for (int i = 0; i < contactList.size(); i++) {
//							if (contactList.getJSONObject(i).getString("UserName").indexOf("@@") != -1) {
//								JSONObject newContact = new JSONObject();
//								newContact.put("UserName", contactList.getJSONObject(i).getString("UserName"));
//								newContact.put("NickName", contactList.getJSONObject(i).getString("NickName"));
//								groupList.add(newContact);
//							}
//						}
//						loginUser.put("groupList", groupList);
//						return groupList;
//					}
//				}
//			}
//		} catch (Exception e) {
//		
//		}
//		return null;
	}

}
