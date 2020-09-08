package com.huawei.sharedrive.uam.uservip.service.impl;

import java.util.List;

import com.huawei.sharedrive.uam.config.domain.EnterpriseAccountProfile;
import com.huawei.sharedrive.uam.config.service.SystemProfileService;
import com.huawei.sharedrive.uam.product.dao.ProductDao;
import com.huawei.sharedrive.uam.product.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.uservip.dao.EnterpriseVipDao;
import com.huawei.sharedrive.uam.uservip.domain.EnterpriseVip;
import com.huawei.sharedrive.uam.uservip.service.EnterpriseVipService;

@Service
public class EnterpriseVipServiceImpl implements EnterpriseVipService{
	@Autowired
	private EnterpriseVipDao enterpriseVipDao;

	@Autowired
	private SystemProfileService systemProfileService;

	@Override
	public void create(EnterpriseVip enterpriseVip) {
		// TODO Auto-generated method stub
		enterpriseVipDao.create(enterpriseVip);
	}

	@Override
	public void update(EnterpriseVip enterpriseVip) {
		// TODO Auto-generated method stub
		enterpriseVipDao.update(enterpriseVip);
		
	}

	@Override
	public EnterpriseVip get(EnterpriseVip enterpriseVip) {
		// TODO Auto-generated method stub
		return enterpriseVipDao.get(enterpriseVip);
	}

	@Override
	public List<EnterpriseVip> listAll() {
		// TODO Auto-generated method stub
		return enterpriseVipDao.listAll();
	}

	@Override
	public Product getProductByEnterpriseAccountId(String appId, long accountId) {
		Product product = enterpriseVipDao.getProductByEnterpriseAccountId(accountId);

		//没有购买套餐或者套餐已经过期
		if(product == null) {
			EnterpriseAccountProfile profile = systemProfileService.buildEnterpriseAccountProfile(appId);
			product = new Product();
			product.setName("默认套餐");
			product.setType((byte) 2);
			product.setAccountNum(profile.getMaxUserAmount());//账号数量
			product.setAccountSpace(profile.getMaxUserQuota());//账号空间配额
			product.setTeamNum(profile.getMaxTeamspaceAmount());//空间数量
			product.setTeamSpace(profile.getMaxTeamspaceQuota());//空间配额
			product.setIntroduce("默认套餐");
		}

		return product;
	}
}
