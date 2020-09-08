package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceAPIListAll {

	private static final String TEAMSPACE_LIST_FILE = "testData/teamspace/listspaceall.txt";
	private boolean showResult = true;
	
	@Test
	public void testListTeamSpaceNormal() throws Exception {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
		openurl.setRequestProperty("Content-type", "application/json");
		openurl.setRequestProperty("Authorization",
				MyTestUtils.getTestUserToken1());
		openurl.setDoInput(true);
		openurl.setDoOutput(true);
		openurl.connect();
		String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE,
				"normal");
		openurl.getOutputStream().write(
				replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1())
						.getBytes());
		MyResponseUtils.assert200(openurl, showResult);
	}
	
	@Test
	public void testListTeamSpaceNormalApp() throws Exception {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		String dateStr = MyTestUtils.getDateString();
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
		 openurl.setRequestProperty("Date", dateStr);
		openurl.setRequestProperty("Content-type", "application/json");
		openurl.setRequestProperty("Authorization",
				MyTestUtils.getAppAuthorization(dateStr));
		openurl.setDoInput(true);
		openurl.setDoOutput(true);
		openurl.connect();
		String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE,
				"normal");
		openurl.getOutputStream().write(
				replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1())
						.getBytes());
		MyResponseUtils.assert200(openurl, showResult);
	}
	
	@Test
    public void testListTeamSpaceNormal1() throws Exception
    {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "normalordertime");
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1()).getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidLimit() throws Exception
    {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "invalidlimit");
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1()).getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
   
    @Test
    public void testListTeamSpaceInvalidOffset() throws Exception
    {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "invalidoffset");
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1()).getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    
    @Test
    public void testListTeamSpacenolimit() throws Exception
    {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "nolimit");
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1()).getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenooffset() throws Exception
    {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "nooffset");
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1()).getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenoorder() throws Exception
    {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "noorder");
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1()).getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenoorderfield() throws Exception
    {
		String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
		System.out.println("url is " + urlString);
		HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "noorderfield");
        openurl.getOutputStream().write(replaceOwnerId(bodyStr, MyTestUtils.getTestCloudUserId1()).getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    private String replaceOwnerId(String bodyString, Long ownerid)
    {
        bodyString = bodyString.replaceAll("#userId#", ownerid.toString());
        return bodyString;
    }
}
