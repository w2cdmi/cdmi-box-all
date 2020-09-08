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

public class GroupMembershipsAPIDeleteMemberTest extends BaseAPITest
{
    
    private static final String DATA_CREATE = "testData/group/create.txt";
    
    private static final String DATA_ADD = "testData/group/addMember.txt";
    
    private String buildUrl(Long groupId, Long userId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "groups/" + groupId + "/memberships/" + userId;
    }
    
    @Test
    public void testNormal() throws Exception
    {
        
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String url = buildUrl(group.getId(), group.getOwnedBy());
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAppNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String addBody = MyFileUtils.getDataFromFile(DATA_ADD, "addUser");
        addBody = addBody.replaceAll("#userId#", MyTestUtils.getTestCloudUserId2() +"");
        GroupMembershipsInfo membershipsInfo = addMember(addBody,
            MyTestUtils.getTestUserToken1(),
            group.getId());
        String dateStr = MyTestUtils.getDateString();
        
        String url = buildUrl(group.getId(), MyTestUtils.getTestCloudUserId2());
        HttpURLConnection connection = getConnection(url,
            METHOD_DELETE,
            MyTestUtils.getAccountAuthorization(dateStr),
            dateStr,
            null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAppDeleteAdmin() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String dateStr = MyTestUtils.getDateString();
        String url = buildUrl(group.getId(), MyTestUtils.getTestCloudUserId2());
        HttpURLConnection connection = getConnection(url,
            METHOD_DELETE,
            MyTestUtils.getAccountAuthorization(dateStr),
            dateStr,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
}
