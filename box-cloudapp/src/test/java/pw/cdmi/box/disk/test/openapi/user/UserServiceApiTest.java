package pw.cdmi.box.disk.test.openapi.user;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import pw.cdmi.box.disk.oauth2.domain.UserToken;
import pw.cdmi.box.disk.test.utils.UserRestTestUtils;
import pw.cdmi.box.disk.utils.PropertiesUtils;
import pw.cdmi.core.exception.BusinessException;
import pw.cdmi.core.utils.JsonUtils;

@Component
public class UserServiceApiTest
{
    
    private static final String LOGIN_USER = "testData/serviceUrl.txt";
    
    private String token = null;
    
    @Test
    public void userLogin()
    {
        
        String urlString = "http://10.169.66.251:8080/enterprise/api/v2/login";
        
        URL url = null;
        HttpURLConnection openurl = null;
        try
        {
            url = new URL(urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("x-device-type", "ios");
            openurl.setRequestProperty("x-device-sn", "111");
            openurl.setRequestProperty("x-device-os", "MIUI 2.3.5");
            openurl.setRequestProperty("x-device-name", "xiaomi-one");
            openurl.setRequestProperty("x-client-version", "V1.1");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String bodyStr = UserRestTestUtils.getDataFromFile(LOGIN_USER);
            openurl.getOutputStream().write(bodyStr.getBytes());
            
            if (openurl.getResponseCode() != 200)
            {
                throw new BusinessException();
            }
            InputStream stream = openurl.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String re = in.readLine();
            System.out.println("re:" + re);
            UserToken userToken = JsonUtils.stringToObject(re, UserToken.class);
            token = userToken.getToken();
            if (StringUtils.isEmpty(token))
            {
                throw new BusinessException();
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            Assert.isTrue(false);
            e.printStackTrace();
        }
        finally
        {
            if (openurl != null)
            {
                openurl.disconnect();
            }
        }
    }
    
    @Test
    public void testGetServiceUrl()
    {
        getServiceUrl("ecm");
        getServiceUrl("ufm");
    }
    
    private void getServiceUrl(String type)
    {
        if (token == null)
        {
            userLogin();
        }
        String urlString = "http://10.169.66.251:8080/enterprise/api/v2/serverurl?type=" + type;
        URL url = null;
        HttpURLConnection openurl = null;
        try
        {
            url = new URL(urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Authorization", token);
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            if (openurl.getResponseCode() != 200)
            {
                throw new BusinessException();
            }
            InputStream stream = openurl.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            System.out.println("result:" + result);
            if (StringUtils.isEmpty(result))
            {
                throw new BusinessException();
            }
            Assert.isTrue(PropertiesUtils.getProperty(type + ".client.domain")
                .equals(JsonUtils.stringToMap(result).get("serverUrl")));
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            Assert.isTrue(false);
            e.printStackTrace();
        }
        finally
        {
            if (openurl != null)
            {
                openurl.disconnect();
            }
        }
    }
}
