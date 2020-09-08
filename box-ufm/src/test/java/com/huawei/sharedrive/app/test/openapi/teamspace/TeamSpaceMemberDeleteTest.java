package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class TeamSpaceMemberDeleteTest extends BaseAPITest
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";
    
    private static final String MEMBER_ADD_FILE = "testData/teamspace/addspacemember.txt";
    
    private boolean showResult = true;
    
    @Test
    public void deleteTeamsapceMemberOwner() throws Exception
    {
        RestTeamSpaceInfo teamSpace = createTeamSpace(MyTestUtils.getTestUserToken1());
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamSpace.getId();
        urlString += "/memberships/" + getMembershipId(teamSpace.getId());
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void deleteTeamMemberNormal() throws Exception
    {
        long teamspaceId = getTeamSpaceResult("normalstatus");
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamspaceId;
        urlString += "/memberships/" + getMembershipId(teamspaceId);
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void deleteTeamMemberWithAbnormalSpace() throws Exception
    {
        long teamspaceId = getTeamSpaceResult("abnormalstatus");
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamspaceId;
        urlString += "/memberships/" + 1;
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.ABNORMAL_TEAMSPACE_STATUS, showResult);
    }
    
    @Test
    public void deleteTeamMemberWithNoSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + 900000;
        urlString += "/memberships/" + 1;
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_TEAMSPACE, showResult);
    }
    
    @Test
    public void deleteTeamMemberNoSuchMemberShip() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + 90000;
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    @Test
    public void deleteTeamMemberNoRight() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + createUserTeamSpace2().getId();
        urlString += "/memberships/" + 1;
        HttpURLConnection openurl = getConnection(urlString, METHOD_DELETE);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    private Long getTeamSpaceResult(String testKey) throws Exception
    {
        if (StringUtils.isBlank(testKey))
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
    
    private Long getMembershipId(long teamspaceId) throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamspaceId;
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
