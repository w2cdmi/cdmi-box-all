package com.huawei.sharedrive.uam.openapi.rest;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.wxpay.sdk.WXWebPayConfigImpl;
import com.github.wxpay.sdk.WXPayUtil;
import com.huawei.sharedrive.uam.accountuser.manager.UserAccountManager;
import com.huawei.sharedrive.uam.enterprise.service.AccountBasicConfigService;
import com.huawei.sharedrive.uam.enterprise.service.EnterpriseAccountService;
import com.huawei.sharedrive.uam.enterpriseuser.service.EnterpriseUserService;
import com.huawei.sharedrive.uam.product.domain.OrderBill;
import com.huawei.sharedrive.uam.product.service.OrderBillService;
import com.huawei.sharedrive.uam.product.service.ProductService;
import com.huawei.sharedrive.uam.product.service.impl.OrderProcessTask;
import com.huawei.sharedrive.uam.uservip.service.EnterpriseVipService;
import com.huawei.sharedrive.uam.weixin.domain.ShareLevel;
import com.huawei.sharedrive.uam.weixin.domain.UserProfitDetail;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.service.ShareLevelService;
import com.huawei.sharedrive.uam.weixin.service.UserProfitDetailService;
import com.huawei.sharedrive.uam.weixin.service.WxUserService;

import pw.cdmi.common.deamon.DeamonService;

@Controller
@RequestMapping(value = "/api/v2/notify")
public class WxPayNotifyApiController {
	
	@Autowired
	private DeamonService executeService;
	
	@Autowired
	private EnterpriseUserService enterpriseUserService;

	@Autowired
	private EnterpriseAccountService enterpriseAccountService;

	@Autowired
	private AccountBasicConfigService basicConfigService;

	@Autowired
	private UserAccountManager userAccountManager;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderBillService orderBillService;
	
	@Autowired
	private EnterpriseVipService  enterpriseVipService;

	private static final Logger logger = LoggerFactory.getLogger(WxPayNotifyApiController.class);

	@RequestMapping("/pay")
	@ResponseBody
	public String weixinNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {

		InputStream inputStream = request.getInputStream();
	    String xmlStr=	IOUtils.toString(inputStream,StandardCharsets.UTF_8);
		String key = WXWebPayConfigImpl.getInstance().getKey();
		
		// 判断签名是否正确
		if (WXPayUtil.isSignatureValid(xmlStr.toString(), key)) {
			String resFailXml =   "<xml>" 
			                            + "<return_code><![CDATA[FAIL]]></return_code>"
						                + "<return_msg><![CDATA[报文为空]]></return_msg>"
		                        + "</xml> ";
		
			String resSuccessXml = "<xml>"
			                           + "<return_code><![CDATA[SUCCESS]]></return_code>"
					                   + "<return_msg><![CDATA[OK]]></return_msg>" 
					             + "</xml> ";
		
			try {
				
				Map<String, String> packageParams = WXPayUtil.xmlToMap(xmlStr.toString());
				String out_trade_no = (String) packageParams.get("out_trade_no");
				OrderBill orderBill = orderBillService.getOrder(out_trade_no);
				
				if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
					// 处理订单
					executeService.execute(new OrderProcessTask(orderBill, enterpriseUserService, enterpriseAccountService,
							basicConfigService, userAccountManager, orderBillService, productService,enterpriseVipService));
                    //订单处理完成，更新状态
					orderBill.setType(OrderBill.STATU_PROCESS);
					orderBillService.updateStatus(orderBill);

					return resSuccessXml.toString();
				} else {
					logger.error("支付失败,错误信息：" + packageParams.get("err_code"));
					logger.error(xmlStr.toString());
					
					orderBill.setStatus(OrderBill.STATU_FAILD);
					orderBillService.updateStatus(orderBill);
					return resFailXml.toString();
					
				}
			
			} catch (Exception e) {
				logger.error("订单失败");
				logger.error(xmlStr.toString());
				return resFailXml.toString();
			}

		} else {
			logger.error("通知签名验证失败");
			logger.error(xmlStr.toString());
			return null;
		}

	}



}
