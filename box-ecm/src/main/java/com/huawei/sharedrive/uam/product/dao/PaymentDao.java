package com.huawei.sharedrive.uam.product.dao;

import com.huawei.sharedrive.uam.product.domain.PaymentInfo;

public interface PaymentDao {

	void create(PaymentInfo payment);

	PaymentInfo getPayment(long orderId);

}
