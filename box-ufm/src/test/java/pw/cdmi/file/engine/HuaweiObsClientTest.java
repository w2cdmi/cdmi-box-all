
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.engine;

import com.obs.services.ObsClient;
import com.obs.services.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/************************************************************
 * @Description:
 * <pre> 测试类 </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/23
 ************************************************************/
@RunWith(JUnit4.class)
public class HuaweiObsClientTest {
    String endPoint = "obs.cn-north-1.myhwclouds.com";
    private String accessKey = "526GZIMDCZ2JIZQJQX1P";
    private String secretKey = "diKMQshRF63CpMeDVatp0zGf0GU9ls4SqW4nt2cd";

    String bucket = "csebucket-d96cd1712c8d059c273308d521ffeb1e";
//    String objectKey = "Bad Apple!!.mp4";
    String objectKey = "123.txt";
    String tempKey = "temp";
    String filename = "中文.xml";
    ObsClient obsClient;

    @Before
    public void init() {
        if(obsClient == null) {
            // 创建ObsClient实例
            obsClient = new ObsClient(accessKey, secretKey, endPoint);
        }
    }

    @After
    public void destroy() {
        if(obsClient != null) {
            try {
                obsClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            obsClient = null;
        }
    }

    @Test
    public void testPresignedUrl() {
        try {
            // URL有效期，3600秒
            long expireSeconds = 24 * 60 * 60;

            // 下载对象
            TemporarySignatureRequest request = new TemporarySignatureRequest(HttpMethodEnum.GET, expireSeconds);
            request.setBucketName(bucket);
//            request.setObjectKey(objectKey);
            request.setObjectKey("/00");
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            System.out.println("\t" + response.getSignedUrl());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testAddMetaData() {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.getMetadata().put("Content-Disposition", "attachment;filename*=utf-8''" + filename);

            CopyObjectRequest request = new CopyObjectRequest(bucket, objectKey, bucket, objectKey);
            request.setNewObjectMetadata(metadata);
            request.setReplaceMetadata(true);
            obsClient.copyObject(request);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testUploadFile() {
        String fileName = "华一云网企业文件宝用户快速使用1.docx";
//        String fileName = "JavaEE开发的颠覆者：Spring Boot实战 中文PDF完整版.rar";
//        String fileName = "开.rar";
        String filePath = "F:\\private\\Chat\\WxWork\\File\\2018-04\\" + fileName;
        String key = "00";
        File file = new File(filePath);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        //使用S3FSObject中的文件长度，不能使用inputStream.available().
        objectMetadata.setContentLength(file.length());


        try {
            //设置文件名元数据，以使用云存储直接下载时，也能获取到正确的文件名
//            objectMetadata.getMetadata().put("Content-Disposition", "attachment;filename*=utf-8''" + fileName);
            objectMetadata.getMetadata().put("Content-Disposition", "attachment;filename*=utf-8''" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));

            PutObjectRequest pubobjRequest = new PutObjectRequest(bucket, "/" + key, new FileInputStream(file));
            pubobjRequest.setMetadata(objectMetadata);

            obsClient.putObject(pubobjRequest);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testParsePath() {
        String path = "[uds][V2_HWY][07736a7e-41ec-a2bf-6103-c2ca6e5921d3][csebucket-c73c7e2e406cee0d86824257bbcdabd1][1512386204408_05033978b104900e937d6521c6bca0d1]";

        path = "[uds][V2_HWY][27f2a72d-6606-7c5b-3bab-caa444e99719][csebucket-305cfd7602b12ce1d2d0e7d2657439dc][1521704404049_0e17865813fdc61ef86d88d7668e5a3e]";
    }
}
