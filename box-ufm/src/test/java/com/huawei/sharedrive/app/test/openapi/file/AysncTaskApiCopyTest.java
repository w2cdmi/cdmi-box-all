package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.huawei.sharedrive.app.share.domain.INodeLink;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.task.RequestAddAsyncTask;
import com.huawei.sharedrive.app.openapi.domain.task.RequestNode;
import com.huawei.sharedrive.app.openapi.domain.user.LinkUser;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.common.util.signature.SignatureUtils;
import pw.cdmi.core.utils.JsonUtils;

/**
 * 异步任务测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class AysncTaskApiCopyTest extends FileBaseAPITest
{
    private Long destParentId;
    
    private Long fileId;
    
    public AysncTaskApiCopyTest()
    {
        url = buildUrl();
        FilePreUploadResponse fileInfo = uploadFile();
        fileId = fileInfo.getFileId();
        RestFolderInfo folderInfo = createRandomFolder();
        destParentId = folderInfo.getId();
    }
    
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        fileId = response.getFileId();
        String body = generalBody(userId1, userId1, destParentId, true, null, null, fileId);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    @Test
    public void testAuthWithLink() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        FilePreUploadResponse fileInfo = uploadFile(folderInfo.getId());
        INodeLink link = createLinkByExpireTime(userId1, fileInfo.getFileId(), null);
        String dateStr = MyTestUtils.getDateString();
        String signature = SignatureUtils.getSignature(link.getPlainAccessCode(), dateStr);
        String body = generalBody(userId1, userId1, destParentId, true, link.getId(), signature, fileId);
        HttpURLConnection connection = getConnection(url, METHOD_PUT,MyTestUtils.getTestUserToken1(), dateStr, body);
        MyResponseUtils.assert201(connection, showResult);
    }
    
    private String buildUrl()
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "tasks/nodes";
    }
    
    private String generalBody(Long srcOwnerId, Long destOwnerId, Long destParentId, Boolean autoRename, String linkCode, String plainAccessCode, Long... srcNodeIds)
    {
        RequestAddAsyncTask task = new RequestAddAsyncTask();
        task.setType("copy");
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
        
        if(StringUtils.isNotBlank(linkCode) && StringUtils.isNotBlank(plainAccessCode))
        {
            LinkUser link = new LinkUser();
            link.setLinkCode(linkCode);
            link.setPlainAccessCode(plainAccessCode);
            task.setLink(link);
        }
        return JsonUtils.toJson(task);
    }
}
