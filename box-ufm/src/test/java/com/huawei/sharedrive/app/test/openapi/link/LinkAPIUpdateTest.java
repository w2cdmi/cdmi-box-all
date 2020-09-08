package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;
import java.util.Date;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;


/**
 * 创建外链测试类
 * 
 * 
 */
public class LinkAPIUpdateTest extends BaseAPITest
{
    private static final String LINK_UPDATE_DATA = "testData/link/update.txt";
    private static final String ADD_SHARE_INITDATA = "testData/share/initData.txt";
    private RestFolderInfo folderInfo;
    private INodeLink inodeLink;
    private RestTeamSpaceInfo spaceInfo;
    public LinkAPIUpdateTest() throws Exception
    {
    	folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
		inodeLink = createLinkByExpireTime(userId1, folderInfo.getId(),null);
//    	spaceInfo = createUserTeamSpace1();
//    	folderInfo = createFolderInTeamSpace(spaceInfo.getId(), 0L);
//    	inodeLink = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(), null);
    }
    
    @Test
    public void testNormal() throws Exception
    {
    	buildUrl(userId1, folderInfo.getId());
//    	buildUrl(spaceInfo.getId(), folderInfo.getId());
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "normal");
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
//    	buildUrl(userId1, folderInfo.getId());
    	buildUrl(spaceInfo.getId(), folderInfo.getId());
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "normal");
    	HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_PUT, body);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testOwnerIdNegative() throws Exception
    {
    	buildUrl(-45L, folderInfo.getId());
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "normal");
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
    
    @Test
    public void testNodeIdNegative() throws Exception
    {
//    	buildUrl(userId1, -3454L);
    	buildUrl(spaceInfo.getId(), -3454L);
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "normal");
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testOwnerIdNotExist() throws Exception
    {
    	buildUrl(4435L, folderInfo.getId());
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "normal");
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testOwnerIdError() throws Exception
    {	
    	buildUrl(userId2, folderInfo.getId());
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "normal");
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testOnlyEffectiveAt() throws Exception
    {
//    	buildUrl(userId1, folderInfo.getId());
    	buildUrl(spaceInfo.getId(), folderInfo.getId());
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "onlyEffectiveAt");
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyPlainAccessCode() throws Exception
    {
//    	buildUrl(userId1, folderInfo.getId());
    	buildUrl(spaceInfo.getId(), folderInfo.getId());
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "onlyPlainAccessCode");
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOnlyExpireAt() throws Exception
    {
//    	buildUrl(userId1, folderInfo.getId());
    	buildUrl(spaceInfo.getId(), folderInfo.getId());
    	String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "onlyExpireAt");
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
    }
    
    @Test
    public void testNodeNotExist() throws Exception
    {
//    	buildUrl(userId1, 4446L);
    	buildUrl(spaceInfo.getId(), 4446L);
   		String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "normal");
   		HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
   		MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    /**
     * 测试用户A将含子文件夹C的文件夹D共享给用户B，
     * 用户B修改子文件夹C的外链
     * @throws Exception
     */
    @Test
    public void testUpdateShareSonFolderAccessCode() throws Exception
    {
    	RestFolderInfo fatherInfo= createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Father", 0L);
    	RestFolderInfo sonInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Son", fatherInfo.getId());
    	
    	//创建外链 含:palinAccessCode,effectiveAt,expireAt
    	createLinkByExpireTime(userId1, sonInfo.getId(), new Date().getTime());
    	createLinkByExpireTime(userId1, fatherInfo.getId(), new Date().getTime());
    	
    	//将含外链的文件夹fatherInfo共享给ID为userId1对应值的用户
    	addShare(userId1, fatherInfo.getId(), ADD_SHARE_INITDATA, "addShareMessage");
    	
    	//userId2的值与initData.txt中名为addShareMessage对应的id值相等
    	buildUrl(userId2, sonInfo.getId());
    	String body = "{\"plainAccessCode\":\"update44@33d\"}";
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testUpdateShareFolderAccessCode() throws Exception
    {
    	RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
    	createLinkByExpireTime(userId1, folderInfo.getId(), new Date().getTime());
    	addShare(userId1, folderInfo.getId(), ADD_SHARE_INITDATA, "addShareMessage");
    	
    	//userId2的值与initData.txt中名为addShareMessage对应的id值相等
    	buildUrl(userId2, folderInfo.getId());
    	String body="{\"plainAccessCode\":\"update44@33d\"}";
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
    	MyResponseUtils.assertReturnCode(connection, ErrorCode.FORBIDDEN_OPER, showResult);
    }
    
    @Test
    public void testFolderNotContainLinkCodeToUpdate() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        buildUrl(userId1, folderInfo.getId());
        String body = MyFileUtils.getDataFromFile(LINK_UPDATE_DATA, "normal");
        HttpURLConnection connection = getConnection(url, METHOD_PUT, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_LINK, showResult);
    }
    
    private void buildUrl(Long ownerId, Long nodeId)
    {
    	url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId + "/" + nodeId;
    }
}
