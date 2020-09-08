package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceAPIListAllTest extends BaseAPITest
{
    private static final String TEAMSPACE_LIST_FILE = "testData/teamspace/listallspace.txt";
    
    private boolean showResult = true;
    
    @Test
    public void testListTeamSpaceNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "normal");
        String dateStr = MyTestUtils.getDateString();
        
//        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceNormal1() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "normalordertime");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceNormal2() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "normalkeyword");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceNormalToken() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    @Test
    public void testListTeamSpaceInvalidOrderFiled() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "invalidOrderfield");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidLimit() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "invalidlimit");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidOffset() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "invalidoffset");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpacenolimit() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "nolimit");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenooffset() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "nooffset");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenoorder() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "noorder");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenoorderfield() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/all";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "noorderfield");
        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, MyTestUtils.getAppAuthorization(dateStr), dateStr, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
}
