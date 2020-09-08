package com.huawei.sharedrive.app.test.openapi.share;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

public class ShareAPICancelShareTest extends BaseAPITest
{
    private static final String SHARE_DATA = "testData/share/addShare.txt";
    private static long inodeId = 108;
    
    public static void main(String[] args)
    {
        ShareAPICancelShareTest testUnit = new ShareAPICancelShareTest();
        testUnit.testCancelShare();
    }

    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()+"CANCLE", 0L);
        addShare(userId1, folderInfo.getId(), SHARE_DATA, "normal");
        addShare(userId1, folderInfo.getId(), SHARE_DATA, "normal2");
        String urlString = MyTestUtils.SERVER_URL_UFM_V2  + folderInfo.getId();
        HttpURLConnection connection = getConnection(urlString, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    
    @Test
    public void testCancelShare()
    {
        String urlString = MyTestUtils.SERVER_UFM_URL_V1 + "share/" + inodeId;
        
        URL url = null;
        
        try
        {
            
            url = new URL(urlString);
            
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("DELETE");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            
//            openurl.getOutputStream().write(bodyStr.getBytes());
            MyTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
   
    
}
