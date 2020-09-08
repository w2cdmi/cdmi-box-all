package com.huawei.sharedrive.app.test.openapi.teamspace;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

public class RoleSettingGetTest extends BaseAPITest
{
    private boolean showResult = true;
    
    /**
     * Token 鉴权
     * 
     * @throws Exception
     */
    @Test
    public void getRoleSetting() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "roles";
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET);
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    @Test
    public void getRoleSettingInvalid() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "roles";
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = getConnectionWithUnauthToken(urlString, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    /**
     * app 鉴权
     * 
     * @throws Exception
     */
    @Test
    public void getRoleSettingApp() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "roles";
        System.out.println("url is " + urlString);

        String dateStr = MyTestUtils.getDateString();
        
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET, MyTestUtils.getAppAuthorization(dateStr), dateStr, null);
        MyResponseUtils.assert200(openurl, showResult);
        
    }
    
    @Test
    public void getRoleSettingAppInvalid() throws Exception
    {
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "roles";
        System.out.println("url is " + urlString);
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection openurl = getConnection(urlString, METHOD_GET,"app,OneBox,ZjhhZDNkOTY1ODFkOGE1N", dateStr, null);
        
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.LOGINUNAUTHORIZED, showResult);
        
    }
    
    @Test
    public void decodeAPPSecurity()
    {
    }
}
