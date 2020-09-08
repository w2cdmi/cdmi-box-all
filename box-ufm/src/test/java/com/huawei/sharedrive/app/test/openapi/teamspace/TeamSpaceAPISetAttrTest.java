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
import com.huawei.sharedrive.app.openapi.domain.teamspace.SetTeamSpaceAttrRequest;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.RandomGUID;

public class TeamSpaceAPISetAttrTest extends BaseAPITest
{
    
    private boolean showResult = true;
    
    public TeamSpaceAPISetAttrTest() throws Exception
    {
        RestTeamSpaceInfo teamSpace = createTeamSpace();
        buildUrl(teamSpace.getId());
    }
    
    @Test
    public void testNormal() throws Exception
    {
        SetTeamSpaceAttrRequest request = new SetTeamSpaceAttrRequest();
        request.setName("uploadNotice");
        request.setValue("disable");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, JsonUtils.toJson(request));
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    @Test
    public void testAppAccountAuth() throws Exception
    {
        SetTeamSpaceAttrRequest request = new SetTeamSpaceAttrRequest();
        request.setName("uploadNotice");
        request.setValue("disable");
        String dateString = MyTestUtils.getDateString();
        String authString = MyTestUtils.getAccountAuthorization(dateString);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, authString, dateString, JsonUtils.toJson(request));
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    @Test
    public void testInvalidName() throws Exception
    {
        SetTeamSpaceAttrRequest request = new SetTeamSpaceAttrRequest();
        request.setName("uploadNotice123");
        request.setValue("disable");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, JsonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
        
    }
    
    @Test
    public void testInvalidValue() throws Exception
    {
        SetTeamSpaceAttrRequest request = new SetTeamSpaceAttrRequest();
        request.setName("uploadNotice");
        request.setValue("disable12");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, JsonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
        
    }
    
    @Test
    public void testForbidden() throws Exception
    {
        SetTeamSpaceAttrRequest request = new SetTeamSpaceAttrRequest();
        request.setName("uploadNotice");
        request.setValue("disable");
        String token = MyTestUtils.getTestUserToken2();
        HttpURLConnection connection = getConnection(url, METHOD_PUT, token, JSonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
        
    }
    
    private void buildUrl(long teamSpaceId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "teamspaces/" + teamSpaceId + "/attributes";
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
