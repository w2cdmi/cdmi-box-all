package pw.cdmi.box.disk.test.openapi.node;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import pw.cdmi.box.disk.test.openapi.share.ShareAndLinkTestUtils;

public class FileAPIGetFileInfoTest
{
    
    private static long inodeId = 678;
    
    private static long ownerId = 4;
    
    public static void main(String[] args)
    {
        FileAPIGetFileInfoTest testUnit = new FileAPIGetFileInfoTest();
        testUnit.testGetFileInfo();
    }
    
    @Test
    public void testGetFileInfo()
    {
        String urlString =  ShareAndLinkTestUtils.SERVER_URL + "files/" + ownerId +"/" + inodeId;
        
        URL url = null;
        
        try
        {
            url = new URL(urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", ShareAndLinkTestUtils.getUserToken());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            ShareAndLinkTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
