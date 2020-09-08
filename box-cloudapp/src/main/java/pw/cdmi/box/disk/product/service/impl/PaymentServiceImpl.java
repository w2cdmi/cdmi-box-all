package pw.cdmi.box.disk.product.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.box.disk.product.dao.PaymentDao;
import pw.cdmi.box.disk.product.domain.PaymentInfo;
import pw.cdmi.box.disk.product.service.PaymentService;

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
