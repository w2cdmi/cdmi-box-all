package com.huawei.sharedrive.app.test.openapi.group;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class GroupMembershipsAPIListMemberTest extends BaseAPITest
{
    private static final String DATA_CREATE = "testData/group/create.txt";
    
    private final static String DATA_MEMBER_LIST = "testData/group/listMember.txt";
    
    private String buildUrl(long groupId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "groups/" + groupId + "/memberships/items";
    }
    
    @Test
    public void testNormal() throws Exception
    {
//        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
//        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken2());
        String body = MyFileUtils.getDataFromFile(DATA_MEMBER_LIST, "normal");
        String url = buildUrl(335);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testListOneRole() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String body = MyFileUtils.getDataFromFile(DATA_MEMBER_LIST, "listAdmin");
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAppNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String dateStr = MyTestUtils.getDateString();
        String body = MyFileUtils.getDataFromFile(DATA_MEMBER_LIST, "normal");
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getAppAuthorization(dateStr),
            dateStr,
            body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
}
