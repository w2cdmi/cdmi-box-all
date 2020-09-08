package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 删除外链测试类
 *
 */
public class LinkAPIDeleteTest extends BaseAPITest
{
	private RestFolderInfo folderInfo;
	private RestFolderInfo spaceFolderInfo;
	private static final String ADD_SHARE_INITDATA = "testData/share/initData.txt";
	private INodeLink iNodeLink;
	private RestTeamSpaceInfo spaceInfo;
	
	public LinkAPIDeleteTest() throws Exception
	{
		folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
		Date date = new Date();
		date = new Date(date.getTime() + 100000L);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));
		iNodeLink = createLinkByExpireTime(userId1, folderInfo.getId(), date.getTime());
//		spaceInfo = createUserTeamSpace1();
//		
//		//团队空间中创建文件夹
//		folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
//		iNodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(), null);
	}
	
	@Test
	public void testNormal() throws Exception
	{
		buildUrl(userId1, folderInfo.getId());
//		buildUrl(spaceInfo.getId(), folderInfo.getId());
		HttpURLConnection connection = getConnection(url, METHOD_DELETE);
		MyResponseUtils.assert200(connection, showResult);
	}
	
    @Test
	public void testUnauthorized() throws Exception
	{
		buildUrl(userId1, folderInfo.getId());
//    	buildUrl(spaceInfo.getId(), folderInfo.getId());
		HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_DELETE, null);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
	}
	
    @Test
    public void testOwnerIdNegative() throws Exception
    {
    	buildUrl(-44L, folderInfo.getId());
//    	buildUrl(-44L, folderInfo.getId());
		HttpURLConnection connection = getConnection(url, METHOD_DELETE);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
    
    @Test
    public void testNoSuchLink() throws Exception
    {
    	RestFolderInfo folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
    	buildUrl(userId1,folderInfo1.getId());
//    	RestFolderInfo folderInfo1 = createFolder(spaceInfo.getId(), new RandomGUID().getValueAfterMD5(), 0L);
//    	buildUrl(spaceInfo.getId(),folderInfo1.getId());
    	HttpURLConnection connection = getConnection(url, METHOD_DELETE);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testNodeIdNegative() throws Exception
    {
    	buildUrl(userId1, -343L);
//    	buildUrl(spaceInfo.getId(), -343L);
		HttpURLConnection connection = getConnection(url, METHOD_DELETE);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testOwnerIdNotExist() throws Exception
    {
    	buildUrl(userId2, folderInfo.getId());
		HttpURLConnection connection = getConnection(url, METHOD_DELETE);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testNodeNotExist() throws Exception
    {
    	buildUrl(userId1, 3343L);
//    	buildUrl(spaceInfo.getId(), 3343L);
		HttpURLConnection connection = getConnection(url, METHOD_DELETE);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    //DTS2014101506275
    @Test
    public void testNoLink() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        buildUrl(userId1, folderInfo.getId());
//      buildUrl(spaceInfo.getId(), folderInfo.getId());
        HttpURLConnection connection = getConnection(url, METHOD_DELETE);
        MyResponseUtils.assertReturnCode(connection,ErrorCode.NO_SUCH_LINK, showResult);
    }
    
    @Test
    public void testDeleteShareFolderAccessCode() throws Exception
    {
    	RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
    	createLinkByExpireTime(userId1, folderInfo.getId(), new Date().getTime());
    	addShare(userId1, folderInfo.getId(), ADD_SHARE_INITDATA, "addShareMessage");
    	
    	//userId2的值与initData.txt中名为addShareMessage对应的id值相等
    	buildUrl(userId2, folderInfo.getId());
    	HttpURLConnection connection = getConnection(url, METHOD_DELETE);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
	private void buildUrl(Long ownerId, Long nodeId)
	{
		url=MyTestUtils.SERVER_URL_UFM_V2+ "links/" + ownerId + "/" + nodeId;
	}
}
