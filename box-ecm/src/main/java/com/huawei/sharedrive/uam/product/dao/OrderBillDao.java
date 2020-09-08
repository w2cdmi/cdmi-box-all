package com.huawei.sharedrive.uam.product.dao;


import java.util.List;

import com.huawei.sharedrive.uam.product.domain.OrderBill;

public interface OrderBillDao {

	void create(OrderBill orderBill);

	List<OrderBill> list(OrderBill orderBill);

	void update(OrderBill orderBill);

	void updateStatus(OrderBill orderBill);

	OrderBill getOrder(String orderId);

}
