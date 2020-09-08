package com.huawei.sharedrive.app.test.openapi.node;

import java.net.HttpURLConnection;

import com.huawei.sharedrive.app.share.domain.INodeLink;
import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 *  获取节点信息测试类
 * 
 * @author  t90006461
 * @version  CloudStor CSE Service Platform Subproject, 2014-7-14
 * @see  
 * @since  
 */
public class NodeAPIGetNodeInfoTest extends FileBaseAPITest
{
    
    private Long nodeId;
    
    public NodeAPIGetNodeInfoTest()
    {
        RestFolderInfo folderInfo = createRandomFolder();
        nodeId = folderInfo.getId();
        url = buildUrl(userId1, 1107L);
    }
    
    @Test
    public void testNormal() throws Exception
    {
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorize() throws Exception
    {
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testAuthWithLink() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        INodeLink link = createLinkByExpireTime(userId1, folderInfo.getId(),null);
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(), link.getPlainAccessCode(), dateStr);
        String url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET, authorization, null,MyTestUtils.getDateString(), null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAll() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        INodeLink link = createLinkByPlianAccessCode(userId1, folderInfo.getId(), "sdfwerw");
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(), link.getPlainAccessCode(), dateStr);
        String url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET, authorization,null, MyTestUtils.getDateString(), null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testAllOfFile() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        FilePreUploadResponse file = uploadFile("D:/junitTest.jpg", folderInfo.getId());
        INodeLink link = createLinkByPlianAccessCode(userId1, file.getFileId(), "sddsddsfwerw");
        String dateStr = MyTestUtils.getDateString();
        String authorization = MyTestUtils.getLinkAuthorization(link.getId(), link.getPlainAccessCode(), dateStr);
        String url = buildUrl(userId1, file.getFileId());
        HttpURLConnection connection = getConnection(url, METHOD_GET, authorization,null, MyTestUtils.getDateString(), null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    
    @Test
    public void testInvalidOwnerId() throws Exception
    {
        String url = buildUrl(-1L, nodeId);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidFolderId() throws Exception
    {
        String url = buildUrl(userId1, -1L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchItem() throws Exception
    {
        String url = buildUrl(userId1, 999999L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    private String buildUrl(Long ownerId, Long folderId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "nodes/" + ownerId + "/" + folderId;
    }
    
}
