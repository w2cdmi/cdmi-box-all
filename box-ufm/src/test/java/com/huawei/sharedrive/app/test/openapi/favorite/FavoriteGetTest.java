package com.huawei.sharedrive.app.test.openapi.favorite;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class FavoriteGetTest extends FavoriteBaseTest
{
    @Test
    public void testNormal()
    {
        long id = 23;
        url = buildUrl(userId1,id); 
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_GET,MyTestUtils.getTestUserToken1(),null);
         
             MyResponseUtils.assert200(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testForbidden()
    {
        long id = 134;
        url = buildUrl(userId2,id); 
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_GET,MyTestUtils.getTestUserToken2(),null);
         
             MyResponseUtils.assert200(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testNoSuchItem()
    {
        long id = 177;
        url = buildUrl(userId1,id); 
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_GET,MyTestUtils.getTestUserToken1(),null);
         
             MyResponseUtils.assert200(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private String buildUrl(long userId,long id)
    {
        return MyTestUtils.SERVER_URL_UFM_V2+"favorites/"+id ;
    }
}
