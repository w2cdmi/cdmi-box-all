package com.huawei.sharedrive.uam.product.domain;

import java.util.Date;

/**
 * 产品优惠表
 * @author wangbojun
 *
 */
public class Rebate {
	
	private long id;
	
	private long productId;	//产品编号
	
	private long duration;	//购买时长，单位：月
	
	private float DiscountRatio;	//折扣系数   九折：0.9
	
	private Date startDate;	//折扣开始时间
	
	private Date endDate;	//折扣结束时间
	
	private String introduce;	//介绍
	
	private Date createDate;	//折扣创建时间
	
	private Date updateDate;	//修改时间

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public float getDiscountRatio() {
		return DiscountRatio;
	}

	public void setDiscountRatio(float discountRatio) {
		DiscountRatio = discountRatio;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
}
