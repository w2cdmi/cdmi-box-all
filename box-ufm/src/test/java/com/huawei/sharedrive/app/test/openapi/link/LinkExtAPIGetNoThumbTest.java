package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

public class LinkExtAPIGetNoThumbTest extends BaseAPITest
{
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DirectGet", 0L);
        FilePreUploadResponse fileResponse = uploadFile("D:/junitTest.jpg", userId1, folderInfo.getId());
        INodeLink inodeLink = createLinkByPlainAccessCode(userId1,
            fileResponse.getFileId(),
            null,
            "direct");
        //参数正常情况
        url = inodeLink.getUrl();
        HttpURLConnection connection = getConnnectionDirect(url, METHOD_GET, 30L, 30L);
        MyResponseUtils.getDitectResult(connection, showResult);
    }

    private HttpURLConnection getConnnectionDirect(String requestUrl, String method, Long height, Long width)
        throws Exception
    {
        URL url = new URL(requestUrl);
        System.out.println("Request url : " + requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
}
