package com.huawei.sharedrive.uam.product.service;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.huawei.sharedrive.uam.product.domain.OrderBill;
import com.huawei.sharedrive.uam.product.domain.Product;


public interface OrderBillService {

	void create(OrderBill orderBill);

	List<OrderBill> list(OrderBill orderBill);

	void updateStatus(OrderBill orderBill);

	String doUnifiedOrder(OrderBill orderBill, HttpServletRequest request, Product product);

	OrderBill getOrder(String orderId);

}
