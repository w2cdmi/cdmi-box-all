package pw.cdmi.box.disk.product.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;
import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;
import pw.cdmi.box.disk.product.dao.OrderBillDao;
import pw.cdmi.box.disk.product.domain.OrderBill;

@Repository
public class OrderBillDaoImpl extends CacheableSqlMapClientDAO implements OrderBillDao{

	@Override
	public void create(OrderBill orderBill) {
		// TODO Auto-generated method stub
		sqlMapClientTemplate.insert("OrderBill.create",orderBill);
	}

	@Override
	public List<OrderBill> list(OrderBill orderBill) {
		// TODO Auto-generated method stub
		return sqlMapClientTemplate.queryForList("OrderBill.list",orderBill);
	}

	@Override
	public void update(OrderBill orderBill) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStatus(OrderBill orderBill) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte getOrderStatus(String id) {
		// TODO Auto-generated method stub
		return (byte) sqlMapClientTemplate.queryForObject("OrderBill.getOrderStatus",id);
	}

}
