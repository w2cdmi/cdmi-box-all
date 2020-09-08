package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import junit.framework.Assert;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
public class TeamSpaceMemberListTest extends BaseAPITest
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";
    private static final String TEAMSPACE_MEMBER_LIST_FILE = "testData/teamspace/listspacemember.txt";
    
    private Long teamspaceId = null;
    private boolean showResult = true;
    
    @Test
    public void testListTeamSpaceNormal() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceNormal1() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "normalordertime");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceNormalUsername() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "normalorderusername");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidLimit() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "invalidlimit");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
   
    @Test
    public void testListTeamSpaceInvalidOffset() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "invalidoffset");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
   
    @Test
    public void testListTeamSpacenolimit() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "nolimit");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenooffset() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "nooffset");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenoorder() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "noorder");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpacenoorderfield() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "noorderfield");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidfield() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "invalidorderfield");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidDirect() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "invalidorderdirect");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    
    @Test
    public void testListTeamSpaceNoTeamRole() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "noTeamRole");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    
    @Test
    public void testListTeamSpaceInvalidTeamRole() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "invalidTeamRole");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_TEAMROLE, showResult);
    }
    
    @Test
    public void testListTeamSpaceNoKeyword() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "nokeyword");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void testListTeamSpaceInvalidKeyword() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "invalidkeyword");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        Assert.assertEquals(0, TeamSpaceUtil.getTeamMemberListResult(openurl).getTotalCount());
    }
    
    @Test
    public void testListTeamSpaceNoRight() throws Exception
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + createUserTeamSpace2().getId();
        urlString += "/memberships/items";
        String bodyStr = MyFileUtils.getDataFromFile(TEAMSPACE_MEMBER_LIST_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    private Long getTeamSpaceId() throws Exception
    {
        if(teamspaceId !=null)
        {
            return teamspaceId;
        }
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, "normalstatus");
        openurl.getOutputStream().write(bodyStr.getBytes());
        
        return TeamSpaceUtil.getTeamSpaceResult(openurl).getId();
    }
}


