package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 异步任务测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class AysncTaskApiGetStatusTest extends FileBaseAPITest
{
    
    @Test
    public void testNormal() throws Exception
    {
        url = buildUrl("289da6727c2-e264-4a1d-add7-44c7b25fa35b");
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    private String buildUrl(String taskId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes/" + taskId;
    }
    
}
