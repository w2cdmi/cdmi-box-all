package pw.cdmi.box.disk.product.domain;

import java.util.Date;

public class OrderBill {
	
	//未支付
	public static byte STATU_UNPAID=1;
	//已支付 处理中
	public static byte STATU_PROCESS=2;
	//已完成
	public static byte STATU_COMPLETE=3;
	//失败 
	public static byte STATU_FAILD=4;
	
	public static byte TYPE_NEWBUY=1;
	
	public static byte TYPE_RENEW=2;
	
	public static byte TYPE_UPGRADE=3;
	
    public static byte USERTYPE_PERSONAL=1;
	
	public static byte USERTYPE_COMPANY=2;
	
	
	
	
	private String id;
	
	private double price;
	
	private byte status;
	//1.新购 2.升级 3.续费
	private byte type;
	//个人或者公司
	private byte userType;
	
	private Date submitDate;
	
	private Date finishedDate;
	
	private String descript;
	
	private long enterpriseId;
	
	private long enterpriseUserId;
	
	private long accountId;
	
	private long cloudUserId;
	
	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	private long productId;
	
	private long duration;

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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
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
	
	
	
}
