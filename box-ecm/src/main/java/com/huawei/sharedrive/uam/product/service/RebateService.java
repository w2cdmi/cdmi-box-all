package com.huawei.sharedrive.uam.product.service;

import com.huawei.sharedrive.uam.product.domain.Rebate;

public interface RebateService {
	
	Rebate getRebateByProductIdAndDuration(long productId, long duration);
	
}
