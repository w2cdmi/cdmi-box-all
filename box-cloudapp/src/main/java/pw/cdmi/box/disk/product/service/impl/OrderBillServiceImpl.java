package pw.cdmi.box.disk.product.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import pw.cdmi.box.disk.httpclient.rest.common.Constants;
import pw.cdmi.box.disk.product.dao.OrderBillDao;
import pw.cdmi.box.disk.product.domain.EnterpriseVip;
import pw.cdmi.box.disk.product.domain.OrderBill;
import pw.cdmi.box.disk.product.service.OrderBillService;
import pw.cdmi.box.disk.teamspace.domain.RestNodeACLCreateRequest;
import pw.cdmi.box.disk.teamspace.domain.RestNodeACLInfo;
import pw.cdmi.core.exception.RestException;
import pw.cdmi.core.restrpc.RestClient;
import pw.cdmi.core.restrpc.domain.TextResponse;
import pw.cdmi.core.utils.JsonUtils;

@Service
public class OrderBillServiceImpl implements OrderBillService{
	
	@Autowired
	private OrderBillDao orderBillDao;
	
	@Resource
	private RestClient uamClientService;

	@Override
	public Map<String, Object> create(OrderBill orderBill,String userToken) {
		// TODO Auto-generated method stub
		
		
		String url = "/api/v2/order/create?productId="+orderBill.getProductId()+"&type="+orderBill.getType();
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performJsonPostTextResponse(url, headers, orderBill);
		String content = response.getResponseBody();
		if (response.getStatusCode()== HttpStatus.OK.value()) {
		   return JsonUtils.stringToMap(content); 
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
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

	@Override
	public byte getOrderStatus(String orderId) {
		// TODO Auto-generated method stub
		return orderBillDao.getOrderStatus(orderId);
	}

	@Override
	public EnterpriseVip getEnterpriseVip(String userToken) {
		// TODO Auto-generated method stub
		String url = "/api/v2/order/getEnterpriseVip";
		Map<String, String> headers = new HashMap<>();
		headers.put("Authorization", userToken);
		TextResponse response = uamClientService.performGetText(url, headers);
		String content = response.getResponseBody();
		if (response.getStatusCode()== HttpStatus.OK.value()) {
		   return JsonUtils.stringToObject(content, EnterpriseVip.class);
		}
		RestException exception = JsonUtils.stringToObject(content, RestException.class);
		throw exception;
	}

}
