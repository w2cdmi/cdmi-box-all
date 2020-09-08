package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 获取文件下载地址
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class FileAPIRedirectDownloadTest extends FileBaseAPITest
{
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        RestFileInfo fileInfo = getFileInfo(userId1, response.getFileId());
        url = buildUrl(userId1, response.getFileId(), fileInfo.getObjectId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidObjectId() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        url = buildUrl(userId1, response.getFileId(), "invalidObjectId");
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
   
    private String buildUrl(long ownerId, long fileId, String objectId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId + "/" + objectId +"/contents";
    }
}
