package com.huawei.sharedrive.uam.openapi.domain;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.huawei.sharedrive.uam.exception.InvalidParamterException;
import com.huawei.sharedrive.uam.weixin.rest.Watermark;

public class RestUserRegister {

	private String appId;

	private long enterpriseId;

	private long deptId;

	private String phone;

	private String checkCode;
	
	private String name;
	
	private String code;
//	
//	// 用户唯一标识
//	private String openId;
//	// 用户在微信开放平台的唯一标识符。
//	private String unionId;
//	private String nickName;
//	private Integer gender;
//	private String country;
//	private String province;
//	private String city;
//	private String language;
//	private String avatarUrl;
//	Watermark watermark;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public long getDeptId() {
		return deptId;
	}

	public void setDeptId(long deptId) {
		this.deptId = deptId;
	}

	public String getCheckCode() {
		return checkCode;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

//	public String getUnionId() {
//		return unionId;
//	}
//
//	public void setUnionId(String unionId) {
//		this.unionId = unionId;
//	}
//
//	public String getOpenId() {
//		return openId;
//	}
//
//	public void setOpenId(String openId) {
//		this.openId = openId;
//	}
//
//	public String getNickName() {
//		return nickName;
//	}
//
//	public void setNickName(String nickName) {
//		this.nickName = nickName;
//	}
//
//	public Integer getGender() {
//		return gender;
//	}
//
//	public void setGender(Integer gender) {
//		this.gender = gender;
//	}
//
//	public String getCountry() {
//		return country;
//	}
//
//	public void setCountry(String country) {
//		this.country = country;
//	}
//
//	public String getProvince() {
//		return province;
//	}
//
//	public void setProvince(String province) {
//		this.province = province;
//	}
//
//	public String getCity() {
//		return city;
//	}
//
//	public void setCity(String city) {
//		this.city = city;
//	}
//
//	public String getLanguage() {
//		return language;
//	}
//
//	public void setLanguage(String language) {
//		this.language = language;
//	}
//
//	public String getAvatarUrl() {
//		return avatarUrl;
//	}
//
//	public void setAvatarUrl(String avatarUrl) {
//		this.avatarUrl = avatarUrl;
//	}
//
//	public Watermark getWatermark() {
//		return watermark;
//	}
//
//	public void setWatermark(Watermark watermark) {
//		this.watermark = watermark;
//	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
