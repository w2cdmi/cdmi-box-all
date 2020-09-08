package pw.cdmi.box.disk.test.openapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class SecurityAPIControllerTest
{
    @Test
    public void getConfigInfo()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/system/config";
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
//            String auth = Constants.tokenType + " " + Constants.token;
//            openurl.setRequestProperty("Authorization", auth);
//            openurl.setRequestProperty("Content-type", "application/json");
            
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
//           
//            String loginBody="{ssss}";
//            openurl.getOutputStream().write(loginBody.getBytes());
            
            BufferedReader in = new BufferedReader(new InputStreamReader(openurl.getInputStream()));
            System.out.println(openurl.getResponseCode());
            String re = in.readLine();
            System.out.println(re);
            
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
