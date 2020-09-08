
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.preview;

import com.baidubce.BceClientConfiguration;
import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.doc.DocClient;
import com.baidubce.services.doc.model.CreateDocumentResponse;
import com.baidubce.services.doc.model.GetDocumentResponse;
import com.baidubce.services.doc.model.ReadDocumentResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

/************************************************************
 * @Description:
 * <pre>百度Document Service客户端验证类</pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, box-dss Component. 2018/3/7
 ************************************************************/
@RunWith(JUnit4.class)
public class BaiduDocClientTest {
    String ACCESS_KEY_ID = "2c2c0f207b7a469f914878cfc332fb42";
    String SECRET_ACCESS_KEY = "960c04cc3c0f4d608a0932da1e5ef953";

    @Test
    public void testCreateDocument() {
        // 初始化一个DocClient
        BceClientConfiguration config = new BceClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
        DocClient client = new DocClient(config);

        File file = new File("e:/tmp/曾旎+13540495002.docx");
        CreateDocumentResponse response = client.createDocument(file, "resume222");
        System.out.println("Document Id = " + response.getDocumentId());
    }

    @Test
    public void testGetDocument() {
        String documentId = "doc-ic6rah9fxhpje8g";

        // 初始化一个DocClient
        BceClientConfiguration config = new BceClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
        DocClient client = new DocClient(config);

        while (true) {
            GetDocumentResponse resp = client.getDocument(documentId);

            if(resp.getStatus().equalsIgnoreCase("PROCESSING")) {
                try {
                    System.out.println("The file is processing, wait a while...");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println(resp);
                break;
            }
        }
    }

    @Test
    public void testReadDocument() {
        String documentId = "doc-ic6rah9fxhpje8g";

        // 初始化一个DocClient
        BceClientConfiguration config = new BceClientConfiguration();
        config.setCredentials(new DefaultBceCredentials(ACCESS_KEY_ID, SECRET_ACCESS_KEY));
        DocClient client = new DocClient(config);

        ReadDocumentResponse resp = client.readDocument(documentId);
        System.out.println(resp);
    }
}
