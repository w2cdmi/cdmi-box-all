package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamMemberInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.BaseConnection;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

public class TeamSpaceMemberUpdateTest extends BaseAPITest
{
    private static final String SPACE_CREATE_FILE = "testData/teamspace/createspace.txt";
    
    private static final String MEMBER_MODIFY_FILE = "testData/teamspace/modifyspacemember.txt";
    
    private static final String MEMBER_ADD_FILE = "testData/teamspace/addspacemember.txt";
    
    private static final String DATA_CREATE = "testData/group/create.txt";
    
    private boolean showResult = true;
    
    private Long teamSpaceId = null;
    
    private Long membershipId = null;
    
    @Test
    public void modifyTeamMemberNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void modifyTeamMemberForGroup() throws Exception
    {
        RestTeamSpaceInfo spaceInfo = createTeamSpace(MyTestUtils.getTestUserToken1());
        String groupBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        System.out.println("===>创建群组");
        RestGroup group = createGroup(groupBody, MyTestUtils.getTestUserToken1());
        String addBody = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "member");
        addBody = addBody.replaceAll("#userId#", group.getId() + "");
        System.out.println("====>添加群组为普通成员");
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + spaceInfo.getId() + "/memberships";
        HttpURLConnection openurl = getConnection(urlString, METHOD_POST, addBody);
        InputStream stream = null;
        BufferedReader in = null;
        RestTeamMemberInfo memberships = null;
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            System.out.println("Return data is------------------------------------------");
            String result = in.readLine();
            memberships =  JSonUtils.stringToObject(result, RestTeamMemberInfo.class);
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
            if (stream != null)
            {
                stream.close();
            }
        }
        System.out.println("====>添加群组为普通成员");
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "normal");
        String urlStringModify  = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + spaceInfo.getId()+ "/memberships/" + memberships.getId();
        HttpURLConnection openurls = getConnection(urlStringModify, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assert200(openurls, showResult);
        
    }
    
    @Test
    public void modifyTeamMemberNormalWithNoRoleSet() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipIdWithNoRole();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void modifyTeamMemberNormalWithAbnormalSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/"
            + getTeamSpaceResult("abnormalstatus");
        teamSpaceId = null;
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.ABNORMAL_TEAMSPACE_STATUS, showResult);
    }
    
    @Test
    public void modifyTeamMemberNormalWithNoSpace() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + 900000;
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_TEAMSPACE, showResult);
    }
    
    @Test
    public void modifyTeamMemberRepeat() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void modifyTeamMemberMemberNoTeamRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "noteamRole");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void modifyTeamMemberTeamSpaceNoRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "norole");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assert200(openurl, showResult);
        
    }
    
    @Test
    public void modifyTeamMemberTeamSpaceInvalidTeamRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "invalidteamRole");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_TEAMROLE, showResult);
        
    }
    
    @Test
    public void modifyTeamMemberTeamSpaceInvalidRole() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "invalidrole");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_RESOURCE_ROLE, showResult);
        
    }
    
    @Test
    public void modifyTeamMemberNoBody() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + getTeamSpaceResult("normalstatus");
        urlString += "/memberships/" + getMembershipId();
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "nobody");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void modifyTeamMemberNoRight() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + createUserTeamSpace2().getId();
        urlString += "/memberships/" + 1;
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_MODIFY_FILE, "normal");
        HttpURLConnection openurl = getConnection(urlString, METHOD_PUT, bodyStr);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    private Long getTeamSpaceResult(String testKey) throws Exception
    {
        if (teamSpaceId != null)
        {
            return teamSpaceId;
        }
        
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
        teamSpaceId = TeamSpaceUtil.getTeamSpaceResult(openurl).getId();
        return teamSpaceId;
    }
    
    private Long getMembershipId() throws Exception
    {
        if (membershipId != null)
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
    
    private Long getMembershipIdWithNoRole() throws Exception
    {
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
        String bodyStr = MyFileUtils.getDataFromFile(MEMBER_ADD_FILE, "norole");
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
