package com.huawei.sharedrive.app.test.openapi.share;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

public class ShareAPICancleShareTest extends BaseAPITest
{
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        url = buildUrl(folderInfo1.getId());
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNoSuchItem() throws Exception
    {
        url = buildUrl(9999L);
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testUnthorized() throws Exception
    {
        RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Shared", 0L);
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage1");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage2");
        url = buildUrl(folderInfo1.getId());
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_DELETE, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    private String buildUrl(Long nodeId)
    {
        return MyTestUtils.SERVER_URL_UFM_V2 + nodeId;
    }
    
}
