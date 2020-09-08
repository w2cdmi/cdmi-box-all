package pw.cdmi.box.disk.test.openapi.share;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class ShareAPIAddShareTest
{
    private static final String FILE_ADD_SHARE = "testData/addShare.txt"; 
    
    
    private static long inodeId = 40723;
    
    public static void main(String[] args) throws Exception
    {
        ShareAPIAddShareTest testUnit = new ShareAPIAddShareTest();
        testUnit.testAddShare();
    }
    
    @Test
    public void testAddShare()
    {
        String urlString =  ShareAndLinkTestUtils.SERVER_URL + "share/add/" + inodeId;
        
        URL url = null;
        
        try
        {
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", ShareAndLinkTestUtils.getUserToken());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            String bodyStr = ShareAndLinkTestUtils.getDataFromFile(FILE_ADD_SHARE);
            
            openurl.getOutputStream().write(bodyStr.getBytes());
            ShareAndLinkTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
