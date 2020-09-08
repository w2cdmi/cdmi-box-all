package pw.cdmi.box.disk.wxrobot.model;

import java.util.Map;

public class WxRobotConfig {
	
	//个人消息配置
	public static byte TYPE_USER=1;
	//群组消息配置
	public static byte TYPE_GROUP=2;
	
	//群组消息配置
    public static byte TYPE_BLACK=3;
	
	public static long VALUE_DEFAULT=1111;
	
	private long robotId;
	private String name;
	private long value;
	private byte type;
	public Map<String, Boolean> config;
	
	
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
		initConfig();
	}
	
	public long getRobotId() {
		return robotId;
	}
	public void setRobotId(long robotId) {
		this.robotId = robotId;
	}
	public void initConfig(){
		this.config=WxRobotConfigHandler.getConfig(this.value);
	}
	public Map<String, Boolean> getConfig() {
		return config;
	}
	public void setConfig(Map<String, Boolean> config) {
		this.config = config;
	}

}
