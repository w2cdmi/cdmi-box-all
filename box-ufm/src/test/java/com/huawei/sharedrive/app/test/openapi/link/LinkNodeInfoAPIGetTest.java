package com.huawei.sharedrive.app.test.openapi.link;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.share.RestLinkCreateRequestV2;
import com.huawei.sharedrive.app.openapi.domain.teamspace.RestTeamSpaceInfo;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;
import com.huawei.sharedrive.app.utils.Utils;

import pw.cdmi.core.utils.JsonUtils;
import pw.cdmi.core.utils.RandomGUID;

/**
 * 获取外链指向的文件夹或文件信息
 * 
 * @author pWX231110
 * 
 */

public class LinkNodeInfoAPIGetTest extends BaseAPITest
{
    private static final String UPDATE_USER = "testData/users/updateUser.txt";
    
    private RestFolderInfo folderInfo;
    
    private INodeLink linkInfo;
    
    private RestTeamSpaceInfo spaceInfo;
    
    public LinkNodeInfoAPIGetTest() throws Exception
    {
        folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        linkInfo = createLinkByExpireTime(userId1, folderInfo.getId(), null);
        
        // spaceInfo = createUserTeamSpace1();
        // folderInfo = createFolder(spaceInfo.getId(), new
        // RandomGUID().getValueAfterMD5(), 0L);
        // linkInfo = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(), null);
        buildUrl();
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(linkInfo.getId(), linkInfo.getPlainAccessCode(), dateStr),
            dateStr,
            null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_GET, null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testLinkCodeError() throws Exception
    {
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization("hbpa3d2o", linkInfo.getPlainAccessCode(), dateStr),
            dateStr,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testLinkNotExist() throws Exception
    {
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization("hbpa3d2o", "dgfdfg@fdf3", dateStr),
            dateStr,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_ITEM, showResult);
    }
    
    @Test
    public void testPlainAccessCodeError() throws Exception
    {
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(linkInfo.getId(), "dfs#dfsd8", dateStr),
            dateStr,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testDataIsNull() throws Exception
    {
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(linkInfo.getId(), linkInfo.getPlainAccessCode(), null),
            null,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testEffectAtNoEffect() throws Exception
    {
        // folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        // linkInfo = createLink(userId1, folderInfo.getId(), 1461845193374L);
        
        folderInfo = createFolder(spaceInfo.getId(), new RandomGUID().getValueAfterMD5(), 0L);
        linkInfo = createLink(spaceInfo.getId(), folderInfo.getId(), 1461845193374L);
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(linkInfo.getId(), linkInfo.getPlainAccessCode(), dateStr),
            dateStr,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LINK_NOT_EFFECTIVE, showResult);
    }
    
    @Test
    public void teseExpireAtIsExpire() throws Exception
    {
        folderInfo = createFolder(spaceInfo.getId(), new RandomGUID().getValueAfterMD5(), 0L);
        linkInfo = createLinkByExpireTime(spaceInfo.getId(), folderInfo.getId(), 1391845693374L);
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(linkInfo.getId(), linkInfo.getPlainAccessCode(), dateStr),
            dateStr,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LINK_EXPIRED, showResult);
    }
    
    @Test
    public void testCreateLinkAndDeleteLink() throws Exception
    {
        deleteLinkCode(userId1, folderInfo.getId());
        buildUrl();
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(linkInfo.getId(), linkInfo.getPlainAccessCode(), dateStr),
            dateStr,
            null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNotContainLinkCode() throws Exception
    {
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(null, null, dateStr),
            dateStr,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_LINK, showResult);
    }
    
    // DTS2014102300644
    @Test
    public void testResponseCreator() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Creator", 0L);
        // 上传文件
        FilePreUploadResponse fileResponse = uploadFile("D:/junitTest.txt", folderInfo.getId());
        // 创建外链
        INodeLink linkV2 = createLink(userId1, fileResponse.getFileId(), 1385845193374L);
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(linkV2.getId(), linkV2.getPlainAccessCode(), dateStr),
            dateStr,
            null);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    //DTS2014102704957
    @Test
    public void testLink() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId2, new RandomGUID().getValueAfterMD5()+"FATHER", 0l,MyTestUtils.getTestUserToken2());
        INodeLink linkV2= createLinkByToken(userId2, folderInfo.getId(), MyTestUtils.getTestUserToken2(), "object", "object");
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + userId2;
        String urlString2 = MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + userId2;
        updateUser(urlString2);
        getUserInfo(urlString);
        buildUrl();
        String dateStr = MyTestUtils.getDateString();
        HttpURLConnection connection = getConnection(url,
            METHOD_GET,
            MyTestUtils.getLinkAuthorization(linkV2.getId(), linkV2.getPlainAccessCode(), dateStr),
            dateStr,
            null);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    private void updateUser(String urlString) throws Exception
    {
        URL url = new URL(urlString);
        System.out.println(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("PUT");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        String bodyStr = MyFileUtils.getDataFromFile(UPDATE_USER, "successData");
        openurl.getOutputStream().write(bodyStr.getBytes());
        MyResponseUtils.assert200(openurl, showResult);
    }
    
    private RestUserCreateRequest getUserInfo(String urlString) throws Exception
    {
        URL url = null;
        url = new URL(urlString);
        HttpURLConnection openurl = (HttpURLConnection) url.openConnection();
        openurl.setRequestMethod("GET");
        openurl.setRequestProperty("Content-type", "application/json");
        String dateStr = MyTestUtils.getDateString();
        openurl.setRequestProperty("Authorization", MyTestUtils.getAppAuthorization(dateStr));
        openurl.setRequestProperty("Date", dateStr);
        openurl.setDoInput(true);
        openurl.setDoOutput(true);
        openurl.connect();
        
        InputStream stream = null;
        BufferedReader in = null;
        stream = openurl.getInputStream();
        in = new BufferedReader(new InputStreamReader(stream));
        System.out.println("Return data is------------------------------------------");
        String resutlt = in.readLine();
        System.out.println(resutlt);
        RestUserCreateRequest user = JSonUtils.stringToObject(resutlt, RestUserCreateRequest.class);
        return user;
    }
    
    private INodeLink createLink(Long ownerId, Long nodeId, Long errectAt)
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + ownerId + "/" + nodeId;
        InputStream stream = null;
        BufferedReader in = null;
        
        RestLinkCreateRequestV2 request = new RestLinkCreateRequestV2();
        request.setPlainAccessCode("qwwdfgs3d@");
        request.setEffectiveAt(errectAt);
        
        try
        {
            HttpURLConnection connection = getConnection(url, METHOD_POST, JsonUtils.toJson(request));
            stream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            INodeLink response = JSonUtils.stringToObject(result, INodeLink.class);
            return response;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Create file link failed!");
        }
        finally
        {
            Utils.close(in);
            Utils.close(stream);
        }
    }
    
    private void buildUrl()
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "links/node";
    }
    
}
