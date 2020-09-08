package com.huawei.sharedrive.uam.product.dao.impl;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.product.dao.PaymentDao;
import com.huawei.sharedrive.uam.product.domain.PaymentInfo;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

@Repository
public class PaymentDaoImpl extends CacheableSqlMapClientDAO implements PaymentDao{

	@Override
	public void create(PaymentInfo payment) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("PaymentInfo.create",payment);
	}

	@Override
	public PaymentInfo getPayment(long orderId) {
		// TODO Auto-generated method stub
		return (PaymentInfo) sqlMapClientTemplate.queryForObject("PaymentInfo.get",orderId);
	}

}
