package com.huawei.sharedrive.app.test.openapi.group;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupModifyRequest;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroupRequest;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

public class GroupAPIModifyTest extends BaseAPITest
{
    private static final String DATA_MODIFY = "testData/group/modify.txt";
    
    private static final String DATA_CREATE = "testData/group/create.txt";
    
    private static final String DATA_ADD = "testData/group/addMember.txt";
    
    private long groupId;
    
    public GroupAPIModifyTest() throws Exception
    {
        RestGroupRequest request = new RestGroupRequest();
        request.setName("modifyGroup");
        RestGroup group = createGroup(JsonUtils.toJson(request), MyTestUtils.getTestUserToken1());
        groupId = group.getId();
    }
    
    public String buildUrl(Long groupId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "groups/" + groupId;
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        
        String url = buildUrl(group.getId());
        String modifyBody = MyFileUtils.getDataFromFile(DATA_MODIFY, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, modifyBody);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testManagerModify() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        
        String addBody = MyFileUtils.getDataFromFile(DATA_ADD, "manager");
        addBody = addBody.replaceAll("#userId#", "" + MyTestUtils.getTestCloudUserId2());
        addMember(addBody, MyTestUtils.getTestUserToken1(), group.getId());
        String url = buildUrl(group.getId());
        
        String modifyBody = MyFileUtils.getDataFromFile(DATA_MODIFY, "normal");
        HttpURLConnection connection = getConnection(url,
            METHOD_PUT,
            MyTestUtils.getTestUserToken2(),
            modifyBody);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAppNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        
        String url = buildUrl(group.getId());
        String modifyBody = MyFileUtils.getDataFromFile(DATA_MODIFY, "normal");
        System.out.println(modifyBody);
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_PUT,
            MyTestUtils.getAccountAuthorization(dateStr),
            dateStr,
            modifyBody);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNotContainOwnedBy() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        
        String url = buildUrl(group.getId());
        String modifyBody = MyFileUtils.getDataFromFile(DATA_MODIFY, "notContainOwnedBy");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, modifyBody);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNotAdmin() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal2");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken2());
        
        String url = buildUrl(group.getId());
        String modifyBody = MyFileUtils.getDataFromFile(DATA_MODIFY, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, modifyBody);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testBadRequest() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal2");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken2());
        
        String url = buildUrl(group.getId());
        String modifyBody = MyFileUtils.getDataFromFile(DATA_MODIFY, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, modifyBody);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
    
    @Test
    public void testInvalidName() throws Exception
    {
        String url = buildUrl(groupId);
        RestGroupModifyRequest modifyRequest = new RestGroupModifyRequest();
        modifyRequest.setName("1?3!");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, JsonUtils.toJson(modifyRequest));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
}
