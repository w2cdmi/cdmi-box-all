package com.huawei.sharedrive.app.test.openapi.share;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 列举共享关系
 * 
 * 
 */
public class ShareAPIListShareUserTest extends BaseAPITest
{
    private RestFolderInfo folderInfo1;
    
    private static final String DATA_LIST = "testData/share/list.txt";
    
    @Test
    public void testNormal() throws Exception
    {
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        buildUrl(userId1, folderInfo1.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormal1() throws Exception
    {
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        buildUrl(userId1, folderInfo1.getId());
        url += "?offset=" + 88;
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormal2() throws Exception
    {
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        buildUrl(userId1, folderInfo1.getId());
        url += "?limit=" + 300;
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNormal3() throws Exception
    {
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        buildUrl(userId1, folderInfo1.getId());
        url += "?limit=" + 300 + "&offset=" + 88;
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testBadRequest1() throws Exception
    {
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        buildUrl(userId1, -55L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        buildUrl(userId1, folderInfo1.getId());
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testForbbiden() throws Exception
    {
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        buildUrl(userId2, folderInfo1.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    //DTS2014102302568
    @Test
    public void testOutOfOffset() throws Exception
    {
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        buildUrl(userId1, folderInfo1.getId());
        url +="?offset=" + 88;
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    private void buildUrl(Long ownerId, Long nodeId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "shareships/" + ownerId + "/" + nodeId;
    }
    
}
