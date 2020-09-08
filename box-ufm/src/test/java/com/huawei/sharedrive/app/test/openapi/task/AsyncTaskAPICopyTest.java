package com.huawei.sharedrive.app.test.openapi.task;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.restv2.task.status.AsyncTaskStatus;
import com.huawei.sharedrive.app.share.domain.INodeLink;
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
public class AsyncTaskAPICopyTest extends BaseAPITest
{
    private static final String FILE_COPY = "testData/jobs/asyncCopy.txt"; 
    
    private boolean showResult = true;
    
    private String urlStr =  MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
    

    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        RestFolderInfo srcFolderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), destFolderInfo.getId());
        RestFolderInfo srcFolderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), destFolderInfo.getId());
        INodeLink nodeLink = createLinkByPlainAccessCode(userId1, srcFolderInfo1.getId(), "dfdfdss", "object");
        String body = MyFileUtils.getDataFromFile(FILE_COPY, "normal");
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", userId1 + "");
        body = body.replaceAll("#srcOwnerId#", userId1 + "");
        body = body.replaceAll("#srcNodeId1#", srcFolderInfo1.getId().toString());
        body = body.replaceAll("#srcNodeId2#", srcFolderInfo2.getId().toString());
        String dateStr = MyTestUtils.getDateString();
        body = body.replaceAll("#plainAccessCode#", MyTestUtils.getLinkSignature(nodeLink.getPlainAccessCode(), dateStr));
        body = body.replaceAll("#linkCode#", nodeLink.getId());
        
        System.out.println(body);
        
        String taskId = AsyncTestUtils.addTask(urlStr, body, showResult, MyTestUtils.getTestUserToken1(),dateStr);
        String urlString =  urlStr + "/" + taskId;
        AsyncTestUtils.loopGetTestResult(urlString, null, showResult);
    }
    
    /**
     * 自己的空间内源节点不存在
     * @throws Exception
     */
    @Test
    public void testNoSuchSourceCopy() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "noSuchSource", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.NO_SUCH_SOURCE;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 目标是源文件夹的子文件夹
     * @throws Exception
     */
    @Test
    public void testSubFolderConflictCopy() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "subFolderConflict", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.SUB_FOLDER_CONFLICT;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCopyToOther() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyToOther", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testOtherToMeForbidden() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyOtherToMeForbidden", showResult);
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
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyShareToMe", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMeBadPlainAccessCode() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyLinkToMeBadAccessCode", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMe() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyLinkToMe", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMeEncryptedForbidden() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyLinkToMeEncryptedForbidden", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FORBBIDEN;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMeEncrypted() throws Exception
    {
        String plainAccessCode = MyFileUtils.getDataFromFile(FILE_COPY, "plainAccessCode");
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyLinkToMeEncrypted", showResult,Calendar.getInstance(), 
            plainAccessCode);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testLinkToMeEncryptedChild() throws Exception
    {
        String plainAccessCode = MyFileUtils.getDataFromFile(FILE_COPY, "plainAccessCode");
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyLinkToMeEncryptedChild", showResult,Calendar.getInstance(), 
            plainAccessCode);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 
     * @throws Exception
     */
    @Test
    public void testShareToMeChild() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "copyShareToMeChild", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 目标就是是源文件夹
     * @throws Exception
     */
    @Test
    public void testSameNode() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "sameNode", showResult);
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
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "sameParent", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.SAME_PARENT_CONFLICT;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 自己的空间内目标节点不存在
     * @throws Exception
     */
    @Test
    public void testNoSuchDestCopy() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "noSuchDest", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.NO_SUCH_DEST;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    /**
     * 自己的空间内正常复制
     * @throws Exception
     */
    @Test
    public void testSelfNormalCopy() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "normalSelf", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.FINISH;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }

    /**
     * 自己的空间内重名冲突复制
     * @throws Exception
     */
    @Test
    public void testSelfRenameCopy() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "sameNameSelf", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.ASYNC_NODES_CONFLICT;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
    
    /**
     * 复制的目标为空
     * @throws Exception
     */
    @Test
    public void testNullDestCopy1() throws Exception
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
        String bodyStr = MyFileUtils.getDataFromFile(FILE_COPY, "nullDest1");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 复制的目标为空
     * @throws Exception
     */
    @Test
    public void testNullDestCopy2() throws Exception
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
        String bodyStr = MyFileUtils.getDataFromFile(FILE_COPY, "nullDest2");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 复制的源文件夹和文件为空
     * @throws Exception
     */
    @Test
    public void testNullSrcCopy1() throws Exception
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
        String bodyStr = MyFileUtils.getDataFromFile(FILE_COPY, "nullSrc1");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 复制的源文件夹和文件为空
     * @throws Exception
     */
    @Test
    public void testNullSrcCopy2() throws Exception
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
        String bodyStr = MyFileUtils.getDataFromFile(FILE_COPY, "nullSrc2");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    /**
     * 复制的源文件夹和文件为空
     * @throws Exception
     */
    @Test
    public void testNullSrcCopy3() throws Exception
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
        String bodyStr = MyFileUtils.getDataFromFile(FILE_COPY, "nullSrc3");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assertReturnCode(openurl, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testSrcNodeIdIsNull() throws Exception
    {
        String taskId = AsyncTestUtils.addTask(urlStr, FILE_COPY, "nodeIdNull", showResult);
        String urlString =  urlStr + "/" + taskId;
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        AsyncTestUtils.loopGetTestResult(urlString, expectedStr, showResult);
    }
    
}
