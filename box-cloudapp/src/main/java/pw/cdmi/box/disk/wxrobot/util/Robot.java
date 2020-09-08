package pw.cdmi.box.disk.wxrobot.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import blade.kit.json.JSON;
import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pw.cdmi.box.disk.user.domain.WxRobot;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.box.disk.wxrobot.model.WxRobotConfig;
import pw.cdmi.box.disk.wxrobot.service.WxRobotService;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.restrpc.RestClient;

@Component
public class Robot {

	private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);

	@Resource(name = "cacheClient")
	private CacheClient cacheClient;

	@Resource
	private RestClient ufmClientService;

	@Resource
	private WxRobotService wechatService;

	public static JSONArray ContactList = new JSONArray();

	public Robot() {
		System.setProperty("jsse.enableSNIExtension", "false");
	}

	public void listenMsgMode() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						if (PropertiesUtils.getProperty("robot.interval.time") == null
								|| "".equals(PropertiesUtils.getProperty("robot.interval.time"))) {
							break;
						} else {
							Thread.sleep(Integer.parseInt(PropertiesUtils.getProperty("robot.interval.time")));
						}

					} catch (InterruptedException e) {
						LOGGER.error("robot.interval.time {}",PropertiesUtils.getProperty("robot.interval.time"));
						break;
					}
					try {
						
						LOGGER.warn("robot.  listenMsgMode");
						List<WxRobot> robotlist = wechatService.ListRunning();
						Iterator iterator = robotlist.iterator();
						while (iterator.hasNext()) {
							WxRobot wxRobot = (WxRobot) iterator.next();
							String key = WxRobotService.WX_PREFIX + wxRobot.getWxUin();
							if (cacheClient.getCache(key) == null) {
								wxRobot.setStatus(WxRobot.STATUS_STOP);
								wechatService.updateBySystem(wxRobot);
								LOGGER.warn("robot. is not find in cached  "+key);
								continue;
							}
							JSONObject wxUser = (JSONObject) cacheClient.getCache(key);
							JSONObject data = webwxsync(wxUser);
							if (data == null) {
								LOGGER.error("{} webwxsync fail", WxRobotService.WX_PREFIX + wxUser);
								wxRobot.setStatus(WxRobot.STATUS_STOP);
								wechatService.updateBySystem(wxRobot);
								cacheClient.deleteCache(key);
								continue;
							}
							handleMsg(wxRobot.getId(), data, wxUser);
							cacheClient.replaceCache(key, wxUser);
						}
						
					} catch (Exception e) {
						LOGGER.error("listenMsgMode fail {}", e.getMessage());
						try {
							Thread.sleep(Integer.parseInt(PropertiesUtils.getProperty("robot.interval.time")));
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	/**
	 * 获取最新消息
	 */
	public JSONObject webwxsync(JSONObject loginUser) {
		try {
			String url = loginUser.getString("base_uri") + "/webwxsync?lang=zh_CN&pass_ticket="
					+ loginUser.getString("pass_ticket") + "&r=" + DateKit.getCurrentUnixTime();
			JSONObject body = new JSONObject();
			body.put("BaseRequest", loginUser.getJSONObject("BaseRequest"));
			body.put("SyncKey", loginUser.getJSONObject("SyncKey"));
			body.put("rr", DateKit.getCurrentUnixTime());
			LOGGER.info("webwxsync request:" + url);
			HttpRequest request = HttpRequest.post(url).header("Content-Type", "application/json;charset=utf-8")
					.header("Cookie", loginUser.getString("cookie")).send(body.toString());
			String res = request.body();
			String[] cookies = loginUser.getString("cookie").split(";");
			for (String str : cookies) {
				String[] arr = str.split("=");
				if (arr[0].equalsIgnoreCase("webwx_data_ticket")) {
					loginUser.put("webwx_data_ticket", arr[1]);
				}
			}
			request.disconnect();
			if (StringKit.isBlank(res)) {
				return null;
			}

			JSONObject jsonObject = JSON.parse(res).asObject();
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if (null != BaseResponse) {
				int ret = BaseResponse.getInt("Ret", -1);
				if (ret == 0) {
					JSONObject SyncKey = jsonObject.getJSONObject("SyncKey");
					loginUser.put("SyncKey", SyncKey);
					StringBuffer synckey = new StringBuffer();
					JSONArray list = SyncKey.getJSONArray("List");
					for (int i = 0, len = list.size(); i < len; i++) {
						JSONObject item = list.getJSONObject(i);
						synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
					}
					loginUser.put("SyncKeyString", synckey.substring(1));
				} else {
					return null;
				}
			}
			return jsonObject;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 获取最新消息
	 */
	public void handleMsg(long robotId, JSONObject data, JSONObject loginUser) {
		if (null == data) {
			return;
		}
		JSONArray AddMsgList = data.getJSONArray("AddMsgList");
		for (int i = 0, len = AddMsgList.size(); i < len; i++) {
			JSONObject msg = AddMsgList.getJSONObject(i);
			handleMsgType(robotId, msg, loginUser);

		}
	}

	/**
	 * 消息检查
	 */
	public int[] syncCheck(JSONObject loginUser) throws Exception {

		int[] arr = new int[2];
		Map<String, String> wxhost = (Map<String, String>) loginUser.get("wxHost");
		String url = "https://" + wxhost.get("webpush") + "/cgi-bin/mmwebwx-bin/synccheck";
		JSONObject BaseRequest = loginUser.getJSONObject("BaseRequest");

		HttpRequest request = HttpRequest
				.get(url, true, "r", DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5), "skey",
						BaseRequest.getString("Skey"), "uin", BaseRequest.getString("Uin"), "sid",
						BaseRequest.getString("Sid"), "deviceid", BaseRequest.getString("DeviceId"), "synckey",
						loginUser.getString("SyncKeyString"), "_", System.currentTimeMillis())
				.header("Cookie", loginUser.getString("cookie"));

		String res = request.body();
		request.disconnect();
		if (StringKit.isBlank(res)) {
			return arr;
		}
		String retcode = Matchers.match("retcode:\"(\\d+)\",", res);
		String selector = Matchers.match("selector:\"(\\d+)\"}", res);
		if (null != retcode && null != selector) {
			arr[0] = Integer.parseInt(retcode);
			arr[1] = Integer.parseInt(selector);
			return arr;
		}
		return arr;
	}

	private void handleMsgType(long robotId, JSONObject msg, JSONObject wxUser) {
		List<WxRobotConfig> configList = wechatService.listConfigBySystem(robotId);
		if (WxRobotFilter.doCheck(wxUser, msg, configList)) {
			new Thread(new DumpFileTask(wxUser, msg, wechatService, ufmClientService)).start();
		}
	}

}