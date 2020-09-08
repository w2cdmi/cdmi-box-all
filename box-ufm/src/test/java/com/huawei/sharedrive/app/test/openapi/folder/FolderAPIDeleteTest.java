package com.huawei.sharedrive.app.test.openapi.folder;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 文件夹删除测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-14
 * @see  
 * @since  
 */
public class FolderAPIDeleteTest extends FileBaseAPITest
{
    private Long folderId;
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + folderId;
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testInvalidFolderId() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + folderId;
        long invalidFolderId = -1;
        String url = buildUrl(userId1, invalidFolderId);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testUnauthorized () throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + folderId;
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_DELETE, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testNoSuchFolder() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        folderId = folderInfo.getId();
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + folderId;
        String url = buildUrl(userId1, 999999L);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FOLDER, showResult);
    }
    
    @Test
    public void testInvalidSpaceStatus() throws Exception
    {
//        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
//        System.out.println(folderInfo.getId());
        url = MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + userId1 + "/" + 775;
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private String buildUrl(Long ownerId, Long folderId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId + "/" + folderId;
    }
    
}
