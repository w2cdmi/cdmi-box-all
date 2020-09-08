package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class RegionAPIControllerTest
{
    
    private boolean showResult = true;
    
    
    
    @Test
    public void testGetRegionList() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "/regions";
        URL url = null;
        url = new URL(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setRequestMethod("GET");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testBadAuthor() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "/regions";
        URL url = null;
        url = new URL(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr) + 1);
        openurl.setRequestProperty("Date", dateStr);
        openurl.setRequestMethod("GET");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testExpiredAuthor() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "/regions";
        URL url = null;
        url = new URL(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, 16);
        String dateStr = MyTestUtils.getDateString(date);
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setRequestMethod("GET");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
}
