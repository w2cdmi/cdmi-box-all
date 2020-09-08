package pw.cdmi.box.disk.test.openapi.user;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import pw.cdmi.box.disk.test.openapi.Constants;
import pw.cdmi.box.disk.test.utils.MyResponseUtils;
import pw.cdmi.box.disk.test.utils.UserRestTestUtils;

public class UserLoginV1Test
{
    private static final String TEST_NORMAL_USER = "testData/testNormalLogin.txt"; 
    
    private boolean showResult = true;
    
    @Test
    public void testNormalLogin() throws Exception
    {
        String urlString =  Constants.SERVER_RUL + "/api/v1/auth/login";
        URL url = null;
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("POST");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
//        RestUserloginRsp user = JSonUtils.stringToObject(userInfoString, RestUserloginRsp.class);
    }
    @Test
    public void userLogin()
    {
        
        String urlString = Constants.SERVER_RUL + "/api/v1/auth/login";
        
        URL url = null;
        
        try
        {
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(TEST_NORMAL_USER);
            
            openurl.getOutputStream().write(bodyStr.getBytes());
            UserRestTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void userLogout()
    {
        
        String urlString = Constants.SERVER_RUL + "/api/v1/auth/logout";
        
        URL url = null;
        
        try
        {
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("DELETE");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", "326385d5699167c052a1a8de05912f2e");
            openurl.connect();
            UserRestTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void refreshToken()
    {
        
        String urlString = Constants.SERVER_RUL + "/api/v1/auth/token";
        
        URL url = null;
        
        try
        {
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", "79513650d475ca1731e9766cb124e99b");
            openurl.connect();
            UserRestTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
