package pw.cdmi.box.disk.test.openapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class ListenServletTest {

	private String token = "5cf8564e44a4f6c7f94dedd08be9b573";

	private String syncVersion ="26";

	public static String printBody(HttpURLConnection conn, boolean isError) throws IOException {
        InputStream in = null;
        if(isError){
            in = conn.getErrorStream();;
        }else{
            in = conn.getInputStream();
        }
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        String temp = null;
        StringBuffer sb = new StringBuffer("");
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        
        System.out.println("Body: " + sb.toString());
        return sb.toString();
    }
    @Test
    public void listenServletTest()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/event/listen?syncVersion=";
        urlString += syncVersion;
        
        URL url;
        
        try {
            
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url
                    .openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Authorization", token);
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            System.out.println(openurl.getResponseCode());

            if(openurl.getResponseCode()/100 ==2  )
            {
                printBody(openurl,false);
            }
            else
            {
                printBody(openurl, true);
            }
           
            System.out.println(openurl.getResponseCode());
            openurl.disconnect();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
   
	
}
