package pw.cdmi.box.disk.oauth2.domain;

import java.util.Date;

import pw.cdmi.box.disk.event.domain.Event;
import pw.cdmi.box.disk.user.domain.User;

public class UserToken extends User {
	public final static int DATA_SERVER_EXPIREDTIME_SECONDS = 300 * 60;

	public static final long EXPIRED_TIME = 30 * 60;

	private static final long serialVersionUID = -7027930092174727949L;

	public static UserToken buildUserToken(User user) {
		UserToken userToken = new UserToken();
		userToken.setCreatedAt(user.getCreatedAt());
		userToken.setDepartment(user.getDepartment());
		userToken.setDomain(user.getDomain());
		userToken.setEmail(user.getEmail());
		userToken.setId(user.getId());
		userToken.setCloudUserId(user.getCloudUserId());
		userToken.setLoginName(user.getLoginName());
		userToken.setModifiedAt(user.getModifiedAt());
		userToken.setName(user.getName());
		userToken.setObjectSid(user.getObjectSid());
		userToken.setPassword(user.getPassword());
		userToken.setSpaceUsed(user.getSpaceUsed());
		userToken.setStatus(user.getStatus());
		userToken.setTeamSpaceFlag(user.getTeamSpaceFlag());
		userToken.setTeamSpaceMaxNum(user.getTeamSpaceMaxNum());
		userToken.setMobile(user.getMobile());
		userToken.setType(user.getType());
		return userToken;
	}

	private String appName;

	private String auth;

	private Date createdAt;

	private String deviceAddress;

	private String deviceAgent;

	private String deviceName;

	private String deviceOS;

	private String deviceSn;

	private int deviceType;

	private String regionIp;

	private Event event;

	private Date expiredAt;

	private String linkCode;

	private String plainAccessCode;

	private String proxyAddress;

	private String refreshToken;

	private String token;

	private String tokenType;
	
	private byte type;
	
	//员工安全等级
  	private byte staffLevel;
	
	private int teamSpaceFlag;

	private int teamSpaceMaxNum;

	private boolean needChangePassword;
	
	 private Long wxCloudUserId;

	private String pwdLevel;

	private byte isAdmin;

	public String getAppName() {
		return appName;
	}

	public String getAuth() {
		return auth;
	}

	@Override
	public Date getCreatedAt() {
		if (createdAt == null) {
			return null;
		}
		return (Date) createdAt.clone();
	}

	public String getDeviceAddress() {
		return deviceAddress;
	}

	public String getDeviceAgent() {
		return deviceAgent;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public String getDeviceOS() {
		return deviceOS;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public int getDeviceType() {
		return deviceType;
	}

	public Event getEvent() {
		if (null == event) {
			event = new Event();
			event.setDeviceAddress(this.deviceAddress);
			event.setDeviceAgent(this.deviceAgent);
			event.setDeviceSn(this.deviceSn);
			event.setDeviceType(this.deviceType);
		}
		return event;
	}

	public Date getExpiredAt() {
		if (expiredAt == null) {
			return null;
		}
		return (Date) expiredAt.clone();
	}

	public String getLinkCode() {
		return linkCode;
	}

	public String getPlainAccessCode() {
		return plainAccessCode;
	}

	public String getProxyAddress() {
		return proxyAddress;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public String getToken() {
		return token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	@Override
	public void setCreatedAt(Date createdAt) {
		if (createdAt == null) {
			this.createdAt = null;
		} else {
			this.createdAt = (Date) createdAt.clone();
		}
	}

	public void setDeviceAddress(String deviceAddress) {
		this.deviceAddress = deviceAddress;
	}

	public void setDeviceAgent(String deviceAgent) {
		this.deviceAgent = deviceAgent;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public void setDeviceType(int deviceType) {
		this.deviceType = deviceType;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public void setExpiredAt(Date expiredAt) {
		if (expiredAt == null) {
			this.expiredAt = null;
		} else {
			this.expiredAt = (Date) expiredAt.clone();
		}
	}

	public void setLinkCode(String linkCode) {
		this.linkCode = linkCode;
	}

	public void setPlainAccessCode(String plainAccessCode) {
		this.plainAccessCode = plainAccessCode;
	}

	public void setProxyAddress(String proxyAddress) {
		this.proxyAddress = proxyAddress;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public byte getType() {
		return type;
	}

	public byte getStaffLevel() {
		return staffLevel;
	}

	public void setStaffLevel(byte staffLevel) {
		this.staffLevel = staffLevel;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getTeamSpaceFlag() {
		return teamSpaceFlag;
	}

	public void setTeamSpaceFlag(int teamSpaceFlag) {
		this.teamSpaceFlag = teamSpaceFlag;
	}

	public int getTeamSpaceMaxNum() {
		return teamSpaceMaxNum;
	}

	public void setTeamSpaceMaxNum(int teamSpaceMaxNum) {
		this.teamSpaceMaxNum = teamSpaceMaxNum;
	}

	public boolean isNeedChangePassword() {
		return needChangePassword;
	}

	public void setNeedChangePassword(boolean needChangePassword) {
		this.needChangePassword = needChangePassword;
	}

	public String getPwdLevel() {
		return pwdLevel;
	}

	public void setPwdLevel(String pwdLevel) {
		this.pwdLevel = pwdLevel;
	}

	public String getRegionIp() {
		return regionIp;
	}

	public void setRegionIp(String regionIp) {
		this.regionIp = regionIp;
	}

	public Long getWxCloudUserId() {
		return wxCloudUserId;
	}

	public void setWxCloudUserId(Long wxCloudUserId) {
		this.wxCloudUserId = wxCloudUserId;
	}

	public byte getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(byte isAdmin) {
		this.isAdmin = isAdmin;
	}
}
