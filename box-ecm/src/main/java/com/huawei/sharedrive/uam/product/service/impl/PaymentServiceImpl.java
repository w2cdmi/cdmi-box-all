package com.huawei.sharedrive.uam.product.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huawei.sharedrive.uam.product.dao.PaymentDao;
import com.huawei.sharedrive.uam.product.domain.PaymentInfo;
import com.huawei.sharedrive.uam.product.service.PaymentService;


@Service
public class PaymentServiceImpl implements PaymentService{
	
	@Autowired
	private PaymentDao paymentDao;

	@Override
	public void create(PaymentInfo payment) {
		// TODO Auto-generated method stub
		paymentDao.create(payment);
	}

	@Override
	public PaymentInfo getPayment(long orderId) {
		// TODO Auto-generated method stub
		return paymentDao.getPayment(orderId);
	}

}
