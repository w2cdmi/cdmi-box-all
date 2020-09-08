package com.huawei.sharedrive.uam.product.service;

import com.huawei.sharedrive.uam.product.domain.PaymentInfo;

public interface PaymentService {

	void create(PaymentInfo payment);

	PaymentInfo getPayment(long orderId);

}
