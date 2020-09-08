package com.huawei.sharedrive.app.test.openapi.object;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.exception.ErrorCode;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.openapi.domain.node.RestFileInfo;
import com.huawei.sharedrive.app.openapi.domain.node.SetObjectAttributeRequest;
import com.huawei.sharedrive.app.test.openapi.FileBaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;

import pw.cdmi.core.utils.JsonUtils;

public class ObjectAPISetAttributeTest extends FileBaseAPITest
{
    private String objectId;
    
    public ObjectAPISetAttributeTest()
    {
        FilePreUploadResponse fileInfo = uploadFile();
        RestFileInfo restFileInfo = getFileInfo(userId1, fileInfo.getFileId());
        objectId = restFileInfo.getObjectId();
        buildUrl(objectId);
    }
    
    @Test
    public void testNormal() throws Exception
    {
        SetObjectAttributeRequest request = new SetObjectAttributeRequest();
        request.setName("confidential");
        request.setValue("failed");
        String date = MyTestUtils.getDateString();
        String authStr = MyTestUtils.getAppSystemAuthorization(date);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, authStr, date, JsonUtils.toJson(request));
        MyResponseUtils.assert200(connection, showResult);
    }
    
    @Test
    public void testNoSuchFile() throws Exception
    {
        buildUrl("180");
        SetObjectAttributeRequest request = new SetObjectAttributeRequest();
        request.setName("confidential");
        request.setValue("false");
        String date = MyTestUtils.getDateString();
        String authStr = MyTestUtils.getAppSystemAuthorization(date);
        HttpURLConnection connection = getConnection(url, METHOD_PUT, authStr, date, JsonUtils.toJson(request));
        MyResponseUtils.assertReturnCode(connection, ErrorCode.NO_SUCH_FILE, showResult);
    }
    
    /**测试设置预览转换文件*/
    @Test
    public void testPreview() throws Exception {
    	buildPreviewUrl("268");
    	SetObjectAttributeRequest request = new SetObjectAttributeRequest();
    	request.setName("confidential");
    	request.setValue("false");
    	String date = MyTestUtils.getDateString();
    	String authStr = MyTestUtils.getAppSystemAuthorization(date);
    	HttpURLConnection connection = getConnection(url, METHOD_PUT, authStr, date, JsonUtils.toJson(request));
    	MyResponseUtils.assert200(connection, showResult);
    }
    
    /**测试通过对象id重定向下载文件*/
    @Test
    public void testContents() throws Exception {
		buildContentsUrl("1767f3b49eaed172a0f0566a38b37b95");
		SetObjectAttributeRequest request = new SetObjectAttributeRequest();
		request.setName("confidential");
        request.setValue("false");
		String date = MyTestUtils.getDateString();
        String authStr = MyTestUtils.getAppSystemAuthorization(date);
        HttpURLConnection connection = getConnection(url, METHOD_GET, authStr, date, null);
        MyResponseUtils.assert200(connection, showResult);
	}
    
    private void buildUrl(String objectId)
    {
        url = MyTestUtils.SERVER_URL_UFM_V2 + "objects/" + objectId + "/attributes";
    }
    
    private void buildContentsUrl(String objectId) {
		url = MyTestUtils.SERVER_URL_UFM_V2 + "objects/" +objectId+ "/contents";
	}
    
    private void buildPreviewUrl(String objectId) {
    	url = MyTestUtils.SERVER_URL_UFM_V2 + "objects/" +objectId+ "/preview/failed";
	}
}
