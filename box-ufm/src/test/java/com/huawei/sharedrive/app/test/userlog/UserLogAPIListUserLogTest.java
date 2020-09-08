package com.huawei.sharedrive.app.test.userlog;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserLogAPIListUserLogTest 
{
    private static final String DATA_LIST_USERLOG = "testData/userlog/listUserLog.txt";
    
    public UserLogAPIListUserLogTest()
    {
        
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String url = buildUrl();
        String body = MyFileUtils.getDataFromFile(DATA_LIST_USERLOG, "normal");
        body = body.replaceAll("#beginTime#",1391845003374L+"");
        body = body.replaceAll("#endTime#", (new Date().getTime() +10000) +"");
        System.out.println("BODY:"+body);
        
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getAppAuthorization(dateStr);
        URL newUrl = new URL(url);
        System.out.println("Request url : " + newUrl);
        HttpURLConnection connection = (HttpURLConnection) newUrl.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Date", dateStr);
        connection.setRequestProperty("Authorization", authorization);
        connection.setDoInput(true);
        
        if (StringUtils.isNotBlank(body))
        {
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }
        else
        {
            connection.setDoOutput(false);
        }
        MyResponseUtils.assert200(connection, true);
    }
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2  + "userlogs";
    }
}
