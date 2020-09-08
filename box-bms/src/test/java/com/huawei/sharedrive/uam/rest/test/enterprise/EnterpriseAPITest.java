package com.huawei.sharedrive.uam.rest.test.enterprise;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.uam.rest.test.util.Constants;
import com.huawei.sharedrive.uam.rest.test.util.EnterpriseRestTestUtils;
import com.huawei.sharedrive.uam.rest.test.util.MyResponseUtils;

import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.utils.DateUtils;

public class EnterpriseAPITest
{

    private static final String ACCESS_ENTERPRISE_KEY = "4d6378ffe0fe4ab59e608452c6531343";
    private static final String SECRET_ENTERPRISE_KEY = "67335f0548ec4a3eb33d0dae7229721d";
    
    private boolean showResult = true;
    
    private static final String URL = "/api/v2/enterprises";
    
    private static final String TEST_CREATE_ENTERPRISE = "testData/testCreateEnterprise.txt";
    
    private static final String TEST_GET_ENTERPRISE = "testData/testGetEnterprise.txt";
    
    @Test
    public void createEnterprise()
    {
        HttpURLConnection openurl = null;
        try
        {
            
            String urlString = Constants.SERVER_RUL + URL;
            URL url = null;
            url = new URL(urlString);
            System.out.println("POST " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            getEnterpriseAtuhHeader(openurl, ACCESS_ENTERPRISE_KEY, SECRET_ENTERPRISE_KEY);
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = EnterpriseRestTestUtils.getDataFromFile(TEST_CREATE_ENTERPRISE);
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyResponseUtils.assert201(openurl, showResult);
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
    public void testGetEnterprise()
    {
        HttpURLConnection openurl = null;
        try
        {
            
            String urlString = Constants.SERVER_RUL + URL + "/items";
            URL url = null;
            url = new URL(urlString);
            System.out.println("POST " + urlString);
            openurl = (HttpURLConnection) url.openConnection();
            getEnterpriseAtuhHeader(openurl, ACCESS_ENTERPRISE_KEY, SECRET_ENTERPRISE_KEY);
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = EnterpriseRestTestUtils.getDataFromFile(TEST_GET_ENTERPRISE);
            openurl.getOutputStream().write(bodyStr.getBytes());
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
