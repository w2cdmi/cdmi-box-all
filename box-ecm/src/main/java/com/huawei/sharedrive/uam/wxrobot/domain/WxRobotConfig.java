package com.huawei.sharedrive.uam.wxrobot.domain;

import java.util.HashMap;
import java.util.Map;

public class WxRobotConfig {
	
	//个人消息配置
	public static byte TYPE_USER=1;
	//群组消息配置
	public static byte TYPE_GROUP=2;
	
	public static byte TYPE_WhiteList=3;
	
	public static long VALUE_DEFAULT=111;
	
	public static String NAME_USER="config_user_def";
	
	public static String NAME_GROUP="config_group_def";
	
	private long robotId;
	private String name;
	private long value;
	private byte type;
	public Map<String, Boolean> config ;
	
	
	public long getRobotId() {
		return robotId;
	}
	public void setRobotId(long robotId) {
		this.robotId = robotId;
	}
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	} 
    
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	
	
	
	public Map<String, Boolean> getConfig() {
		return config;
	}
	public void setConfig(Map<String, Boolean> config) {
		this.config = config;
	}
	
	
	public  void initConfig() {
		    this.config = new HashMap<String, Boolean>();
	    	Boolean file =  (int) (this.value % 10)==1? true : false;
	    	Boolean image = (int) (this.value % 100)/10==1? true : false;
	    	Boolean video = (int) (this.value % 1000)/100==1? true : false;
	        this.config.put("file", file);
	        this.config.put("image", image);
	        this.config.put("video", video);
	}
	
	public  void parseConfig() {
		    long tempValue = 0;
		    if(this.config.get("file")){
		    	tempValue = tempValue+1;
		    }else{
		    	tempValue = tempValue+0;
		    }
		    if(this.config.get("image")){
		    	tempValue = tempValue+10;
		    }else{
		    	tempValue = tempValue+0;
		    }
		    if(this.config.get("video")){
		    	tempValue = tempValue+100;
		    }else{
		    	tempValue = tempValue+0;
		    }
		    this.value = tempValue;
	      
	}
}
