package com.huawei.sharedrive.app.test.openapi.favorite;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.favorite.domain.FavoriteNode;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.node.favor.FavoriteNodeCreateRequest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import junit.framework.Assert;
import pw.cdmi.core.utils.JsonUtils;

public class FavoriteCreateTest extends FavoriteBaseTest
{
    @Test
    public void testNormal()
    {
//        String body = "{\"type\":\""+FavoriteNode.MYSPACE+"\",\"parent\":0,\"name\":\"test\",\"node\":{\"ownedBy\":1,\"id\":782},\"linkcode\":\"\"}";
//        System.out.println(body);
        
        FavoriteNodeCreateRequest createRequest =  new FavoriteNodeCreateRequest();
        createRequest.setName("abc");
        createRequest.setType(FavoriteNode.LINK);
        createRequest.setParent((long)0);
        createRequest.setNode(new NodeCreateRequest((long)11,(long)0.5));
        createRequest.setLinkCode("6k3zxmgx");
        
       // createRequest.setNode(new NodeCreateRequest((long)1,(long)783));
        String body = JsonUtils.toJson(createRequest);
      //  body="{\"type\":\"myspace\",\"parent\":0,\"name\":\"abc\",\"node\":{\"id\":783,\"ownedBy\":1}}";
        System.out.println(body);
        try
        {
//            System.out.println(MyTestUtils.getTestUserToken1());
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        url = buildUrl(); 
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST,MyTestUtils.getTestUserToken1(),body);
         
             MyResponseUtils.assert201(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testCreateListChridren()
    {
        Long parentId = (long)0;
        for(int i =0 ;i<100; i++)
        {
            String body = "{\"type\":0,\"linkcode\":\"\",\"parentId\":"+parentId+"}";
           // String body = "{\"type\":0,\"ownerId\":,\"name\":\"test"+i+"\",\"nodeId\":,\"linkcode\":\"\",\"parentId\":"+parentId+"}";
            System.out.println(body);
            
            url = buildUrl(); 
            try
            {
                HttpURLConnection connection = getConnection(url, METHOD_POST,MyTestUtils.getTestUserToken1(), body);
                parentId= assertHttpStatus(connection,true,201);
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    @Test
    public void testInvalidParameter()
    {

        String body = "{\"type\":5,\"ownerId\":1,\"name\":\"test\",\"nodeId\":782,\"linkcode\":\"\",\"parentId\":0}";
        System.out.println(body);
        
        url = buildUrl(); 
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST,MyTestUtils.getTestUserToken1(), body);
         
             MyResponseUtils.assert201(connection, showResult);
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


        String body = "{\"type\":1,\"ownerId\":1,\"name\":\"test\",\"nodeId\":900,\"linkcode\":\"\",\"parentId\":0}";
        System.out.println(body);
        
        url = buildUrl(); 
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST,MyTestUtils.getTestUserToken1(), body);
         
             MyResponseUtils.assert201(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    
    }
    @Test
    public void testNoSuchParent()
    {


        String body = "{\"type\":1,\"ownerId\":1,\"name\":\"test\",\"nodeId\":780,\"linkcode\":\"\",\"parentId\":66}";
        System.out.println(body);
        
        url = buildUrl(); 
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST,MyTestUtils.getTestUserToken1(), body);
         
             MyResponseUtils.assert201(connection, showResult);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    
    
    }
    
    private static Long assertHttpStatus(HttpURLConnection openurl, boolean showResult, int expectedStatusCode) throws Exception 
    {
        int returnCode = openurl.getResponseCode();
        long day=openurl.getDate();
        Date date = new Date(day);
        System.out.println(openurl.getHeaderField("server"));
        System.out.println(openurl.getHeaderField("Server"));
        System.out.println(openurl.getDate()+"   "+ date);
        System.out.println(openurl.getContentLength());
        System.out.println(openurl.getContentType());
        if(showResult)
        {
            InputStream stream = null;
            BufferedReader in = null;
            try
            {
                stream = openurl.getInputStream();
                in = new BufferedReader(new InputStreamReader(stream));
                System.out.println("Return data is------------------------------------------");
                String s = in.readLine();
                System.out.println(s);
             FavoriteNode favoriteNode = JsonUtils.stringToObject(s, FavoriteNode.class);
             return favoriteNode.getId();
            }
           catch(Exception e)
           {
               stream = openurl.getErrorStream();
               if(null == stream)
               {
                   System.err.println("There is not any return data.");
                   e.printStackTrace();
               }
               else
               {
                   in = new BufferedReader(new InputStreamReader(stream));
                   System.out.println("Error return data is------------------------------------------");
                   System.err.println(in.readLine());
               }
           }
           finally
           {
               System.out.println(openurl.getContentLength());
               IOUtils.closeQuietly(in);
               IOUtils.closeQuietly(stream);
               openurl.disconnect();
           }
        }
        Assert.assertEquals(expectedStatusCode, returnCode);
        return (long)0;
    }
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 +"favorites";
    }
}
