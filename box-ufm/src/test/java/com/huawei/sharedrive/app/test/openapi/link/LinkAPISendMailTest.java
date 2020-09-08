package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.share.RestLinkSendRequest;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 创建外链测试类
 *
 */
public class LinkAPISendMailTest extends BaseAPITest
{
	private static final String LINK_MAIL_DATA = "testData/link/linkSendMail.txt";
	private RestFolderInfo folderInfo;
	private INodeLink linkInfo;
	private RestTeamSpaceInfo spaceInfo;
	
	public LinkAPISendMailTest() throws Exception
	{
//		folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
//		linkInfo = createLinkByExpireTime(userId1, folderInfo.getId(), null);
		
		spaceInfo = createUserTeamSpace1();
		folderInfo = createFolder(spaceInfo.getId(), new RandomGUID().getValueAfterMD5(), 0L);
		linkInfo = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(), null);
	}
	
	@Test
	public void testNormal() throws Exception
	{
//		buildUrl(userId1, folderInfo.getId());
		buildUrl(spaceInfo.getId(), folderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_MAIL_DATA, "normal");
		RestLinkSendRequest sendRequest = JSonUtils.stringToObject(body, RestLinkSendRequest.class);
		
		//外链linkUrl由uam 和 cloudapp层提供 ufm层不提供  此处请根据uam中配置的ip进行配置
		sendRequest.setLinkUrl("https://10.169.34.108:8443/cloudapp/p/" + linkInfo.getId());
		HttpURLConnection connection = getConnection(url, METHOD_POST, JSonUtils.toJson(sendRequest));
		MyResponseUtils.assert200(connection, showResult);
	}
	
	@Test
	public void testUnauthorized() throws Exception
	{
//		buildUrl(userId1, folderInfo.getId());
		buildUrl(spaceInfo.getId(), folderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_MAIL_DATA, "normal");
		RestLinkSendRequest sendRequest = JSonUtils.stringToObject(body, RestLinkSendRequest.class);
		sendRequest.setLinkUrl("https://10.169.34.108:8443/cloudapp/p/"+linkInfo.getId());
		HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, JSonUtils.toJson(sendRequest));
		MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
	}
	
	@Test
	public void testMessageEmpty() throws Exception
	{
//		buildUrl(userId1, folderInfo.getId());
		buildUrl(spaceInfo.getId(), folderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_MAIL_DATA, "messageEmpty");
		RestLinkSendRequest sendRequest = JSonUtils.stringToObject(body, RestLinkSendRequest.class);
		sendRequest.setLinkUrl("https://10.169.34.108:8443/cloudapp/p/" + linkInfo.getId());
		HttpURLConnection connection = getConnection(url, METHOD_POST, JSonUtils.toJson(sendRequest));
		MyResponseUtils.assert200(connection, showResult);
	}
	
	@Test
	public void testEmailEmpty() throws Exception
	{
//		buildUrl(userId1, folderInfo.getId());
		buildUrl(spaceInfo.getId(), folderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_MAIL_DATA, "emailEmpty");
		RestLinkSendRequest sendRequest = JSonUtils.stringToObject(body, RestLinkSendRequest.class);
		sendRequest.setLinkUrl("https://10.169.34.108:8443/cloudapp/p/" + linkInfo.getId());
		HttpURLConnection connection = getConnection(url, METHOD_POST, JSonUtils.toJson(sendRequest));
		MyResponseUtils.assertReturnCode(connection, ErrorCode.BAD_REQUEST, showResult);
	}
	
	@Test
	public void testUserError() throws Exception
	{
		buildUrl(userId2, folderInfo.getId());
		String body = MyFileUtils.getDataFromFile(LINK_MAIL_DATA, "normal");
		RestLinkSendRequest sendRequest = JSonUtils.stringToObject(body, RestLinkSendRequest.class);
		sendRequest.setLinkUrl("https://10.169.34.108:8443/cloudapp/p/" + linkInfo.getId());
		HttpURLConnection connection = getConnection(url, METHOD_POST, JSonUtils.toJson(sendRequest));
		MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
	}
	
	@Test
	public void testFolderNotExist() throws Exception
	{
//		buildUrl(userId1, 4552L);
		buildUrl(spaceInfo.getId(), 4552L);
		String body = MyFileUtils.getDataFromFile(LINK_MAIL_DATA, "normal");
		RestLinkSendRequest sendRequest = JSonUtils.stringToObject(body, RestLinkSendRequest.class);
		sendRequest.setLinkUrl("https://10.169.34.108:8443/cloudapp/p/" + linkInfo.getId());
		HttpURLConnection connection = getConnection(url, METHOD_POST, JSonUtils.toJson(sendRequest));
		MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
	}
	
	private void buildUrl(Long ownerId, Long nodeId)
	{
		url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId +"/" + nodeId + "/sendemail";
	}
}
