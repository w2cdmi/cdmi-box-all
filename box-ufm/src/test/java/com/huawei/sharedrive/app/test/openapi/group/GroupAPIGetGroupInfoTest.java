package com.huawei.sharedrive.app.test.openapi.group;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class GroupAPIGetGroupInfoTest extends BaseAPITest
{
    private static final String DATA_CREATE = "testData/group/create.txt";
    
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
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testForbidden() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken2());
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    
    @Test
    public void testAppNormal() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        RestGroup group = createGroup(createBody, MyTestUtils.getTestUserToken1());
        String url = buildUrl(group.getId());
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection =getConnection(url, METHOD_GET, MyTestUtils.getAppAuthorization(dateStr), dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAppNormalCreateAndGet() throws Exception
    {
        String createBody = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        String dateStr = MyTestUtils.getDateString();
        RestGroup group = createGroup(createBody, MyTestUtils.getAccountAuthorization(dateStr), dateStr);
        String url = buildUrl(group.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNoSucGroup() throws Exception
    {
        String url = buildUrl(99999L);
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection =getConnection(url, METHOD_GET, MyTestUtils.getAppAuthorization(dateStr), dateStr, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_GROUP, showResult);
    }
}
