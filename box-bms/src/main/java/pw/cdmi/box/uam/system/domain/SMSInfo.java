package pw.cdmi.box.uam.system.domain;

import java.io.Serializable;

public class SMSInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String concatPhone;

	private String templateId;
	
	private String name;
	
	private String password;
	
	private String price;
	
	private String identifyCode;
	
	public String getConcatPhone() {
		return concatPhone;
	}

	public void setConcatPhone(String concatPhone) {
		this.concatPhone = concatPhone;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIdentifyCode() {
		return identifyCode;
	}

	public void setIdentifyCode(String identifyCode) {
		this.identifyCode = identifyCode;
	}
	
}
