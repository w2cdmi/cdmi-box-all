package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.task.RequestAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.RequestNode;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

/**
 * 异步任务测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class AysncTaskApiMoveTest extends FileBaseAPITest
{
    private Long destParentId;
    
    private Long fileId;
    
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl();
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String body = generalBody(userId2, userId1, destParentId, false, fileId);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testNoSuchSource() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        url = buildUrl();
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
        String body = generalBody(userId1, userId2, destParentId, false, fileId);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
    }
    
    private String generalBody(Long srcOwnerId, Long destOwnerId, Long destParentId, Boolean autoRename, Long... srcNodeIds)
    {
        RequestAddAsyncTask task = new RequestAddAsyncTask();
        task.setType("move");
        task.setDestOwnerId(destOwnerId);
        task.setDestFolderId(destParentId);
        task.setAutoRename(false);
        task.setSrcOwnerId(srcOwnerId);
        RequestNode node = null;
        
        List<RequestNode> list = new ArrayList<>();
        for(Long id : srcNodeIds)
        {
            node = new RequestNode();
            node.setSrcNodeId(id);
            list.add(node);
        }
        task.setSrcNodeList(list);
        return JsonUtils.toJson(task);
    }
}
