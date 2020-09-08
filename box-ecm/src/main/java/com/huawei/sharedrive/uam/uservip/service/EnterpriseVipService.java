package com.huawei.sharedrive.uam.uservip.service;

import java.util.List;

import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.uservip.domain.EnterpriseVip;

public interface EnterpriseVipService {
	void create(EnterpriseVip enterpriseVip);

	void update(EnterpriseVip enterpriseVip);

	EnterpriseVip get(EnterpriseVip enterpriseVip);

	List<EnterpriseVip> listAll();

	//查询企业账号下的套餐信息，如果没有购买，返回免费套餐
	Product getProductByEnterpriseAccountId(String appId, long accountId);
}
