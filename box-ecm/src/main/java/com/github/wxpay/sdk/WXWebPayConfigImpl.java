package com.github.wxpay.sdk;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.huawei.sharedrive.uam.util.PropertiesUtils;

public class WXWebPayConfigImpl extends WXPayConfig{

    private byte[] certData;
    private static WXWebPayConfigImpl INSTANCE;


    public static WXWebPayConfigImpl getInstance() throws Exception{
        if (INSTANCE == null) {
            synchronized (WXWebPayConfigImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WXWebPayConfigImpl();
                }
            }
        }
        return INSTANCE;
    }
    
    public WXWebPayConfigImpl() throws Exception {
        String certPath = "/opt/deploy/apiclient_cert.p12";
        File file = new File(certPath);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    public String getAppID() {
        return PropertiesUtils.getProperty("wx.webpay.appid");
    }

    public String getMchID() {
    	
        return PropertiesUtils.getProperty("wx.webpay.mchid");
    }

    public String getKey() {
        return PropertiesUtils.getProperty("wx.webpay.key");
    }
    

    public InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }


    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    IWXPayDomain getWXPayDomain() {
        return WXPayDomainSimpleImpl.instance();
    }

    public String getPrimaryDomain() {
        return "api.mch.weixin.qq.com";
    }

    public String getAlternateDomain() {
        return "api2.mch.weixin.qq.com";
    }

    @Override
    public int getReportWorkerNum() {
        return 1;
    }

    @Override
    public int getReportBatchSize() {
        return 2;
    }
}
