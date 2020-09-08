
/*
 * 版权声明(Copyright Notice)：
 * Copyright(C) 2017-2018 聚数科技成都有限公司。保留所有权利。
 * Copyright(C) 2017-2018 www.cdmi.pw Inc. All rights reserved.
 * 警告：本内容仅限于聚数科技成都有限公司内部传阅，禁止外泄以及用于其他的商业项目
 */
package pw.cdmi.file.service.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import pw.cdmi.file.domain.FileObject;
import pw.cdmi.file.domain.FsEndpoint;
import pw.cdmi.file.engine.CloudFilesClient;
import pw.cdmi.file.engine.HuaweiObsClient;
import pw.cdmi.file.engine.TencentCosClient;
import pw.cdmi.file.model.S3FsEndpoint;
import pw.cdmi.file.model.S3FsObject;
import pw.cdmi.file.service.CloudFilesService;
import pw.cdmi.file.service.impl.CloudFilesServiceImpl;

/************************************************************
 * @Description:
 * <pre> CloudFilesServiceImpl 测试类 </pre>
 * @author Rox
 * @version 3.0.1
 * @Project Alpha CDMI Service Platform, storbox-ufm Component. 2018/4/24
 ************************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:applicationContext-*.xml",
})
@TransactionConfiguration(defaultRollback = true)
public class CloudFilesServiceImplTest {

    @Autowired
    private CloudFilesService cloudFilesService;

    @Test
    public void testGetDownloadUrl() {
        Assert.assertNotNull(cloudFilesService);

        String objectId = "66d04e13ce7cab1b9c7cceb27018485b";
        String url = cloudFilesService.getDownloadUrl(objectId);
        System.out.println(url);

        Assert.assertNotNull(url);
    }

    @Test
    public void testHuaweiObsGet() {
        FileObject object = new FileObject();
        object.setObjectId("66d04e13ce7cab1b9c7cceb27018485b");
        object.setPath("[uds][V2_HWY][07736a7e-41ec-a2bf-6103-c2ca6e5921d3][csebucket-28de71f812484d49d8eb15f0e12e7a96][1523604449577_66d04e13ce7cab1b9c7cceb27018485b]");
        object.setSha1("MD5:c45dd0cf2ed9d9cc345d37d43885bfb6;BlockMD5:4625988a1d5a38b4f7fc6de4aef6fe8d");
        object.setLength(12030);

        FsEndpoint fsEndpoint = new FsEndpoint();
        fsEndpoint.setId("07736a7e-41ec-a2bf-6103-c2ca6e5921d3");
        fsEndpoint.setEndpoint("obs.cn-north-1.myhwclouds.com:80:80:CFVM5YVJNN7QBQO3DCQ0:d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzM0NDQzNzM3NDYzMjMxNDIzMTMwMzY0MTQxMzkzODMxMzUzNTQ1NDYzNzM0NDEzOTMyMzA0NjMyMzMzNjQ1NDMzOTM0MzAzMTQyNDYzODM5MzQ0NjMyNDUzMTQ1MzQ0NTM0NDQ0NDM0MzAzMjM1MzQ0MjQyMzMzODM2Mzk0NDQ0NDUzNzQ0NDEzODM1NDMzMjM1NDE0MzMyNDYzNTM4Mzc0MjQxNDY0MjM2NDEzMTM2MzczMDMyMzYzNjM1NDMzMTs7MzUzMDMwMzAzMDtCOTUzMTI0QUVCMTM4NkU2ODEwQzZDQzIxRTZFQUFFQTs2NUQ4RUQ1NjhGREMyREZFOw:d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzM2MzQzNjM1MzQ0MTMwNDQ0NTMxNDIzMDQ2NDYzMjQ2MzUzMjM0NDMzMzM0MzA0MTMzNDQ0NTQzMzY0NTQzNDM0MzM4MzAzMzQ0NDQ0MTM4NDIzMDM1NDQ0NjMyMzk0MzM3NDMzMDQ1NDQ0NjQ2MzgzNDMyMzc0NjM0NDUzNzQ2NDEzMjM5NDQzMjM3NDYzNjQ2MzUzNDMzNDEzNzMxNDYzMDMzNDIzMjMzMzgzNTM4MzQzMzM3MzU0NDM0MzczMzszMTM1MzAzMTM1MzczNDMyMzgzMzM5MzQzMjszNTMwMzAzMDMwOzNCM0VDMTRDMzNEMzgwQUQ2REJDM0Y2NjIzNkQ1MzZFOw");
        fsEndpoint.setFsType("uds");

        S3FsObject s3FsObject = new S3FsObject(object);
        S3FsEndpoint s3FsEndpoint = new S3FsEndpoint(fsEndpoint);

        CloudFilesClient client = new HuaweiObsClient();
        String url = client.getDownloadUrl(s3FsEndpoint, s3FsObject);
        System.out.println(url);
        Assert.assertNotNull(url);
        Assert.assertTrue(url.startsWith("https://"));
    }

    @Test
    public void testTencentCosGet() {
        FileObject object = new FileObject();
        object.setObjectId("89dfa6a6d08bfb04c6ce770045765062");
        object.setPath("[tencent_cloud_cos][V2_HWY][9c19b7b6-ba9b-28b5-440c-c085266933d7][filepro-cos-bucket-1255692311][1524470299281_89dfa6a6d08bfb04c6ce770045765062]");
        object.setSha1("MD5:2d0fc6fd1cf7b579b275cacc0db38216;BlockMD5:363a1500a154796177b5b17397986595");
        object.setLength(20282755);

        FsEndpoint fsEndpoint = new FsEndpoint();
        fsEndpoint.setId("9c19b7b6-ba9b-28b5-440c-c085266933d7");
        fsEndpoint.setEndpoint("filepro-cos-bucket-1255692311.cos.ap-chengdu.myqcloud.com:80:80:AKID09B9zlQSfvJdrwySkBYyCMZv480jFriP:d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzM5Mzk0NTM4MzQ0MTQ1Mzk0MTMxMzM0NTMwMzkzNjM4NDEzNzMwNDMzMDM5MzAzOTQxNDEzODMzMzUzMzM2MzA0NTQxNDYzOTQzMzY0MTM4MzYzNzQ1MzczOTQzMzYzNzQ1MzUzNjQxNDUzMDMzMzY0MTQyMzYzODQyNDMzNTM3NDUzMzQyMzgzMDMwMzIzNzMxNDY0MjQ1NDMzMzQ0MzY0MjQ1NDYzNTMxNDU0NDMxMzMzNjMyMzk0NTQ2NDEzMTs7MzUzMDMwMzAzMDtDOUM1NzkyQkY1Q0ZCMTg4QTVCOTZCMEI3Njk3NjkwNDswNDFENkRGNzUyNTBDQzNGOw:d2NjX2NyeXB0ATQxNDU1MzVGNDM0MjQzOzM2MzI0NDQ0Mzk0NjQzMzk0MTM1MzMzNDM0MzM0MjM4NDQzNzMyMzE0MzQ2NDQ0MjM0NDMzMDM2MzEzOTMwMzkzNDMzNDE0NTQ0NDU0NDQ0MzQzNTQxNDIzOTM1NDIzMTQzNDMzNzMzMzM0MjMzNDEzMTM2MzM0MTQzNDYzNjQ2MzQzNTMyMzQ0MzQ1Mzg0NDQ0Mzk0NjQzNDI0MjQxNDEzMDM5MzI0MjQ1NDMzNDM2MzMzNjM2MzI0MjQ2NDUzNTszMTM1MzAzMTM1MzczNDMyMzgzMzM5MzQzMjszNTMwMzAzMDMwOzA4Q0U4MDNFMjA2ODZFNjdBM0NEOUEzRTM1NjQ2MDZGOw");
        fsEndpoint.setFsType("tencent_cloud_cos");

        S3FsObject s3FsObject = new S3FsObject(object);
        S3FsEndpoint s3FsEndpoint = new S3FsEndpoint(fsEndpoint);

        CloudFilesClient client = new TencentCosClient();
        String url = client.getDownloadUrl(s3FsEndpoint, s3FsObject);
        System.out.println(url);
        Assert.assertNotNull(url);
        Assert.assertTrue(url.startsWith("https://"));
    }

    @Test
    public void addMetaData() {

    }
}
