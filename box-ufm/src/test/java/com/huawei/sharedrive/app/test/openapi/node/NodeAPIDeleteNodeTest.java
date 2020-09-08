package com.huawei.sharedrive.app.test.openapi.node;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

public class NodeAPIDeleteNodeTest extends BaseAPITest
{
    
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() +"DS", 0l);
        FilePreUploadResponse fileInfo = uploadFile("D:/junitTest.jpg", folderInfo.getId());
        url = MyTestUtils.SERVER_URL_UFM_V2 + "nodes/" +userId1 +"/" + fileInfo.getFileId();
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    
}
