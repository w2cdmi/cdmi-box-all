package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceAPIListUserTest extends BaseAPITest
{
    private static final String TEAMSPACE_LIST_FILE = "testData/teamspace/listspace.txt";
    
    private boolean showResult = true;
    
    @Test
    public void testListTeamSpaceNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceNormal1() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "normalordertime");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidUser() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "invaliduser");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, -900l));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidLimit() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "invalidlimit");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidOffset() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "invalidoffset");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpaceNosuchUser() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "nosuchuser");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, 9000L));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testListTeamSpacenouser() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "nouser");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpacenolimit() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "nolimit");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenooffset() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "nooffset");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenoorder() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "noorder");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenoorderfield() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "noorderfield");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId1));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpaceOther() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, replaceOwnerId(bodyStr, userId2));
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    private String replaceOwnerId(String bodyString, Long ownerid)
    {
        bodyString = bodyString.replaceAll("#userId#", ownerid.toString());
        return bodyString;
    }
    // @Test
    // public void testListTeamSpacenoorderdirect() throws Exception
    // {
    // String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/items";
    // URL url = null;
    // url = new URL(urlString);
    // System.out.println("url is " + urlString);
    // HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
    // openurl.setRequestMethod("POST");
    // openurl.setRequestProperty("Content-type", "application/json");
    // openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
    // openurl.setDoInput(true);
    // openurl.setDoOutput(true);
    // openurl.connect();
    // String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_LIST_FILE, "noorderdirect");
    // openurl.getOutputStream().write(bodyStr.getBytes());
    // MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    // }
    
    // private RestTeamMemberList getTeamspaceList(String bodyStr) throws Exception
    // {
    // return JSonUtils.stringToObject(bodyStr,RestTeamMemberList.class);
    // }
}
