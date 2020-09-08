package com.huawei.sharedrive.app.test.openapi.task;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 添加共享人测试类
 * @author l90003768
 *
 */
public class AsyncTaskAPICleanTest extends BaseAPITest
{
    private static final String FILE_CLEAN = "testData/jobs/asyncClean.txt"; 
    
    private String urlStr =  MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
    
    public static void main(String[] args) throws Exception
    {
        AsyncTaskAPICleanTest testUnit = new AsyncTaskAPICleanTest();
        testUnit.testClean();
    }
    
    @Test
    public void testClean()
    {
        String urlString =  MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
        
        URL url = null;
        
        try
        {
            url = new URL(urlString);
            System.out.println("url is " + urlString);
            HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
            openurl.setRequestMethod("PUT");
            openurl.setRequestProperty("Content-type", "application/json");
            openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
            openurl.setDoInput(true);
            openurl.setDoOutput(true);
            openurl.connect();
            
            String bodyStr = MyFileUtils.getDataFromFile(FILE_CLEAN);
            
            openurl.getOutputStream().write(bodyStr.getBytes());
            MyTestUtils.output(openurl);
            openurl.disconnect();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void testSrcNodeIdNull() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_CLEAN, "nodeIdNull", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.NO_SUCH_SOURCE;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(),srcFolderInfo.getId());
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(),srcFolderInfo.getId());
        String body = MyFileUtils.getDataFromFile(FILE_CLEAN, "normal");
        body = body.replaceAll("#srcOwnerId#", userId1 +"");
        body = body.replaceAll("#srcNodeId1#", folderInfo1.getId().toString());
        body = body.replaceAll("#srcNodeId2#", folderInfo2.getId().toString());
        System.out.println(body);
        String taskId = AsyncTestUtils.addTask(urlStr, body, showResult, MyTestUtils.getTestUserToken1(),null);
        String urlString =  urlStr + "/" + taskId;
        AsyncTestUtils.loopGetTestResult(urlString, null, showResult);
    }
    
    
}
