package com.huawei.sharedrive.app.test.openapi.task;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 添加共享人测试类
 * @author l90003768
 *
 */
public class AsyncTaskAPIDeleteTest extends BaseAPITest
{
    private static final String FILE_DELETE = "testData/jobs/asyncDelete.txt"; 
    
    private boolean showResult = true;
    
    
    private String urlStr =  MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
    
    
    private RestFolderInfo folderInfo;
    
    public AsyncTaskAPIDeleteTest() throws Exception
    {
    	folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
    	
    	//addShareMessage中对应的ID与userId2相等
//    	addShare(userId1, folderInfo.getId(), FILE_DELETE, "addShareMessage");
    }
    
    @Test
    public void testNormal() throws Exception
    {
    	String body = "{\"srcOwnerId\":" + userId1 + ",\"type\":\"delete\",\"srcNodeList\":[{\"srcNodeId\":" + folderInfo.getId() + "}]}";
        System.out.println(body);
    	String taskId = AsyncTestUtils.addTask(urlStr, body, showResult, MyTestUtils.getTestUserToken1(),null);
        String urlString =  urlStr + "/" + taskId;
        AsyncTestUtils.loopGetTestResult(urlString, null, showResult);
    }
    
    /**
     * DTS2014070702093
     * @throws Exception
     */
    @Test
    public void testShareReceivedExecuteDelete() throws Exception
    {
    	String body = "{\"srcOwnerId\":" + userId1 + ",\"type\":\"delete\",\"srcNodeList\":[{\"srcNodeId\":" + folderInfo.getId()+"}]}";
        String taskId = AsyncTestUtils.addTask(urlStr, body, showResult, MyTestUtils.getTestUserToken2(),null);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testShareToMe() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_DELETE, "shareToMe", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
}
