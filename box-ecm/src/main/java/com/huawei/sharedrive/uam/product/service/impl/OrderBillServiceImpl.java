package com.huawei.sharedrive.uam.product.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.core.restrpc.RestClient;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXWebPayConfigImpl;
import com.github.wxpay.sdk.WXPayUtil;
import com.huawei.sharedrive.uam.product.dao.OrderBillDao;
import com.huawei.sharedrive.uam.product.domain.OrderBill;
import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.product.service.OrderBillService;
import com.huawei.sharedrive.uam.util.PropertiesUtils;

@Service
public class OrderBillServiceImpl implements OrderBillService {

	@Autowired
	private OrderBillDao orderBillDao;

	@Resource
	private RestClient uamClientService;

	@Override
	public void create(OrderBill orderBill) {
		// TODO Auto-generated method stub
		orderBillDao.create(orderBill);
	}

	@Override
	public void updateStatus(OrderBill orderBill) {
		// TODO Auto-generated method stub
		orderBillDao.updateStatus(orderBill);
	}

	@Override
	public List<OrderBill> list(OrderBill orderBill) {
		// TODO Auto-generated method stub
		return orderBillDao.list(orderBill);
	}

	/**
	 * 统一订单接口
	 */
	@Override
	public String doUnifiedOrder(OrderBill orderBill,
			HttpServletRequest request, Product product) {
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("body", product.getCompanyName() + "-" + product.getName());
		data.put("out_trade_no", orderBill.getId());
		data.put("fee_type", "CNY");
		data.put("total_fee", (long)orderBill.getPayMoney() + "");
		data.put("spbill_create_ip", request.getRemoteAddr());
		data.put("notify_url", PropertiesUtils.getProperty("wx.notify.url"));
		data.put("trade_type", "NATIVE");
		data.put("product_id", orderBill.getProductId() + "");
		try {
			WXWebPayConfigImpl config = WXWebPayConfigImpl.getInstance();
			WXPay wxpay = new WXPay(config);
			data.put("sign", WXPayUtil.generateSignature(data, config.getKey()));
			Map<String, String> r = wxpay.unifiedOrder(data);
			return r.get("code_url");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public OrderBill getOrder(String orderId) {
		// TODO Auto-generated method stub
		OrderBill orderBill = orderBillDao.getOrder(orderId);
		return orderBill;
	}

}
