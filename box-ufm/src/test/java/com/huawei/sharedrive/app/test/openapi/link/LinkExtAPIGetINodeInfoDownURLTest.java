package com.huawei.sharedrive.app.test.openapi.link;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.huawei.sharedrive.app.openapi.domain.link.RestLinkExtRequest;
import com.huawei.sharedrive.app.openapi.domain.node.FilePreUploadResponse;
import com.huawei.sharedrive.app.share.domain.INodeLink;
import com.huawei.sharedrive.app.test.openapi.BaseAPITest;
import com.huawei.sharedrive.app.test.openapi.common.MyResponseUtils;
import com.huawei.sharedrive.app.test.openapi.common.MyTestUtils;
import com.huawei.sharedrive.app.test.openapi.share.JSonUtils;

public class LinkExtAPIGetINodeInfoDownURLTest extends BaseAPITest
{
    private RestLinkExtRequest extRequest = new RestLinkExtRequest();
    private INodeLink inodeLink;
    
    public LinkExtAPIGetINodeInfoDownURLTest()
    {
        FilePreUploadResponse response = uploadFile();
        inodeLink = createLinkByPlainAccessCode(userId1, response.getFileId(),"sddaa","direct");
    }
    
    @Test
    public void testNormal() throws Exception
    {
        String dateStr = MyTestUtils.getDateString();
        extRequest.setPlainAccessCode(MyTestUtils.getLinkSignature(inodeLink.getPlainAccessCode(), dateStr));
        extRequest.setType("thumbnail");
        url = buildUrl(inodeLink.getId());
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            dateStr,
            JSonUtils.toJson(extRequest));
        MyResponseUtils.getDitectResult(connection, showResult);
    }
    
    @Test
    public void testNormal1() throws Exception
    {
        String dateStr = MyTestUtils.getDateString();
        extRequest.setPlainAccessCode(MyTestUtils.getLinkSignature(inodeLink.getPlainAccessCode(), dateStr));
        url = buildUrl(inodeLink.getId());
        HttpURLConnection connection = getConnection(url,
            METHOD_POST,
            MyTestUtils.getTestUserToken1(),
            dateStr,
            JSonUtils.toJson(extRequest));
        MyResponseUtils.getDitectResult(connection, showResult);
    }
    
    private String buildUrl(String linkCode)
    {
        String url = MyTestUtils.SERVER_URL_UFM + "f/" + linkCode;
        return url;
    }
}
