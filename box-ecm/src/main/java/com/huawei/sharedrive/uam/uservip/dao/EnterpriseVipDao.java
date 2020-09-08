package com.huawei.sharedrive.uam.uservip.dao;

import java.util.List;

import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.uservip.domain.EnterpriseVip;

public interface EnterpriseVipDao {

	void create(EnterpriseVip enterpriseVip);

	void update(EnterpriseVip enterpriseVip);

	EnterpriseVip get(EnterpriseVip enterpriseVip);

	List<EnterpriseVip> listAll();

	Product getProductByEnterpriseAccountId(long accountId);
}
