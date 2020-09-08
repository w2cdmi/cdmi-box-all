package com.huawei.sharedrive.uam.openapi.domain.user;

import java.util.List;

import pw.cdmi.box.domain.Order;

public class PageRequestUserProfits {

	// 查询条数
    private Integer limit;
    // 偏移量
    private Long offset;

    private List<Order> order;

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}
    
    
}
