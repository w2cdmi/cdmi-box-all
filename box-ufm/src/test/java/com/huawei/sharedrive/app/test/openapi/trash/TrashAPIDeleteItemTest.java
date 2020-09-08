package com.huawei.sharedrive.app.test.openapi.trash;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 删除回收站资源测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-17
 * @see  
 * @since  
 */
public class TrashAPIDeleteItemTest extends BaseAPITest
{
    
    private Long nodeId;
    
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        nodeId = response.getFileId();
        url = buildUrl(userId1, nodeId);
        
        deleteNode(userId1, nodeId);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        nodeId = response.getFileId();
        url = buildUrl(userId1, nodeId);
        
        deleteNode(userId1, nodeId);
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_DELETE, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testNoSuchItem() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        nodeId = response.getFileId();
        url = buildUrl(userId1, nodeId);
        
        deleteNode(userId1, nodeId);
        String url = buildUrl(userId1, 999999L);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        FilePreUploadResponse response = uploadFile();
//        nodeId = response.getFileId();
//        deleteNode(userId1, nodeId);
//        System.out.println(nodeId);
        
        url = buildUrl(userId1, 932L);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private String buildUrl(Long ownerId, Long nodeId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "trash/" + ownerId + "/" + nodeId;
    }
}
