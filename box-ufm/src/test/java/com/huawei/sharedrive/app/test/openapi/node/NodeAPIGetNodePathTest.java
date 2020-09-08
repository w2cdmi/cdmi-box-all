package com.huawei.sharedrive.app.test.openapi.node;

import java.net.HttpURLConnection;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

/**
 * 获取节点路径测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-8-12
 * @see
 * @since
 */
public class NodeAPIGetNodePathTest extends FileBaseAPITest
{
    
    public NodeAPIGetNodePathTest()
    {
    }
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folder1 = createRandomFolder();
        RestFolderInfo folder2 = createFolder(userId1, "folderLevel2", folder1.getId(),new Date().getTime(),new Date().getTime());
        url = buildUrl(userId1, folder2.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testGetCleanedNodePath() throws Exception
    {
        RestFolderInfo folder1 = createRandomFolder();
        RestFolderInfo folder2 = createFolder(userId1, "folderLevel2", folder1.getId());
        cleanNode(userId1, folder1.getId());
        url = buildUrl(userId1, folder2.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testGetDeleteToTrashNodePath() throws Exception
    {
        RestFolderInfo folderInfo = createRandomFolder();
        deleteNode(userId1, folderInfo.getId());
        url = buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
        
    }
    
    private String buildUrl(Long ownerId, Long nodeId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "nodes/" + ownerId + "/" + nodeId + "/path";
    }
}
