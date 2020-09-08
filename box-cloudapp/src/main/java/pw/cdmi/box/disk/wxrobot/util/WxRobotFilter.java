package pw.cdmi.box.disk.wxrobot.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import pw.cdmi.box.disk.wxrobot.model.WxRobotConfig;

public class WxRobotFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WxRobotFilter.class);
	
	public static String[] vedioTypes=new  String[]{"avi","wmv","mpeg","mp4","mov","mkv","flv","f4v","m4v","rmvb","rm","3gp","dat","ts","mts","vob"};
	
	public static boolean doCheck(JSONObject wxUser,JSONObject msg, List<WxRobotConfig> configList) {
		// TODO Auto-generated method stub
		String fromUserName=msg.getString("FromUserName");
		System.out.println("FromUserName----"+msg.getString("FromUserName"));
		System.out.println("UserName    ----"+wxUser.getJSONObject("user").getString("UserName"));
		if(fromUserName.equals(wxUser.getJSONObject("user").getString("UserName"))){
			
			return false;
		}
		String filename = msg.getString("FileName");
		int msgType = msg.getInt("MsgType", 0);
		int appMsgType = msg.getInt("AppMsgType", 0);
		WxRobotConfig userConfig = null;
		WxRobotConfig groupConfig = null;
		List<WxRobotConfig> blackList=new ArrayList<>();
		for(int i=0;i<configList.size();i++){
			WxRobotConfig WxRobotConfig=configList.get(i);
			if(WxRobotConfig.getType()==WxRobotConfig.TYPE_USER){
				userConfig=WxRobotConfig;
			}
			if(WxRobotConfig.getType()==WxRobotConfig.TYPE_GROUP){
				groupConfig=WxRobotConfig;
			}
			if(WxRobotConfig.getType()==WxRobotConfig.TYPE_BLACK){
				blackList.add(WxRobotConfig);
			}
		}
		if(fromUserName.indexOf("@@")>-1){
			String groupName=getGroupName(wxUser,msg.getString("FromUserName"));
			for(int i=0;i<blackList.size();i++){
				if(blackList.get(i).getName().equals(groupName)){
					return false;
				}
			}
		    return	check(msgType,appMsgType,filename,groupConfig);	
		}else{
			return	check(msgType,appMsgType,filename,userConfig);	
		}
		

	}
	
	public static boolean check(int msgType,int appMsgType,String filename,WxRobotConfig wxRobotConfig){
		 LOGGER.info("checkMSG:"+wxRobotConfig.getRobotId()+" msgType---"+msgType+"    appMsgType--"+appMsgType);
		 if(msgType==49 && appMsgType==6){
			 String type=getType(filename);
			 return wxRobotConfig.getConfig().get(type);
		 }
         if(msgType==3 && appMsgType==0){
        	 return wxRobotConfig.getConfig().get("image");
		 }
         if(msgType==43 && appMsgType==0){
        	 return wxRobotConfig.getConfig().get("video");
		 }
	     return false;
	}
	
	public static String getFileType(int msgType,int appMsgType,String filename){
		 if(msgType==49 && appMsgType==6){
			 String type=getType(filename);
			 return type;
		 }
        if(msgType==3 && appMsgType==0){
       	 return "image";
		 }
        if(msgType==43 && appMsgType==0){
       	 return "video";
		 }
	     return "";
	}
	
	public static String getType(String fileName){
		String type=fileName.substring(fileName.lastIndexOf(".")+1);
		for(int i=0;i<vedioTypes.length;i++){
			if(type.equals(vedioTypes[i])){
				return "video";
			}
		}
		return "file";
	}
	
	
	public static String getGroupName(JSONObject wxUser,String fromUserName){
		JSONArray groupList=wxUser.getJSONArray("groupList");
		for(int i=0;i<groupList.size();i++){
			JSONObject member=groupList.getJSONObject(i);
			if(member.getString("UserName").equals(fromUserName)){
				return member.getString("NickName");
			}
		}
		return "";
	}

}
