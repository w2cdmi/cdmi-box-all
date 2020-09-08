package com.huawei.sharedrive.app.test.openapi.favorite;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class FavoriteListTest extends FavoriteBaseTest
{
    @Test
    public void testNormal()
    {
        
        String[] orderFile = {"name","modifiedAt"};
        String body = "{\"limit\": 10,\"offset\": 0,\"keyword\": \"\",\"order\": [ {\"field\": \"modifiedAt\",\"direction\": \"desc\"}]}";
        
        System.out.println(body);
        
        url = buildUrl(userId1,0 );
        try
        {
//            HttpURLConnection connection = getConnection(url,
//                METHOD_POST,
//                MyTestUtils.getTestUserToken1(),
//                body);
          HttpURLConnection connection = getConnection(url,
          METHOD_POST,
         "adsfadfafd",
          body);
            
            MyResponseUtils.assert200(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testUnauthorized()
    {
        
        String[] orderFile = {"name","modifiedAt"};
        String body = "{\"limit\": 10,\"offset\": 0,\"keyword\": \"\",\"order\": [ {\"field\": \"name\",\"direction\": \"desc\"}]}";
        
        System.out.println(body);
        
        url = buildUrl(userId1,22 );
        try
        {
            HttpURLConnection connection = getConnection(url,
                METHOD_POST,
                MyTestUtils.getTestUserToken1()+"ab ",
                body);
            
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
        
        String[] orderFile = {"name","modifiedAt"};
        String body = "{\"limit\": 10,\"offset\": 0,\"keyword\": \"\",\"order\": [ {\"field\": \"name\",\"direction\": \"desc\"}]}";
        
        System.out.println(body);
        
        url = buildUrl(userId2,22 );
        try
        {
            HttpURLConnection connection = getConnection(url,
                METHOD_POST,
                MyTestUtils.getTestUserToken1(),
                body);
            
            MyResponseUtils.assert200(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testInvalidParameter()
    {
        
        String[] orderFile = {"name","modifiedAt"};
        String body = "{\"limit\": -10,\"offset\": 0,\"keyword\": 1,\"order\": [ {\"field\": \"name\",\"direction\": \"desc\"},{\"field\": \"modifiedAt\",\"direction\": \"desc\"}]}";
        
        System.out.println(body);
        
        url = buildUrl(userId1,324 );
        try
        {
            HttpURLConnection connection = getConnection(url,
                METHOD_POST,
                MyTestUtils.getTestUserToken1(),
                body);
            
            MyResponseUtils.assert200(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private String buildUrl(long userId, long id)
    {
        return MyTestUtils.SERVER_URL_UFM_V2+"favorites/" + id + "/items";
    }
}
