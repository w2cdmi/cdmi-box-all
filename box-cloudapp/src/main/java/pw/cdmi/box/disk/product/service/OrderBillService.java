package pw.cdmi.box.disk.product.service;

import java.util.List;
import java.util.Map;

import pw.cdmi.box.disk.product.domain.EnterpriseVip;
import pw.cdmi.box.disk.product.domain.OrderBill;

public interface OrderBillService {

	Map<String, Object> create(OrderBill orderBill, String string);

	List<OrderBill> list(OrderBill orderBill);

	void updateStatus(OrderBill orderBill);

	byte getOrderStatus(String orderId);

	EnterpriseVip getEnterpriseVip(String token);

}
