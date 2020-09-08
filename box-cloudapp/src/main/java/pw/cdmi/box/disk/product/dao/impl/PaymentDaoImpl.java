package pw.cdmi.box.disk.product.dao.impl;

import org.springframework.stereotype.Repository;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.product.dao.PaymentDao;
import pw.cdmi.box.disk.product.domain.PaymentInfo;

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
