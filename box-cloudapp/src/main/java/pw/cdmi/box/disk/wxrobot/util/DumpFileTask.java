package pw.cdmi.box.disk.wxrobot.util;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import blade.kit.json.JSONObject;
import pw.cdmi.box.disk.httpclient.rest.request.RestLoginResponse;
import pw.cdmi.box.disk.wxrobot.service.WxRobotService;
import pw.cdmi.core.restrpc.RestClient;

public class DumpFileTask  implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DumpFileTask.class);
	
	JSONObject wxUser;
	
	JSONObject msg;
	
	WxRobotService wechatService;
	
	private RestClient ufmClientService;
	
	
	DumpFileTask(JSONObject wxUser,JSONObject msg,WxRobotService wechatService,RestClient ufmClientService){
		this.wxUser=wxUser;
		this.msg=msg;
		this.wechatService=wechatService;
		this.ufmClientService=ufmClientService;
	}

	@Override
	public void run() {
		int msgType = msg.getInt("MsgType", 0);
		int appMsgType = msg.getInt("AppMsgType", 0);
		String mediaid = msg.getString("MediaId");
		String filename = msg.getString("FileName");
		JSONObject user = wxUser.getJSONObject("user");
		JSONObject wxhost = wxUser.getJSONObject("wxHost");
		String filePathName=buildWxTempFile(filename,wxUser);
		String wxFileUrl="";
		LOGGER.info("checkMSG: "+msg.toString());
		if(msgType==49 && appMsgType==6){
			String fromUserName = msg.getString("FromUserName");
			wxFileUrl=buildFileUrl(mediaid, filename, wxUser, fromUserName,wxhost.getString("file"));
			HttpClientUtils.downFile(wxUser, wxFileUrl, filePathName);
			
		}else if(msgType==3 && appMsgType==0){
			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmssSSS");
			filename=format.format(new Date())+".jpg";
			filePathName=filePathName+"/"+filename;
			wxFileUrl=buildImageUrl(wxUser,msg.getString("MsgId"),wxhost.getString("file"));
			HttpClientUtils.downImage(wxUser,wxFileUrl,filePathName);
			
		}else if(msgType==43 && appMsgType==0){
			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddhhmmssSSS");
			filename=format.format(new Date())+".mp4";
			filePathName=filePathName+"/"+filename;
			wxFileUrl=buildVedioUrl(wxUser,msg.getString("MsgId"),wxhost.getString("file"));
			HttpClientUtils.downVedio(wxUser,wxFileUrl,filePathName);
		}
		try {
			JSONObject loginInfo = wxUser.getJSONObject("loginInfo");
			JSONObject headers = wxUser.getJSONObject("headers");
			RestLoginResponse loginResp = wechatService.loginByUserToken(loginInfo, headers);
			String fileType=WxRobotFilter.getFileType(msgType,appMsgType,filename);
			
			HttpClientUtils.dumpFile(wxFileUrl, filePathName, loginResp, filename, 
					fileType,new File(filePathName).length(),ufmClientService,user.getString("NickName"),wxUser.getString("language"));
			
		} catch (Exception e) {
			LOGGER.error("wxRobot dumpFile fail! clouduserId:" + user.getString("cloudUserId")
					+ " fileName:" + filename);
			LOGGER.error(e.getMessage());
		}
	
	}
	
	private String buildFileUrl(String mediaid, String filename, JSONObject loginUser, String sender, String fileHost) {
		JSONObject wxhost = loginUser.getJSONObject("wxHost");
		filename=URLEncoder.encode(filename);
		String url = "https://" + wxhost.getString("file") + "/cgi-bin/mmwebwx-bin/webwxgetmedia?" + "sender=" + sender
				+ "&mediaid=" + mediaid + "&filename=" + filename + "&fromuser="
				+ loginUser.getJSONObject("BaseRequest").getString("Uin") + "&pass_ticket="
				+ loginUser.getString("pass_ticket") + "&webwx_data_ticket=" + loginUser.getString("webwx_data_ticket");
		return url;
	}
	
	private String buildImageUrl(JSONObject loginUser,String msgId, String fileHost) {
		JSONObject wxhost = loginUser.getJSONObject("wxHost");
		String url = "https://" + wxhost.getString("host") + "/cgi-bin/mmwebwx-bin/webwxgetmsgimg?MsgID=" + msgId
				+ "&skey=" +loginUser.getJSONObject("BaseRequest").getString("Skey");
		return url;
	}
	
	private String buildVedioUrl(JSONObject loginUser,String msgId, String fileHost) {
		JSONObject wxhost = loginUser.getJSONObject("wxHost");
		String url = "https://" + wxhost.getString("host") + "/cgi-bin/mmwebwx-bin/webwxgetvideo?msgid=" + msgId
				+ "&skey=" +loginUser.getJSONObject("BaseRequest").getString("Skey") ;
		return url;
	}

	private String buildWxTempFile( String filename, JSONObject loginUser) {
		String filePath = loginUser.getString("tempWxFilePath") + "/" + filename;
		return filePath;
	}
}
