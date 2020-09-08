package pw.cdmi.box.disk.test.openapi.file;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import pw.cdmi.box.disk.client.domain.user.RestUserloginRsp;
import pw.cdmi.box.disk.test.openapi.share.ShareAndLinkTestUtils;

public class FileAPIGetDownloadObjectUrlTest
{
    
    private static long inodeId = 40780;
    
    
    private static String objectId = "2def77fbb1244fd8c727915790e158f5";
    
    public static void main(String[] args)
    {
        FileAPIGetDownloadObjectUrlTest testUnit1 = new FileAPIGetDownloadObjectUrlTest();
        testUnit1.testGetObjectUrl();
    }
    
    
    @Test
    public void testGetObjectUrl()
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
        String urlString = ShareAndLinkTestUtils.SERVER_URL + "files/" + user.getUserId() + "/" + inodeId + "/" + objectId
            + "/getDownloadObj";
        
        HttpURLConnection openurl = null;
        try
        {
            System.out.println(urlString);
            URL url = new URL(urlString);
            openurl = (HttpURLConnection) url.openConnection();
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
        finally
        {
            openurl.disconnect();
        }
    }
    
}
