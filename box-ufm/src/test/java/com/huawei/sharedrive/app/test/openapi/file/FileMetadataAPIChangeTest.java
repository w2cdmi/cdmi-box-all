package com.huawei.sharedrive.app.test.openapi.file;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

public class FileMetadataAPIChangeTest extends BaseAPITest
{
    @Test
    public void getFolderChangeMetadata() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5()+"META", 0L);
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "metadata/";
        urlString += MyTestUtils.getTestCloudUserId1() + "/0/change?modifiedAt="+1391845193374L+"&zip=true";
        URL url = null;
        //String body = "{\"modifiedAt\":"+(folderInfo1.getModifiedAt().getTime() - 5000000L)+"}";
        url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("GET");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(false);
        //openurl.getOutputStream().write(body.getBytes());
        openurl.connect();
       // System.out.println("RequestBody:"+body);
        String filepath = "D:/SQL.LITE" + System.currentTimeMillis() + ".zip";
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
}
