package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
/**
 * 获取外链信息
 * @author l90005448
 *
 */

public class LinkAPIGetTestV2
{
    
    public static void main(String[] args)
    {
        LinkAPIGetTestV2 testUnit = new LinkAPIGetTestV2();
        testUnit.testGetLink();
    }
    
    /**
     * 获取外链
     */
    @Test
    public void testGetLink()
    {
        String urlString =  MyTestUtils.SERVER_UFM_URL_V1 + "links/node";
//        String urlString =  ClouddriveRestTestUtils.SERVER_URL_V2 + "links/" + 4 + "/" + 2;
        
        HttpURLConnection openurl = null;
        try
        {
            URL url = new URL(urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
//            openurl.setRequestProperty("Authorization", ClouddriveRestTestUtils.getUserToken1());
            openurl.setRequestProperty("Authorization", "TEST");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            
            MyTestUtils.output(openurl);
            openurl.disconnect();
            
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
    
}
