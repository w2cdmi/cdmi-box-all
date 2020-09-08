package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserAPIControllerDeleteTest
{
    private boolean showResult = true;
    
    private String getUrl(Long id)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "users/" + id;
    }
    
    @Test
    public void testSuccessDeleteUser() throws Exception
    {
        String urlString = getUrl(75L);
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("DELETE");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAccountAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assert200(openurl, showResult);
    }
    
}
