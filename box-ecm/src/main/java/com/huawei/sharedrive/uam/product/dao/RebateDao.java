package com.huawei.sharedrive.uam.product.dao;

import java.util.List;

import com.huawei.sharedrive.uam.product.domain.Rebate;

public interface RebateDao {
	
	//根据用户选择的产品信息，查询产品折扣
	Rebate getRebate(Rebate rebate);
	
	//查询产品的所有优惠信息
	List<Rebate> getByProductId(long productId);
}
