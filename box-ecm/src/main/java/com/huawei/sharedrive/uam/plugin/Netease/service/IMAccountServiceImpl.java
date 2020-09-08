package com.huawei.sharedrive.uam.plugin.Netease.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pw.cdmi.core.utils.JsonUtils;

import com.huawei.sharedrive.uam.plugin.Netease.CheckSumBuilder;
import com.huawei.sharedrive.uam.plugin.Netease.dao.IMAccountDao;
import com.huawei.sharedrive.uam.plugin.Netease.domain.IMAccount;
import com.huawei.sharedrive.uam.util.PropertiesUtils;
@Service
public class IMAccountServiceImpl implements IMAccountService{

	@Autowired
	private IMAccountDao imAccountDaoImpl;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IMAccountServiceImpl.class);
	 
	 public  void createIMAccount(Map<String, Object> prameter) {
		 
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    HttpPost httpPost = new HttpPost(PropertiesUtils.getProperty("ImServurl")+"/user/create.action");

		    fillHeader(httpPost);
	        
	        // 设置请求的参数
	        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	        nvps.add(new BasicNameValuePair("accid",prameter.get("accid").toString()));
	        nvps.add(new BasicNameValuePair("name", prameter.get("name").toString()));
	        nvps.add(new BasicNameValuePair("icon", prameter.get("icon").toString()));
	        nvps.add(new BasicNameValuePair("token", prameter.get("token").toString()));
	        
	        try {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
				HttpResponse response = httpClient.execute(httpPost);
			    if(response.getStatusLine().getStatusCode()==200){
			    	IMAccount imAccount=new IMAccount();
			    	imAccount.setAccountId(Long.parseLong(prameter.get("accountId").toString()));
			    	imAccount.setCloudUserId(Long.parseLong(prameter.get("cloudUserId").toString()));
			    	imAccount.setIMaccid(prameter.get("accid").toString());
			     	imAccount.setIMPwd(prameter.get("token").toString());
			     	imAccount.setName(prameter.get("name").toString());
			     	imAccount.setIcon(prameter.get("icon").toString());
			     	imAccount.setCreatedAt(new Date());
			     	imAccount.setEnterpriseId((Long.parseLong(prameter.get("enterpriseId").toString())));
					imAccount.setUserId((Long.parseLong(prameter.get("userId").toString())));


				    imAccountDaoImpl.insert(imAccount);
			    }
				
			} catch (Exception e) {
				LOGGER.error("IM账号开户失败！,error info: " + e.toString());
			}

	     
	}
	
	 public  void getUinfos( List<String> accidList){
		 
		    HttpPost httpPost = new HttpPost(PropertiesUtils.getProperty("ImServurl")+"/user/getUinfos.action");
		    DefaultHttpClient httpClient = new DefaultHttpClient();
		    fillHeader(httpPost);
	        // 设置请求的参数
	        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	        nvps.add(new BasicNameValuePair("accids",JsonUtils.toJson(accidList)));
	     
	        try {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
				HttpResponse response = httpClient.execute(httpPost);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        
	 }
	 
	 private  String createRandom(int leng){
		 StringBuilder str=new StringBuilder();//定义变长字符串
		 Random random=new Random();
		 //随机生成数字，并添加到字符串
		 for(int i=0;i<leng;i++){
		     str.append(random.nextInt(10));
		 }
		 //将字符串转换为数字并输出
		 return str.toString();
	 }
	 
	 //设置消息头
	 private  void fillHeader(HttpPost httpPost){
		    String curTime = String.valueOf((new Date()).getTime() / 1000L);
		    String randomString=createRandom(8);
		    String checkSum = CheckSumBuilder.getCheckSum(PropertiesUtils.getProperty("ImSecret"), randomString ,curTime);
		    //header设置
	        httpPost.addHeader("AppKey", PropertiesUtils.getProperty("ImKey"));
	        httpPost.addHeader("Nonce", randomString);
	        httpPost.addHeader("CurTime", curTime);
	        httpPost.addHeader("CheckSum", checkSum);
	        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
	 }

	@Override
	public IMAccount get(long accountId, Long cloudUserId) {
		// TODO Auto-generated method stub
		IMAccount imAccount=new IMAccount();
		imAccount.setAccountId(accountId);
		imAccount.setCloudUserId(cloudUserId);
		return imAccountDaoImpl.get(imAccount);
	}

	@Override
	public IMAccount getByIMaccid(String IMaccid) {
		IMAccount imAccount = new IMAccount();
		imAccount.setIMaccid(IMaccid);
		return imAccountDaoImpl.getByIMaccid(imAccount);
	}


}
