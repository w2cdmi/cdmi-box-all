package pw.cdmi.box.disk.product.domain;

import java.util.Date;

public class PaymentInfo {
	
	private long id;
	
	private long orderId;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	//1.微信支付 2.支付宝 3.银行卡
	private byte type;
	
	private String payAccount;
	
	private double payAmout;
	
	private Date payDate;
	
	private String remark;

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getPayAccount() {
		return payAccount;
	}

	public void setPayAccount(String payAccount) {
		this.payAccount = payAccount;
	}

	public double getPayAmout() {
		return payAmout;
	}

	public void setPayAmout(double payAmout) {
		this.payAmout = payAmout;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	

}
