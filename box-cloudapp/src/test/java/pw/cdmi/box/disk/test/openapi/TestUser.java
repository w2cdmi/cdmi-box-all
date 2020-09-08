package pw.cdmi.box.disk.test.openapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestUser
{
    
    public static void printBody(HttpURLConnection conn, boolean isError) throws IOException
    {
        InputStream in = null;
        if (isError)
        {
            in = conn.getErrorStream();
            ;
        }
        else
        {
            in = conn.getInputStream();
        }
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        String temp = null;
        StringBuffer sb = new StringBuffer("");
        while ((temp = br.readLine()) != null)
        {
            sb.append(temp);
        }
        
        System.out.println("Body: " + sb.toString());
    }
    
    @Test
    public void userLogin()
    {
        
        URL url;
        try
        {
            url = new URL("http://127.0.0.1:8080/api/v1/auth/login");
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String loginBody="{\"loginName\":\"c00110381\", \"password\":\"chen16940CHEN!@\", \"deviceType\":\"pc\", \"deviceSN\":\"23412\", \"deviceOS\":\"23412\", \"deviceName\":\"23412\", \"deviceAddress\":\"127.0.0.1\",\"deviceAgent\":\"df\"}";
            openurl.getOutputStream().write(loginBody.getBytes());
            
            System.out.println(openurl.getResponseCode());
            
            if (openurl.getResponseCode() / 100 == 2)
            {
                printBody(openurl, false);
            }
            else
            {
                printBody(openurl, true);
            }
            
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
        
        URL url;
        try
        {
            url = new URL("http://127.0.0.1:8080/api/v1/auth/token");
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", Constants.refreshToken);
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            System.out.println(openurl.getResponseCode());
            
            if (openurl.getResponseCode() / 100 == 2)
            {
                printBody(openurl, false);
            }
            else
            {
                printBody(openurl, true);
            }
            
            openurl.disconnect();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public static void addUser(String userstr)
    {
        URL url;
        try
        {
            url = new URL("http://127.0.0.1:8080/api/1.0/test/");
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            openurl.getOutputStream().write(userstr.getBytes());
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public static String getUser()
    {
        URL url;
        try
        {
            url = new URL("http://127.0.0.1:8080/api/1.0/test/test?offset=1&limit=100");
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
            System.out.println(openurl.getResponseCode());
            String re = in.readLine();
            System.out.println(re);
            
            openurl.disconnect();
            return re;
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
        
    }
    
    @Test
    public void testUser()
    {
        URL url;
        try
        {
            url = new URL("http://127.0.0.1:8080/sharedrive/api/v1/auth/test");
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("POST");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            String test = "{\"total\":3,\"testUnits\":[{\"id\":\"120\",\"type\":\"1\"},{\"id\":\"121\",\"type\":\"1\"},{\"id\":\"122\",\"type\":\"1\"}]}";
            openurl.getOutputStream().write(test.getBytes());
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    @Test
    public void getTestUser()
    {
        URL url;
        try
        {
            url = new URL("http://127.0.0.1:8080/sharedrive/api/v1/auth/test");
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
            System.out.println(openurl.getResponseCode());
            String re = in.readLine();
            System.out.println(re);
            
            openurl.disconnect();
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    public static void main(String[] args)
    {
        
        // String PATTERN_STR_FILENAME = "[^/<>*?:\"|\\\\]{1,255}";
        String PATTERN_STR_FILENAME = "[^/\\\\]{1,255}";
        
        Pattern PATTERN_FILENAME = Pattern.compile(PATTERN_STR_FILENAME);
        String name = "test";
        try
        {
            Matcher m = PATTERN_FILENAME.matcher(name);
            System.out.println(m.matches());
        }
        catch (Exception e)
        {
            
        }
        
    }
    
}
