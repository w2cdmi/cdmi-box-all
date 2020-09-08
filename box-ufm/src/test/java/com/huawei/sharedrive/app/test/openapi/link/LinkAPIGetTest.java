package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.share.RestShareRequestV2;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 获取外键测试类
 * 
 */
public class LinkAPIGetTest extends BaseAPITest
{
    private static final String LINK_CREATE_DATA = "testData/link/create.txt";
    
    private static final String ADD_SHARE_INITDATA = "testData/share/initData.txt";
    
    private RestFolderInfo folderInfo;
    
    private RestFolderInfo spaceFolderInfo;
    
    private INodeLink iNodeLink;
    
    private RestTeamSpaceInfo spaceInfo;
    
    @Test
    public void testNormal() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "dsd", 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), 1499845193374L);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        buildUrl(userId1, folderInfo.getId());
        // buildUrl(spaceInfo.getId(), folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), 1439845193374L);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        // buildUrl(userId1, folderInfo.getId());
        buildUrl(spaceInfo.getId(), folderInfo.getId());
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testOwnerIdNegative() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        buildUrl(-223L, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
    
    @Test
    public void testNodeIdNegative() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        // buildUrl(userId1, -33L);
        buildUrl(spaceInfo.getId(), -33L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testOwnerNotExist() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        buildUrl(6666L, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testNodeNotExist() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        // buildUrl(userId1, 9933L);
        buildUrl(spaceInfo.getId(), 9933L);
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testNoSuchLink() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        // RestFolderInfo folderInfo2 = createFolder(userId1, new
        // RandomGUID().getValueAfterMD5(), 0L);
        // buildUrl(userId1, folderInfo2.getId());
        RestFolderInfo folderInfo2 = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        buildUrl(spaceInfo.getId(), folderInfo2.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_LINK, showResult);
    }
    
    @Test
    public void testOverExpireAt() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        buildUrl(userId1, folderInfo.getId());
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(),
        // folderInfo.getId(),1391845293374L);
        // buildUrl(spaceInfo.getId(), folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_LINK, showResult);
    }
    
    @Test
    public void testCreateLinkAndDeleteLink() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        // spaceInfo = createUserTeamSpace1();
        //
        // //在群空间中创建文件夹
        // folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
        // iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(),null);
        deleteLinkCode(userId1, folderInfo.getId());
        buildUrl(userId1, folderInfo.getId());
        // buildUrl(spaceInfo.getId(), folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    // DTS2015061507879
    @Test
    public void testCreateLinkAndShareAndGet() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        RestShareRequestV2 shareInfo = addShare(folderInfo.getOwnedBy(),
            folderInfo.getId(),
            ADD_SHARE_INITDATA,
            "addAndShare");
        iNodeLink = createLinkByExpireTime(folderInfo.getOwnedBy(), folderInfo.getId(), new Date().getTime() +10000000);
        
        System.out.println("ShareInfo:" + JSonUtils.toJson(shareInfo));
        buildUrl(userId1, folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_GET, MyTestUtils.getTestUserToken2(), null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    private void buildUrl(Long ownerId, Long nodeId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId + "/" + nodeId;
    }
    
}
