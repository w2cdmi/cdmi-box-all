package com.huawei.sharedrive.uam.weixin.domain;

import java.math.BigDecimal;
import java.util.Date;

public class UserProfitDetail {
	
	public static byte STATUS_UNPAID=1;
	
	public static byte STATUS_PAYMENT=2;
	
	public static byte STATUS_PAYFAIL=3;
	//自动转账
	public static byte TYPE_AUTO=1;
	//手动转账
	public static byte TYPE_MANUAL=2;
	
    //个人账户
	public static byte USERTYPE_PERSONAL = 1;
	 //企业账户
	public static byte USERTYPE_COMPANY = 2;
	
	public static String SOURCE_BUYSPACE = "buyspace";
	
	private String id;
	
	private String siteId;
	
	private String appId;
	
	private byte userType;
	
	private long cloudUserId;
	
	private String unionID;
	
	private String openId;
	
	private long enterpriseId;
	
	private String orderId;
	
	private String userName;
	
	private BigDecimal  payMoney; 
	
	private Date createAt;
	
	private Date finishAt;
	
	private byte status;
	
	private byte type;
	
	private double proportions;
	
	private int attempts;
	
	private String failReason;
	
	private String source;
	

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getUnionID() {
		return unionID;
	}

	public void setUnionID(String unionID) {
		this.unionID = unionID;
	}


	public BigDecimal getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}

	public Date getCreateAt() {
		return createAt;
	}

	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}

	public Date getFinishAt() {
		return finishAt;
	}

	public void setFinishAt(Date finishAt) {
		this.finishAt = finishAt;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte getUser_type() {
		return userType;
	}

	public void setUser_type(byte user_type) {
		this.userType = user_type;
	}

	public long getCloudUserId() {
		return cloudUserId;
	}

	public void setCloudUserId(long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public byte getUserType() {
		return userType;
	}

	public void setUserType(byte userType) {
		this.userType = userType;
	}

	public double getProportions() {
		return proportions;
	}

	public void setProportions(double proportions) {
		this.proportions = proportions;
	}

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
    
	
}
