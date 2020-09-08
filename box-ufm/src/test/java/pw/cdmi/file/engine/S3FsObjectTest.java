
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.engine;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import pw.cdmi.file.model.S3FsObject;

/************************************************************
 * @Description:
 * <pre> CloudFilesServiceImpl 测试类 </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
@RunWith(JUnit4.class)
public class S3FsObjectTest {

    @Test
    public void testParse() {
        String path = "[uds][V2_HWY][07736a7e-41ec-a2bf-6103-c2ca6e5921d3][csebucket-c73c7e2e406cee0d86824257bbcdabd1][1512386204408_05033978b104900e937d6521c6bca0d1]";
        S3FsObject s3 = new S3FsObject(path);
        Assert.assertNotNull(s3);
        Assert.assertEquals("uds", s3.getCloud());
        Assert.assertEquals("1512386204408_05033978b104900e937d6521c6bca0d1", s3.getFilename());


        path = "[uds][V2_HWY][27f2a72d-6606-7c5b-3bab-caa444e99719][csebucket-305cfd7602b12ce1d2d0e7d2657439dc][1521704404049_0e17865813fdc61ef86d88d7668e5a3e]";
        s3 = new S3FsObject(path);
        Assert.assertNotNull(s3);
        Assert.assertEquals("uds", s3.getCloud());
        Assert.assertEquals("1521704404049_0e17865813fdc61ef86d88d7668e5a3e", s3.getFilename());

        path = "[tencent_cloud_cos][V2_HWY][9c19b7b6-ba9b-28b5-440c-c085266933d7][filepro-cos-bucket-1255692311][1517925188938_1549b041fda38df3886af1329498c295]";
        s3 = new S3FsObject(path);
        Assert.assertNotNull(s3);
        Assert.assertEquals("tencent_cloud_cos", s3.getCloud());
        Assert.assertEquals("1517925188938_1549b041fda38df3886af1329498c295", s3.getFilename());
    }
}
