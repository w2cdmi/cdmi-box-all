package com.huawei.sharedrive.app.test.openapi.file;

import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.NodeCopyRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.share.RestPutShareRequestV2;
import com.huawei.sharedrive.app.share.domain.SharedUser;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

/**
 * 综合场景测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-7
 * @see
 * @since
 */
public class ComprehensiveTest extends FileBaseAPITest
{
    
    public ComprehensiveTest()
    {
    }
    
    /**
     * 删除共享关系后再复制被共享的文件
     * 
     * @throws Exception
     */
    @Test
    public void testDeleteShareThenCopy() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        FilePreUploadResponse fileInfo = uploadFile(folderInfo.getId());
        
        // 设置共享
        String setShareUrl = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + userId1 + "/"
            + folderInfo.getId();
        String body = generalAddShareRquestBody(userId2);
        HttpURLConnection connection = getConnection(setShareUrl, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
        
        // 取消共享
        String cancelShareUrl = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + userId1 + "/"
            + folderInfo.getId();
        HttpURLConnection con1 = getConnection(cancelShareUrl, METHOD_DELETE);
        MyResponseUtils.assert200(con1, showResult);
        
        // User2复制已删除共享的文件
        String copyUrl = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileInfo.getFileId()
            + "/copy";
        String copyBody = generalCopyBody(userId2, 0L, true);
        HttpURLConnection con2 = getConnectionForUser2(copyUrl, METHOD_PUT, copyBody);
        MyResponseUtils.assertReturnCode(con2, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    /**
     * 删除目录或获取目录下文件信息及下载地址
     * 
     * @throws Exception
     */
    @Test
    public void testGetFileInfoFromDeleteFolder() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        FilePreUploadResponse fileInfo = uploadFile(folderInfo.getId());
        
        deleteNode(userId1, folderInfo.getId());
        
        String getInfoUrl = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileInfo.getFileId();
        HttpURLConnection connection = getConnection(getInfoUrl, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
        
        String downloadUrl =  MyTestUtils.SERVER_URL_UFM_V2 + "files/" + userId1 + "/" + fileInfo.getFileId() + "/url";
        HttpURLConnection conn = getConnection(downloadUrl, METHOD_GET);
        MyResponseUtils.assertReturnCode(conn, ErrorCode.NO_SUCH_FILE, showResult);
        
        
        
    }
    
    
    private String generalAddShareRquestBody(Long sharedUserId)
    {
        RestPutShareRequestV2 request = new RestPutShareRequestV2();
        request.setSharedUserList(new ArrayList<>());
        SharedUser sharedUser = new SharedUser();
        sharedUser.setId(sharedUserId);
        request.getSharedUserList().add(sharedUser);
        return JsonUtils.toJson(request);
    }
    
    private String generalCopyBody(Long destOwnerId, Long destParentId, Boolean autoRename)
    {
        NodeCopyRequest request = new NodeCopyRequest();
        request.setDestOwnerId(destOwnerId);
        request.setDestParent(destParentId);
        request.setAutoRename(autoRename);
        return JsonUtils.toJson(request);
    }
}
