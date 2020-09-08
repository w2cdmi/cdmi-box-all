package com.huawei.sharedrive.uam.weixin.domain;
/* 
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2017 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2017 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */

import java.util.Date;

/************************************************************
 * @author Rox
 * @version 3.0.1
 * @Description: <pre>微信用户</pre>
 * @Project Alpha CDMI Service Platform, box-uam-web Component. 2017/7/26
 ************************************************************/
public class WxUser {
	
	public static byte STATUS_NORMAL;
	
	public static final byte TYPE_TEMPORARY_VIP3 = 103; //普通账号升级为钻石VIP
    public static final byte TYPE_TEMPORARY_VIP2 = 102; //普通账号升级为铂金VIP
    public static final byte TYPE_TEMPORARY_VIP1 = 101; //普通账号升级为黄金VIP
    public static final byte TYPE_TEMPORARY = 0; 		//普通账号
	
    //用户在微信开放平台的唯一标识符。
    String unionId;
    //用户唯一标识
    String openId;
    //微信用户信息标识
    String uin;
    String nickName;
    Integer gender;
    String mobile;
    String email;
    String country;
    String province;
    String city;
    String language;
    String avatarUrl;
    Date createdAt;
    Date modifiedAt;
    
    private String inviterId;
    
    private long countInvitByMe;
    private long countTotalProfits;
    
    private long countTodayInvitByMe;
    private long countTodayProfits;
    
    private Byte status;
    private Long cloudUserId;
    private Integer regionId;
    private Long quota;
    private Byte type;	//账号类型
    
    private int shareLevel;	//账号类型
    

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

	public Long getCloudUserId() {
		return cloudUserId;
	}

	public void setCloudUserId(Long cloudUserId) {
		this.cloudUserId = cloudUserId;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Integer getRegionId() {
		return regionId;
	}

	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}

	public Long getQuota() {
		return quota;
	}

	public void setQuota(Long quota) {
		this.quota = quota;
	}

	public Byte getType() {
		return type;
	}

	public void setType(Byte type) {
		this.type = type;
	}

	public String getInviterId() {
		return inviterId;
	}

	public void setInviterId(String inviterId) {
		this.inviterId = inviterId;
	}

	public long getCountInvitByMe() {
		return countInvitByMe;
	}

	public void setCountInvitByMe(long countInvitByMe) {
		this.countInvitByMe = countInvitByMe;
	}

	public long getCountTodayInvitByMe() {
		return countTodayInvitByMe;
	}

	public void setCountTodayInvitByMe(long countTodayInvitByMe) {
		this.countTodayInvitByMe = countTodayInvitByMe;
	}

	public int getShareLevel() {
		return shareLevel;
	}

	public void setShareLevel(int shareLevel) {
		this.shareLevel = shareLevel;
	}

	public long getCountTotalProfits() {
		return countTotalProfits;
	}

	public void setCountTotalProfits(long countTotalProfits) {
		this.countTotalProfits = countTotalProfits;
	}

	public long getCountTodayProfits() {
		return countTodayProfits;
	}

	public void setCountTodayProfits(long countTodayProfits) {
		this.countTodayProfits = countTodayProfits;
	}

	
	
}
