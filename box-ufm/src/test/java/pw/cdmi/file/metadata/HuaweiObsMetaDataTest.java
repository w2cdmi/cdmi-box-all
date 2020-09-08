
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.metadata;

import com.obs.services.ObsClient;
import com.obs.services.model.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

/************************************************************
 * @Description:
 * <pre> 测试类 </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/23
 ************************************************************/
@RunWith(JUnit4.class)
public class HuaweiObsMetaDataTest {
    String endPoint = "obs.cn-north-1.myhwclouds.com";
    private String accessKey = "526GZIMDCZ2JIZQJQX1P";
    private String secretKey = "diKMQshRF63CpMeDVatp0zGf0GU9ls4SqW4nt2cd";

    @Test
    public void testAddMetaData() {
        try {
            String bucket = "csebucket-d96cd1712c8d059c273308d521ffeb1e";
            String objectKey = "123.txt";
            String filename = "中文.xml";

            // 创建ObsClient实例
            ObsClient obsClient = new ObsClient(accessKey, secretKey, endPoint);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentMd5("sddd");
            metadata.getMetadata().put("Content-Disposition", "attachment;filename=" + filename + "");

            CopyObjectRequest request = new CopyObjectRequest(bucket, objectKey, bucket, objectKey);
            request.setNewObjectMetadata(metadata);
            request.setReplaceMetadata(true);
            obsClient.copyObject(request);

            // 关闭obsClient
            obsClient.close();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void test() {
        String path = "[uds][V2_HWY][07736a7e-41ec-a2bf-6103-c2ca6e5921d3][csebucket-c73c7e2e406cee0d86824257bbcdabd1][1512386204408_05033978b104900e937d6521c6bca0d1]";

        path = "[uds][V2_HWY][27f2a72d-6606-7c5b-3bab-caa444e99719][csebucket-305cfd7602b12ce1d2d0e7d2657439dc][1521704404049_0e17865813fdc61ef86d88d7668e5a3e]";
    }
}
