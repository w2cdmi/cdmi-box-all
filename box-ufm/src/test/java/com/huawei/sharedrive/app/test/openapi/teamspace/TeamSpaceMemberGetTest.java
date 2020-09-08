package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceMemberGetTest extends BaseAPITest
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";
    private static final String MEMBER_ADD_FILE = "testData/teamspace/addspacemember.txt";
    
    private boolean showResult = true;
    
    private Long teamSpaceId = null;
    
    private Long membershipId = null;
    
    @Test
    public void getTeamMemberNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipId();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void getTeamMemberWithAbnormalSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("abnormalstatus");
        teamSpaceId = null;
        urlString += "/memberships/" + getMembershipId();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.ABNORMAL_TEAMSPACE_STATUS, showResult);
    }
    
    
    @Test
    public void getTeamMemberWithNoSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + 900000;
        urlString += "/memberships/" + getMembershipId();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_TEAMSPACE, showResult);
    }
 
    @Test
    public void getTeamMemberNoSuchMemberShip() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + 90000;
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    @Test
    public void getTeamMemberNoRight() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + createUserTeamSpace2().getId();
        urlString += "/memberships/" + getMembershipId();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    private Long getTeamSpaceResult(String testKey) throws Exception
    {
        if(teamSpaceId != null)
        {
            return teamSpaceId;
        }
        
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
        teamSpaceId = TeamSpaceUtil.getTeamSpaceResult(openurl).getId();
        return teamSpaceId;
    }
    
    private Long getMembershipId() throws Exception
    {
        if(membershipId != null)
        {
            return membershipId;
        }
        
        long teamId = getTeamSpaceResult("normalstatus");
        
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamId;
        urlString += "/memberships";
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = BaseConnection.getURLConnection(urlString, "POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "normal");
        bodyStr = replaceUserId(bodyStr, MyTestUtils.getTestCloudUserId2());
        openurl.getOutputStream().write(bodyStr.getBytes());
        
        return TeamSpaceUtil.getTeamMemberResult(openurl).getId();
    }
    
    private String replaceUserId(String bodyString, Long userId)
    {
        bodyString = bodyString.replaceAll("#userId#", userId.toString());
        return bodyString;
    }
}
