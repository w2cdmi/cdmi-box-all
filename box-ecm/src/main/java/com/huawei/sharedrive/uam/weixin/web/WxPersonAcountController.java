package com.huawei.sharedrive.uam.weixin.web;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.huawei.sharedrive.uam.weixin.domain.*;
import com.huawei.sharedrive.uam.weixin.service.ShareLevelService;
import com.huawei.sharedrive.uam.weixin.service.UserProfitDetailService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pw.cdmi.common.domain.enterprise.EnterpriseAccount;
import pw.cdmi.core.exception.InvalidParamException;
import pw.cdmi.core.restrpc.RestClient;

import com.github.wxpay.sdk.WXPayUtil;
import com.github.wxpay.sdk.WXPayConstants.SignType;
import com.huawei.sharedrive.uam.accountuser.domain.UserAccount;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseService;
import com.huawei.sharedrive.uam.enterpriseuser.domain.EnterpriseUser;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.oauth2.domain.UserToken;
import com.huawei.sharedrive.uam.oauth2.service.impl.UserTokenHelper;
import com.huawei.sharedrive.uam.product.domain.OrderBill;
import com.huawei.sharedrive.uam.product.domain.Product;
import com.huawei.sharedrive.uam.product.domain.ProductDurationPrice;
import com.huawei.sharedrive.uam.product.domain.Rebate;
import com.huawei.sharedrive.uam.product.service.OrderBillService;
import com.huawei.sharedrive.uam.product.service.ProductDurationPriceService;
import com.huawei.sharedrive.uam.product.service.ProductService;
import com.huawei.sharedrive.uam.product.service.RebateService;
import com.huawei.sharedrive.uam.uservip.domain.UserVip;
import com.huawei.sharedrive.uam.uservip.service.UserVipService;
import com.huawei.sharedrive.uam.weixin.rest.WxMpSessionKey;
import com.huawei.sharedrive.uam.weixin.rest.proxy.WxMpOauth2Proxy;
import com.huawei.sharedrive.uam.weixin.service.WxUserService;

@Controller
@RequestMapping(value = "/api/v2/person/acount")
public class WxPersonAcountController {

	private static Logger logger = LoggerFactory.getLogger(WxPersonAcountController.class);

	@Autowired
	private UserTokenHelper userTokenHelper;

	@Autowired
	private WxMpOauth2Proxy wxMpOauth2Proxy;
	
	@Autowired
	private OrderBillService orderBillService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
    private EnterpriseUserService enterpriseUserService;
	
	@Resource
	private RestClient ufmClientService;
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	@Autowired
	private EnterpriseAccountService enterpriseAccountService;
	
	@Autowired
	private RebateService rebateService;
	
	@Autowired
	private UserAccountManager userAccountManager;
	
	@Autowired
	private UserVipService userVipService;
	
	@Autowired
	private WxUserService wxUserService;
	
	@Autowired
	private ProductDurationPriceService productDurationPriceService;

	@Autowired
	private UserProfitDetailService transferMoney2UserDetailService;

	@Autowired
	private ShareLevelService shareLevelService;
	

	/**
	 * 获取微信用户openId
	 */
	@RequestMapping(value = "/openId", method = RequestMethod.GET)
	public ResponseEntity<String> getWxUserOpenId(@RequestHeader("Authorization") String authorization, @RequestParam String mpId, @RequestParam String code) throws Exception {
		userTokenHelper.checkTokenAndGetUser(authorization);

		WxMpSessionKey wxMpSessionKey = wxMpOauth2Proxy.getSessionKeyByCode(mpId, code);
		return new ResponseEntity<>(wxMpSessionKey.getOpenid(), HttpStatus.OK);
	}
	
	/**
	 * 统一下单
	 */
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	public ResponseEntity<?> order(@RequestHeader("Authorization") String authorization,
			@RequestBody RestWxUserOrderRequest restWxUserOrderRequest,
			HttpServletRequest request) {
		
		restWxUserOrderRequest.checkParameter();
		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
		
		//获取产品信息
		Product product = productService.get(restWxUserOrderRequest.getProductId());
		if(product == null){
			logger.warn("Product information does not exist, productId: " + restWxUserOrderRequest.getProductId());
			return new ResponseEntity<>("Failed to get ProductInfo: return value is null", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		//获取订单信息
		OrderBill orderBill = getOrderBill(userToken, restWxUserOrderRequest, product);
		//微信统一下单接口
		return new ResponseEntity<OrderBill>(orderBill, HttpStatus.OK);
		
	}
	
	/**
	 * 统一下单
	 */
	@RequestMapping(value = "/unifiedorder", method = RequestMethod.POST)
	public ResponseEntity<?> unifiedorder(@RequestHeader("Authorization") String authorization,
			@RequestBody RestWxUserOrderRequest restWxUserOrderRequest,
			HttpServletRequest request) {
		
		restWxUserOrderRequest.checkParameter();
		UserToken userToken = userTokenHelper.checkTokenAndGetUser(authorization);
		
		//获取产品信息
		Product product = productService.get(restWxUserOrderRequest.getProductId());
		if(product == null){
			logger.warn("Product information does not exist, productId: " + restWxUserOrderRequest.getProductId());
			return new ResponseEntity<>("Failed to get ProductInfo: return value is null", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		//创建订单
		OrderBill orderBill = getOrderBill(userToken, restWxUserOrderRequest, product);
		orderBillService.create(orderBill);
		if(orderBill == null){
			return new ResponseEntity<>("create orderBill fail", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(orderBill.getPayMoney() < 0){
			return new ResponseEntity<>("create orderBill fail", HttpStatus.OK);
		}
		
		//获取真实ip地址
		String spbillCreateIP = WXPayUtil.getIpAddr(request);
		//微信统一下单接口
		return douUnifiedorder(product, orderBill, spbillCreateIP, orderBill.getId(), restWxUserOrderRequest.getOpenId(), restWxUserOrderRequest.getMpId());
		
	}
	
	/**
	 * 调用微信统一下单接口
	 * @param product	产品名字
	 * @param spbillCreateIP	真是ip
	 * @param outTradeNo	订单号
	 * @param openId	微信openId
	 * @param mpId	微信小程序appid
	 * @return
	 */
	public ResponseEntity<?> douUnifiedorder(Product product, OrderBill orderBill, String spbillCreateIP, String outTradeNo, String openId, String mpId){
		logger.info("wx pay unidied start");
		Map<String, String> packageParams = new HashMap<String, String>();
		Map<String, String> appids = wxMpOauth2Proxy.getWxMpIds();
		String appid = null;
		if(appids == null || appids.size() == 0){
			logger.error("wx pay unified order sign exception, appids is null or size 0");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}else{
			appid = appids.get(mpId);
			if(appid.isEmpty()){
				logger.error("wx pay unified order sign exception, " + mpId + "is appid null");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		logger.info("wx pay unidied appid {}", appid);
		packageParams.put("appid", appid);
		Map<String, String> mchids = wxMpOauth2Proxy.getWxMpMchIds();
		String mchid = null;
		if(mchids == null || mchids.size() == 0){
			logger.error("wx pay unified order sign exception, mchids is null or size 0");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}else{
			mchid = mchids.get(mpId);
			if(appid.isEmpty()){
				logger.error("wx pay unified order sign exception, " + mpId + "is mchid null");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		packageParams.put("mch_id", mchid);
		packageParams.put("nonce_str", WXPayUtil.generateNonceStr());
		packageParams.put("body", product.getName());
		packageParams.put("detail", product.getIntroduce());
		packageParams.put("out_trade_no", outTradeNo);
		packageParams.put("total_fee", (int)orderBill.getPayMoney() + "");// 支付金额，这边需要转成字符串类型，否则后面的签名会失败
		packageParams.put("spbill_create_ip", spbillCreateIP);
		packageParams.put("notify_url", WxPayConfig.NOTIFY_URL);// 支付成功后的回调地址
		packageParams.put("trade_type", WxPayConfig.TRADETYPE);// 支付方式
		packageParams.put("openid", openId);

		// MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
		String requestXml = "";
		try {
			requestXml = WXPayUtil.generateSignedXml(packageParams, WxPayConfig.KEY, SignType.MD5);
		} catch (Exception e) {
			logger.error("wx pay unified order sign exception,outTradeNo:" + outTradeNo);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 调用统一下单接口，并接受返回的结果
		String result = WXPayUtil.httpRequest(WxPayConfig.PAY_URL, "POST", requestXml);

		Map<String, String> map = new HashMap<String, String>();
		try {
			map = WXPayUtil.xmlToMap(result);
		} catch (Exception e) {
			logger.error("xml convert to map exception,outTradeNo:" + outTradeNo);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		String return_code = (String) map.get("return_code");
		String result_code = (String) map.get("result_code");

		Map<String, String> response = new HashMap<String, String>();// 返回给小程序端需要的参数
		if ("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)) {
			String nonceStr = WXPayUtil.generateNonceStr();
			String timeStamp = new Date().getTime() + "";

			String prepay_id = (String) map.get("prepay_id");// 返回的预付单信息

			response.put("appId", appid);
			response.put("nonceStr", nonceStr);
			response.put("package", "prepay_id=" + prepay_id);
			response.put("signType", "MD5");
			response.put("timeStamp", timeStamp);// 时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
			
			String paySign = null;
			try {
				paySign = WXPayUtil.generateSignature(response,
						WxPayConfig.KEY, SignType.MD5);
			} catch (Exception e) {
				logger.error("wx pay sign exception");
				e.printStackTrace();
			}
			response.remove("appId");
			response.put("paySign", paySign);
			response.put("surplusCost", orderBill.getSurplusCost() + "");
			response.put("payMoney", orderBill.getPayMoney() + "");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/** 
     * @Description:微信支付     
     * @return 
     * @throws Exception  
     */  
    @RequestMapping(value="/notify/pay")
    @ResponseBody  
    public String wxNotify(HttpServletRequest request,HttpServletResponse response) throws Exception{  
    	String resXml = "";
        InputStream inputStream = request.getInputStream();
	    String notityXml=	IOUtils.toString(inputStream,StandardCharsets.UTF_8);
	    Map<String, String> map = WXPayUtil.xmlToMap(notityXml);
          
        String returnCode = (String) map.get("return_code");
        if("SUCCESS".equals(returnCode)){
            //验证签名是否正确  
        	if(WXPayUtil.isSignatureValid(map, WxPayConfig.KEY)){
        		String out_trade_no = (String)map.get("out_trade_no");
        		if(StringUtils.isNotEmpty(out_trade_no)){
        			logger.info("out_trade_no:" + out_trade_no);
        			OrderBill orderBill = orderBillService.getOrder(out_trade_no);
        			logger.info("order type:" + orderBill.getType());
        			if(OrderBill.TYPE_NEWBUY == orderBill.getType()){
        				//创建会员信息
        				createUserVip(orderBill);
        			}else if(OrderBill.TYPE_RENEW == orderBill.getType()){
        				//更新会员记录
        				renewUserVip(orderBill);
        			}else if(OrderBill.TYPE_UPGRADE == orderBill.getType()){
        				upgradeUserVip(orderBill);
        			}
        			
        			//设置会员类型，会员存储
        			updatePersonAcount(orderBill);
    				
    				//更新订单信息
    				orderBill.setStatus(OrderBill.STATU_COMPLETE);
    				orderBill.setFinishedDate(new Date());
    				orderBillService.updateStatus(orderBill);
					createTransferMoney2User(orderBill);
        			
        			//通知微信服务器已经支付成功  
                    resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"  
                    + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
        		}else{
        			resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"  
        		            + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";  
        		}
        	} 
        }else{  
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"  
            + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";  
        }
  
        return resXml;
        
    }
    
    public void updatePersonAcount(OrderBill orderBill){
    	
    	if(orderBill.getProductId() <= 0){
    		throw new InvalidParamException();
    	}
    	Product product = productService.get(orderBill.getProductId());
    	if(product == null){
    		logger.error("product is null, productId" + orderBill.getProductId());
    		throw new InvalidParamException();
    	}
    	
    	if(orderBill.getEnterpriseId() == 0){
    		WxUser dbWxUser = wxUserService.getCloudUserId(orderBill.getCloudUserId());
    		if(dbWxUser == null){
    			logger.error("nosuch user, clouduserId" + orderBill.getCloudUserId());
    		}
    		dbWxUser.setQuota(product.getAccountSpace()+104857600);
    		dbWxUser.setType(getUserVip(orderBill.getEnterpriseId(), orderBill.getCloudUserId()));
    		wxUserService.update(dbWxUser);
    		wxUserService.updateWxUserAccount(dbWxUser);
    	}else{
    		logger.error("update account fail, orderId: " + orderBill.getId());
    	}
    }
    
    public byte getUserVip(long enterpriseId,long cloudUserId){
    	byte vip = WxUser.TYPE_TEMPORARY;
    	OrderBill orderBill =  new OrderBill();
    	orderBill.setEnterpriseId(enterpriseId);
    	orderBill.setCloudUserId(cloudUserId);
    	List<OrderBill> orderBillList = orderBillService.list(orderBill);
    	if(orderBillList != null && orderBillList.isEmpty()){
    		double payMoney = 0;
    		for(OrderBill order:orderBillList){
    			payMoney = payMoney + order.getPayMoney();
    		}
    		//黄金会员：48   铂金会员：88   砖石会员：168
    		if(payMoney > 168){
    			vip = WxUser.TYPE_TEMPORARY_VIP3;
    		}else if(payMoney > 88){
    			vip = WxUser.TYPE_TEMPORARY_VIP2;
    		}else if(payMoney > 48){
    			vip = WxUser.TYPE_TEMPORARY_VIP1;
    		}else{
    			vip = WxUser.TYPE_TEMPORARY;
    		}
    	}
    	return vip;
    }
    
    public OrderBill getOrderBill(UserToken userToken, RestWxUserOrderRequest restWxUserOrderRequest, Product product){
    	OrderBill orderBill = new OrderBill();
		// 商户订单号
		String outTradeNo = String.valueOf(new Date().getTime()) + (int) (Math.random() * 100);
		
		orderBill.setId(outTradeNo);
		orderBill.setEnterpriseId(userToken.getEnterpriseId());
		orderBill.setEnterpriseUserId(userToken.getId());
		orderBill.setAccountId(userToken.getAccountId());
		orderBill.setCloudUserId(userToken.getCloudUserId());
		orderBill.setSubmitDate(new Date());
		
		//表示为个人账号
		if(userToken.getAccountId() == 0){
			orderBill.setUserType(OrderBill.USERTYPE_PERSONAL);
			
			UserVip userVipParam =new UserVip();
			userVipParam.setEnterpriseId(userToken.getEnterpriseId());
			userVipParam.setEnterpriseUserId(userToken.getId());
			userVipParam.setEnterpriseAccountId(userToken.getAccountId());
			userVipParam.setCloudUserId(userToken.getCloudUserId());
			UserVip userVip = userVipService.get(userVipParam);
			if(userVip == null){
				orderBill.setType(OrderBill.TYPE_NEWBUY);
			}else{
				if(restWxUserOrderRequest.getProductId() == userVip.getProductId()){
					orderBill.setType(OrderBill.TYPE_RENEW);
				}else if(restWxUserOrderRequest.getProductId() > userVip.getProductId()){
					orderBill.setType(OrderBill.TYPE_UPGRADE);
				}else{
					logger.error("create order fail, old productId: " + userVip.getProductId() + ", new productId: " + restWxUserOrderRequest.getProductId());
					throw new InvalidParamException();
				}
			}
			orderBill.setProductId(restWxUserOrderRequest.getProductId());
			orderBill.setDuration(restWxUserOrderRequest.getDuration());
			
			ProductDurationPrice productDurationPrice = productDurationPriceService.getByProductIdAndDuration(restWxUserOrderRequest.getProductId(), restWxUserOrderRequest.getDuration());
			if(productDurationPrice == null){
				logger.error("get productDurationPrice fail, productId: " + restWxUserOrderRequest.getProductId() + ", duration: " + restWxUserOrderRequest.getDuration());
				throw new InvalidParamException();
			}
			orderBill.setTotalPrice(productDurationPrice.getPrice());
			//是否有优惠
			Rebate rebate = rebateService.getRebateByProductIdAndDuration(restWxUserOrderRequest.getProductId(), restWxUserOrderRequest.getDuration());
			if(rebate != null && rebate.getDiscountRatio() != 1){
				orderBill.setDiscountRatio(rebate.getDiscountRatio());
			}else{
				orderBill.setDiscountRatio(OrderBill.DISCOUNT_NONE);
			}
			
			orderBill.setDiscountPrice(orderBill.getTotalPrice() * orderBill.getDiscountRatio());
			
			
			if(OrderBill.TYPE_NEWBUY == orderBill.getType()){
				orderBill.setSurplusCost(OrderBill.COST_INIT);	//不是升级会员等级，初始剩余金额值为零
				orderBill.setPayMoney(orderBill.getDiscountPrice());
			}else if(OrderBill.TYPE_RENEW == orderBill.getType() || OrderBill.TYPE_UPGRADE == orderBill.getType()){
				orderBill.setSurplusCost(calculatingSurplusCost(userToken));
				if(orderBill.getDiscountPrice() - orderBill.getSurplusCost() < 0){
					//当之前剩余钱大于现在应付的费用，则将多余的钱
					logger.error("surplus money greater than pay money");
					throw new InvalidParamException();
				}else{
					orderBill.setPayMoney(orderBill.getDiscountPrice() - orderBill.getSurplusCost());
				}
			}else{
				logger.error("create order fail, order type is exception, type: " + orderBill.getType() + ", enterpriseId: " + userToken.getEnterpriseId());
				return null;
			}
			
			orderBill.setStatus(OrderBill.STATU_UNPAID);
		}
		
		return orderBill;
    }
    
    //计算剩余费用
    public double calculatingSurplusCost(UserToken userToken){
    	double surplusCost = 0;
    	UserVip userVipParam =new UserVip();
		userVipParam.setEnterpriseId(userToken.getEnterpriseId());
		userVipParam.setEnterpriseUserId(userToken.getId());
		userVipParam.setEnterpriseAccountId(userToken.getAccountId());
		userVipParam.setCloudUserId(userToken.getCloudUserId());
		UserVip userVip = userVipService.get(userVipParam);
		
		if(userVip == null){
			logger.warn("userVip info is not exist, cloudUserId: " + userToken.getCloudUserId());
			return surplusCost;
		}
		
		Product product = productService.get(userVip.getProductId());
		if(product == null){
			logger.error("product info is not exist, productId: " + userVip.getProductId());
			return surplusCost;
		}
		
		Date nowDate =  new Date();
        
		int days = differentDaysByMillisecond(nowDate, userVip.getExpireDate());
		//之前会员套餐已经过期，或者剩下时间不满一天能
		if(days <= 0){
			return surplusCost;
		}
		//如果有更新时间，则用更新时间为开始时间
		Calendar startCalendar = Calendar.getInstance();
		Date startDate = null;
		if(userVip.getUpdateDate() == null){
			startDate = userVip.getStartDate();
		}else{
			startDate = userVip.getUpdateDate();
		}
		startCalendar.setTime(startDate);
		
		Calendar expireCalendar = Calendar.getInstance();
        expireCalendar.setTime(userVip.getExpireDate());
        //会员持续时间
        int monthsByMonth = expireCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        int monthsByYear = (expireCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)) * 12;
        int months = monthsByMonth + monthsByYear;
        
        int totalDays = differentDaysByMillisecond(startDate, userVip.getExpireDate());
		int nowDays = differentDaysByMillisecond(new Date(), userVip.getExpireDate());
		//会员打折
		ProductDurationPrice productDurationPrice = productDurationPriceService.getByProductIdAndDuration(userVip.getProductId(), months);
		if(productDurationPrice == null){
        	logger.error("get productDurationPrice fail , productId :" + userVip.getProductId() + ", duration: " + months);
        	throw new InvalidParamException();
        }
        double totalPrice =  productDurationPrice.getPrice();
        logger.info("last product Price:" + productDurationPrice.getPrice());
        
		Rebate rebate = rebateService.getRebateByProductIdAndDuration(product.getId(), months);
		if(rebate == null){
			surplusCost = totalPrice*Math.round(((double)nowDays/totalDays) * 100) * 0.01;
		}else{
			//打折后的价格
			double discountPrice = totalPrice * rebate.getDiscountRatio();
			surplusCost = discountPrice*Math.round((nowDays/totalDays) * 100) * 0.01;
		}
    	return surplusCost;
    }
    
    public int differentDaysByMillisecond(Date startDate,Date endDate)
    {
    	if(endDate.getTime() < startDate.getTime()){
    		return -1;
    	}
        int days = (int) ((endDate.getTime() - startDate.getTime()) / (1000*3600*24));
        return days;
    }
    
    //获取用户会员信息
    public UserVip getUserVipByOrderInfo(OrderBill orderBill){
    	UserVip userVip = null;
    	if(orderBill == null || orderBill.getCloudUserId() == 0){
    		return userVip;
    	}
    	UserVip userVipParam = new UserVip();
    	userVipParam.setEnterpriseId(orderBill.getEnterpriseId());
    	userVipParam.setEnterpriseUserId(orderBill.getEnterpriseUserId());
    	userVipParam.setEnterpriseAccountId(orderBill.getAccountId());
    	userVipParam.setCloudUserId(orderBill.getCloudUserId());
    	
    	userVip = userVipService.get(userVipParam);
    	return userVip;
    }
    
    //创建用户会员信息
    public void createUserVip(OrderBill orderBill){
    	logger.info("create vip user, order id: " + orderBill.getId());
    	if(orderBill == null || orderBill.getCloudUserId() == 0){
    		return;
    	}
    	UserVip userVipParam = new UserVip();
    	userVipParam.setEnterpriseId(orderBill.getEnterpriseId());
    	userVipParam.setEnterpriseUserId(orderBill.getEnterpriseUserId());
    	userVipParam.setEnterpriseAccountId(orderBill.getAccountId());
    	userVipParam.setCloudUserId(orderBill.getCloudUserId());
    	userVipParam.setProductId(orderBill.getProductId());
    	
    	Date startDate = new Date();
    	userVipParam.setStartDate(startDate);
    	
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(startDate);
    	calendar.add(Calendar.MONTH, orderBill.getDuration());
    	
    	userVipParam.setExpireDate(calendar.getTime());
    	userVipService.create(userVipParam);
    }
    
    //延长会员时间
    public void renewUserVip(OrderBill orderBill){
    	//获取会员之前账号信息
    	UserVip userVip = getUserVipByOrderInfo(orderBill);
    	if(userVip == null){
    		logger.error("renewUserVip userVip info fail, orderBillId: " + orderBill.getId());
    		return;
    	}
    	logger.info("vip user expire date:" + userVip.getExpireDate());
    	logger.info("user buy vip duration:" + orderBill.getDuration());
    	
    	Date nowDate = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(nowDate);
    	calendar.add(Calendar.MONTH, orderBill.getDuration());
    	Date expireDate = calendar.getTime();
    	userVip.setUpdateDate(nowDate);
    	userVip.setExpireDate(expireDate);
    	userVipService.update(userVip);
    }
    
    //升级会员等级
    public void upgradeUserVip(OrderBill orderBill){
    	//获取会员之前账号信息
    	UserVip userVip = getUserVipByOrderInfo(orderBill);
    	if(userVip == null){
    		logger.error("upgrade userVip info fail, orderBillId: " + orderBill.getId());
    		return;
    	}
    	
    	Date nowDate = new Date();
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTime(nowDate);
    	calendar.add(Calendar.MONTH, orderBill.getDuration());
    	Date expireDate = calendar.getTime();
    	
    	userVip.setUpdateDate(new Date());
    	userVip.setExpireDate(expireDate);
    	userVipService.update(userVip);
    }



	private void createTransferMoney2User(OrderBill orderBill) {
		// TODO Auto-generated method stub
		if(orderBill.getUserType()==OrderBill.USERTYPE_PERSONAL){
			WxUser wxUser= wxUserService.getCloudUserId(orderBill.getCloudUserId());
		

			if( wxUser.getInviterId()!=null && !wxUser.getInviterId().equals("")){
				WxUser inviter  = wxUserService.getByUnionId(wxUser.getInviterId());
				ShareLevel shareleve = shareLevelService.get(inviter.getShareLevel());
				if(shareleve!=null){
					String id = String.valueOf(new Date().getTime()) + (int) (Math.random() * 100);
					UserProfitDetail userProfitDetail = new UserProfitDetail();
					userProfitDetail.setId(id);
					userProfitDetail.setOrderId(orderBill.getId());
					userProfitDetail.setCreateAt(new Date());
					userProfitDetail.setSource(UserProfitDetail.SOURCE_BUYSPACE);
					userProfitDetail.setAttempts(0);
					userProfitDetail.setCloudUserId(inviter.getCloudUserId());
					userProfitDetail.setOpenId(inviter.getOpenId());
					userProfitDetail.setUnionID(inviter.getUnionId());
					userProfitDetail.setUserName(wxUser.getNickName());
					userProfitDetail.setStatus(UserProfitDetail.STATUS_UNPAID);
					userProfitDetail.setUser_type(UserProfitDetail.USERTYPE_PERSONAL);
					userProfitDetail.setPayMoney(BigDecimal.valueOf(Math.round(orderBill.getPayMoney()*shareleve.getProportions())));
					if(userProfitDetail.getPayMoney().intValue()>10000){
						userProfitDetail.setType(UserProfitDetail.TYPE_MANUAL);
					}else{
						userProfitDetail.setType(UserProfitDetail.TYPE_AUTO);
					}
				
					userProfitDetail.setProportions(shareleve.getProportions());
					if(userProfitDetail.getPayMoney().intValue()!=0){
						transferMoney2UserDetailService.create(userProfitDetail);
						inviter.setCountTotalProfits(inviter.getCountTotalProfits()+userProfitDetail.getPayMoney().longValue());
						inviter.setCountTodayProfits(inviter.getCountTodayProfits()+userProfitDetail.getPayMoney().longValue());
						wxUserService.updateCountTotalProfits(inviter);
						wxUserService.updateCountTodayProfits(inviter);
					}
				
				}
		
			}
		}

	}

}
