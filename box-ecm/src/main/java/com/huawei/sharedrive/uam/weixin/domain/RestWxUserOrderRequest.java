package com.huawei.sharedrive.uam.weixin.domain;

import pw.cdmi.core.exception.InvalidParamException;

public class RestWxUserOrderRequest {
	
	private String openId;
	
	private long productId;
	
	private String mpId;
	
	private int duration;

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getMpId() {
		return mpId;
	}

	public void setMpId(String mpId) {
		this.mpId = mpId;
	}

	public void checkParameter() throws InvalidParamException
    {
        if (openId == null)
        {
            throw new InvalidParamException();
        }
        
        if (productId == 0)
        {
            throw new InvalidParamException();
        }
        
        if(duration < 3){
        	throw new InvalidParamException();
        }
	}
}
