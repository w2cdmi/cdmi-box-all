package pw.cdmi.box.disk.product.service;

import pw.cdmi.box.disk.product.domain.PaymentInfo;

public interface PaymentService {

	void create(PaymentInfo payment);

	PaymentInfo getPayment(long orderId);

}
