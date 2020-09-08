package com.huawei.sharedrive.app.test.openapi.group;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.group.GroupMembershipsInfo;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class GroupAPIDeleteTest extends BaseAPITest
{
    
    private static final String DATA_CREATE = "testData/group/create.txt";
    
    private static final String DATA_ADD = "testData/group/addMember.txt";
    
    private static final String DATA_LISTALL = "testData/group/listUserGroups.txt";
    
    private String buildUrl(long groupId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "groups/" + groupId;
    }
    
    //DTS2015040904171
    @Test
    public void testNormal1() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        System.out.print("===>删除群组");
        String url1 = buildUrl(group.getId());
        HttpURLConnection connection1 = getConnection(url1, METHOD_DELETE);
        MyResponseUtils.assert200(connection1, showResult);
        System.out.print("==>创建群组");
        String createBody1 = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        createGroup(createBody1, MyTestUtils.getTestUserToken1());
        System.out.print("===>列举群组");
        String urlUser = MyTestUtils.SERVER_URL_UFM_V2 + "groups/items";
        String body1 = MyFileUtils.getDataFromFile(DATA_LISTALL, "normal");
        HttpURLConnection connection = getConnection(urlUser, METHOD_POST, body1);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormal() throws Exception
    {
        //创建群组
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        //更改拥有者
        String body = MyFileUtils.getDataFromFile(DATA_ADD, "normal");
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "groups/" + group.getId() + "/memberships";
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
        String url1 = buildUrl(group.getId());
        HttpURLConnection connection1 = getConnection(url1, METHOD_DELETE);
        MyResponseUtils.assert200(connection1, showResult);
//        String createBody1 = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
//        RestGroup group1 = createGroup(createBody1, MyTestUtils.getTestUserToken1());

    }
    
    @Test
    public void testAppNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String url = buildUrl(group.getId());
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url, METHOD_DELETE, MyTestUtils.getAppAuthorization(dateStr), dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAccountNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String url = buildUrl(group.getId());
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getAppAuthorization(dateStr).replaceAll("app,", "account,");
        HttpURLConnection connection = getConnection(url, METHOD_DELETE, authorization, dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnAuthoriztion() throws Exception
    {
        String url = buildUrl(999L);
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_DELETE, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testNoSuchGroup() throws Exception
    {
        String url = buildUrl(999L);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_GROUP, showResult);
    }
    
    @Test
    public void testNotGroupOwner() throws Exception
    {
        // String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        // RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        // GroupMembershipsInfo shipsInfo = addMember(null,
        // MyTestUtils.getTestUserToken1(), group.getId());
        GroupMembershipsInfo shipsInfo = addMember(null, MyTestUtils.getTestUserToken1(), 53L);
        String url = buildUrl(shipsInfo.getGroup().getId());
        HttpURLConnection connection = getConnection(url,
            METHOD_DELETE,
            MyTestUtils.getTestUserToken2(),
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testAppAuthorized() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String url = buildUrl(group.getId());
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url, METHOD_DELETE, MyTestUtils.getAppAuthorization(dateStr), dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
}
