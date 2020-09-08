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

public class AsyncTaskAPIAddTaskTest extends BaseAPITest
{
    private static final String FILE_MOVE = "testData/jobs/asyncMove.txt";
    
    private static final String FILE_COPY = "testData/jobs/asyncCopy.txt";
    
    private static final String FILE_CLEAN = "testData/jobs/asyncClean.txt";
    
    private static final String FILE_RESTORE = "testData/jobs/asyncRestore.txt";
    
    private static final String FILE_DELETE = "testData/jobs/asyncDelete.txt";
    
    private boolean showResult = true;
    
    private String urlStr = MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
    
    // DTS2014061908973
    @Test
    public void getAddTaskMoveNodeIdNull() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_MOVE, "nodeIdNull");
        body = body.replaceAll("#srcOwnerId#", userId1 + "");
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    @Test
    public void getAddTaskMoveSrcOwnerIdNull() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_MOVE, "srcOwnerIdNull");
        body = body.replaceAll("#srcNodeId#", srcFolderInfo.getId().toString());
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    //DTS2014111006696
    @Test
    public void getAddTaskMoveDestOwnerIdNull() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_MOVE, "destOwnerIdNull");
        body = body.replaceAll("#srcNodeId#", srcFolderInfo.getId().toString());
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#srcOwnerId#", srcFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    @Test
    public void testAddTaskCleanSrcOwnerIdNull() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_CLEAN, "srcOwnerIdNull");
        body = body.replaceAll("#srcNodeId#", srcFolderInfo.getId().toString());
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    @Test
    public void testAddTaskCleanSrcNodeIdNull() throws Exception
    {
        createFolder(userId1, new RandomGUID().getValueAfterMD5() + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_CLEAN, "srcNodeIdNull");
        body = body.replaceAll("#srcOwnerId#", userId1 + "");
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), null);
    }
    
    @Test
    public void testAddTaskDeleteSrcNodeIdNull() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_DELETE, "nodeIdNull");
        body = body.replaceAll("#srcOwnerId#", userId1 + "");
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    @Test
    public void testAddTaskDeleteSrcOwnerIdNull() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_DELETE, "srcOwnerId");
        body = body.replaceAll("#srcNodeId#", srcFolderInfo.getId().toString());
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    @Test
    public void testAddTaskRestorSrcOwnerIdNull() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_RESTORE, "srcOwnerIdNull");
        body = body.replaceAll("#srcNodeId#", srcFolderInfo.getId().toString());
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    @Test
    public void testAddTaskRestoreSrcNodeIdNull() throws Exception
    {
        createFolder(userId1, new RandomGUID().getValueAfterMD5() + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_RESTORE, "srcNodeIdNull");
        body = body.replaceAll("#srcOwnerId#", userId1 + "");
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), null);
    }
    
    // DTS2014061908973
    @Test
    public void getAddTaskCopyNodeIdNull() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_COPY, "nodeIdNull");
        body = body.replaceAll("#srcOwnerId#", userId1 + "");
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    @Test
    public void testAddTaskCopyNoContainSrcOwnerId() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_COPY, "srcOwnerId");
        body = body.replaceAll("#srcNodeId#", srcFolderInfo.getId().toString());
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#destOwnerId#", destFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    //DTS2014111006696
    @Test
    public void testAddTaskCopyNoContainDestOwnerId() throws Exception
    {
        RestFolderInfo srcFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "SrcFolder", 0L);
        RestFolderInfo destFolderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()
            + "DestFolder", 0L);
        String body = MyFileUtils.getDataFromFile(FILE_COPY, "destOwnerId");
        body = body.replaceAll("#srcNodeId#", srcFolderInfo.getId().toString());
        body = body.replaceAll("#destFolderId#", destFolderInfo.getId().toString());
        body = body.replaceAll("#srcOwnerId#", srcFolderInfo.getOwnedBy().toString());
        System.out.println("RequestBody:" + body);
        String expectedStr = AsyncTaskStatus.INVALID_PARAM;
        addStatu(urlStr, body, showResult, MyTestUtils.getTestUserToken1(), expectedStr);
    }
    
    private void addStatu(String urlString, String bodyStr, boolean showResult, String token,
        String expectedStr) throws Exception
    {
        URL url = new URL(urlString);
        System.out.println("url is " + urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        openurl.setRequestProperty("Authorization", token);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        
        openurl.getOutputStream().write(bodyStr.getBytes());
        AsyncTestUtils.assertStatus(openurl, expectedStr, showResult);
    }
}
