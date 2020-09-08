package com.huawei.sharedrive.uam.weixin.service.task;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfigImpl;
import com.github.wxpay.sdk.WXPayConstants.SignType;
import com.github.wxpay.sdk.WXPayUtil;
import com.github.wxpay.sdk.WXWebPayConfigImpl;
import com.huawei.sharedrive.uam.weixin.domain.UserProfitDetail;
import com.huawei.sharedrive.uam.weixin.domain.WxPayConfig;
import com.huawei.sharedrive.uam.weixin.domain.WxUser;
import com.huawei.sharedrive.uam.weixin.service.UserProfitDetailService;
import com.huawei.sharedrive.uam.weixin.service.WxUserService;

import pw.cdmi.common.job.JobExecuteContext;
import pw.cdmi.common.job.JobExecuteRecord;
import pw.cdmi.common.job.quartz.QuartzJobTask;


@Service("userProfitDetailTask")
public class UserProfitDetailTask extends QuartzJobTask {

	@Autowired
	private WxUserService wxUserService;

	@Autowired
	private UserProfitDetailService userProfitDetailService;

	private static Logger logger = LoggerFactory.getLogger(UserProfitDetailTask.class);

	@Override
	public void doTask(JobExecuteContext arg0, JobExecuteRecord record) {
		List<UserProfitDetail> list = userProfitDetailService.listByTypeAndStatus(UserProfitDetail.TYPE_AUTO, UserProfitDetail.STATUS_UNPAID);

		for (UserProfitDetail userProfitDetail : list) {
			try {
				enterprisePay(userProfitDetail);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("userProfitDetailTask fail id {}", userProfitDetail.getId());
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}


	private void enterprisePay(UserProfitDetail userProfitDetail) throws Exception {

		WxUser wxuser = wxUserService.getByUnionId(userProfitDetail.getUnionID());
		String outTradeNo = String.valueOf(new Date().getTime()) + (int) (Math.random() * 100);
		WXWebPayConfigImpl wXPayConfig = WXWebPayConfigImpl.getInstance();
		WXPay wxpay = new WXPay(wXPayConfig);
		Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("mch_appid", wXPayConfig.getAppID());
		packageParams.put("mchid", wXPayConfig.getMchID());
		packageParams.put("nonce_str", WXPayUtil.generateNonceStr());
		packageParams.put("partner_trade_no", outTradeNo);
		packageParams.put("openid", userProfitDetail.getOpenId());
		packageParams.put("check_name", "NO_CHECK");
		packageParams.put("re_user_name",wxuser.getNickName());
		packageParams.put("amount", userProfitDetail.getPayMoney().longValue() + "");
		packageParams.put("desc", "营销大使收益");
		packageParams.put("spbill_create_ip", InetAddress.getLocalHost().getHostAddress());
		packageParams.put("sign", WXPayUtil.generateSignature(packageParams, wXPayConfig.getKey(), SignType.MD5));
		Map<String, String> resultParams = wxpay.unifiedWithCert(packageParams, WxPayConfig.TRANSFER_URL);
		String return_code = (String) resultParams.get("return_code");
		String result_code = (String) resultParams.get("result_code");
	
		if ("SUCCESS".equals(return_code) && "SUCCESS".equals(result_code)) {
			userProfitDetail.setStatus(UserProfitDetail.STATUS_PAYMENT);
			userProfitDetail.setFinishAt(new Date());
			userProfitDetailService.updateStatus(userProfitDetail);
		}else{
			userProfitDetail.setStatus(UserProfitDetail.STATUS_PAYFAIL);
			userProfitDetail.setFailReason(resultParams.get("err_code"));
			userProfitDetail.setFinishAt(new Date());
			userProfitDetailService.updateStatus(userProfitDetail);
		}
	}

}
