package pw.cdmi.box.disk.test.openapi.node;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import pw.cdmi.box.disk.test.openapi.share.ShareAndLinkTestUtils;

public class FolderAPIGetFolderTest
{
    
    private static long inodeId = 364;
    
    private static long ownerId = 3;
    
    public static void main(String[] args)
    {
        FolderAPIGetFolderTest testUnit = new FolderAPIGetFolderTest();
        testUnit.testGetFolder();
    }
    
    @Test
    public void testGetFolder()
    {
        String urlString =  ShareAndLinkTestUtils.SERVER_URL + "folders/" + ownerId +"/" + inodeId;
        
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
