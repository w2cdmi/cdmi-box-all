package com.huawei.sharedrive.uam.rest.test.account;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.uam.openapi.domain.RestTokenCreateResponse;
import com.huawei.sharedrive.uam.rest.test.util.Constants;
import com.huawei.sharedrive.uam.rest.test.util.MyResponseUtils;
import com.huawei.sharedrive.uam.rest.test.util.MyTestUtils;

import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.utils.DateUtils;

public class AccountAPIGetAttrTest
{
  
    private static final String URL = "/api/v2/accounts/attributes";
    
    private static final String ACCESS_ACCOUNT_KEY = "0d0e642e9387f539748f2e4c8ebca3a2";
    private static final String SECRET_ACCOUNT_KEY = "20ab1c3233157fcb5d14ee07340b666f";
    
    private static final String NAME_PREFIX = "?name=";
    
    private static final String NAME_SERVER_RECEIVE = NAME_PREFIX + "serverReceive";
    private static final String NAME_PROTOCOL_RECEIVE = NAME_PREFIX + "protocolReceive";
    private static final String NAME_PORT_RECEIVE = NAME_PREFIX + "portReceive";
    
    private static final String NAME_SERVER_SEND = NAME_PREFIX + "serverSend";
    private static final String NAME_PROTOCOL_SEND = NAME_PREFIX + "protocolSend";
    private static final String NAME_PORT_SEND = NAME_PREFIX + "portSend";
    
    
    
    private boolean showResult = true;
    
    @Test
    public void testAccountTokenAuth() throws Exception
    {
        HttpURLConnection openurl = null;
        try
        {
            RestTokenCreateResponse restTokenCreateResponse = MyTestUtils.login();
            String urlString = Constants.SERVER_RUL + URL + NAME_PORT_SEND;
            URL url = null;
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestProperty("Authorization", restTokenCreateResponse.getToken());
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    @Test
    public void getAccountByApp()
    {
        HttpURLConnection openurl = null;
        try
        {
            
            String urlString = Constants.SERVER_RUL + URL;
            URL url = null;
            url = new URL(urlString);
            System.out.println("GET " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            getAppAtuhHeader(openurl, ACCESS_ACCOUNT_KEY, SECRET_ACCOUNT_KEY);
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.connect();
            MyResponseUtils.assert200(openurl, showResult);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            openurl.disconnect();
        }
    }
    
    private void getAppAtuhHeader(HttpURLConnection openurl, String accessKeyId, String appSecretKey)
    {
        Date now = new Date();
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, now, null);
        String appSignatureKey = SignatureUtils.getSignature(appSecretKey, dateStr);
        String authorization = "app," + accessKeyId + ',' + appSignatureKey;
        openurl.setRequestProperty("Authorization", authorization);
        openurl.setRequestProperty("Date", dateStr);
        System.out.println("Authorization: " + authorization);
        System.out.println("Date: " + dateStr);
    }
    
}
