package com.huawei.sharedrive.app.test.openapi.task;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 添加共享人测试类
 * @author l90003768
 *
 */
public class AsyncTaskAPIRestoreTest extends BaseAPITest
{
    private static final String FILE_RESTORE = "testData/jobs/asyncRestore.txt"; 
    
    private boolean showResult = true;
    
    private String urlStr =  MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
    
    public AsyncTaskAPIRestoreTest()
    {
    	
    	
    }
    
    @Test
    public void testNormal1() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), srcFolderInfo.getId());
        RestFolderInfo folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), srcFolderInfo.getId());
        String body = MyFileUtils.getDataFromFile(FILE_RESTORE, "normal1");
        body = body.replaceAll("#srcOwnerId#", userId1 + "");
        body = body.replaceAll("#srcNodeId1#", folderInfo1.getId().toString());
        body = body.replaceAll("#srcNodeId2#", folderInfo2.getId().toString());
        System.out.println(body);
        String taskId = AsyncTestUtils.addTask(urlStr, body, showResult, MyTestUtils.getTestUserToken1(),null);
        String urlString =  urlStr + "/" + taskId;
        AsyncTestUtils.loopGetTestResult(urlString, null, showResult);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void testNormal() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_RESTORE, "normal", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    @Test
    public void testRestoreNewFolder() throws Exception
    {	
    	
    	//新父文件夹
    	RestFolderInfo newFatherInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "newFather", 0L);
    	
    	//destFolderId为当前恢复到新父的文件夹中的id
    	String body = "{\"srcOwnerId\":" + userId1 + ",\"type\":\"restoreTrash\",\"destFolderId\":" + newFatherInfo.getId() + "}";
    	String taskId = AsyncTestUtils.addTask(urlStr, body, showResult, MyTestUtils.getTestUserToken1(),null);
    	String urlString = urlStr + "/" + taskId;
    	String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void testOthers() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_RESTORE, "other", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    
    /**
     * @throws Exception
     */
    @Test
    public void testTeamspaceOwner() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_RESTORE, "teamOwner", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void testTeamspaceOther() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_RESTORE, "teamOther", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    private void deleteFolder(Long ownerId, Long nodeId)
    {
    	String deleteUrl = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId + "/" + nodeId;
    	
    	 HttpURLConnection connection;
		try {
			connection = getConnection(deleteUrl, METHOD_DELETE);
			MyResponseUtils.assert200(connection, showResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
