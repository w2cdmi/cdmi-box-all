package com.huawei.sharedrive.uam.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.product.dao.RebateDao;
import com.huawei.sharedrive.uam.product.domain.Rebate;
import com.huawei.sharedrive.uam.product.service.RebateService;

@Service
public class RebateServiceImpl implements RebateService{
	
	@Autowired
	private RebateDao rebateDao;

	@Override
	public Rebate getRebateByProductIdAndDuration(long productId, long duration) {
		// TODO Auto-generated method stub
		Rebate rebate = new Rebate();
		rebate.setProductId(productId);
		rebate.setDuration(duration);
		return rebateDao.getRebate(rebate);
	}

}
