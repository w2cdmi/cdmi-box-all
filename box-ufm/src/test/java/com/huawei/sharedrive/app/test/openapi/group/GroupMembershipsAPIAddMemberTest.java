package com.huawei.sharedrive.app.test.openapi.group;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class GroupMembershipsAPIAddMemberTest extends BaseAPITest
{
    private static final String DATA_ADD = "testData/group/addMember.txt";
    
    private static final String DATA_CREATE = "testData/group/create.txt";
    
    private String buildUrl(Long groupId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "groups/" + groupId + "/memberships";
    }
    
    
    @Test
    public void testManagerAddManager() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken2());
        String manager1 = MyFileUtils.getDataFromFile(DATA_ADD, "manager");
        manager1 = manager1.replaceAll("#userId#", "" +MyTestUtils.getTestCloudUserId1());
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST, MyTestUtils.getTestUserToken2(), manager1);
        MyResponseUtils.assert201(connection, showResult);
        
        String manager2 = MyFileUtils.getDataFromFile(DATA_ADD, "manager");
        manager2 = manager2.replaceAll("#userId#", "170");
        String url2 = buildUrl(group.getId());
        HttpURLConnection connection2 = getConnection(url2, METHOD_POST, manager2);
        MyResponseUtils.assertReturnCode(connection2, ErrorCode.FORBIDDEN_OPER, showResult);
        
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String body = MyFileUtils.getDataFromFile(DATA_ADD, "normal");
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
        
    }
    
    @Test
    public void testAppNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String dateStr = MyTestUtils.getDateString();
        String body = MyFileUtils.getDataFromFile(DATA_ADD, "normal");
        System.out.println(body +"=====>");
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getAccountAuthorization(dateStr),
            dateStr,
            body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testUnAuthorized() throws Exception
    {
        String url = buildUrl(999L);
        String body = MyFileUtils.getDataFromFile(DATA_ADD, "normal");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testExceedGroupMaxMemberNum() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "onlyOneMember");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String body = MyFileUtils.getDataFromFile(DATA_ADD, "normal");
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.EXCEED_MAX_GROUP_MEMBER_NUM, showResult);
    }
    
    @Test
    public void testNoUser() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String body = MyFileUtils.getDataFromFile(DATA_ADD, "noSuchUser");
        body = body.replaceAll("#userId#", "9999999");
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    @Test
    public void testAddTeamspace() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String body = MyFileUtils.getDataFromFile(DATA_ADD, "teamSpaceUser");
        
        RestTeamSpaceInfo  space = createTeamSpace(MyTestUtils.getTestUserToken1());
        body = body.replaceAll("#userId#", space.getId() + "");
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_USER, showResult);
    }
}
