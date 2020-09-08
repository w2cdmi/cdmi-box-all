package com.huawei.sharedrive.uam.product.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.huawei.sharedrive.uam.product.dao.OrderBillDao;
import com.huawei.sharedrive.uam.product.domain.OrderBill;

import pw.cdmi.box.dao.impl.CacheableSqlMapClientDAO;

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
		sqlMapClientTemplate.update("OrderBill.updateStatus",orderBill);
		
	}

	@Override
	public OrderBill getOrder(String orderId) {
		// TODO Auto-generated method stub
		return (OrderBill) sqlMapClientTemplate.queryForObject("OrderBill.orderId",orderId);
	}

}
