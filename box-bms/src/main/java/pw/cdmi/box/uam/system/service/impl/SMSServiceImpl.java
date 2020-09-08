package pw.cdmi.box.uam.system.service.impl;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import pw.cdmi.box.uam.system.domain.SMSInfo;
import pw.cdmi.box.uam.system.service.SMSService;
import pw.cdmi.box.uam.util.Constants;
import pw.cdmi.box.uam.util.PropertiesUtils;

@Component
public class SMSServiceImpl implements SMSService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SMSServiceImpl.class);

	private BlockingQueue<SMSInfo> smsQueue = new LinkedBlockingQueue<SMSInfo>(1000);

	private ExecutorService taskPool;

	private static String smsSErviceAddr = PropertiesUtils.getProperty("smsServiceAddr");

	@PostConstruct
	public void init() {
		int nThreads = Constants.SMS_QUEUE_TASK_NUM;
		taskPool = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(nThreads));
		SendSMSTask sendSMSTask = null;
		for (int i = 0; i < nThreads; i++) {
			sendSMSTask = new SendSMSTask();
			taskPool.execute(sendSMSTask);
		}
	}

	@PreDestroy
	public void close() {
		taskPool.shutdown();
	}

	private class SendSMSTask implements Runnable {
		@Override
		public void run() {
			while (true) {
				try {
					send();
				} catch (InterruptedException e) {
					LOGGER.error("Fail in send sms!", e);
					break;
				} catch (Throwable e) {
					LOGGER.error("Fail in send sms!", e);
				}
			}
		}

		private void send() throws InterruptedException, HttpException, IOException {
			SMSInfo take = smsQueue.take();
			HttpClient httpclient = new HttpClient();// 方法调用
			// http://v1.avatardata.cn/Sms/Send?key=[您申请的APPKEY]&mobile=XXXXXX&templateId=XXXXXX&param=XXXXX
			PostMethod post = new PostMethod(smsSErviceAddr);// 接口地址
			post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");// 格式转换
			post.addParameter("key", "14155c4eb8244421b52d66b7566fb681");// appkeyID
			post.addParameter("mobile", take.getConcatPhone());// 发送手机号
			post.addParameter("templateId", take.getTemplateId());// 短信模板id
			post.addParameter("param", take.getName());// 用户名
			if (take.getPassword() != null) {
				post.addParameter("param", take.getPassword());// 密码
			}
			if (take.getPrice() != null) {
				post.addParameter("param", take.getPrice());// 价格套餐
			}
			if (take.getIdentifyCode() != null) {
				post.addParameter("param", take.getIdentifyCode());// 验证码
			}
			httpclient.executeMethod(post);
		}
	}

	@Override
	public void sendSMS(String templateId, String concatPhone, String password, String price,String identifyCode) {
		SMSInfo smsInfo = new SMSInfo();
		smsInfo.setConcatPhone(concatPhone);
		smsInfo.setName(concatPhone);
		smsInfo.setPassword(password);
		smsInfo.setTemplateId(templateId);
		smsInfo.setPrice(price);
		smsInfo.setIdentifyCode(identifyCode);
		try {
			smsQueue.put(smsInfo);
		} catch (InterruptedException e) {
			LOGGER.error("Fail in send mail!", e);
		}
	}

}
