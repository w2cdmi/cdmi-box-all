package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 创建外链测试类
 * 
 * @author l90005448
 * 
 */
public class LinkAPIDeleteTestV2
{
    
    
    public static void main(String[] args)
    {
        LinkAPIDeleteTestV2 testUnit = new LinkAPIDeleteTestV2();
        testUnit.testDeleteLink();
    }
    
    @Test
    public void testDeleteLink()
    {
//        RestUserloginRsp user = null;
//        try
//        {
//            user = ClouddriveRestTestUtils.getUser1();
//        }
//        catch (Exception e1)
//        {
//            e1.printStackTrace();
//        }
//        
//        String urlString = ClouddriveRestTestUtils.SERVER_URL + "link/" + user.getUserId() + "/" + nodeId;
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + 3 + "/" + 5;
        
        HttpURLConnection openurl = null;
        try
        {
            URL url = new URL(urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("DELETE");
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
            if(openurl != null)
            {
                openurl.disconnect();
            }
        }
    }
    
}
