package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 *创建外链接类
 *
 */
public class LinkAPICreateTest extends BaseAPITest
{
	/**
	 * 存储外链接数据
	 */
	private static final String LINK_CREATE_DATA = "testData/link/create.txt";
	
	private RestFolderInfo folderInfo;
	
	private RestFolderInfo spaceFolderInfo;
	
	private RestTeamSpaceInfo spaceInfo;
	
	public LinkAPICreateTest() throws Exception
	{	
		Long parentId = Long.parseLong(MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "parentId"));
		
		//创建群空间
//		spaceInfo = createUserTeamSpace1();
		
		//创建文件夹
		folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
//		spaceFolderInfo = createFolder(spaceInfo.getId(), new RandomGUID().getValueAfterMD5(), parentId);
		
	}
	
	@Test
	   public void testNormal() throws Exception
	    {   
	        
	        buildUrl(userId1, folderInfo.getId());
//	      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
	        String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "object");
	        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
	        MyResponseUtils.assert201(connection, showResult);
	    }
	
	@Test
	public void testNormalDirect() throws Exception
	{	
	    FilePreUploadResponse file = uploadFile("D:/junitTest.txt", folderInfo.getId());
		buildUrl(userId1, file.getFileId());
//		buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
		HttpURLConnection connection = getConnection(url, METHOD_POST, body);
		MyResponseUtils.assert201(connection, showResult);
	}
	
	@Test
    public void testNormalObject() throws Exception
    {   
        FilePreUploadResponse file = uploadFile("D:/junitTest.txt", folderInfo.getId());
        buildUrl(userId1, file.getFileId());
//      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
        String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "object");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
    }
	
	@Test
	public void testDelteAndToCreate() throws Exception
	{
	    buildUrl(userId1, folderInfo.getId());
//      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
        String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
        
        //删除外链
        String urlDelete =MyTestUtils.SERVER_URL_UFM_V2+ "links/" + userId1 + "/" + folderInfo.getId();
        HttpURLConnection connection1 = getConnection(urlDelete, METHOD_DELETE);
        MyResponseUtils.assert200(connection1, showResult);
        System.out.println("=============>再次创建外链");
        buildUrl(userId1, folderInfo.getId());
//      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
        String body2 = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
        HttpURLConnection connection2 = getConnection(url, METHOD_POST, body2);
        MyResponseUtils.assert201(connection2, showResult);
	}
	
	@Test
	public void testHashLinkCodeAndToNewCreate() throws Exception
	{
	    buildUrl(userId1, folderInfo.getId());
//      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
        String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
        buildUrl(userId1, folderInfo.getId());
//      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
        String newBody = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
        HttpURLConnection newConnection = getConnection(url, METHOD_POST, newBody);
        MyResponseUtils.assert201(newConnection, showResult);
	}
	
	@Test
	public void testNormalForDirect() throws Exception
	{
	    FilePreUploadResponse response = uploadFile();
	    buildUrl(userId1,response.getFileId());
	    String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
	}
	
	@Test
	public void testNormal1() throws Exception
	{	
		buildUrl(userId1, folderInfo.getId());
//		buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal1");
		HttpURLConnection connection = getConnection(url, METHOD_POST, body);
		MyResponseUtils.assert201(connection, showResult);
	}
	
	@Test
	public void testNotContainPlainAccess() throws Exception
	{
		buildUrl(userId1, folderInfo.getId());
//		buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "notContainPlainAccess");
		HttpURLConnection connection = getConnection(url, METHOD_POST,body);
		MyResponseUtils.assert201(connection, showResult);
	}
	
	@Test
	public void testUnauthorized() throws Exception
	{
		buildUrl(userId1, folderInfo.getId());
//		buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
		HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
	}
	
	@Test
	public void testOwnerIdNegative() throws Exception
	{
		buildUrl(-334L, folderInfo.getId());
//		buildUrl(-334L,spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
		HttpURLConnection connection = getConnection(url, METHOD_POST,body);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
	}
	
	@Test
	public void testNodeIdNegative() throws Exception
	{
		buildUrl(userId1, -45L);
//		buildUrl(spaceInfo.getId(),-45L);
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
		HttpURLConnection connection = getConnection(url,METHOD_POST, body);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
	}
	
	@Test
	public void testEffectiveAtNegative() throws Exception
	{
		buildUrl(userId1, folderInfo.getId());
//		buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "effectiveAtNegative");
		HttpURLConnection connection = getConnection(url, METHOD_POST, body);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
	}
	
	@Test
	public void testEffectiveAtOverExpireAt() throws Exception
	{
		buildUrl(userId1, folderInfo.getId());
//		buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "effectiveAtOverExpireAt");
		HttpURLConnection connection = getConnection(url, METHOD_POST, body);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
	}
	
	@Test
	public void testOnlyExpireAt() throws Exception
	{
		buildUrl(userId1, folderInfo.getId());
//		buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "onlyExpireAt");
		HttpURLConnection connection = getConnection(url, METHOD_POST, body);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
	}
	
	@Test
	public void testNoSuchFolder() throws Exception
	{
		buildUrl(userId1, 3342L);
//		buildUrl(spaceInfo.getId(), 3342L);
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
		HttpURLConnection connection = getConnection(url, METHOD_POST, body);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
	}
	
	@Test
	public void testNoUserOrSpaceTeam() throws Exception
	{
		buildUrl(2888L, folderInfo.getId());
//		buildUrl(2888L, spaceFolderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
		HttpURLConnection connection = getConnection(url, METHOD_POST, body);
		MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
	}
	
	@Test
	public void testNoData() throws Exception
	{
		folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
		buildUrl(userId1, folderInfo.getId());
		String body="{}";
		HttpURLConnection connection = getConnection(url, METHOD_POST, body);
		MyResponseUtils.assert201(connection, showResult);
	}
	
	@Test
    public void testFolderToCreateDirect() throws Exception
    {   
        buildUrl(userId1, folderInfo.getId());
//      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
        String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "folderToCreateDirect");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
	
	@Test
	public void testFolderToCreateDirectAndToCreateObject() throws Exception
	{
	    buildUrl(userId1, folderInfo.getId());
//      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
        String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "folderToCreateDirect");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
        
        buildUrl(userId1, folderInfo.getId());
//      buildUrl(spaceInfo.getId(), spaceFolderInfo.getId());
        String body1 = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "folderToCreateObject");
        HttpURLConnection connection1 = getConnection(url, METHOD_POST, body1);
        MyResponseUtils.assert201(connection1, showResult);
	}
	
	@Test
	public void testCreateDirect() throws Exception
	{
	    RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
	    FilePreUploadResponse fileResponse = uploadFile("d:/junitTest.jpg", folderInfo.getId());
	    buildUrl(userId1, fileResponse.getFileId());
        String body = MyFileUtils.getDataFromFile(LINK_CREATE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert201(connection, showResult);
	}
	
	private void buildUrl(Long ownerId, Long nodeId) throws Exception
	{
		url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId + "/" + nodeId;
	}
    
}
