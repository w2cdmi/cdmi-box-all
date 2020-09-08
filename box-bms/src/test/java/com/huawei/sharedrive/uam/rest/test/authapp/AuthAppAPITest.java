package com.huawei.sharedrive.uam.rest.test.authapp;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.uam.rest.test.util.Constants;
import com.huawei.sharedrive.uam.rest.test.util.MyResponseUtils;

import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.utils.DateUtils;

public class AuthAppAPITest
{
    private static final String ACCESS_ENTERPRISE_KEY = "25c497dd871040e8b390904d16b1d78d";
    private static final String SECRET_ENTERPRISE_KEY = "bec2f189196447109b3002409b8da6b6";
    
    private boolean showResult = true;
    
    @Test
    public void TestGetAppList()
    {
        HttpURLConnection openurl = null;
        try
        {
            
            String urlString = Constants.SERVER_RUL + "/api/v2/applications ";
            URL url = null;
            url = new URL(urlString);
            System.out.println("GET " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            getEnterpriseAtuhHeader(openurl, ACCESS_ENTERPRISE_KEY, SECRET_ENTERPRISE_KEY);
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
    
    private void getEnterpriseAtuhHeader(HttpURLConnection openurl, String accessKeyId, String appSecretKey)
    {
        Date now = new Date();
        String dateStr = DateUtils.dataToString(DateUtils.RFC822_DATE_FORMAT, now, null);
        String appSignatureKey = SignatureUtils.getSignature(appSecretKey, dateStr);
        String authorization = "enterprisemanage," + accessKeyId + ',' + appSignatureKey;
        openurl.setRequestProperty("Authorization", authorization);
        openurl.setRequestProperty("Date", dateStr);
        System.out.println("Authorization: " + authorization);
        System.out.println("Date: " + dateStr);
    }
}
