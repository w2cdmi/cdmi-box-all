package pw.cdmi.box.disk.wxrobot.service;

import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import blade.kit.json.JSONObject;
import pw.cdmi.box.disk.client.domain.node.INode;
import pw.cdmi.box.disk.httpclient.rest.request.RestLoginResponse;
import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.user.domain.WxRobot;
import pw.cdmi.box.disk.user.service.impl.UserServiceImpl;
import pw.cdmi.box.disk.weixin.domain.WxUser;
import pw.cdmi.box.disk.wxrobot.util.HttpClientUtils;
import pw.cdmi.box.disk.wxrobot.util.Robot;
import pw.cdmi.common.cache.CacheClient;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
public class WxLoginTask implements Runnable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Robot.class);
	
	String uuid;
	Robot robot;
	WxRobotService  wxRobotService;
	CacheClient cacheClient;
	UserToken userToken;
	String token;
	String tempWxFilePath;
	Locale local;
	RestClient ufmClientService;
	WxUser wxLoginUser;
	
	public WxLoginTask(String uuid,Robot robot,WxRobotService  wxRobotService,CacheClient cacheClient,
			UserToken userToken,String tempWxFilePath,String token,Locale local,RestClient ufmClientService){
		this.uuid=uuid;
		this.robot=robot;
		this.wxRobotService=wxRobotService;
		this.cacheClient=cacheClient;
		this.userToken=userToken;
		this.tempWxFilePath=tempWxFilePath;
		this.token=token;
		this.local=local;
		this.ufmClientService=ufmClientService;
	}
	public WxLoginTask(String uuid, Robot robot, WxRobotService wxRobotService, CacheClient cacheClient,
			WxUser wxUser, String tempWxFilePath,UserToken userToken, String token, Locale locale, RestClient ufmClientService) {
		// TODO Auto-generated constructor stub
		this.uuid=uuid;
		this.robot=robot;
		this.wxRobotService=wxRobotService;
		this.cacheClient=cacheClient;
		this.tempWxFilePath=tempWxFilePath;
		this.wxLoginUser=wxUser;
		this.ufmClientService=ufmClientService;
		this.token=token;
		this.userToken=userToken;
	}
	
	@Override
	public void run() {
		JSONObject wxUser = new JSONObject();
		wxUser.put("uuid", uuid);
		wxUser.put("tempWxFilePath", tempWxFilePath);
		String resultCode;
		int tip=1;
		while((resultCode=wxRobotService.waitForLogin(uuid,wxUser,tip))!=null){
			try {
				if(resultCode.equals("200")){
					try {
						if(wxLoginUser==null){
							processEnterprise(wxUser,cacheClient,userToken.getCloudUserId());
						}else{
							processPerson(wxUser,cacheClient,userToken.getCloudUserId());
						}
					    break;
					}  catch (RestException restException) {
						LOGGER.error( restException.getMessage());
						break;
					}catch (Exception e) {
						LOGGER.error(e.getMessage());
						break;
					}
				}else if(resultCode.equals("201")){
					tip=0;
				}else if(resultCode.equals("fail")){
					break;
				}else if(resultCode.equals("400")){
					break;
				}
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void buildLogin(JSONObject loginUser,UserToken userToken){
		LOGGER.info("UserToken ---- loginName:"+userToken.getLoginName()+",appId:"+userToken.getAppId()+
				",passWord:"+userToken.getOldPassword()+",domain:"+userToken.getDeviceName()+",accountId:"+ userToken.getAccountId()+
				",cloudUserId:"+userToken.getCloudUserId());
		
		JSONObject loginInfo=new JSONObject();
		loginInfo.put("loginName", userToken.getLoginName());
		loginInfo.put("appId", userToken.getAppId());
		loginInfo.put("domain", userToken.getDomain());
		loginInfo.put("enterpriseId", userToken.getEnterpriseId());
		loginInfo.put("enterpriseUserId", userToken.getId());
		loginInfo.put("cloudUserId", userToken.getCloudUserId());
		loginUser.put("loginInfo", loginInfo);
		
		JSONObject headers=new JSONObject();
        headers.put("x-device-type", UserServiceImpl.transDeviceStrType(userToken.getDeviceType()));
        headers.put("x-device-sn", userToken.getDeviceSn());
        headers.put("x-device-os", userToken.getDeviceOS());
        headers.put("x-device-name", userToken.getDeviceName());
        headers.put("x-client-version", userToken.getDeviceAgent());
        headers.put("x-real-ip", userToken.getDeviceAddress());
        headers.put("x-proxy-ip",userToken.getDeviceAddress());
        loginUser.put("headers", headers);
        
        
	}
	
	
	
	
	private void processEnterprise(JSONObject wxUser,CacheClient cacheClient,long clouduserId){
		
		
		wxRobotService.login(wxUser,cacheClient,clouduserId);
		wxRobotService.wxInit(wxUser,wxRobotService);
		wxRobotService.getContact(wxUser);
		JSONObject baseRequset=wxUser.getJSONObject("BaseRequest");
		String key=WxRobotService.WX_PREFIX+baseRequset.getString("Uin");
		JSONObject oldWxUser = (JSONObject) cacheClient.getCache(key);
		if(oldWxUser!=null){
			JSONObject loginInfo=oldWxUser.getJSONObject("loginInfo");
			WxRobot updateWxRobot=new WxRobot();
			updateWxRobot.setWxUin(baseRequset.getString("Uin"));
			updateWxRobot.setAccountId(loginInfo.getLong("accountId", 0));
			updateWxRobot.setCloudUserId(loginInfo.getLong("cloudUserId", 0));
			updateWxRobot.setStatus(WxRobot.STATUS_STOP);
			wxRobotService.updateBySystem(updateWxRobot);
			cacheClient.deleteCache(key);
		}
		JSONObject userInfo=wxUser.getJSONObject("user");
		WxRobot wxRobot=wxRobotService.getRobotByUin(baseRequset.getString("Uin"),token);
		if(wxRobot==null){
			wxRobotService.create(baseRequset.getString("Uin"),userInfo.getString("NickName"), userToken,token);
		}else{
			if(wxRobot.getStatus()!=WxRobot.STATUS_RUNNING){
				wxRobot.setStatus(WxRobot.STATUS_RUNNING);
				wxRobot.setLastStartAt(new Date());
				wxRobot.setWxName(userInfo.getString("NickName"));
				wxRobotService.update(wxRobot,token);
			}
		}
		wxUser.put("language", local.getLanguage());
		buildLogin(wxUser,userToken);
		cacheClient.addCache(key, wxUser);
		RestLoginResponse loginResp=new RestLoginResponse();
        loginResp.setCloudUserId(userToken.getCloudUserId());
        loginResp.setToken(token);
      
        HttpClientUtils.getWxFolder(loginResp,"file",wxUser.getJSONObject("user").getString("NickName"),local.getLanguage(), ufmClientService);
		
	}
	
	
	private void processPerson(JSONObject wxUser ,CacheClient cacheClient,long cloudUserId){
		
		
		wxRobotService.login(wxUser,cacheClient,cloudUserId);
		wxRobotService.wxInit(wxUser,wxRobotService);
//		wxRobotService.getContact(wxUser);
		JSONObject baseRequset=wxUser.getJSONObject("BaseRequest");
		String key=WxRobotService.WX_PREFIX+baseRequset.getString("Uin");
		JSONObject oldWxUser = (JSONObject) cacheClient.getCache(key);
		if(oldWxUser!=null){
			JSONObject loginInfo=oldWxUser.getJSONObject("loginInfo");
			WxRobot updateWxRobot=new WxRobot();
			updateWxRobot.setWxUin(baseRequset.getString("Uin"));
			updateWxRobot.setAccountId(loginInfo.getLong("accountId", 0));
			updateWxRobot.setCloudUserId(loginInfo.getLong("cloudUserId", 0));
			updateWxRobot.setStatus(WxRobot.STATUS_STOP);
			wxRobotService.updateBySystem(updateWxRobot);
			cacheClient.deleteCache(key);
		}
		JSONObject userInfo=wxUser.getJSONObject("user");
		WxRobot wxRobot=wxRobotService.getRobotByUin(baseRequset.getString("Uin"),token);
		if(wxRobot==null){
			wxRobotService.create(baseRequset.getString("Uin"),userInfo.getString("NickName"), userToken,token);
		}else{
			if(wxRobot.getStatus()!=WxRobot.STATUS_RUNNING){
				wxRobot.setStatus(WxRobot.STATUS_RUNNING);
				wxRobot.setLastStartAt(new Date());
				wxRobot.setWxName(userInfo.getString("NickName"));
				wxRobotService.update(wxRobot,token);
			}
		}

		if(local==null){
			local=Locale.getDefault();
			wxUser.put("language", local.getLanguage());
		}else{
			wxUser.put("language", local.getLanguage());
		}

		buildLogin(wxUser,wxLoginUser);
		cacheClient.addCache(key, wxUser);
		RestLoginResponse loginResp=new RestLoginResponse();
        loginResp.setCloudUserId(userToken.getCloudUserId());
        loginResp.setToken(token);
      
        HttpClientUtils.getWxFolder(loginResp,"file",wxUser.getJSONObject("user").getString("NickName"),local.getLanguage(), ufmClientService);
		
	}
	
	
	
	private void buildLogin(JSONObject loginUser,WxUser wxLoginUser){
		LOGGER.info("UserToken ---- loginName:"+userToken.getLoginName()+",appId:"+userToken.getAppId()+
				",passWord:"+userToken.getOldPassword()+",domain:"+userToken.getDeviceName()+",accountId:"+ userToken.getAccountId()+
				",cloudUserId:"+userToken.getCloudUserId());
		
		JSONObject loginInfo=new JSONObject();
		loginInfo.put("loginName", userToken.getLoginName());
		loginInfo.put("wxUnionId", wxLoginUser.getUnionId());
		loginInfo.put("appId", userToken.getAppId());
		loginInfo.put("cloudUserId", userToken.getCloudUserId());
		loginUser.put("loginInfo", loginInfo);
		
		JSONObject headers=new JSONObject();
        headers.put("x-device-type", UserServiceImpl.transDeviceStrType(userToken.getDeviceType()));
        headers.put("x-device-sn", userToken.getDeviceSn());
        headers.put("x-device-os", userToken.getDeviceOS());
        headers.put("x-device-name", userToken.getDeviceName());
        headers.put("x-client-version", userToken.getDeviceAgent());
        headers.put("x-real-ip", userToken.getDeviceAddress());
        headers.put("x-proxy-ip",userToken.getDeviceAddress());
        loginUser.put("headers", headers);
        
        
	}
	

}
