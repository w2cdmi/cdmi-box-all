package com.huawei.sharedrive.app.test.openapi.link;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

import pw.cdmi.core.utils.RandomGUID;

public class LinkExtAPIDirectLinkTest extends BaseAPITest
{
    private INodeLink inodeLink;
    
    private static final String UPDATE_USER = "testData/users/updateUser.txt";
    
    private static final String LINK_POST_DATA = "testData/link/directPost.txt";
    
    public LinkExtAPIDirectLinkTest()
    {
        
    }
    
    @Test
    public void testNormal() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0l);
        FilePreUploadResponse response = uploadFile("d:/junitTest.jpg", folderInfo.getId());
        
        long fileID = response.getFileId();
        String plainAccessCode = "8qwewwss8";
        
        inodeLink = createLinkByPlainAccessCode(userId1, fileID, plainAccessCode, "direct");
        System.out.println(inodeLink.getAccess());
        // url = PropertiesUtils.getProperty("ufm.server.domain") +"/f"+
        // inodeLink.getPlainAccessCode();
        url = inodeLink.getUrl();
        // url = "http://10.169.68.106:8080/ufm/f/jzfkbjp8";
        System.out.println(url + "================");
        String dateStr = MyTestUtils.getDateString();
        
        String body = "{" + "\"plainAccessCode\":" + "\""
            + MyTestUtils.getLinkSignature(plainAccessCode, dateStr) + "\"," + "\"type\":\"thumbnail\","
            + "\"height\":" + "30," + "\"width\":" + "30" + "}";
        // String body =
        // "{"+"\"plainAccessCode\":"+"\""+MyTestUtils.getLinkSignature("88888",
        // dateStr)+"\"}";
        System.out.println(body);
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            dateStr,
            body);
        MyResponseUtils.getDitectResult(connection, showResult);
    }
    
    @Test
    public void testPrepareuploadTwoFile() throws Exception
    {
        // 上传文件
        FilePreUploadResponse response1 = uploadFile("D:/junitTest.jpg", 0L);
        String plainAccessCode = "3242ersdf";
        
        // 创建外链 直链
        INodeLink inodeLink1 = createLinkByPlainAccessCode(userId1,
            response1.getFileId(),
            plainAccessCode,
            "direct");
        String url1 = inodeLink1.getUrl();
        
        // 下载文件
        String dateStr = MyTestUtils.getDateString();
        String body1 = "{" + "\"plainAccessCode\":" + "\""
            + MyTestUtils.getLinkSignature(plainAccessCode, dateStr) + "\"," + "\"type\":\"thumbnail\","
            + "\"height\":" + "30," + "\"width\":" + "30" + "}";
        HttpURLConnection connection = getConnection(url1,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            dateStr,
            body1);
        MyResponseUtils.getDitectResult(connection, showResult);
        
        // 获取上传的文件的信息
        RestFileInfo fileInfo1 = getInodeInfo(userId1, response1.getFileId());
        System.out.println("======fileInfo1" + JSonUtils.toJson(fileInfo1));
        // 闪传上文件
        RestFileInfo result2 = uploadFile("D:/junitTest.jpg", userId1, 0L, fileInfo1.getSha1());
        System.out.println("==========fileInfo2" + JSonUtils.toJson(result2));
        // 查看闪传文件的状态
        String linkUrl = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + userId1 + "/" + result2.getId();
        HttpURLConnection connectionLink = getConnection(linkUrl, METHOD_GET);
        MyResponseUtils.assert200(connectionLink, showResult);
    }
    
    @Test
    public void testExpireOver() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Down", 0L);
        // 上传文件
        FilePreUploadResponse response1 = uploadFile("D:/junitTest.jpg", folderInfo.getId());
        String plainAccessCode = "3242we4df";
        
        // 创建外链 直链
        INodeLink inodeLink1 = createLinkByPlainAccessCode(userId1,
            response1.getFileId(),
            plainAccessCode,
            "direct");
        String url1 = inodeLink1.getUrl();
        
        // 下载文件
        String dateStr = MyTestUtils.getDateString();
        String body1 = "{" + "\"plainAccessCode\":" + "\""
            + MyTestUtils.getLinkSignature(plainAccessCode, dateStr) + "\"," + "\"type\":\"thumbnail\","
            + "\"height\":" + "30," + "\"width\":" + "30" + "}";
        HttpURLConnection connection = getConnection(url1,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            dateStr,
            body1);
        MyResponseUtils.getDitectResult(connection, showResult);
    }
    
    @Test
    public void testCanclePlainCode() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5() + "Down", 0L);
        
        // 上传文件
        FilePreUploadResponse response1 = uploadFile("D:/junitTest.jpg", folderInfo.getId());
        String plainAccessCode = "3242we4df";
        
        // 创建外链 直链
        INodeLink inodeLink1 = createLinkByPlainAccessCode(userId1,
            response1.getFileId(),
            plainAccessCode,
            "direct");
        
        // 取消外链
        String urlDelete = MyTestUtils.SERVER_URL_UFM_V2 + "links/" + userId1 + "/" + response1.getFileId();
        HttpURLConnection connectionDelete = getConnection(urlDelete, METHOD_DELETE);
        MyResponseUtils.assert200(connectionDelete, showResult);
        String url1 = inodeLink1.getUrl();
        
        // 下载文件
        String dateStr = MyTestUtils.getDateString();
        String body1 = "{" + "\"plainAccessCode\":" + "\"" + MyTestUtils.getLinkSignature(null, dateStr)
            + "\"," + "\"type\":\"thumbnail\"," + "\"height\":" + "30," + "\"width\":" + "30" + "}";
        HttpURLConnection connection = getConnection(url1,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            dateStr,
            body1);
        MyResponseUtils.getDitectResult(connection, showResult);
    }
    
    // DTS2014111006696
    @Test
    public void testForbiddenUser() throws Exception
    {
        String name = new RandomGUID().getValueAfterMD5();
        name = name.substring(name.length() - 4);
        RestFolderInfo folderInfo = createFolder(userId2, name + "forbid", 0L);
        FilePreUploadResponse fileInfo = uploadFile("D:/junitTest.jpg", userId2, folderInfo.getId());
        name = name.substring(name.length() - 4);
        INodeLink inodeLink = createLinkByToken(userId2,
            fileInfo.getFileId(),
            MyTestUtils.getTestUserToken2(),
            "dss" + name + "dss",
            "object");
        String url1 = inodeLink.getUrl();
        System.out.println(url1);
        // 更新用户状态为1，禁用
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + userId2;
        updateUser(urlString);
        getUserInfo(urlString);
        String dateStr = MyTestUtils.getDateString();
        String body1 = "{" + "\"plainAccessCode\":" + "\""
            + MyTestUtils.getLinkSignature(inodeLink.getPlainAccessCode(), dateStr) + "\","
            + "\"type\":\"thumbnail\"," + "\"height\":" + "30," + "\"width\":" + "30" + "}";
        HttpURLConnection connection = getConnection(url1,
            METHOD_POST,
            MyTestUtils.getTestUserToken2(),
            dateStr,
            body1);
        MyResponseUtils.getDitectResult(connection, showResult);
    }
    
    @Test
    public void testUploadFileAndOtherUserDownloadByDirect() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0l);
        FilePreUploadResponse response = uploadFile("d:/junitTest.jpg", folderInfo.getId());
        long fileID = response.getFileId();
        String plainAccessCode = "sdsds23";
        
        //创建外链码
        INodeLink inodeLink = createLinkByPlainAccessCode(userId1, fileID, plainAccessCode, "direct");
        
        String url = inodeLink.getUrl();
        System.out.println("URL:" + url);
        String dateStr = MyTestUtils.getDateString();
        
        String body = MyFileUtils.getDataFromFile(LINK_POST_DATA,"normal");
        body = body.replaceAll("#plainAccessCode#", MyTestUtils.getLinkSignature(plainAccessCode, dateStr));
        System.out.println("BODY:" + body);
        
        //用户2通过POST直链接口下载该文件
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getTestUserToken2(),
            dateStr,
            body);
        MyResponseUtils.getDitectResult(connection, showResult);
    }
    
    @Test
    public void testNoPlainAccessCodeAndOtherUploadLoad() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0l);
        FilePreUploadResponse response = uploadFile("d:/junitTest.jpg", folderInfo.getId());
        long fileID = response.getFileId();
        
        //创建外链码
        INodeLink inodeLink = createLinkByPlainAccessCode(userId1, fileID, null, "direct");
        
        String url = inodeLink.getUrl();
        System.out.println("URL:" + url);
        String dateStr = MyTestUtils.getDateString();
        
        String body = MyFileUtils.getDataFromFile(LINK_POST_DATA,"noPlainAccesscode");
        System.out.println("BODY:" + body);
        
        //用户2通过POST直链接口下载该文件
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getTestUserToken2(),
            dateStr,
            body);
        MyResponseUtils.getDitectResult(connection, showResult);
    }
    
    private RestFileInfo getInodeInfo(Long ownerId, Long fileId) throws Exception
    {
        String url = MyTestUtils.SERVER_URL_UFM_V2 + "files/" + ownerId + "/" + fileId;
        HttpURLConnection openurl = getConnection(url, METHOD_GET);
        InputStream stream = null;
        BufferedReader in = null;
        RestFileInfo inodeInfo = null;
        try
        {
            stream = openurl.getInputStream();
            in = new BufferedReader(new InputStreamReader(stream));
            String result = in.readLine();
            inodeInfo = JSonUtils.stringToObject(result, RestFileInfo.class);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(stream);
        }
        return inodeInfo;
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
        RestUserCreateRequest user = JSonUtils.stringToObject(resutlt, RestUserCreateRequest.class);
        return user;
    }
    
}
