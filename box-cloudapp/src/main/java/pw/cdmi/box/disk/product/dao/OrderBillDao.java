package pw.cdmi.box.disk.product.dao;

import java.util.List;

import pw.cdmi.box.disk.product.domain.OrderBill;

public interface OrderBillDao {

	void create(OrderBill orderBill);

	List<OrderBill> list(OrderBill orderBill);

	void update(OrderBill orderBill);

	void updateStatus(OrderBill orderBill);

	byte getOrderStatus(String orderId);

}
