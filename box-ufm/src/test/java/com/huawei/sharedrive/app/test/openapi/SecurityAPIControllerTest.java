package com.huawei.sharedrive.app.test.openapi;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class SecurityAPIControllerTest
{
    private static final String SERVER_RUL_APP = "http://10.169.34.108:8080/cloudapp";
    @Test
    public void getConfigInfoV1()
    {
        System.out.println("-------------------- getTeamSpaceInfo---------------------");
        String urlString = SERVER_RUL_APP + "/api/v1/system/config";
        //urlString += "?offset=";
        
        try
        {
            HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "GET");
            
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            MyTestUtils.outputWithCheck(openurl, null, null);
            
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void getConfigInfoV2()
    {
        System.out.println("-------------------- getTeamSpaceInfo---------------------");
        String urlString = SERVER_RUL_APP + "/api/v2/config?option=linkAccessKeyRule";
        //urlString += "?offset=";
        
        try
        {
            HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "GET");
            
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            MyTestUtils.outputWithCheck(openurl, null, null);
            
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
