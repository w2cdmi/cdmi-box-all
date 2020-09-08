package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 文件预上传测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class FileAPIMultiPreUploadTest extends FileBaseAPITest
{
    private static final String TEST_DATA = "testData/file/filePreUploadMulti.txt";
    
    public FileAPIMultiPreUploadTest()
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1+ "/multi";
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String body = MyFileUtils.getDataFromFile(TEST_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
}
