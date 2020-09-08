package com.huawei.sharedrive.app.test.openapi.node;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.GetNodeByNameRequest;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.RandomGUID;

/**
 * 根据名称获取节点信息测试类
 * 
 * @author t90006461
 * @version CloudStor CSE Service Platform Subproject, 2014-7-14
 * @see
 * @since
 */
public class NodeAPIGetNodeByNameTest extends FileBaseAPITest
{
    
    private static final String NODE_NAME = "junitTest";
    
    private static final String LONG_FILE_NAME = "asdf21";
    
    private Long parent;
    
    public NodeAPIGetNodeByNameTest()
    {
        RestFolderInfo parentFolder = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0l);
        parent = parentFolder.getId();
        System.out.println(parent);
        createFolder(userId1, NODE_NAME, parent);
        uploadFile("D:/junitTest.jpg", parent);
        url = buildUrl(userId1, parentFolder.getId());
        
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String body = genaralRequest(NODE_NAME);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testLongFileName() throws Exception
    {
        String body = genaralRequest(LONG_FILE_NAME);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testUnauthorize() throws Exception
    {
        String body = genaralRequest(NODE_NAME);
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.TOKENUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testInvalidOwnerId() throws Exception
    {
        String url = buildUrl(-1L, parent);
        String body = genaralRequest(NODE_NAME);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testInvalidParentId() throws Exception
    {
        String url = buildUrl(userId1, -1L);
        String body = genaralRequest(NODE_NAME);
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testNoSuchItem() throws Exception
    {
        String body = genaralRequest("UnknownNodeName");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    private String buildUrl(Long ownerId, Long parent)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + "folders/" + ownerId + "/" + parent + "/children";
    }
    
    private String genaralRequest(String name)
    {
        GetNodeByNameRequest request = new GetNodeByNameRequest(name);
        return JsonUtils.toJson(request);
    }
}
