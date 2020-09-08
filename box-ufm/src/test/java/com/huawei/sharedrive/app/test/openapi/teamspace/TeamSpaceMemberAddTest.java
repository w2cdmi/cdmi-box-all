package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceMemberAddTest extends BaseAPITest
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";
    private static final String MEMBER_ADD_FILE = "testData/teamspace/addspacemember.txt";
    private boolean showResult = true;
    
//    String urlString = Constants.SERVER_RUL + "/api/v2/teamspaces/" + teamspaceId;
//    urlString += "/memberships";
    
    
    @Test
    public void addTeamMemberNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addTeamMemberNormalSystem() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "normalSystem");
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assert201(openurl, showResult);
    }
    
    @Test
    public void addTeamMemberNormalWithAbnormalSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("abnormalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.ABNORMAL_TEAMSPACE_STATUS, showResult);
    }
    
    
    @Test
    public void addTeamMemberNormalWithNoSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + 900000;
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_TEAMSPACE, showResult);
    }
    
    
    @Test
    public void addTeamMemberRepeat() throws Exception
    {
        long teaspaceId = getTeamSpaceId("normalstatus");
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teaspaceId;
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assert201(openurl, showResult);
        
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl11 = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl11, ErrorCode.EXIST_MEMBER_CONFLICT, showResult);
    }
    
    @Test
    public void addTeamMemberMemberNoTeamRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "noteamRole");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void addTeamMemberTeamSpaceNoMember() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "nomember");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceAddAdmin() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "addadmin");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_TEAMROLE, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceNoMemberId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "nomemberid");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceNoMemberType() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "nomembertype");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        Assert.assertEquals("user", TeamSpaceUtil.getTeamMemberResult(openurl).getMember().getType());
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceNoRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "norole");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assert201(openurl, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceInvalidTeamRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "invalidteamRole");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_TEAMROLE, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceInvalidId() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "invalidid");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceNoSuchUser() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "nosuchuser");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_USER, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceInvalidType() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "invalidtype");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceInvalidRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceId("normalstatus");
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "invalidrole");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_RESOURCE_ROLE, showResult);
      
    }
    
    @Test
    public void addTeamMemberTeamSpaceNoRight() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + createUserTeamSpace2().getId();
        urlString += "/memberships";
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, userId2);
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, bodyStr);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
      
    }
    
    private Long getTeamSpaceId(String testKey) throws Exception
    {
        if(StringUtils.isBlank(testKey))
        {
            testKey = "normalstatus";
        }
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(SPACE_CREATE_FILE, testKey);
        openurl.getOutputStream().write(bodyStr.getBytes());
        
        return TeamSpaceUtil.getTeamSpaceResult(openurl).getId();
    }
    
    private String replaceUserId(String bodyString, Long userId)
    {
        bodyString = bodyString.replaceAll("#userId#", userId.toString());
        return bodyString;
    }
}
