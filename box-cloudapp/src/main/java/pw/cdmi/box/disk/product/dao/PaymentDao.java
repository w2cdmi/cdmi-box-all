package pw.cdmi.box.disk.product.dao;

import pw.cdmi.box.disk.product.domain.PaymentInfo;

public interface PaymentDao {

	void create(PaymentInfo payment);

	PaymentInfo getPayment(long orderId);

}
