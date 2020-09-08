package com.huawei.sharedrive.app.test.openapi.task;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 异步复制任务测试类
 * @author l90003768
 *
 */
public class AsyncTaskAPIMoveTest extends BaseAPITest
{
    private static final String FILE_MOVE = "testData/jobs/asyncMove.txt"; 
    
    private boolean showResult = true;
    
    
    private String urlStr =  MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
    
    private RestFolderInfo srcFolder;
    
    private RestFolderInfo destFolder;
    
    public AsyncTaskAPIMoveTest()
    {
        srcFolder = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "SRC", 0L);
        destFolder = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "DEST", 0L);
        createFolder(userId1,srcFolder.getName(),destFolder.getId());
    }
    @Test
    public void testNoContainAutoRename() throws Exception
    {
        String body = "{\"type\":\"move\",  \"destFolderId\":" +
                                    destFolder.getId() + ", \"destOwnerId\":" +
                                    userId1 + ", \"srcOwnerId\":" + 
                                    userId1 + ", \"srcNodeList\": [  {\"srcNodeId\":" + 
                                    srcFolder.getId() + "}  ],\"autoRename\":\"false\"}";
//        String body = "{\"type\":\"move\",  \"destFolderId\":" +
//            destFolder.getId() + ", \"destOwnerId\":" +
//            userId1 + ", \"srcOwnerId\":" + 
//            userId1 + ", \"srcNodeList\": [  {\"srcNodeId\":" + 
//            srcFolder.getId() + "}  ]}";
        System.out.println(body);
        String taskId = AsyncTestUtils.addTask(urlStr, body, showResult, MyTestUtils.getTestUserToken1(),null);
        String urlString =  urlStr + "/" + taskId;
//        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, null, showResult);
    }
  
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMe() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveLinkToMe", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    @Test
    public void testNodeIdNull() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "nodeIdNull", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMeEncrypted() throws Exception
    {
        String plainAccessCode = MyFileUtils.getDataFromFile(FILE_MOVE, "plainAccessCode");
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveLinkToMeEncrypted", showResult,Calendar.getInstance(), 
            plainAccessCode);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMeEncryptedChild() throws Exception
    {
        String plainAccessCode = MyFileUtils.getDataFromFile(FILE_MOVE, "plainAccessCode");
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveLinkToMeEncryptedChild", showResult,Calendar.getInstance(), 
            plainAccessCode);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.NO_SUCH_SOURCE;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMeEncryptedForbidden() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveLinkToMeEncryptedForbidden", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testMoveToMeForbidden() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveOtherToMeForbidden", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testMoveToOther() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveToOther", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testMoveToTeam() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveToTeam", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testMoveOtherToMe() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveOtherToMe", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 自己的空间内目标节点不存在
     * @throws Exception
     */
    @Test
    public void testNoSuchDestmove() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "noSuchDest", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.NO_SUCH_DEST;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 自己的空间内源节点不存在
     * @throws Exception
     */
    @Test
    public void testNoSuchSourceMove() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "noSuchSource", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.NO_SUCH_SOURCE;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 复制的目标为空
     * @throws Exception
     */
    @Test
    public void testNullDestMove1() throws Exception
    {
        URL url = new URL(urlStr);
        System.out.println("url is " + url);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(FILE_MOVE, "nullDest1");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 复制的目标为空
     * @throws Exception
     */
    @Test
    public void testNullDestMove2() throws Exception
    {
        URL url = new URL(urlStr);
        System.out.println("url is " + url);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(FILE_MOVE, "nullDest2");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 复制的源文件夹和文件为空
     * @throws Exception
     */
    @Test
    public void testNullSrcMove1() throws Exception
    {
        URL url = new URL(urlStr);
        System.out.println("url is " + url);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(FILE_MOVE, "nullSrc1");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    
    /**
     * 复制的源文件夹和文件为空
     * @throws Exception
     */
    @Test
    public void testNullSrcMove2() throws Exception
    {
        URL url = new URL(urlStr);
        System.out.println("url is " + url);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(FILE_MOVE, "nullSrc2");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 复制的源文件夹和文件为空
     * @throws Exception
     */
    @Test
    public void testNullSrcMove3() throws Exception
    {
        URL url = new URL(urlStr);
        System.out.println("url is " + url);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", MyTestUtils.getTestUserToken1());
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(FILE_MOVE, "nullSrc3");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 目标就是是源文件夹
     * @throws Exception
     */
    @Test
    public void testSameNode() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "sameNode", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.SAME_NODE_CONFLICT;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }

    /**
     * 目标和源文件夹父文件夹相同
     * 暂时不提供
     * @throws Exception
     */
//    @Test
    public void testSameParent() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "sameParent", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.SAME_PARENT_CONFLICT;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    
    /**
     * 自己的空间内正常复制
     * @throws Exception
     */
    @Test
    public void testSelfNormalmove() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "normalSelf", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 自己的空间内重名冲突复制
     * @throws Exception
     */
    @Test
    public void testSelfRenamemove() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "sameNameSelf", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.ASYNC_NODES_CONFLICT;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testShareToMe() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveShareToMe", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testShareToMeChild() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "moveShareToMeChild", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 目标是源文件夹的子文件夹
     * @throws Exception
     */
    @Test
    public void testSubFolderConflict() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_MOVE, "subFolderConflict", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.SUB_FOLDER_CONFLICT;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
}
