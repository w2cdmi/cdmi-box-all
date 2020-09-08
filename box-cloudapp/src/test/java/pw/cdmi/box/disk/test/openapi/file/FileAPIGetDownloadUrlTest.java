package pw.cdmi.box.disk.test.openapi.file;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import pw.cdmi.box.disk.client.domain.user.RestUserloginRsp;
import pw.cdmi.box.disk.test.openapi.share.ShareAndLinkTestUtils;

public class FileAPIGetDownloadUrlTest
{
    
    private static long inodeId = 40780;
    
    public static void main(String[] args)
    {
        FileAPIGetDownloadUrlTest testUnit1 = new FileAPIGetDownloadUrlTest();
        testUnit1.testGetDownloadUrl();
    }
    
    
    @Test
    public void testGetDownloadUrl()
    {
        RestUserloginRsp user = null;
        try
        {
            user = ShareAndLinkTestUtils.getUser();
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
        String urlString = ShareAndLinkTestUtils.SERVER_URL + "files/" + user.getUserId() + "/" + inodeId + "/contents";
        
        HttpURLConnection openurl = null;
        try
        {
            URL url = new URL(urlString);
            System.out.println(urlString);
            openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("GET");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", user.getToken());
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
        finally
        {
            openurl.disconnect();
        }
    }
    
}
