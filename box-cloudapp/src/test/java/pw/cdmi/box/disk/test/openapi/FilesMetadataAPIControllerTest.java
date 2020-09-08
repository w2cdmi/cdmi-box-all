package pw.cdmi.box.disk.test.openapi;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class FilesMetadataAPIControllerTest
{
    private String syncVersion = "0";
    
    @Test
    public void getFolderChangeMetadata()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/metadata/";
        urlString += Constants.owner_id + "/0";
        urlString += "/change";
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Authorization", Constants.token);
            openurl.setRequestProperty("Content-type", "application/json");
            
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            
            String loginBody = "{\"modifiedAt\":126548997000}";
            //
            openurl.getOutputStream().write(loginBody.getBytes());
            openurl.connect();
            
            String filepath = "J:/SQL_0.LITE";
            FileOutputStream outputStream = new FileOutputStream(new File(filepath));
            byte[] b = new byte[1024 * 64];
            int length;
            while ((length = openurl.getInputStream().read(b)) > 0)
            {
                outputStream.write(b, 0, length);
            }
            
            outputStream.close();
            
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
    public void getFolderMetadata()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/metadata/";
        urlString += Constants.owner_id + "/";
        urlString += Constants.folder_id;
        
        URL url;
        
        try
        {
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Authorization", Constants.token);
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            String filepath = "J:/SQL_0.LITE";
            FileOutputStream outputStream = new FileOutputStream(new File(filepath));
            byte[] b = new byte[1024 * 64];
            int length;
            while ((length = openurl.getInputStream().read(b)) > 0)
            {
                outputStream.write(b, 0, length);
            }
            
            outputStream.close();
            
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
    public void getSyncMetadata()
    {
        String urlString = "http://127.0.0.1:8080/sharedrive/api/v1/metadata/";
        urlString += Constants.owner_id + "?syncVersion=";
        urlString += syncVersion;
        
        URL url;
        
        try
        {
            
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Authorization", Constants.token);
            openurl.setDoInput(true);
            openurl.setDoOutput(false);
            openurl.connect();
            
            String filepath = "J:/SQL_0.LITE";
            FileOutputStream outputStream = new FileOutputStream(new File(filepath));
            byte[] b = new byte[1024 * 64];
            int length;
            while ((length = openurl.getInputStream().read(b)) > 0)
            {
                outputStream.write(b, 0, length);
            }
            
            outputStream.close();
            
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
