package com.huawei.sharedrive.app.test.openapi.mailmsg;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class MailMsgAPIGetInfoTest extends BaseAPITest
{
    private static final String CREATE_FILE = "testData/teamspace/createspace.txt";

    private boolean showResult = true;
    private long parentId = 0;
    
    public MailMsgAPIGetInfoTest()
    {
        try
        {
            parentId = createFolder(userId1, System.currentTimeMillis() + "", 0L).getId();
            String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + parentId;
            System.out.println("url is " + urlString);
            String bodyStr = MyFileUtils.getDataFromFile(CREATE_FILE, "normal");
            HttpURLConnection openurl = getConnection(urlString,
                METHOD_POST,
                MyTestUtils.getTestUserToken1(),
                bodyStr);
            MyResponseUtils.assert201(openurl, showResult);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Get folderId id failed!");
        }
    }
    
    @Test
    public void getNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + parentId + "?type=" + 0;
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_GET,
            MyTestUtils.getTestUserToken1());
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void getInvalidType() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId1 + "/" + parentId + "?type=" + 3;
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_GET,
            MyTestUtils.getTestUserToken1());
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void getOther() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "mailmsgs/" + userId2 + "/" + parentId + "?type=" + 0;
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString,
            METHOD_GET,
            MyTestUtils.getTestUserToken1());
        MyResponseUtils.assert201(openurl, showResult);
    }
    
}
