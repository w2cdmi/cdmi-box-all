package com.huawei.sharedrive.app.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class UserAPIControllerGetDetailsTest extends BaseAPITest {
	
	private static final String URL_DETAILS = MyTestUtils.SERVER_URL_UFM_V2 + "/users/details";
	
	private static final String GET_DETAILS = "testData/users/getDetails.txt";
	
	@Test
	public void testNormal() throws Exception
	{
		URL url = new URL(URL_DETAILS);
        System.out.println(URL_DETAILS);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        String bodyStr = MyFileUtils.getDataFromFile(GET_DETAILS, "normal");
        openurl.getOutputStream().write(bodyStr.getBytes());
        openurl.connect();
        MyResponseUtils.assert200(openurl, showResult);
	}
}
