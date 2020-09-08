package com.huawei.sharedrive.app.test.openapi.share;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFolderInfo;
import com.huawei.sharedrive.app.openapi.domain.user.RestUserCreateRequest;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyFileUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.RandomGUID;

/**
 * 列举共享资源
 * 
 * @author pWX231110
 * 
 */
public class ShareAPIListShareToMeTest extends BaseAPITest
{
    private static final String SHARE_DATA = "testData/share/addShare.txt";
    /**
     * 存储列举资源的数据
     */
    private static final String SHARE_LIST_DATA = "testData/share/listShareToMe.txt";
    
    private static final String UPDATE_USER = "testData/users/updateUser.txt";
    
    private RestFolderInfo folderInfo1;
    
    private RestFolderInfo folderInfo2;
    
    /**
     * Contain all parameter
     * 
     * @throws Exception
     */
    @Test
    public void testContainAll() throws Exception
    {
        // 创建一个文件夹
        FilePreUploadResponse file = uploadFile("D:/junitTest.jpg", userId1, 0L);
        // 设置共享关系
        addShare(userId1, file.getFileId(), SHARE_ADD, "addShareMessage1");
//        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "containAll");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Parameters don't contain order
     * 
     * @throws Exception
     */
    @Test
    public void testNotCotaintOrder() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "notCotaintOrder");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Parameter don't contain thumbnail
     * 
     * @throws Exception
     */
    @Test
    public void testNotCotainThumbnail() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "notCotainThumbnail");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Parameters don't Contain keyWord
     * 
     * @throws Exception
     */
    @Test
    public void testNotContainKeyWord() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "notContainKeyWord");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Don't contain any parameters
     * 
     * @throws Exception
     */
    @Test
    public void testAllParameterNotContain() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = "{}";
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Parameters only contain limit
     * 
     * @throws Exception
     */
    @Test
    public void testOnlyContainLimit() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "onlyContainLimit");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Parameters only contain offset
     * 
     * @throws Exception
     */
    @Test
    public void testOnlyContainOffset() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "onlyContainOffset");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Parameters only contain order
     * 
     * @throws Exception
     */
    @Test
    public void testOnlyOrder() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "onlyOrder");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Parameter is only order which contain field only
     * 
     * @throws Exception
     */
    @Test
    public void testOnlyOrderAndOrderOnlyContainField() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "onlyOrderAndOrderOnlyContainField");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    /**
     * Parameters contain thumbnail and order which is only contain field.
     * 
     * @throws Exception
     */
    @Test
    public void testContainThumbnailAndOrderOnlyContainField() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "containThumbnailAndOrderOnlyContainField");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOrderByNameASC() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "orderByNameASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOrderByNameDESC() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "orderByNameDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOrderByModifiedAtASC() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "orderByModifiedAtASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOrderByModifiedAtDESC() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "orderByModifiedAtDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOrderBySizeDESC() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "orderBySizeDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOrderBySizeASC() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "orderBySizeASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOrderByTypeDESC() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "orderByTypeDESC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testOrderByTypeASC() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "orderByTypeASC");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testUnauthorized() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "containAll");
        HttpURLConnection connection = getConnectionWithUnauthToken(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.LOGINUNAUTHORIZED, showResult);
    }
    
    @Test
    public void testLimitNegative() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "limitNegative");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testLimitOverMaxValue() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "limitOverMaxValue");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOffsetNegative() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "offsetNegative");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testFieldEncroach() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "fieldEncroach");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_PARAMTER, showResult);
    }
    
    @Test
    public void testOnlySetHeiht() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "onlySetHeight");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, "Error", showResult);
    }
    
    @Test
    public void testOnlySetWidth() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "onlySetWidth");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assertReturnCode(connection, "Error", showResult);
    }
    
    @Test
    public void testShareToMeAll() throws Exception
    {
        // 创建一个文件夹
        folderInfo1 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        folderInfo2 = createFolder(userId1, new RandomGUID().getValueAfterMD5(), 0L);
        
        // 设置共享关系
        addShare(userId1, folderInfo1.getId(), SHARE_ADD, "addShareMessage");
        addShare(userId1, folderInfo2.getId(), SHARE_ADD, "addShareMessage");
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "containAll");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    //DTS2014070401674 
    @Test
    public void testShare() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId1, new RandomGUID().getValueAfterMD5()+"MyShare", 0L);
        addShare(userId1, folderInfo.getId(), "testData/share/addShare.txt", "normal");
        String urlString = MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + userId2;
        String urlString2 = MyTestUtils.SERVER_URL_UFM_V2 + "users" + "/" + userId2;
        updateUser(urlString2);
        getUserInfo(urlString);
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "containAll");
        HttpURLConnection connection = getConnection(url, METHOD_GET, MyTestUtils.getTestUserToken2(), body);
        MyResponseUtils.assertReturnCode(connection, ErrorCode.INVALID_SPACE_STATUS, showResult);
    }
    
    //DTS2014102704957
    @Test
    public void testFahterDelete() throws Exception
    {
        RestFolderInfo folderInfo = createFolder(userId2, new RandomGUID().getValueAfterMD5() +"Father", 0L, MyTestUtils.getTestUserToken2());
        FilePreUploadResponse fileResponse = uploadFile("D:/junitTest.jpg", userId2, folderInfo.getId());
        addShare(userId2, fileResponse.getFileId(), SHARE_DATA, "normal2", MyTestUtils.getTestUserToken2());
        deleteNode(userId2, folderInfo.getId());
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "containAll");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
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
    
    @Test
    public void testShareTomeAll() throws Exception
    {
        buildUrl();
        String body = MyFileUtils.getDataFromFile(SHARE_LIST_DATA, "containAll");
        HttpURLConnection connection = getConnection(url, METHOD_POST, body);
        MyResponseUtils.assert200(connection, showResult);
    }
    
    private void buildUrl()
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "shares/received";
    }
}
