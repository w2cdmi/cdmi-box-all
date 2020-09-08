package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class NodePermissionsGetTest extends BaseAPITest
{
    private boolean showResult = true;
    
    @Test
    public void testGetPermissionsNormal() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "permissions/"  + getTeamSpaceId();
        urlString += "/0/" + MyTestUtils.getTestCloudUserId1();
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assert200(openurl, showResult);
    }
 
    @Test
    public void testGetPermissionsNoSuchNode() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "permissions/"  + getTeamSpaceId();
        urlString += "/2/" + MyTestUtils.getTestCloudUserId1();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testGetPermissionsNoSuchOwner() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "permissions/" + 900000000;
        urlString += "/0/" + MyTestUtils.getTestCloudUserId1();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testGetPermissionsNoSuchUser() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "permissions/"  + getTeamSpaceId();
        urlString += "/0/" + 90000000;
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.NO_SUCH_USER, showResult);
    }
    
    @Test
    public void testGetPermissionsInvalidNode() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "permissions/"  + getTeamSpaceId();
        urlString += "/-1/" + MyTestUtils.getTestCloudUserId1();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    
    /**
     * 查看其他资源拥有者权限
     * 
     * @throws Exception
     */
    @Test
    public void testGetPermissionsIOtherOwner() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "permissions/"  + userId2;
        urlString += "/0/" + MyTestUtils.getTestCloudUserId1();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    private Long getTeamSpaceId() throws Exception
    {
        return createUserTeamSpace1().getId();
    }
}
