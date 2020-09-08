package com.huawei.sharedrive.uam.product.domain;

import java.util.Date;

public class OrderBill {

	// 未支付
	public static byte STATU_UNPAID = 1;
	// 已支付 处理中
	public static byte STATU_PROCESS = 2;
	// 已完成
	public static byte STATU_COMPLETE = 3;
	// 失败
	public static byte STATU_FAILD = 4;
	// 新买
	public static byte TYPE_NEWBUY = 1;
	// 续费
	public static byte TYPE_RENEW = 2;
	// 升级
	public static byte TYPE_UPGRADE = 3;

	public static byte USERTYPE_PERSONAL = 1;

	public static byte USERTYPE_COMPANY = 2;
	// 无折扣
	public static float DISCOUNT_NONE = 1;
	
	// 余额初始值
	public static float COST_INIT = 0;

	private String id;

	private long productId;

	private int duration;

	private double totalPrice;

	private float discountRatio;

	private double discountPrice;
	//账号剩余费用
	private double surplusCost;
	//用户本次应付金额
	private double payMoney;

	// 新购 续费 升级
	private byte type;

	// 用户类型 个人或者企业
	private byte userType;

	private Date submitDate;

	private Date finishedDate;

	private long enterpriseId;

	private long enterpriseUserId;

	private long accountId;

	private long cloudUserId;

	private String descript;

	// 订单状态
	private byte status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public Date getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public long getEnterpriseUserId() {
		return enterpriseUserId;
	}

	public void setEnterpriseUserId(long enterpriseUserId) {
		this.enterpriseUserId = enterpriseUserId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public byte getUserType() {
		return userType;
	}

	public void setUserType(byte userType) {
		this.userType = userType;
	}

	public long getCloudUserId() {
		return cloudUserId;
	}

	public void setCloudUserId(long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public float getDiscountRatio() {
		return discountRatio;
	}

	public void setDiscountRatio(float discountRatio) {
		this.discountRatio = discountRatio;
	}

	public double getDiscountPrice() {
		return discountPrice;
	}

	public void setDiscountPrice(double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public double getSurplusCost() {
		return surplusCost;
	}

	public void setSurplusCost(double surplusCost) {
		this.surplusCost = surplusCost;
	}

	public double getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	
}
