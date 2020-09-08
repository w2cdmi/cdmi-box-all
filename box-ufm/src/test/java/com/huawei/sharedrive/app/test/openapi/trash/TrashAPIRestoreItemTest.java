package com.huawei.sharedrive.app.test.openapi.trash;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 还原回收站资源测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-17
 * @see
 * @since
 */
public class TrashAPIRestoreItemTest extends BaseAPITest
{
    private Long nodeId;
    
    private RestFolderInfo fatherInfo;
    private RestFolderInfo sonInfo;
    
    @Test
    public void testNormal() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        nodeId = response.getFileId();
        url = buildUrl(userId1, nodeId);
        
        deleteNode(userId1, nodeId);
        HttpURLConnection connection = getConnection(url, METHOD_PUT);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        nodeId = response.getFileId();
        url = buildUrl(userId1, nodeId);
        
        deleteNode(userId1, nodeId);
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testNormlaNewFather() throws Exception
    {   
        FilePreUploadResponse response = uploadFile();
        nodeId = response.getFileId();
        url = buildUrl(userId1, nodeId);
        
        deleteNode(userId1, nodeId);
    	fatherInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
    	sonInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), fatherInfo.getId());
    	createFolder(userId1, new RandomGUID().getValueAfterMD5(), sonInfo.getId());
    	
    	deleteFolder(userId1, sonInfo.getId());
    	
    	//恢复到另外的新文件夹中
    	RestFolderInfo newFatherInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
    	String body = "{\"destFolderId\":" + newFatherInfo.getId() + ",\"autoRename\":true}";
    	url = buildUrl(userId1, sonInfo.getId());
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNoSuchFile() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        nodeId = response.getFileId();
        url = buildUrl(userId1, nodeId);
        
        deleteNode(userId1, nodeId);
        String url = buildUrl(userId1, 999999L);
        HttpURLConnection connection = getConnection(url, METHOD_PUT);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    @Test
    public void testNoSuchItem() throws Exception
    {
        FilePreUploadResponse response = uploadFile();
        nodeId = response.getFileId();
        url = buildUrl(userId1, nodeId);
        
        String url = buildUrl(userId1, nodeId);
        HttpURLConnection connection = getConnection(url, METHOD_PUT);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        FilePreUploadResponse response = uploadFile();
//        nodeId = response.getFileId();
//        deleteNode(userId1, nodeId);
//        System.out.println(nodeId);
        
        url = buildUrl(userId1, 942L);
        HttpURLConnection connection = getConnection(url, METHOD_PUT);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private String buildUrl(Long ownerId, Long nodeId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "trash/" + ownerId + "/" + nodeId;
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
