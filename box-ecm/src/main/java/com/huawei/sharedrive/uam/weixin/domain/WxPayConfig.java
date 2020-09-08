package com.huawei.sharedrive.uam.weixin.domain;

import com.huawei.sharedrive.uam.util.PropertiesUtils;

public class WxPayConfig {
	//小程序appid
    public static final String APPID = PropertiesUtils.getProperty("wx.pay.appId");
    //微信支付的商户id
    public static final String MCH_ID = PropertiesUtils.getProperty("wx.pay.mchId");
    //微信支付的商户密钥
    public static final String KEY = PropertiesUtils.getProperty("wx.pay.key");
    //支付成功后的服务器回调url
    public static final String NOTIFY_URL = PropertiesUtils.getProperty("wx.pay.notifyURL");
    //签名方式，固定值
    public static final String SIGNTYPE = "MD5";
    //交易类型，小程序支付的固定值为JSAPI
    public static final String TRADETYPE = "JSAPI";
    //微信统一下单接口地址
    public static final String PAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    
    //企业付款到零钱url
    public static final String TRANSFER_URL = "/mmpaymkttransfers/promotion/transfers";
    
    
    
}
