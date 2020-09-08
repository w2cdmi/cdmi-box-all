package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceAPICreateTest extends BaseAPITest
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";
    
    private boolean showResult = true;
    
    
    @Test
    public void testCreateTeamSpaceNormal() throws Exception
    {
        for(int i=0;i<200;i++)
        {
            String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
            String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "normal");
            HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
            MyResponseUtils.assert201(openurl, showResult);
        }
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void testCreateTeamSpaceRepeat() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
       
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "repeat");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void testCreateTeamSpaceNoname() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
       
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "noName");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testCreateTeamSpaceNoDescp() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "nodescp");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void testCreateTeamSpaceEmptyname() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
       
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "emptyName");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testCreateTeamSpaceEmptyDescp() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "emptydescp");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    
    @Test
    public void testCreateTeamSpaceNoSize() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "nosize");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void testCreateTeamSpaceNoStatus() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "nostatus");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void testCreateTeamSpaceInvalidSize() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "invalidsize");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);

    }
     @Test
    public void testCreateTeamSpaceZeroSize() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "zerosize");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);

    }
	  
    @Test
    public void testCreateTeamSpaceInvalidStatus() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
       
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "invalidstatus");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);

    }
    
    @Test
    public void testCreateTeamSpaceLongName() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "longname");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testCreateTeamSpaceLongNameLt255() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "longname1");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert201(openurl, showResult);

    }
    
    
    @Test
    public void testCreateTeamSpaceDisable() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "abnormalstatus");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert201(openurl, showResult);
    }
}
