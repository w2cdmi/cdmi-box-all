package com.huawei.sharedrive.app.test.openapi.mailmsg;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * @author t00159390
 * 
 */
public class MailMsgAPIAddTest extends BaseAPITest
{
    private static final String CREATE_FILE = "testData/mailmasg/setmsg.txt";
    
    private boolean showResult = true;
    
    private long parentId = 0;
    
    public MailMsgAPIAddTest()
    {
        try
        {
            parentId = createFolder(userId1, System.currentTimeMillis() + "", 0L).getId();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Get folderId id failed!");
        }
    }
    
    @Test
    public void addNormal() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addNoType() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addNoSubject() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    
    @Test
    public void addNoMsg() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addEmptySubject() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addEmptyMsg() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addInvalidType() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addLongSubject() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addLongMsg() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addOtherUser() throws Exception
    {
        long folderId = createFolder(userId1, System.currentTimeMillis() + "", parentId).getId();
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + folderId;
        System.out.println("url is " + urlString);
        String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_POST,
            MyTestUtils.getTestUserToken2(),
            bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
}
