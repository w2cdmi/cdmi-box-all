package com.huawei.sharedrive.app.test.openapi.group;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.group.RestGroup;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class GroupAPIListGroupsTest extends BaseAPITest
{
    
    private static final String DATA_CREATE = "testData/group/create.txt";
    
    private static final String DATA_LISTALL = "testData/group/listAll.txt";
    
    private static final String DATA_ADD = "testData/group/addMember.txt";
    
    public String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "groups/all";
    }
    
    private String buildDelUrl(long groupId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "groups/" + groupId;
    }
    
    @Test
    public void testNormalTimes() throws Exception
    {
        RestGroup group = null;
        String body = MyFileUtils.getDataFromFile(DATA_CREATE, "normal");
        String bodyAdd1 = MyFileUtils.getDataFromFile(DATA_ADD, "normal");
        String bodyAdd2 = MyFileUtils.getDataFromFile(DATA_ADD, "manager");
        bodyAdd2 = bodyAdd2.replaceAll("#userId#", "181");
        for (int i = 0; i < 50; i++)
        {
            try
            {
                group = createGroup(body, MyTestUtils.getTestUserToken1());
                String delUrl = buildDelUrl(group.getId());
                addMember(bodyAdd1, MyTestUtils.getTestUserToken1(), group.getId());
                addMember(bodyAdd2, MyTestUtils.getTestUserToken1(), group.getId());
                System.out.println("=============>列举群组");
                String body1 = MyFileUtils.getDataFromFile(DATA_LISTALL, "normal");
                String url = buildUrl();
                HttpURLConnection connection = getConnection(url, METHOD_POST, body1);
                MyResponseUtils.assert200(connection, showResult);
                //删除群组
                if(i %2 ==0)
                {
                    System.out.println("=============>删除群组");
                    HttpURLConnection connection1 = getConnection(delUrl, METHOD_DELETE);
                    MyResponseUtils.assert200(connection1, showResult);
                    System.out.println("=============>列举群组");
                    HttpURLConnection connection2 = getConnection(url, METHOD_POST, body1);
                    MyResponseUtils.assert200(connection2, showResult);
                }
            }
            catch(Exception e)
            {
                System.out.println("Error:" + e.getMessage());
            }
        }
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(DATA_LISTALL, "normal");
        String url = buildUrl();
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAppNormal() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(DATA_LISTALL, "normal");
        String url = buildUrl();
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getAccountAuthorization(dateStr),
            dateStr,
            body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnAutorization() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(DATA_LISTALL, "normal");
        String url = buildUrl();
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
}
