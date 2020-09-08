package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceCreateRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

import pw.cdmi.core.utils.RandomGUID;

public class TeamSpaceAPIGetAttrTest extends BaseAPITest
{
    
    private boolean showResult = true;
    
    private long teamSpaceId;
    
    public TeamSpaceAPIGetAttrTest() throws Exception
    {
        RestTeamSpaceInfo teamSpace = createTeamSpace();
        teamSpaceId = teamSpace.getId();
        
    }
    
    @Test
    public void testNormal() throws Exception
    {
        buildUrl(teamSpaceId, "uploadNotice");
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    @Test
    public void testAll() throws Exception
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamSpaceId + "/attributes";
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    @Test
    public void testAppAccountAuth() throws Exception
    {
        buildUrl(teamSpaceId, "uploadNotice");
        String dateStr = MyTestUtils.getDateString();
        String authStr = MyTestUtils.getAccountAuthorization(dateStr);
        HttpURLConnection connection = getConnection(url, METHOD_GET, authStr, dateStr, null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidName() throws Exception
    {
        buildUrl(teamSpaceId, "uploadNotice123");
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
        
    }
    
    @Test
    public void testForbidden() throws Exception
    {
        buildUrl(teamSpaceId, "uploadNotice");
        String token = MyTestUtils.getTestUserToken2();
        HttpURLConnection connection = getConnection(url, METHOD_GET, token, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
        
    }
    
    private void buildUrl(long teamSpaceId, String name)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamSpaceId + "/attributes?name=" + name;
    }
    
    private void close(Closeable obj)
    {
        if (obj != null)
        {
            try
            {
                obj.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private RestTeamSpaceInfo createTeamSpace() throws Exception
    {
        RestTeamSpaceCreateRequest request = new RestTeamSpaceCreateRequest();
        request.setName(new RandomGUID().getValueAfterMD5());
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces";
        InputStream stream = null;
        BufferedReader in = null;
        try
        {
            HttpURLConnection connection = getConnection(urlString, METHOD_POST, JSonUtils.toJson(request));
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            RestTeamSpaceInfo response = JSonUtils.stringToObject(result, RestTeamSpaceInfo.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create folder Failed!");
        }
        finally
        {
            close(in);
            close(stream);
        }
    }
}
