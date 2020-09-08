package com.huawei.sharedrive.uam.openapi.rest;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import pw.cdmi.common.cache.CacheClient;

import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.product.service.OrderBillService;
import com.huawei.sharedrive.uam.product.service.ProductService;
import com.huawei.sharedrive.uam.product.service.RebateService;
import com.huawei.sharedrive.uam.uservip.service.EnterpriseVipService;

@Controller
@RequestMapping(value = "/api/v2/order")
public class OrderBillAPIController {

	
	private static Logger logger = LoggerFactory.getLogger(OrderBillAPIController.class);
	
	@Autowired
	private OrderBillService orderBillService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Resource(name = "cacheClient")
	private CacheClient cacheClient;
	
	@Autowired
	private RebateService rebateService;
	

	@Autowired
	private EnterpriseVipService enterpriseVipService;

//	@RequestMapping(value = "/create", method = { RequestMethod.POST })
//	public ResponseEntity<?> createOrder(
//			@RequestHeader("Authorization") String authorization,
//			@RequestBody OrderBill orderBill, HttpServletRequest request) {
//		try {
//			UserToken userToken = userTokenHelper
//					.checkTokenAndGetUser(authorization);
//			Product product = productService.get(orderBill.getProductId());
//			orderBill.setId(WXPayUtil.generateUUID());
//			orderBill.setEnterpriseId(userToken.getEnterpriseId());
//			orderBill.setEnterpriseUserId(userToken.getId());
//			orderBill.setAccountId(userToken.getAccountId());
//			orderBill.setStatus(OrderBill.STATU_UNPAID);
//			orderBill.setSubmitDate(new Date());
//			countPrice(orderBill);
//			orderBillService.create(orderBill);
//			String url = orderBillService.doUnifiedOrder(orderBill, request,
//					product);
//
//			Map<String, Object> result = new HashMap<>();
//			result.put("orderId", orderBill.getId());
//			result.put("url", url);
//			return new ResponseEntity<Map<String, Object>>(result,
//					HttpStatus.OK);
//		} catch (Exception e) {
//			return new ResponseEntity<String>(e.getMessage(),
//					HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	@RequestMapping(value = "/getEnterpriseVip", method = { RequestMethod.GET })
//	@ResponseBody
//	public ResponseEntity<?> getEnterpriseVip(
//			@RequestHeader("Authorization") String authorization,
//			HttpServletRequest request) {
//		try {
//			UserToken userToken = userTokenHelper
//					.checkTokenAndGetUser(authorization);
//			EnterpriseVip filter = new EnterpriseVip();
//			filter.setEnterpriseId(userToken.getEnterpriseId());
//			filter.setEnterpriseAccountId(userToken.getAccountId());
//			EnterpriseVip enterpriseVip = enterpriseVipService.get(filter);
//			return new ResponseEntity<EnterpriseVip>(enterpriseVip,
//					HttpStatus.OK);
//		} catch (Exception e) {
//			return new ResponseEntity<String>(e.getMessage(),
//					HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	@RequestMapping(value = "/list", method = { RequestMethod.GET })
//	public ResponseEntity<?> listOrder(
//			@RequestHeader("Authorization") String authorization,
//			OrderBill orderBill, HttpServletRequest request) {
//
//		try {
//			UserToken user = userTokenHelper
//					.checkTokenAndGetUser(authorization);
//			orderBill.setEnterpriseId(user.getEnterpriseId());
//			orderBill.setEnterpriseUserId(user.getId());
//			List<OrderBill> orderBillList = orderBillService.list(orderBill);
//			return new ResponseEntity<List<OrderBill>>(orderBillList,
//					HttpStatus.OK);
//		} catch (Exception e) {
//			// TODO: handle exception
//			return new ResponseEntity<String>(e.getMessage(),
//					HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	private void countPrice(OrderBill orderBill) throws Exception {
//	    
//		if (orderBill.getUserType() == OrderBill.USERTYPE_COMPANY) {
//			Product product = productService.get(orderBill.getProductId());
//		    Rebate rebate = rebateService.getRebateByProductIdAndDuration(product.getId(), orderBill.getDuration());
////		    long totalprice=product.getOriginalPrice()* orderBill.getDuration();
//		    double surplusCost = calculatingSurplusCost(orderBill);
//		    if(rebate!=null){
////		    	    orderBill.setDiscountPrice(product.getOriginalPrice()*rebate.getDiscountRatio());
//				    orderBill.setDiscountRatio(rebate.getDiscountRatio());
//				    orderBill.setSurplusCost(surplusCost);
////				    orderBill.setPrice(product.getOriginalPrice());
////				    orderBill.setTotalPrice(totalprice);
//				    orderBill.setPayMoney(orderBill.getDiscountPrice()*orderBill.getDuration()-surplusCost);
//		    }else{
////		        orderBill.setPrice(product.getOriginalPrice());
////			    orderBill.setTotalPrice(totalprice);
////			    orderBill.setPayMoney(totalprice-surplusCost);
//		    }
//		   
//		}
//	}
//	
//    //计算剩余费用
//    public double calculatingSurplusCost(OrderBill orderBill){
//    	double surplusCost = 0;
//		EnterpriseVip filter = new EnterpriseVip();
//		filter.setEnterpriseId(orderBill.getEnterpriseId());
//		filter.setEnterpriseAccountId(orderBill.getAccountId());
//		EnterpriseVip enterpriseVip = enterpriseVipService.get(filter);
//		if(enterpriseVip == null){
//			logger.warn("EnterpriseVip info is not exist, cloudUserId: " + orderBill.getCloudUserId());
//			return surplusCost;
//		}
//		Product product = productService.get(enterpriseVip.getProductId());
//		if(product == null){
//			logger.error("product info is not exist, productId: " + orderBill.getProductId());
//			return surplusCost;
//		}
//		
//		Date nowDate =  new Date();
//		int days = differentDaysByMillisecond(nowDate, enterpriseVip.getExpireDate());
//		//之前会员套餐已经过期，或者剩下时间不满一天能
//		if(days <= 0){
//			return surplusCost;
//		}
//		
//		Calendar startCalendar = Calendar.getInstance();
//		startCalendar.setTime(enterpriseVip.getStartDate());
//		
//		Calendar expireCalendar = Calendar.getInstance();
//        expireCalendar.setTime(enterpriseVip.getExpireDate());
//        //会员持续时间
//        int monthsByMonth = expireCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
//        int monthsByYear = (expireCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12;
//        int months = monthsByMonth + monthsByYear;
//        
//        int totalDays = differentDaysByMillisecond(enterpriseVip.getStartDate(), enterpriseVip.getExpireDate());
//		int nowDays = differentDaysByMillisecond(new Date(), enterpriseVip.getExpireDate());
//        //会员打折
//        Rebate rebate = rebateService.getRebateByProductIdAndDuration(product.getId(), months);
//        double totalPrice =  product.getOriginalPrice() * months;
//		if(rebate == null){
//			surplusCost = totalPrice * Math.round((nowDays/totalDays) * 100) * 0.01;
//		}else{
//			//打折后的价格
//			double discountPrice = totalPrice * rebate.getDiscountRatio();
//			surplusCost = discountPrice * Math.round((nowDays/totalDays) * 100) * 0.01;
//		}
//		
//    	return surplusCost;
//    }
//    
//
//public int differentDaysByMillisecond(Date startDate,Date endDate)
//{
//	if(endDate.getTime() < startDate.getTime()){
//		return -1;
//	}
//    int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000*3600*24));
//    return days;
//}


}
